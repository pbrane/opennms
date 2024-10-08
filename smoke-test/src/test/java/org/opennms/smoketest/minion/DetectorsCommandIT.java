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
package org.opennms.smoketest.minion;

import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.hasSize;

import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.opennms.smoketest.junit.MinionTests;
import org.opennms.smoketest.stacks.OpenNMSStack;
import org.opennms.smoketest.utils.CommandTestUtils;
import org.opennms.smoketest.utils.SshClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

/**
 * Verifies the list of available detectors by parsing the output
 * of the 'opennms:list-detectors' command issued in the Karaf shell.
 *
 * We try running this command several times since the feature providing
 * the 'list-detectors' command the feature(s) providing the detectors
 * may take some time to load.
 *
 * @author jwhite
 * @author chandrag
 */
@Category(MinionTests.class)
public class DetectorsCommandIT {

    private static final Logger LOG = LoggerFactory.getLogger(DetectorsCommandIT.class);

    @ClassRule
    public static final OpenNMSStack stack = OpenNMSStack.MINION;

    private ImmutableMap<String, String> expectedDetectors = ImmutableMap.<String, String> builder()
            .put("ActiveMQ", "org.opennms.netmgt.provision.detector.jms.ActiveMQDetector")
            .put("BGP_Session", "org.opennms.netmgt.provision.detector.snmp.BgpSessionDetector")
            .put("BSF", "org.opennms.netmgt.provision.detector.bsf.BSFDetector")
            .put("CITRIX", "org.opennms.netmgt.provision.detector.simple.CitrixDetector")
            .put("Cisco_IP_SLA", "org.opennms.netmgt.provision.detector.snmp.CiscoIpSlaDetector")
            .put("DHCP", "org.opennms.netmgt.provision.detector.dhcp.DhcpDetector")
            .put("DNS", "org.opennms.netmgt.provision.detector.datagram.DnsDetector")
            .put("Dell_OpenManageChassis", "org.opennms.netmgt.provision.detector.snmp.OpenManageChassisDetector")
            .put("DiskUsage", "org.opennms.netmgt.provision.detector.snmp.DiskUsageDetector")
            .put("DominoIIOP", "org.opennms.netmgt.provision.detector.simple.DominoIIOPDetector")
            .put("FTP", "org.opennms.netmgt.provision.detector.simple.FtpDetector")
            .put("GP", "org.opennms.netmgt.provision.detector.generic.GpDetector")
            .put("HOST-RESOURCES", "org.opennms.netmgt.provision.detector.snmp.HostResourceSWRunDetector")
            .put("HTTP", "org.opennms.netmgt.provision.detector.simple.HttpDetector")
            .put("HTTPS", "org.opennms.netmgt.provision.detector.simple.HttpsDetector")
            .put("ICMP", "org.opennms.netmgt.provision.detector.icmp.IcmpDetector")
            .put("IMAP", "org.opennms.netmgt.provision.detector.simple.ImapDetector")
            .put("IMAPS", "org.opennms.netmgt.provision.detector.simple.ImapsDetector")
            .put("JDBC", "org.opennms.netmgt.provision.detector.jdbc.JdbcDetector")
            .put("JSR160", "org.opennms.netmgt.provision.detector.jmx.Jsr160Detector")
            .put("JdbcQueryDetector", "org.opennms.netmgt.provision.detector.jdbc.JdbcQueryDetector")
            .put("JdbcStoredProcedureDetector",
                    "org.opennms.netmgt.provision.detector.jdbc.JdbcStoredProcedureDetector")
            .put("LDAP", "org.opennms.netmgt.provision.detector.simple.LdapDetector")
            .put("LDAPS", "org.opennms.netmgt.provision.detector.simple.LdapsDetector")
            .put("LOOP", "org.opennms.netmgt.provision.detector.loop.LoopDetector")
            .put("MSExchange", "org.opennms.netmgt.provision.detector.msexchange.MSExchangeDetector")
            .put("Memcached", "org.opennms.netmgt.provision.detector.simple.MemcachedDetector")
            .put("NOTES", "org.opennms.netmgt.provision.detector.simple.NotesHttpDetector")
            .put("NRPE", "org.opennms.netmgt.provision.detector.simple.NrpeDetector")
            .put("NTP", "org.opennms.netmgt.provision.detector.datagram.NtpDetector")
            .put("OMSAStorage", "org.opennms.netmgt.provision.detector.snmp.OmsaStorageDetector")
            .put("PERC", "org.opennms.netmgt.provision.detector.snmp.PercDetector")
            .put("POP3", "org.opennms.netmgt.provision.detector.simple.Pop3Detector")
            .put("SMB", "org.opennms.netmgt.provision.detector.smb.SmbDetector")
            .put("SMTP", "org.opennms.netmgt.provision.detector.simple.SmtpDetector")
            .put("SNMP", "org.opennms.netmgt.provision.detector.snmp.SnmpDetector")
            .put("SSH", "org.opennms.netmgt.provision.detector.ssh.SshDetector")
            .put("TCP", "org.opennms.netmgt.provision.detector.simple.TcpDetector")
            .put("TrivialTime", "org.opennms.netmgt.provision.detector.simple.TrivialTimeDetector")
            .put("WEB", "org.opennms.netmgt.provision.detector.web.WebDetector")
            .put("WMI", "org.opennms.netmgt.provision.detector.wmi.WmiDetector")
            .put("WS-Man", "org.opennms.netmgt.provision.detector.wsman.WsManDetector")
            .put("WSManWQL", "org.opennms.netmgt.provision.detector.wsman.WsManWQLDetector")
            .put("Win32Service", "org.opennms.netmgt.provision.detector.snmp.Win32ServiceDetector").build();

    @Test
    public void canLoadDetectorsOnMinion() throws Exception {
        final InetSocketAddress sshAddr = stack.minion().getSshAddress();
        await().atMost(3, MINUTES).pollInterval(15, SECONDS).pollDelay(0, SECONDS)
            .until(() -> listAndVerifyDetectors("Minion", sshAddr), hasSize(0));
    }

    @Test
    public void canLoadDetectorsOnOpenNMS() throws Exception {
        final InetSocketAddress sshAddr = stack.opennms().getSshAddress();
        await().atMost(3, MINUTES).pollInterval(15, SECONDS).pollDelay(0, SECONDS)
            .until(() -> listAndVerifyDetectors("OpenNMS", sshAddr), hasSize(0));
    }

    public List<String> listAndVerifyDetectors(String host, InetSocketAddress sshAddr) throws Exception {
        List<String> unmatchedDetectors = new ArrayList<>();
        try (final SshClient sshClient = new SshClient(sshAddr, "admin", "admin")) {
            // List the detectors
            PrintStream pipe = sshClient.openShell();
            pipe.println("opennms:list-detectors");
            pipe.println("logout");
            await().atMost(1, MINUTES).until(sshClient.isShellClosedCallable());

            // Parse the output
            String shellOutput = CommandTestUtils.stripAnsiCodes(sshClient.getStdout());

            shellOutput = StringUtils.substringAfter(shellOutput, "opennms:list-detectors");
            LOG.info("Detectors output: {}", shellOutput);
            Map<String, String> detectorMap = new HashMap<String, String>();
            for (String detector : shellOutput.split("\\r?\\n")) {
                if (StringUtils.isNotBlank(detector)) {
                    String detectorSet[] = detector.split(":");
                    if (detectorSet.length >= 2) {
                        detectorMap.put(detectorSet[0], detectorSet[1]);
                    }
                }
            }
            LOG.info("Found detectors: {}",  detectorMap);

            // Verify
            for (String detectorName : expectedDetectors.keySet()) {
                if (!detectorMap.containsKey(detectorName)) {
                    unmatchedDetectors.add(detectorName);
                }
            }
        }
        return unmatchedDetectors;
    }

}
