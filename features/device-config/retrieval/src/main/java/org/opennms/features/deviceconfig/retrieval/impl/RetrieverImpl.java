/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2022 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2022 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.features.deviceconfig.retrieval.impl;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import org.opennms.core.concurrent.FutureUtils;
import org.opennms.core.utils.InetAddressUtils;
import org.opennms.features.deviceconfig.retrieval.api.Retriever;
import org.opennms.features.deviceconfig.sshscripting.SshScriptingService;
import org.opennms.features.deviceconfig.tftp.TftpFileReceiver;
import org.opennms.features.deviceconfig.tftp.TftpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.vavr.control.Either;

/**
 * Orchestrates device config retrieval.
 * <p>
 * Device config uploads are triggered using the SshScriptingService and files received by a tft server are processed.
 */
public class RetrieverImpl implements Retriever, AutoCloseable {

    private static Logger LOG = LoggerFactory.getLogger(RetrieverImpl.class);

    private static String SCRIPT_VAR_FILENAME = "filename";
    private static String SCRIPT_VAR_TFTP_SERVER_IP = "tftpServerIp";
    private static String SCRIPT_VAR_TFTP_SERVER_PORT = "tftpServerPort";
    private static String SCRIPT_VAR_CONFIG_TYPE = "configType";

    private final SshScriptingService sshScriptingService;
    private final TftpServer tftpServer;
    private final ExecutorService executor;
    private final String tftpServerIp;

    public RetrieverImpl(SshScriptingService sshScriptingService, TftpServer tftpServer) throws Exception {
        this.sshScriptingService = sshScriptingService;
        this.tftpServer = tftpServer;
        this.executor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("device-config-retriever-%d").build());
        this.tftpServerIp = determineIp();
    }

    private String determineIp() throws Exception {
        try(final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddressUtils.UNPINGABLE_ADDRESS, 10002);
            return socket.getLocalAddress().getHostAddress();
        }
    }

    @Override
    public CompletionStage<Either<Failure, Success>> retrieveConfig(
            Protocol protocol,
            String script,
            String user,
            String password,
            String host,
            int port,
            String configType,
            Map<String, String> vars,
            Duration timeout
    ) {
        LOG.debug("retrieve config - host: " + host + "; port: " + port);
        var vs = new HashMap<String, String>();
        vs.putAll(vars);
        // generate a unique filename
        // -> the filename is used to distinguish incoming files
        // -> scripts must use "tftp put <localfile> ${filename}" (or similar) to upload the device config
        var filename = configTypeToUploadFileName(configType);
        vs.put(SCRIPT_VAR_FILENAME, filename);

        // set the ip address and port of the tftp server
        vs.put(SCRIPT_VAR_TFTP_SERVER_IP, tftpServerIp);
        vs.put(SCRIPT_VAR_TFTP_SERVER_PORT, String.valueOf(tftpServer.getPort()));
        vs.put(SCRIPT_VAR_CONFIG_TYPE, configType);

        if (protocol == Protocol.TFTP) {
            // the file receiver is registered with the tftp server
            // -> it triggers the file upload as soon as the future that is returned is created
            // -> it waits for the file or for a timeout
            // -> it is unregistered from the tftp server when the future is completed
            var tftpFileReceiver = new TftpFileReceiverImpl(
                    host,
                    port,
                    filename,
                    () -> sshScriptingService.execute(script, user, password, host, port, vs, timeout)
            );

            try {
                tftpServer.register(tftpFileReceiver);

                try {
                    return FutureUtils.completionStage(
                            tftpFileReceiver::completeNowOrLater,
                            timeout,
                            tftpFileReceiver::onTimeout,
                            executor
                    ).whenComplete((e, t) -> tftpServer.unregister(tftpFileReceiver));
                } catch (RuntimeException e) {
                    // make sure the file receiver is unregistered in case no future is returned
                    tftpServer.unregister(tftpFileReceiver);
                    throw e;
                }
            } catch (Exception e) {
                var message = "could not trigger device config retrievel - host: " + host + "; port: " + port;
                LOG.error(message, e);
                return CompletableFuture.completedFuture(Either.left(new Failure(message)));
            }

        } else {

            var message = "unsupported protocol for device config retrieval - host: " + host + "; port: " + port + "; protocol: " + protocol;
            LOG.error(message);
            return CompletableFuture.completedFuture(Either.left(new Failure(message)));
        }
    }

    @Override
    public void close() {
        executor.shutdown();
    }

    private static class TftpFileReceiverImpl implements TftpFileReceiver, FutureUtils.Completer<Either<Failure, Success>> {

        private final String host;
        private final int port;
        private final String uploadFileName;
        private final Supplier<Optional<SshScriptingService.Failure>> uploadTrigger;

        private volatile CompletableFuture<Either<Failure, Success>> future;

        public TftpFileReceiverImpl(
                String host,
                int port,
                String uploadFileName,
                Supplier<Optional<SshScriptingService.Failure>> uploadTrigger
        ) {
            this.host = host;
            this.port = port;
            this.uploadFileName = Objects.requireNonNull(uploadFileName);
            this.uploadTrigger = uploadTrigger;
        }

        private void fail(String msg, Optional<String> stdout, Optional<String> stderr) {
            LOG.error(msg);
            future.complete(Either.left(new Failure(msg, stdout, stderr)));
        }

        @Override
        public void completeNowOrLater(CompletableFuture<Either<Failure, Success>> future) {
            // store the future in order to complete it when a matching file is received
            this.future = future;
            // trigger the upload
            // -> if triggering the upload failed then complete the future with that failure
            try {
                uploadTrigger
                        .get()
                        .stream()
                        .forEach(failure -> fail(scriptingFailureMsg(host, port, failure.message), failure.stdout, failure.stderr));
            } catch (Throwable e) {
                var msg = scriptingFailureMsg(host, port, e.getMessage());
                LOG.error(msg, e);
                fail(msg, Optional.empty(), Optional.empty());
            }
        }

        public void onTimeout(CompletableFuture<Either<Failure, Success>> future) {
            this.future = future;
            fail(timeoutFailureMsg(host, port), Optional.empty(), Optional.empty());
        }

        @Override
        public void onFileReceived(InetAddress address, String fileName, byte[] content) {
            if (uploadFileName.equals(fileName)) {
                // it is unlikely, that the file receiver receives a file (with matching filename!) before the file
                // upload was triggered
                // -> just to be sure check that the future is set
                if (future != null) {
                    LOG.debug("received config - host: " + host + "; port: " + port + "; address: " + address.getHostAddress());
                    future.complete(Either.right(new Success(content, fileName)));
                }
            }
        }
    }

    static String scriptingFailureMsg(String host, int port, String msg) {
        return "could not trigger device config upload - host: " + host + "; port: " + port + "; msg: " + msg;
    }
    static String timeoutFailureMsg(String host, int port) {
        return "device config was not received in time. host: " + host + "; port: " + port;
    }

    static String configTypeToUploadFileName(String configType) {
        return configType + "." + uploadCounter.incrementAndGet();
    }

    static String uploadFileNameToConfigType(String filename) {
        return filename.substring(0, filename.lastIndexOf('.'));
    }

    private static AtomicLong uploadCounter = new AtomicLong();
}
