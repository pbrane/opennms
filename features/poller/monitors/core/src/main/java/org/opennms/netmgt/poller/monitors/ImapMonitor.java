/*
 * Licensed to The OpenNMS Group, Inc (TOG) under one or more
 * contributor license agreements.  See the LICENSE.md file
 * distributed with this work for additional information
 * regarding copyright ownership.
 *
 * TOG licenses this file to You under the GNU Affero General
 * Public License Version 3 (the "License") or (at your option)
 * any later version.  You may not use this file except in
 * compliance with the License.  You may obtain a copy of the
 * License at:
 *
 *      https://www.gnu.org/licenses/agpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.  See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.opennms.netmgt.poller.monitors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.util.Map;

import org.opennms.core.utils.DefaultSocketWrapper;
import org.opennms.core.utils.ParameterMap;
import org.opennms.core.utils.SocketWrapper;
import org.opennms.core.utils.TimeoutTracker;
import org.opennms.netmgt.poller.MonitoredService;
import org.opennms.netmgt.poller.PollStatus;
import org.opennms.netmgt.poller.support.AbstractServiceMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <P>
 * This class is designed to be used by the service poller framework to test the
 * availability of the IMAP service on remote interfaces. The class implements
 * the ServiceMonitor interface that allows it to be used along with other
 * plug-ins by the service poller framework.
 * </P>
 *
 * @author <A HREF="mailto:tarus@opennms.org">Tarus Balog </A>
 * @author <A HREF="mailto:sowmya@opennms.org">Sowmya Nataraj </A>
 * @author <A HREF="mailto:mike@opennms.org">Mike Davidson </A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS </A>
 * @author <A HREF="mailto:tarus@opennms.org">Tarus Balog </A>
 * @author <A HREF="mailto:sowmya@opennms.org">Sowmya Nataraj </A>
 * @author <A HREF="mailto:mike@opennms.org">Mike Davidson </A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS </A>
 * @author <A HREF="mailto:tarus@opennms.org">Tarus Balog </A>
 * @author <A HREF="mailto:sowmya@opennms.org">Sowmya Nataraj </A>
 * @author <A HREF="mailto:mike@opennms.org">Mike Davidson </A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS </A>
 * @author <A HREF="mailto:tarus@opennms.org">Tarus Balog </A>
 * @author <A HREF="mailto:sowmya@opennms.org">Sowmya Nataraj </A>
 * @author <A HREF="mailto:mike@opennms.org">Mike Davidson </A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS </A>
 * @version CVS 1.1.1.1
 */
public class ImapMonitor extends AbstractServiceMonitor {
    private static final Logger LOG = LoggerFactory.getLogger(ImapMonitor.class);

    /**
     * Default IMAP port.
     */
    private static final int DEFAULT_PORT = 143;

    /**
     * Default retries.
     */
    private static final int DEFAULT_RETRY = 0;

    /**
     * Default timeout. Specifies how long (in milliseconds) to block waiting
     * for data from the monitored interface.
     */
    private static final int DEFAULT_TIMEOUT = 3000;

    /**
     * The start of the initial banner received from the server
     */
    private static String IMAP_START_RESPONSE_PREFIX = "* OK ";

    /**
     * The LOGOUT request sent to the server to close the connection
     */
    private static String IMAP_LOGOUT_REQUEST = "ONMSPOLLER LOGOUT\r\n";

    /**
     * The BYE response received from the server in response to the logout
     */
    private static String IMAP_BYE_RESPONSE_PREFIX = "* BYE";

    /**
     * The LOGOUT response received from the server in response to the logout
     */
    private static String IMAP_LOGOUT_RESPONSE_PREFIX = "ONMSPOLLER OK ";

    /**
     * {@inheritDoc}
     *
     * <P>
     * Poll the specified address for IMAP service availability.
     * </P>
     *
     * <P>
     * During the poll an attempt is made to connect on the specified port (by
     * default TCP port 143). If the connection request is successful, the
     * banner line generated by the interface is parsed and if it starts with a '*
     * OK , it indicates that we are talking to an IMAP server and we continue.
     * Next, a 'LOGOUT' command is sent to the interface. Again the response is
     * parsed and the response is verified to see that we get a '* OK'. If the
     * interface's response is valid we set the service status to
     * SERVICE_AVAILABLE and return.
     * </P>
     */
    @Override
    public PollStatus poll(MonitoredService svc, Map<String, Object> parameters) {
        // Process parameters
        //

        TimeoutTracker tracker = new TimeoutTracker(parameters, DEFAULT_RETRY, DEFAULT_TIMEOUT);
        // Retries
        //
	final int port = determinePort(parameters);

        // Get interface address from NetworkInterface
        //
        InetAddress ipAddr = svc.getAddress();


        LOG.debug("ImapMonitor.poll: address: {} port: {} {}", ipAddr, port, tracker);

        PollStatus serviceStatus = PollStatus.unavailable();

        for (tracker.reset(); tracker.shouldRetry() && !serviceStatus.isAvailable(); tracker.nextAttempt()) {
            Socket socket = null;
            try {
                //
                // create a connected socket
                //
                tracker.startAttempt();

                socket = new Socket();
                socket.connect(new InetSocketAddress(ipAddr, port), tracker.getConnectionTimeout());
                socket.setSoTimeout(tracker.getSoTimeout());

                // We're connected, so upgrade status to unresponsive
                serviceStatus = PollStatus.unresponsive();
                
 		// For SSL support
                socket = getSocketWrapper().wrapSocket(socket);

                BufferedReader rdr = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //
                // Tokenize the Banner Line, and check the first
                // line for a valid return.
                //
                String banner = rdr.readLine();
                
                double responseTime = tracker.elapsedTimeInMillis();


                LOG.debug("ImapMonitor.Poll(): banner: {}", banner);

                if (banner != null && banner.startsWith(IMAP_START_RESPONSE_PREFIX)) {
                    //
                    // Send the LOGOUT
                    //
                    socket.getOutputStream().write(IMAP_LOGOUT_REQUEST.getBytes());

                    //
                    // get the returned string, tokenize, and
                    // verify the correct output.
                    //
                    String response = rdr.readLine();
                    if (response != null && response.startsWith(IMAP_BYE_RESPONSE_PREFIX)) {
                        response = rdr.readLine();
                        if (response != null && response.startsWith(IMAP_LOGOUT_RESPONSE_PREFIX)) {
                            serviceStatus = PollStatus.available(responseTime);
                        }
                    }
                }

                // If we get this far and the status has not been set
                // to available, then something didn't verify during
                // the banner checking or logout process.
                if (!serviceStatus.isAvailable()) {
                    serviceStatus = PollStatus.unavailable();
                }

            } catch (NoRouteToHostException e) {
            	
            	String reason = "No route to host exception for address: " + ipAddr;
                LOG.debug(reason, e);
                serviceStatus = PollStatus.unavailable(reason);

            } catch (ConnectException e) {
                // Connection refused. Continue to retry.
            	String reason = "Connection exception for address: " + ipAddr;
                LOG.debug(reason, e);
                serviceStatus = PollStatus.unavailable(reason);
            } catch (InterruptedIOException e) {
            	String reason = "did not connect to host with " + tracker;
                LOG.debug(reason);
                serviceStatus = PollStatus.unavailable(reason);
            } catch (IOException e) {
            	String reason = "IOException while polling address: " + ipAddr;
                LOG.debug(reason, e);
                serviceStatus = PollStatus.unavailable(reason);
            } finally {
                try {
                    // Close the socket
                    if (socket != null)
                        socket.close();
                } catch (IOException e) {
                    e.fillInStackTrace();
                    LOG.debug("ImapMonitor.poll: Error closing socket.", e);
                }
            }
        }

        //
        // return the status of the service
        //
        return serviceStatus;
    }

    protected int determinePort(final Map<String, Object> parameters) {
        return ParameterMap.getKeyedInteger(parameters, "port", DEFAULT_PORT);
    }

    /**
     * <p>wrapSocket</p>
     *
     * @param socket a {@link java.net.Socket} object.
     * @return a {@link java.net.Socket} object.
     * @throws java.io.IOException if any.
     */
    protected SocketWrapper getSocketWrapper() {
        return new DefaultSocketWrapper();
    }


}
