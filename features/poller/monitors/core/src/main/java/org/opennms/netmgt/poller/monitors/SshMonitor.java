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

import java.net.InetAddress;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.opennms.core.utils.ParameterMap;
import org.opennms.core.utils.TimeoutTracker;
import org.opennms.netmgt.poller.InsufficientParametersException;
import org.opennms.netmgt.poller.MonitoredService;
import org.opennms.netmgt.poller.PollStatus;
import org.opennms.netmgt.poller.monitors.support.Ssh;
import org.opennms.netmgt.poller.support.AbstractServiceMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is designed to be used by the service poller framework to test
 * the availability of SSH remote interfaces. The class implements the
 * ServiceMonitor interface that allows it to be used along with other
 * plug-ins by the service poller framework.
 *
 * @author <a href="mailto:ranger@opennms.org">Benjamin Reed</a>
 * @author <a href="http://www.opennms.org/">OpenNMS</a>
 */
public final class SshMonitor extends AbstractServiceMonitor {

    private static final Logger LOG = LoggerFactory.getLogger(SshMonitor.class);

    private static final int DEFAULT_RETRY = 0;
    /**
     * Constant <code>DEFAULT_TIMEOUT=3000</code>
     */
    public static final int DEFAULT_TIMEOUT = 3000;

    /**
     * Constant <code>DEFAULT_PORT=22</code>
     */
    public static final int DEFAULT_PORT = 22;

    /**
     * {@inheritDoc}
     *
     * Poll an {@link InetAddress} for SSH availability.
     *
     * During the poll an attempt is made to connect on the specified port. If
     * the connection request is successful, the banner line generated by the
     * interface is parsed and if the banner text indicates that we are
     * talking
     * to Provided that the interface's response is valid we mark the poll
     * status
     * as available and return.
     * <p>
     * @param address
     * @param parameters
     * @return
     */
    public PollStatus poll(final InetAddress address, final Map<String, Object> parameters) {

        TimeoutTracker tracker = new TimeoutTracker(parameters, DEFAULT_RETRY, DEFAULT_TIMEOUT);

        int port = ParameterMap.getKeyedInteger(parameters, "port", DEFAULT_PORT);
        String banner = ParameterMap.getKeyedString(parameters, "banner", null);
        String match = ParameterMap.getKeyedString(parameters, "match", null);
        String clientBanner = ParameterMap.getKeyedString(parameters, "client-banner", Ssh.DEFAULT_CLIENT_BANNER);
        PollStatus ps = PollStatus.unavailable();

        Ssh ssh = new Ssh(address, port, tracker.getConnectionTimeout());
        ssh.setClientBanner(clientBanner);

        Pattern regex = null;
        try {
            if (match == null && (banner == null || banner.equals("*"))) {
                regex = null;
            } else if (match != null) {
                regex = Pattern.compile(match);
                LOG.debug("match: /{}/", match);
            } else if (banner != null) {
                regex = Pattern.compile(banner);
                LOG.debug("banner: /{}/", banner);
            }
        } catch (final PatternSyntaxException e) {
            final String matchString = match == null ? banner : match;
            LOG.info("Invalid regular expression for SSH banner match /{}/: {}", matchString, e.getMessage());
            return ps;
        }

        for (tracker.reset(); tracker.shouldRetry() && !ps.isAvailable(); tracker.nextAttempt()) {
            try {
                ps = ssh.poll(tracker);
            } catch (final InsufficientParametersException e) {
                LOG.error("An error occurred polling host '{}'", address, e);
                break;
            }

            if (!ps.isAvailable()) {
                // not able to connect, retry
                continue;
            }

            // If banner matching string is null or wildcard ("*") then we
            // only need to test connectivity and we've got that!
            if (regex == null) {
                return ps;
            } else {
                String response = ssh.getServerBanner();

                if (response == null) {
                    return PollStatus.unavailable("server closed connection before banner was received.");
                }

                if (regex.matcher(response).find()) {
                    LOG.debug("isServer: matching response={}", response);
                    return ps;
                } else {
                    // Got a response but it didn't match... no need to attempt
                    // retries
                    LOG.debug("isServer: NON-matching response={}", response);
                    return PollStatus.unavailable("server responded, but banner did not match '" + banner + "'");
                }
            }
        }
        return ps;
    }

    /**
     * {@inheritDoc}
     *
     * Poll the specified address for service availability.
     * <p>
     * @param svc
     * @param parameters
     * @return
     *         <p>
     * @see #poll(InetAddress, Map)
     */
    @Override
    public PollStatus poll(MonitoredService svc, Map<String, Object> parameters) {
        return poll(svc.getAddress(), parameters);
    }

}
