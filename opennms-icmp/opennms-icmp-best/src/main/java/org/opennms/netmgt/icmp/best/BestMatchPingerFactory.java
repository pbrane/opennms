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
package org.opennms.netmgt.icmp.best;

import java.net.InetAddress;
import java.util.Arrays;

import static org.opennms.core.utils.InetAddressUtils.addr;

import org.opennms.core.utils.InetAddressUtils;
import org.opennms.netmgt.icmp.AbstractPingerFactory;
import org.opennms.netmgt.icmp.NullPinger;
import org.opennms.netmgt.icmp.Pinger;
import org.opennms.netmgt.icmp.jna.JnaPinger;
import org.opennms.netmgt.icmp.jni.JniPinger;
import org.opennms.netmgt.icmp.jni6.Jni6Pinger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BestMatchPingerFactory extends AbstractPingerFactory {
    private static Logger LOG = LoggerFactory.getLogger(BestMatchPingerFactory.class);
    private static InetAddress LOOPBACK = InetAddressUtils.getLocalLoopbackAddress().orElse(addr("127.0.0.1"));

    Class<? extends Pinger> m_pingerClass = null;

    @Override
    public Class<? extends Pinger> getPingerClass() {
        initialize();
        return m_pingerClass;
    }

    private static PingerMatch tryPinger(final Class<? extends Pinger> pingerClass) {
        boolean v4 = false;
        boolean v6 = false;

        final Pinger pinger;
        try {
            pinger = pingerClass.newInstance();
        } catch (final Throwable t) {
            LOG.info("Failed to get instance of {}: {}", pingerClass, t.getMessage());
            LOG.trace("Failed to get instance of {}.", pingerClass, t);
            return PingerMatch.NONE;
        }

        try {
            if (pinger.isV4Available()) {
                pinger.initialize4();
                v4 = true;
            }
        } catch (final Throwable t) {
            LOG.info("Failed to initialize {} for IPv4: ", pingerClass, t.getMessage());
            LOG.trace("Failed to initialize {} for IPv4.", pingerClass, t);
        }

        try {
            if (pinger.isV6Available()) {
                pinger.initialize6();
                v6 = true;
            }
        } catch (final Throwable t) {
            LOG.info("Failed to initialize {} for IPv4: {}", pingerClass, t.getMessage());
            LOG.trace("Failed to initialize {} for IPv4.", pingerClass, t);
        }

        final long timeout = Long.valueOf(System.getProperty("org.opennms.netmgt.icmp.best.timeout", "500"), 10);

        // try the found loopback, fall back to 127.0.0.1 or ::1 as a last resort
        for (final InetAddress tryme : new InetAddress[] { LOOPBACK, addr("127.0.0.1"), addr("::1") }) {
            try {
                final Number result = pinger.ping(tryme, timeout, 0);
                if (result == null) {
                    throw new IllegalStateException("No result pinging localhost.");
                }

                // as long as we have v4 and/or v6, return success if pinger.ping() passes
                if (v4 && v6) {
                    return PingerMatch.IPv46;
                } else if (v6) {
                    return PingerMatch.IPv6;
                } else if (v4) {
                    return PingerMatch.IPv4;
                }
            } catch (final Throwable t) {
                LOG.info("Found pinger {}, but it was unable to ping localhost: {}", pingerClass, t.getMessage());
                LOG.trace("Pinger failure:", t);
            }
        }

        // if none of the loopback addresses works, give up
        return PingerMatch.NONE;
    }

    static Class<? extends Pinger> findPinger() {
        final String pingerClassStr = System.getProperty("org.opennms.netmgt.icmp.pingerClass");
        if (pingerClassStr != null) {
            try {
                final Class<? extends Pinger> pingerClass = Class.forName(pingerClassStr).asSubclass(Pinger.class);
                LOG.warn("Not scanning for best pinger because explicit pinger class has been set: {}", pingerClassStr);
                return pingerClass;
            } catch (final Throwable t) {
                LOG.error("org.opennms.netmgt.icmp.pingerClass is set ({}), but it failed to initialize! Erroring out.", pingerClassStr, t);
                throw new IllegalStateException("Unable to initialize pinger class set in org.opennms.netmgt.icmp.pingerClass", t);
            }
        }

        PingerMatch match = PingerMatch.NONE;
        Class<? extends Pinger> pinger = NullPinger.class;

        LOG.info("Searching for best available pinger...");
        for (final Class<? extends Pinger> pingerClass : Arrays.asList(JniPinger.class, Jni6Pinger.class, JnaPinger.class)) {
            final PingerMatch tried = tryPinger(pingerClass);
            if (tried.compareTo(match) > 0) {
                match = tried;
                pinger = pingerClass;
            }
        }

        LOG.info("Best available pinger is: {}", pinger);
        return pinger;
    }

    private void initialize() {
        if (m_pingerClass == null) {
            // If the default (0) DSCP pinger has already been initialized, use the
            // same class in case it's been manually overridden with a setInstance()
            // call (ie, in the Remote Poller)
            final Pinger defaultPinger = m_pingers.getIfPresent(1);
            if (defaultPinger != null) {
                m_pingerClass = defaultPinger.getClass();
            } else {
                m_pingerClass = findPinger();
            }
        }
    }
}
