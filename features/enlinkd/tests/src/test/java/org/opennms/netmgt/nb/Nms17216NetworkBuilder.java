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
package org.opennms.netmgt.nb;


import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.opennms.core.utils.InetAddressUtils;
import org.opennms.netmgt.model.OnmsNode;

/**
 * @author <a href="mailto:brozow@opennms.org">Mathew Brozowski</a>
 * @author <a href="mailto:antonio@opennms.it">Antonio Russo</a>
 * @author <a href="mailto:alejandro@opennms.org">Alejandro Galue</a>
 */

public class    Nms17216NetworkBuilder extends NmsNetworkBuilder {
    public static final String ROUTER1_IP = "192.168.100.245";
    public static final String ROUTER1_NAME = "Router1";
    public static final String ROUTER1_SYSOID = ".1.3.6.1.4.1.9.1.576";

    public static final Map<InetAddress,Integer> ROUTER1_IP_IF_MAP =  new HashMap<>();
    public static final Map<InetAddress,InetAddress> ROUTER1_IP_MK_MAP =  new HashMap<>();
    public static final Map<Integer,String> ROUTER1_IF_IFNAME_MAP = new HashMap<>();
    public static final Map<Integer,String> ROUTER1_IF_IFDESCR_MAP = new HashMap<>();
    public static final Map<Integer,String> ROUTER1_IF_MAC_MAP = new HashMap<>();
    public static final Map<Integer,String> ROUTER1_IF_IFALIAS_MAP = new HashMap<>();

    public static final String ROUTER2_IP = "192.168.100.241";
    public static final String ROUTER2_NAME = "Router2";
    public static final String ROUTER2_SYSOID = ".1.3.6.1.4.1.9.1.1045";

    public static final Map<InetAddress,Integer> ROUTER2_IP_IF_MAP =  new HashMap<>();
    public static final Map<InetAddress,InetAddress> ROUTER2_IP_MK_MAP =  new HashMap<>();
    public static final Map<Integer,String> ROUTER2_IF_IFNAME_MAP = new HashMap<>();
    public static final Map<Integer,String> ROUTER2_IF_IFDESCR_MAP = new HashMap<>();
    public static final Map<Integer,String> ROUTER2_IF_MAC_MAP = new HashMap<>();
    public static final Map<Integer,String> ROUTER2_IF_IFALIAS_MAP = new HashMap<>();

    public static final String ROUTER3_IP = "172.16.50.1";
    public static final String ROUTER3_NAME = "Router3";
    public static final String ROUTER3_SYSOID = ".1.3.6.1.4.1.9.1.1045";

    public static final Map<InetAddress,Integer> ROUTER3_IP_IF_MAP =  new HashMap<>();
    public static final Map<InetAddress,InetAddress> ROUTER3_IP_MK_MAP =  new HashMap<>();
    public static final Map<Integer,String> ROUTER3_IF_IFNAME_MAP = new HashMap<>();
    public static final Map<Integer,String> ROUTER3_IF_IFDESCR_MAP = new HashMap<>();
    public static final Map<Integer,String> ROUTER3_IF_MAC_MAP = new HashMap<>();
    public static final Map<Integer,String> ROUTER3_IF_IFALIAS_MAP = new HashMap<>();

    public static final String ROUTER4_IP = "10.10.10.1";
    public static final String ROUTER4_NAME = "Router4";
    public static final String ROUTER4_SYSOID = ".1.3.6.1.4.1.9.1.1045";

    public static final Map<InetAddress,Integer> ROUTER4_IP_IF_MAP =  new HashMap<>();
    public static final Map<InetAddress,InetAddress> ROUTER4_IP_MK_MAP =  new HashMap<>();
    public static final Map<Integer,String> ROUTER4_IF_IFNAME_MAP = new HashMap<>();
    public static final Map<Integer,String> ROUTER4_IF_IFDESCR_MAP = new HashMap<>();
    public static final Map<Integer,String> ROUTER4_IF_MAC_MAP = new HashMap<>();
    public static final Map<Integer,String> ROUTER4_IF_IFALIAS_MAP = new HashMap<>();

    public static final String SWITCH1_IP = "172.16.10.1";
    public static final String SWITCH1_NAME = "Switch1";
    public static final String SWITCH1_SYSOID = ".1.3.6.1.4.1.9.1.614";
    public static final String SWITCH1_LLDP_CHASSISID = "0016c8bd4d80";

    public static final Map<InetAddress,InetAddress> SWITCH1_IP_MK_MAP =  new HashMap<>();
    public static final Map<InetAddress,Integer> SWITCH1_IP_IF_MAP =  new HashMap<>();
    public static final Map<Integer,String> SWITCH1_IF_IFNAME_MAP = new HashMap<>();
    public static final Map<Integer,String> SWITCH1_IF_IFDESCR_MAP = new HashMap<>();
    public static final Map<Integer,String> SWITCH1_IF_MAC_MAP = new HashMap<>();
    public static final Map<Integer,String> SWITCH1_IF_IFALIAS_MAP = new HashMap<>();

    public static final String SWITCH2_IP = "172.16.10.2";
    public static final String SWITCH2_NAME = "Switch2";
    public static final String SWITCH2_SYSOID = ".1.3.6.1.4.1.9.1.696";
    public static final String SWITCH2_LLDP_CHASSISID = "0016c894aa80";

    public static final Map<InetAddress,InetAddress> SWITCH2_IP_MK_MAP =  new HashMap<>();
    public static final Map<InetAddress,Integer> SWITCH2_IP_IF_MAP =  new HashMap<>();
    public static final Map<Integer,String> SWITCH2_IF_IFNAME_MAP = new HashMap<>();
    public static final Map<Integer,String> SWITCH2_IF_IFDESCR_MAP = new HashMap<>();
    public static final Map<Integer,String> SWITCH2_IF_MAC_MAP = new HashMap<>();
    public static final Map<Integer,String> SWITCH2_IF_IFALIAS_MAP = new HashMap<>();

    public static final String SWITCH3_IP = "172.16.10.3";
    public static final String SWITCH3_NAME = "Switch3";
    public static final String SWITCH3_SYSOID = ".1.3.6.1.4.1.9.1.716";
    public static final String SWITCH3_LLDP_CHASSISID = "f4ea67ebdc00";

    public static final Map<InetAddress,InetAddress> SWITCH3_IP_MK_MAP =  new HashMap<>();
    public static final Map<InetAddress,Integer> SWITCH3_IP_IF_MAP =  new HashMap<>();
    public static final Map<Integer,String> SWITCH3_IF_IFNAME_MAP = new HashMap<>();
    public static final Map<Integer,String> SWITCH3_IF_IFDESCR_MAP = new HashMap<>();
    public static final Map<Integer,String> SWITCH3_IF_MAC_MAP = new HashMap<>();
    public static final Map<Integer,String> SWITCH3_IF_IFALIAS_MAP = new HashMap<>();

    public static final String SWITCH4_IP = "172.16.50.2";
    public static final String SWITCH4_NAME = "Switch4";
    public static final String SWITCH4_SYSOID = ".1.3.6.1.4.1.9.1.716";
    public static final String SWITCH4_LLDP_CHASSISID = "a4187504e400";

    public static final Map<InetAddress,InetAddress> SWITCH4_IP_MK_MAP =  new HashMap<>();
    public static final Map<InetAddress,Integer> SWITCH4_IP_IF_MAP =  new HashMap<>();
    public static final Map<Integer,String> SWITCH4_IF_IFNAME_MAP = new HashMap<>();
    public static final Map<Integer,String> SWITCH4_IF_IFDESCR_MAP = new HashMap<>();
    public static final Map<Integer,String> SWITCH4_IF_MAC_MAP = new HashMap<>();
    public static final Map<Integer,String> SWITCH4_IF_IFALIAS_MAP = new HashMap<>();

    public static final String SWITCH5_IP = "172.16.10.4";
    public static final String SWITCH5_NAME = "Switch5";
    public static final String SWITCH5_SYSOID = ".1.3.6.1.4.1.9.1.716";
    public static final String SWITCH5_LLDP_CHASSISID = "f4ea67f82980";

    public static final Map<InetAddress,Integer> SWITCH5_IP_IF_MAP =  new HashMap<>();
    public static final Map<InetAddress,InetAddress> SWITCH5_IP_MK_MAP =  new HashMap<>();
    public static final Map<Integer,String> SWITCH5_IF_IFNAME_MAP = new HashMap<>();
    public static final Map<Integer,String> SWITCH5_IF_IFDESCR_MAP = new HashMap<>();
    public static final Map<Integer,String> SWITCH5_IF_MAC_MAP = new HashMap<>();
    public static final Map<Integer,String> SWITCH5_IF_IFALIAS_MAP = new HashMap<>();

    public static final String ROUTER1_SNMP_RESOURCE = "classpath:/linkd/nms17216/router1-walk.txt";
    public static final String ROUTER2_SNMP_RESOURCE = "classpath:/linkd/nms17216/router2-walk.txt";
    public static final String ROUTER3_SNMP_RESOURCE = "classpath:/linkd/nms17216/router3-walk.txt";
    public static final String ROUTER4_SNMP_RESOURCE = "classpath:/linkd/nms17216/router4-walk.txt";

    public static final String SWITCH1_SNMP_RESOURCE = "classpath:/linkd/nms17216/switch1-walk.txt";
    public static final String SWITCH2_SNMP_RESOURCE = "classpath:/linkd/nms17216/switch2-walk.txt";
    public static final String SWITCH3_SNMP_RESOURCE = "classpath:/linkd/nms17216/switch3-walk.txt";
    public static final String SWITCH4_SNMP_RESOURCE = "classpath:/linkd/nms17216/switch4-walk.txt";
    public static final String SWITCH5_SNMP_RESOURCE = "classpath:/linkd/nms17216/switch5-walk.txt";


    static {
        ROUTER1_IP_IF_MAP.put(InetAddressUtils.addr("192.168.100.249"), 13);
        ROUTER1_IP_MK_MAP.put(InetAddressUtils.addr("192.168.100.249"), InetAddressUtils.addr("255.255.255.252"));
        ROUTER1_IP_IF_MAP.put(InetAddressUtils.addr("192.168.100.245"), 7);
        ROUTER1_IP_MK_MAP.put(InetAddressUtils.addr("192.168.100.245"), InetAddressUtils.addr("255.255.255.252"));
        ROUTER1_IF_IFNAME_MAP.put(17, "Vl1");
        ROUTER1_IF_IFDESCR_MAP.put(17, "Vlan1");
        ROUTER1_IF_MAC_MAP.put(17, "00170e4e60e0");
        ROUTER1_IF_IFNAME_MAP.put(16, "Nu0");
        ROUTER1_IF_IFDESCR_MAP.put(16, "Null0");
        ROUTER1_IF_IFNAME_MAP.put(11, "Fa0/1/2");
        ROUTER1_IF_IFDESCR_MAP.put(11, "FastEthernet0/1/2");
        ROUTER1_IF_MAC_MAP.put(11, "0014a9f8fd66");
        ROUTER1_IF_IFNAME_MAP.put(7, "Fa0/0");
        ROUTER1_IF_IFDESCR_MAP.put(7, "FastEthernet0/0");
        ROUTER1_IF_MAC_MAP.put(7, "00170e4e60e0");
        ROUTER1_IF_IFNAME_MAP.put(4, "BR1/0/1");
        ROUTER1_IF_IFDESCR_MAP.put(4, "BRI1/0/1");
        ROUTER1_IF_IFNAME_MAP.put(28, "BR1/0/1");
        ROUTER1_IF_IFDESCR_MAP.put(28, "BRI1/0/1-Physical");
        ROUTER1_IF_IFNAME_MAP.put(10, "Fa0/1/1");
        ROUTER1_IF_IFDESCR_MAP.put(10, "FastEthernet0/1/1");
        ROUTER1_IF_MAC_MAP.put(10, "0014a9f8fd65");
        ROUTER1_IF_IFNAME_MAP.put(12, "Fa0/1/3");
        ROUTER1_IF_IFDESCR_MAP.put(12, "FastEthernet0/1/3");
        ROUTER1_IF_MAC_MAP.put(12, "0014a9f8fd67");
        ROUTER1_IF_IFNAME_MAP.put(25, "BR1/0/0");
        ROUTER1_IF_IFDESCR_MAP.put(25, "BRI1/0/0-Signaling");
        ROUTER1_IF_IFNAME_MAP.put(13, "Se0/0/0");
        ROUTER1_IF_IFDESCR_MAP.put(13, "Serial0/0/0");
        ROUTER1_IF_IFNAME_MAP.put(15, "Vo0");
        ROUTER1_IF_IFDESCR_MAP.put(15, "VoIP-Null0");
        ROUTER1_IF_IFNAME_MAP.put(20, "Foreign Exchange Station 0/3/0");
        ROUTER1_IF_IFDESCR_MAP.put(20, "Foreign Exchange Station 0/3/0");
        ROUTER1_IF_IFNAME_MAP.put(23, "Foreign Exchange Station 1/1/1");
        ROUTER1_IF_IFDESCR_MAP.put(23, "Foreign Exchange Station 1/1/1");
        ROUTER1_IF_IFNAME_MAP.put(21, "Foreign Exchange Station 0/3/1");
        ROUTER1_IF_IFDESCR_MAP.put(21, "Foreign Exchange Station 0/3/1");
        ROUTER1_IF_IFNAME_MAP.put(14, "Se0/0/1");
        ROUTER1_IF_IFDESCR_MAP.put(14, "Serial0/0/1");
        ROUTER1_IF_IFNAME_MAP.put(31, "BR1/0/1:2");
        ROUTER1_IF_IFDESCR_MAP.put(31, "BRI1/0/1:2-Bearer Channel");
        ROUTER1_IF_IFNAME_MAP.put(9, "Fa0/1/0");
        ROUTER1_IF_IFDESCR_MAP.put(9, "FastEthernet0/1/0");
        ROUTER1_IF_MAC_MAP.put(9, "0014a9f8fd64");
        ROUTER1_IF_IFNAME_MAP.put(1, "BR1/0/0");
        ROUTER1_IF_IFDESCR_MAP.put(1, "BRI1/0/0");
        ROUTER1_IF_IFNAME_MAP.put(22, "Foreign Exchange Station 1/1/0");
        ROUTER1_IF_IFDESCR_MAP.put(22, "Foreign Exchange Station 1/1/0");
        ROUTER1_IF_IFNAME_MAP.put(24, "BR1/0/0");
        ROUTER1_IF_IFDESCR_MAP.put(24, "BRI1/0/0-Physical");
        ROUTER1_IF_IFNAME_MAP.put(30, "BR1/0/1:1");
        ROUTER1_IF_IFDESCR_MAP.put(30, "BRI1/0/1:1-Bearer Channel");
        ROUTER1_IF_IFNAME_MAP.put(29, "BR1/0/1");
        ROUTER1_IF_IFDESCR_MAP.put(29, "BRI1/0/1-Signaling");
        ROUTER1_IF_IFNAME_MAP.put(27, "BR1/0/0:2");
        ROUTER1_IF_IFDESCR_MAP.put(27, "BRI1/0/0:2-Bearer Channel");
        ROUTER1_IF_IFNAME_MAP.put(26, "BR1/0/0:1");
        ROUTER1_IF_IFDESCR_MAP.put(26, "BRI1/0/0:1-Bearer Channel");
        ROUTER1_IF_IFNAME_MAP.put(8, "Fa0/1");
        ROUTER1_IF_IFDESCR_MAP.put(8, "FastEthernet0/1");
        ROUTER1_IF_MAC_MAP.put(8, "00170e4e60e1");

        ROUTER2_IP_IF_MAP.put(InetAddressUtils.addr("192.168.100.241"), 13);
        ROUTER2_IP_MK_MAP.put(InetAddressUtils.addr("192.168.100.241"), InetAddressUtils.addr("255.255.255.252"));
        ROUTER2_IP_IF_MAP.put(InetAddressUtils.addr("192.168.100.250"), 12);
        ROUTER2_IP_MK_MAP.put(InetAddressUtils.addr("192.168.100.250"), InetAddressUtils.addr("255.255.255.252"));
        ROUTER2_IF_IFNAME_MAP.put(25, "BR0/2/0:2");
        ROUTER2_IF_IFDESCR_MAP.put(25, "BRI0/2/0:2-Bearer Channel");
        ROUTER2_IF_IFNAME_MAP.put(27, "BR0/2/1");
        ROUTER2_IF_IFDESCR_MAP.put(27, "BRI0/2/1-Signaling");
        ROUTER2_IF_IFNAME_MAP.put(12, "Se0/0/0");
        ROUTER2_IF_IFDESCR_MAP.put(12, "Serial0/0/0");
        ROUTER2_IF_IFNAME_MAP.put(17, "Nu0");
        ROUTER2_IF_IFDESCR_MAP.put(17, "Null0");
        ROUTER2_IF_IFNAME_MAP.put(4, "BR0/2/1");
        ROUTER2_IF_IFDESCR_MAP.put(4, "BRI0/2/1");
        ROUTER2_IF_IFNAME_MAP.put(28, "BR0/2/1:1");
        ROUTER2_IF_IFDESCR_MAP.put(28, "BRI0/2/1:1-Bearer Channel");
        ROUTER2_IF_IFNAME_MAP.put(16, "Vo0");
        ROUTER2_IF_IFDESCR_MAP.put(16, "VoIP-Null0");
        ROUTER2_IF_IFNAME_MAP.put(26, "BR0/2/1");
        ROUTER2_IF_IFDESCR_MAP.put(26, "BRI0/2/1-Physical");
        ROUTER2_IF_IFNAME_MAP.put(13, "Se0/0/1");
        ROUTER2_IF_IFDESCR_MAP.put(13, "Serial0/0/1");
        ROUTER2_IF_IFNAME_MAP.put(8, "Gi0/0");
        ROUTER2_IF_IFDESCR_MAP.put(8, "GigabitEthernet0/0");
        ROUTER2_IF_MAC_MAP.put(8, "5057a8f5b8d0");
        ROUTER2_IF_IFNAME_MAP.put(22, "BR0/2/0");
        ROUTER2_IF_IFDESCR_MAP.put(22, "BRI0/2/0-Physical");
        ROUTER2_IF_IFNAME_MAP.put(24, "BR0/2/0:1");
        ROUTER2_IF_IFDESCR_MAP.put(24, "BRI0/2/0:1-Bearer Channel");
        ROUTER2_IF_IFNAME_MAP.put(23, "BR0/2/0");
        ROUTER2_IF_IFDESCR_MAP.put(23, "BRI0/2/0-Signaling");
        ROUTER2_IF_IFNAME_MAP.put(10, "Gi0/2");
        ROUTER2_IF_IFDESCR_MAP.put(10, "GigabitEthernet0/2");
        ROUTER2_IF_MAC_MAP.put(10, "5057a8f5b8d2");
        ROUTER2_IF_IFNAME_MAP.put(14, "Se0/1/0");
        ROUTER2_IF_IFDESCR_MAP.put(14, "Serial0/1/0");
        ROUTER2_IF_IFNAME_MAP.put(11, "Gi0/3");
        ROUTER2_IF_IFDESCR_MAP.put(11, "GigabitEthernet0/3");
        ROUTER2_IF_MAC_MAP.put(11, "5057a8f5b8d3");
        ROUTER2_IF_IFNAME_MAP.put(15, "Se0/1/1");
        ROUTER2_IF_IFDESCR_MAP.put(15, "Serial0/1/1");
        ROUTER2_IF_IFNAME_MAP.put(9, "Gi0/1");
        ROUTER2_IF_IFDESCR_MAP.put(9, "GigabitEthernet0/1");
        ROUTER2_IF_MAC_MAP.put(9, "5057a8f5b8d1");
        ROUTER2_IF_IFNAME_MAP.put(7, "Em0/0");
        ROUTER2_IF_IFDESCR_MAP.put(7, "Embedded-Service-Engine0/0");
        ROUTER2_IF_MAC_MAP.put(7, "000000000000");
        ROUTER2_IF_IFNAME_MAP.put(1, "BR0/2/0");
        ROUTER2_IF_IFDESCR_MAP.put(1, "BRI0/2/0");
        ROUTER2_IF_IFNAME_MAP.put(29, "BR0/2/1:2");
        ROUTER2_IF_IFDESCR_MAP.put(29, "BRI0/2/1:2-Bearer Channel");
        ROUTER3_IP_IF_MAP.put(InetAddressUtils.addr("192.168.100.1"), 8);
        ROUTER3_IP_MK_MAP.put(InetAddressUtils.addr("192.168.100.1"), InetAddressUtils.addr("255.255.255.192"));
        ROUTER3_IP_IF_MAP.put(InetAddressUtils.addr("192.168.100.242"), 13);
        ROUTER3_IP_MK_MAP.put(InetAddressUtils.addr("192.168.100.242"), InetAddressUtils.addr("255.255.255.252"));
        ROUTER3_IP_IF_MAP.put(InetAddressUtils.addr("172.16.50.1"), 9);
        ROUTER3_IP_MK_MAP.put(InetAddressUtils.addr("172.16.50.1"), InetAddressUtils.addr("255.255.255.0"));
        ROUTER3_IF_IFNAME_MAP.put(17, "Nu0");
        ROUTER3_IF_IFDESCR_MAP.put(17, "Null0");
        ROUTER3_IF_IFNAME_MAP.put(22, "BR0/2/0");
        ROUTER3_IF_IFDESCR_MAP.put(22, "BRI0/2/0-Physical");
        ROUTER3_IF_IFNAME_MAP.put(13, "Se0/0/1");
        ROUTER3_IF_IFDESCR_MAP.put(13, "Serial0/0/1");
        ROUTER3_IF_IFNAME_MAP.put(4, "BR0/2/1");
        ROUTER3_IF_IFDESCR_MAP.put(4, "BRI0/2/1");
        ROUTER3_IF_IFNAME_MAP.put(12, "Se0/0/0");
        ROUTER3_IF_IFDESCR_MAP.put(12, "Serial0/0/0");
        ROUTER3_IF_IFNAME_MAP.put(29, "BR0/2/1:2");
        ROUTER3_IF_IFDESCR_MAP.put(29, "BRI0/2/1:2-Bearer Channel");
        ROUTER3_IF_IFNAME_MAP.put(10, "Gi0/2");
        ROUTER3_IF_IFDESCR_MAP.put(10, "GigabitEthernet0/2");
        ROUTER3_IF_MAC_MAP.put(10, "2c542d36686a");
        ROUTER3_IF_IFNAME_MAP.put(23, "BR0/2/0");
        ROUTER3_IF_IFDESCR_MAP.put(23, "BRI0/2/0-Signaling");
        ROUTER3_IF_IFNAME_MAP.put(28, "BR0/2/1:1");
        ROUTER3_IF_IFDESCR_MAP.put(28, "BRI0/2/1:1-Bearer Channel");
        ROUTER3_IF_IFNAME_MAP.put(25, "BR0/2/0:2");
        ROUTER3_IF_IFDESCR_MAP.put(25, "BRI0/2/0:2-Bearer Channel");
        ROUTER3_IF_IFNAME_MAP.put(9, "Gi0/1");
        ROUTER3_IF_IFDESCR_MAP.put(9, "GigabitEthernet0/1");
        ROUTER3_IF_MAC_MAP.put(9, "2c542d366869");
        ROUTER3_IF_IFNAME_MAP.put(16, "Vo0");
        ROUTER3_IF_IFDESCR_MAP.put(16, "VoIP-Null0");
        ROUTER3_IF_IFNAME_MAP.put(7, "Em0/0");
        ROUTER3_IF_IFDESCR_MAP.put(7, "Embedded-Service-Engine0/0");
        ROUTER3_IF_MAC_MAP.put(7, "000000000000");
        ROUTER3_IF_IFNAME_MAP.put(26, "BR0/2/1");
        ROUTER3_IF_IFDESCR_MAP.put(26, "BRI0/2/1-Physical");
        ROUTER3_IF_IFNAME_MAP.put(11, "Gi0/3");
        ROUTER3_IF_IFDESCR_MAP.put(11, "GigabitEthernet0/3");
        ROUTER3_IF_MAC_MAP.put(11, "2c542d36686b");
        ROUTER3_IF_IFNAME_MAP.put(8, "Gi0/0");
        ROUTER3_IF_IFDESCR_MAP.put(8, "GigabitEthernet0/0");
        ROUTER3_IF_MAC_MAP.put(8, "2c542d366868");
        ROUTER3_IF_IFNAME_MAP.put(27, "BR0/2/1");
        ROUTER3_IF_IFDESCR_MAP.put(27, "BRI0/2/1-Signaling");
        ROUTER3_IF_IFNAME_MAP.put(24, "BR0/2/0:1");
        ROUTER3_IF_IFDESCR_MAP.put(24, "BRI0/2/0:1-Bearer Channel");
        ROUTER3_IF_IFNAME_MAP.put(15, "Se0/1/1");
        ROUTER3_IF_IFDESCR_MAP.put(15, "Serial0/1/1");
        ROUTER3_IF_IFNAME_MAP.put(1, "BR0/2/0");
        ROUTER3_IF_IFDESCR_MAP.put(1, "BRI0/2/0");
        ROUTER3_IF_IFNAME_MAP.put(14, "Se0/1/0");
        ROUTER3_IF_IFDESCR_MAP.put(14, "Serial0/1/0");
        ROUTER4_IP_IF_MAP.put(InetAddressUtils.addr("192.168.100.2"), 3);
        ROUTER4_IP_MK_MAP.put(InetAddressUtils.addr("192.168.100.2"), InetAddressUtils.addr("255.255.255.192"));
        ROUTER4_IP_IF_MAP.put(InetAddressUtils.addr("10.10.10.1"), 16);
        ROUTER3_IP_MK_MAP.put(InetAddressUtils.addr("10.10.10.1"), InetAddressUtils.addr("255.255.255.0"));
        ROUTER4_IF_IFNAME_MAP.put(10, "Vo0");
        ROUTER4_IF_IFDESCR_MAP.put(10, "VoIP-Null0");
        ROUTER4_IF_IFNAME_MAP.put(3, "Gi0/1");
        ROUTER4_IF_IFDESCR_MAP.put(3, "GigabitEthernet0/1");
        ROUTER4_IF_MAC_MAP.put(3, "2c542d27a9c1");
        ROUTER4_IF_IFNAME_MAP.put(1, "Em0/0");
        ROUTER4_IF_IFDESCR_MAP.put(1, "Embedded-Service-Engine0/0");
        ROUTER4_IF_MAC_MAP.put(1, "000000000000");
        ROUTER4_IF_IFNAME_MAP.put(9, "Se0/1/1");
        ROUTER4_IF_IFDESCR_MAP.put(9, "Serial0/1/1");
        ROUTER4_IF_IFNAME_MAP.put(6, "Se0/0/0");
        ROUTER4_IF_IFDESCR_MAP.put(6, "Serial0/0/0");
        ROUTER4_IF_IFNAME_MAP.put(5, "Gi0/3");
        ROUTER4_IF_IFDESCR_MAP.put(5, "GigabitEthernet0/3");
        ROUTER4_IF_MAC_MAP.put(5, "2c542d27a9c3");
        ROUTER4_IF_IFNAME_MAP.put(7, "Se0/0/1");
        ROUTER4_IF_IFDESCR_MAP.put(7, "Serial0/0/1");
        ROUTER4_IF_IFNAME_MAP.put(8, "Se0/1/0");
        ROUTER4_IF_IFDESCR_MAP.put(8, "Serial0/1/0");
        ROUTER4_IF_IFNAME_MAP.put(4, "Gi0/2");
        ROUTER4_IF_IFDESCR_MAP.put(4, "GigabitEthernet0/2");
        ROUTER4_IF_MAC_MAP.put(4, "2c542d27a9c2");
        ROUTER4_IF_IFNAME_MAP.put(16, "Lo0");
        ROUTER4_IF_IFDESCR_MAP.put(16, "Loopback0");
        ROUTER4_IF_IFNAME_MAP.put(2, "Gi0/0");
        ROUTER4_IF_IFDESCR_MAP.put(2, "GigabitEthernet0/0");
        ROUTER4_IF_MAC_MAP.put(2, "2c542d27a9c0");
        ROUTER4_IF_IFNAME_MAP.put(11, "Nu0");
        ROUTER4_IF_IFDESCR_MAP.put(11, "Null0");
        SWITCH1_IP_IF_MAP.put(InetAddressUtils.addr("192.168.100.246"), 10101);
        SWITCH1_IP_MK_MAP.put(InetAddressUtils.addr("192.168.100.246"), InetAddressUtils.addr("255.255.255.252"));
        SWITCH1_IP_IF_MAP.put(InetAddressUtils.addr("172.16.10.1"), 10);
        SWITCH1_IP_MK_MAP.put(InetAddressUtils.addr("172.16.10.1"), InetAddressUtils.addr("255.255.255.0"));
        SWITCH1_IP_IF_MAP.put(InetAddressUtils.addr("172.16.20.1"), 20);
        SWITCH1_IP_MK_MAP.put(InetAddressUtils.addr("172.16.20.1"), InetAddressUtils.addr("255.255.255.0"));
        SWITCH1_IP_IF_MAP.put(InetAddressUtils.addr("172.16.30.1"), 30);
        SWITCH1_IP_MK_MAP.put(InetAddressUtils.addr("172.16.30.1"), InetAddressUtils.addr("255.255.255.0"));
        SWITCH1_IP_IF_MAP.put(InetAddressUtils.addr("172.16.40.1"), 40);
        SWITCH1_IP_MK_MAP.put(InetAddressUtils.addr("172.16.40.1"), InetAddressUtils.addr("255.255.255.0"));
        SWITCH1_IF_IFNAME_MAP.put(10128, "Gi0/28");
        SWITCH1_IF_IFDESCR_MAP.put(10128, "GigabitEthernet0/28");
        SWITCH1_IF_MAC_MAP.put(10128, "0016c8bd4d9c");
        SWITCH1_IF_IFNAME_MAP.put(10113, "Gi0/13");
        SWITCH1_IF_IFDESCR_MAP.put(10113, "GigabitEthernet0/13");
        SWITCH1_IF_MAC_MAP.put(10113, "0016c8bd4d8d");
        SWITCH1_IF_IFNAME_MAP.put(5001, "Po1");
        SWITCH1_IF_IFDESCR_MAP.put(5001, "Port-channel1");
        SWITCH1_IF_MAC_MAP.put(5001, "0016c8bd4d8c");
        SWITCH1_IF_IFNAME_MAP.put(10117, "Gi0/17");
        SWITCH1_IF_IFDESCR_MAP.put(10117, "GigabitEthernet0/17");
        SWITCH1_IF_MAC_MAP.put(10117, "0016c8bd4d91");
        SWITCH1_IF_IFNAME_MAP.put(10106, "Gi0/6");
        SWITCH1_IF_IFDESCR_MAP.put(10106, "GigabitEthernet0/6");
        SWITCH1_IF_MAC_MAP.put(10106, "0016c8bd4d86");
        SWITCH1_IF_IFNAME_MAP.put(10122, "Gi0/22");
        SWITCH1_IF_IFDESCR_MAP.put(10122, "GigabitEthernet0/22");
        SWITCH1_IF_MAC_MAP.put(10122, "0016c8bd4d96");
        SWITCH1_IF_IFNAME_MAP.put(30, "Vl30");
        SWITCH1_IF_IFDESCR_MAP.put(30, "Vlan30");
        SWITCH1_IF_MAC_MAP.put(30, "0016c8bd4dc4");
        SWITCH1_IF_IFNAME_MAP.put(10, "Vl10");
        SWITCH1_IF_IFDESCR_MAP.put(10, "Vlan10");
        SWITCH1_IF_MAC_MAP.put(10, "0016c8bd4dc2");
        SWITCH1_IF_IFNAME_MAP.put(10102, "Gi0/2");
        SWITCH1_IF_IFDESCR_MAP.put(10102, "GigabitEthernet0/2");
        SWITCH1_IF_MAC_MAP.put(10102, "0016c8bd4d82");
        SWITCH1_IF_IFNAME_MAP.put(10111, "Gi0/11");
        SWITCH1_IF_IFDESCR_MAP.put(10111, "GigabitEthernet0/11");
        SWITCH1_IF_MAC_MAP.put(10111, "0016c8bd4d8b");
        SWITCH1_IF_IFNAME_MAP.put(10109, "Gi0/9");
        SWITCH1_IF_IFDESCR_MAP.put(10109, "GigabitEthernet0/9");
        SWITCH1_IF_MAC_MAP.put(10109, "0016c8bd4d89");
        SWITCH1_IF_IFNAME_MAP.put(10126, "Gi0/26");
        SWITCH1_IF_IFDESCR_MAP.put(10126, "GigabitEthernet0/26");
        SWITCH1_IF_MAC_MAP.put(10126, "0016c8bd4d9a");
        SWITCH1_IF_IFNAME_MAP.put(10105, "Gi0/5");
        SWITCH1_IF_IFDESCR_MAP.put(10105, "GigabitEthernet0/5");
        SWITCH1_IF_MAC_MAP.put(10105, "0016c8bd4d85");
        SWITCH1_IF_IFNAME_MAP.put(10116, "Gi0/16");
        SWITCH1_IF_IFDESCR_MAP.put(10116, "GigabitEthernet0/16");
        SWITCH1_IF_MAC_MAP.put(10116, "0016c8bd4d90");
        SWITCH1_IF_IFNAME_MAP.put(10123, "Gi0/23");
        SWITCH1_IF_IFDESCR_MAP.put(10123, "GigabitEthernet0/23");
        SWITCH1_IF_MAC_MAP.put(10123, "0016c8bd4d97");
        SWITCH1_IF_IFNAME_MAP.put(10127, "Gi0/27");
        SWITCH1_IF_IFDESCR_MAP.put(10127, "GigabitEthernet0/27");
        SWITCH1_IF_MAC_MAP.put(10127, "0016c8bd4d9b");
        SWITCH1_IF_IFNAME_MAP.put(10110, "Gi0/10");
        SWITCH1_IF_IFDESCR_MAP.put(10110, "GigabitEthernet0/10");
        SWITCH1_IF_MAC_MAP.put(10110, "0016c8bd4d8a");
        SWITCH1_IF_IFNAME_MAP.put(20, "Vl20");
        SWITCH1_IF_IFDESCR_MAP.put(20, "Vlan20");
        SWITCH1_IF_MAC_MAP.put(20, "0016c8bd4dc3");
        SWITCH1_IF_IFNAME_MAP.put(1, "Vl1");
        SWITCH1_IF_IFDESCR_MAP.put(1, "Vlan1");
        SWITCH1_IF_MAC_MAP.put(1, "0016c8bd4dc0");
        SWITCH1_IF_IFNAME_MAP.put(40, "Vl40");
        SWITCH1_IF_IFDESCR_MAP.put(40, "Vlan40");
        SWITCH1_IF_MAC_MAP.put(40, "0016c8bd4dc5");
        SWITCH1_IF_IFNAME_MAP.put(10104, "Gi0/4");
        SWITCH1_IF_IFDESCR_MAP.put(10104, "GigabitEthernet0/4");
        SWITCH1_IF_MAC_MAP.put(10104, "0016c8bd4d84");
        SWITCH1_IF_IFNAME_MAP.put(10119, "Gi0/19");
        SWITCH1_IF_IFDESCR_MAP.put(10119, "GigabitEthernet0/19");
        SWITCH1_IF_MAC_MAP.put(10119, "0016c8bd4d93");
        SWITCH1_IF_IFNAME_MAP.put(10501, "Nu0");
        SWITCH1_IF_IFDESCR_MAP.put(10501, "Null0");
        SWITCH1_IF_IFNAME_MAP.put(10121, "Gi0/21");
        SWITCH1_IF_IFDESCR_MAP.put(10121, "GigabitEthernet0/21");
        SWITCH1_IF_MAC_MAP.put(10121, "0016c8bd4d95");
        SWITCH1_IF_IFNAME_MAP.put(10115, "Gi0/15");
        SWITCH1_IF_IFDESCR_MAP.put(10115, "GigabitEthernet0/15");
        SWITCH1_IF_MAC_MAP.put(10115, "0016c8bd4d8f");
        SWITCH1_IF_IFNAME_MAP.put(10101, "Gi0/1");
        SWITCH1_IF_IFDESCR_MAP.put(10101, "GigabitEthernet0/1");
        SWITCH1_IF_MAC_MAP.put(10101, "0016c8bd4dc1");
        SWITCH1_IF_IFNAME_MAP.put(10112, "Gi0/12");
        SWITCH1_IF_IFDESCR_MAP.put(10112, "GigabitEthernet0/12");
        SWITCH1_IF_MAC_MAP.put(10112, "0016c8bd4d8c");
        SWITCH1_IF_IFNAME_MAP.put(10108, "Gi0/8");
        SWITCH1_IF_IFDESCR_MAP.put(10108, "GigabitEthernet0/8");
        SWITCH1_IF_MAC_MAP.put(10108, "0016c8bd4d88");
        SWITCH1_IF_IFNAME_MAP.put(10107, "Gi0/7");
        SWITCH1_IF_IFDESCR_MAP.put(10107, "GigabitEthernet0/7");
        SWITCH1_IF_MAC_MAP.put(10107, "0016c8bd4d87");
        SWITCH1_IF_IFNAME_MAP.put(10118, "Gi0/18");
        SWITCH1_IF_IFDESCR_MAP.put(10118, "GigabitEthernet0/18");
        SWITCH1_IF_MAC_MAP.put(10118, "0016c8bd4d92");
        SWITCH1_IF_IFNAME_MAP.put(10120, "Gi0/20");
        SWITCH1_IF_IFDESCR_MAP.put(10120, "GigabitEthernet0/20");
        SWITCH1_IF_MAC_MAP.put(10120, "0016c8bd4d94");
        SWITCH1_IF_IFNAME_MAP.put(10125, "Gi0/25");
        SWITCH1_IF_IFDESCR_MAP.put(10125, "GigabitEthernet0/25");
        SWITCH1_IF_MAC_MAP.put(10125, "0016c8bd4d99");
        SWITCH1_IF_IFNAME_MAP.put(10103, "Gi0/3");
        SWITCH1_IF_IFDESCR_MAP.put(10103, "GigabitEthernet0/3");
        SWITCH1_IF_MAC_MAP.put(10103, "0016c8bd4d83");
        SWITCH1_IF_IFNAME_MAP.put(10124, "Gi0/24");
        SWITCH1_IF_IFDESCR_MAP.put(10124, "GigabitEthernet0/24");
        SWITCH1_IF_MAC_MAP.put(10124, "0016c8bd4d98");
        SWITCH1_IF_IFNAME_MAP.put(10114, "Gi0/14");
        SWITCH1_IF_IFDESCR_MAP.put(10114, "GigabitEthernet0/14");
        SWITCH1_IF_MAC_MAP.put(10114, "0016c8bd4d8e");
        SWITCH2_IP_IF_MAP.put(InetAddressUtils.addr("172.16.10.2"), 10);
        SWITCH2_IP_MK_MAP.put(InetAddressUtils.addr("172.16.10.2"), InetAddressUtils.addr("255.255.255.0"));
        SWITCH2_IF_IFNAME_MAP.put(10103, "Gi0/3");
        SWITCH2_IF_IFDESCR_MAP.put(10103, "GigabitEthernet0/3");
        SWITCH2_IF_MAC_MAP.put(10103, "0016c894aa83");
        SWITCH2_IF_IFNAME_MAP.put(10104, "Gi0/4");
        SWITCH2_IF_IFDESCR_MAP.put(10104, "GigabitEthernet0/4");
        SWITCH2_IF_MAC_MAP.put(10104, "0016c894aa84");
        SWITCH2_IF_IFNAME_MAP.put(10111, "Gi0/11");
        SWITCH2_IF_IFDESCR_MAP.put(10111, "GigabitEthernet0/11");
        SWITCH2_IF_MAC_MAP.put(10111, "0016c894aa8b");
        SWITCH2_IF_IFNAME_MAP.put(10, "Vl10");
        SWITCH2_IF_IFDESCR_MAP.put(10, "Vlan10");
        SWITCH2_IF_MAC_MAP.put(10, "0016c894aac1");
        SWITCH2_IF_IFNAME_MAP.put(10115, "Gi0/15");
        SWITCH2_IF_IFDESCR_MAP.put(10115, "GigabitEthernet0/15");
        SWITCH2_IF_MAC_MAP.put(10115, "0016c894aa8f");
        SWITCH2_IF_IFNAME_MAP.put(10109, "Gi0/9");
        SWITCH2_IF_IFDESCR_MAP.put(10109, "GigabitEthernet0/9");
        SWITCH2_IF_MAC_MAP.put(10109, "0016c894aa89");
        SWITCH2_IF_IFNAME_MAP.put(5002, "Po2");
        SWITCH2_IF_IFDESCR_MAP.put(5002, "Port-channel2");
        SWITCH2_IF_MAC_MAP.put(5002, "0016c894aa94");
        SWITCH2_IF_IFNAME_MAP.put(5001, "Po1");
        SWITCH2_IF_IFDESCR_MAP.put(5001, "Port-channel1");
        SWITCH2_IF_MAC_MAP.put(5001, "0016c894aa81");
        SWITCH2_IF_IFNAME_MAP.put(10118, "Gi0/18");
        SWITCH2_IF_IFDESCR_MAP.put(10118, "GigabitEthernet0/18");
        SWITCH2_IF_MAC_MAP.put(10118, "0016c894aa92");
        SWITCH2_IF_IFNAME_MAP.put(10101, "Gi0/1");
        SWITCH2_IF_IFDESCR_MAP.put(10101, "GigabitEthernet0/1");
        SWITCH2_IF_MAC_MAP.put(10101, "0016c894aa81");
        SWITCH2_IF_IFNAME_MAP.put(10117, "Gi0/17");
        SWITCH2_IF_IFDESCR_MAP.put(10117, "GigabitEthernet0/17");
        SWITCH2_IF_MAC_MAP.put(10117, "0016c894aa91");
        SWITCH2_IF_IFNAME_MAP.put(10108, "Gi0/8");
        SWITCH2_IF_IFDESCR_MAP.put(10108, "GigabitEthernet0/8");
        SWITCH2_IF_MAC_MAP.put(10108, "0016c894aa88");
        SWITCH2_IF_IFNAME_MAP.put(10112, "Gi0/12");
        SWITCH2_IF_IFDESCR_MAP.put(10112, "GigabitEthernet0/12");
        SWITCH2_IF_MAC_MAP.put(10112, "0016c894aa8c");
        SWITCH2_IF_IFNAME_MAP.put(10123, "Gi0/23");
        SWITCH2_IF_IFDESCR_MAP.put(10123, "GigabitEthernet0/23");
        SWITCH2_IF_MAC_MAP.put(10123, "0016c894aa97");
        SWITCH2_IF_IFNAME_MAP.put(10122, "Gi0/22");
        SWITCH2_IF_IFDESCR_MAP.put(10122, "GigabitEthernet0/22");
        SWITCH2_IF_MAC_MAP.put(10122, "0016c894aa96");
        SWITCH2_IF_IFNAME_MAP.put(10102, "Gi0/2");
        SWITCH2_IF_IFDESCR_MAP.put(10102, "GigabitEthernet0/2");
        SWITCH2_IF_MAC_MAP.put(10102, "0016c894aa82");
        SWITCH2_IF_IFNAME_MAP.put(10113, "Gi0/13");
        SWITCH2_IF_IFDESCR_MAP.put(10113, "GigabitEthernet0/13");
        SWITCH2_IF_MAC_MAP.put(10113, "0016c894aa8d");
        SWITCH2_IF_IFNAME_MAP.put(10501, "Nu0");
        SWITCH2_IF_IFDESCR_MAP.put(10501, "Null0");
        SWITCH2_IF_IFNAME_MAP.put(10110, "Gi0/10");
        SWITCH2_IF_IFDESCR_MAP.put(10110, "GigabitEthernet0/10");
        SWITCH2_IF_MAC_MAP.put(10110, "0016c894aa8a");
        SWITCH2_IF_IFNAME_MAP.put(10106, "Gi0/6");
        SWITCH2_IF_IFDESCR_MAP.put(10106, "GigabitEthernet0/6");
        SWITCH2_IF_MAC_MAP.put(10106, "0016c894aa86");
        SWITCH2_IF_IFNAME_MAP.put(10120, "Gi0/20");
        SWITCH2_IF_IFDESCR_MAP.put(10120, "GigabitEthernet0/20");
        SWITCH2_IF_MAC_MAP.put(10120, "0016c894aa94");
        SWITCH2_IF_IFNAME_MAP.put(10124, "Gi0/24");
        SWITCH2_IF_IFDESCR_MAP.put(10124, "GigabitEthernet0/24");
        SWITCH2_IF_MAC_MAP.put(10124, "0016c894aa98");
        SWITCH2_IF_IFNAME_MAP.put(10107, "Gi0/7");
        SWITCH2_IF_IFDESCR_MAP.put(10107, "GigabitEthernet0/7");
        SWITCH2_IF_MAC_MAP.put(10107, "0016c894aa87");
        SWITCH2_IF_IFNAME_MAP.put(1, "Vl1");
        SWITCH2_IF_IFDESCR_MAP.put(1, "Vlan1");
        SWITCH2_IF_MAC_MAP.put(1, "0016c894aac0");
        SWITCH2_IF_IFNAME_MAP.put(10114, "Gi0/14");
        SWITCH2_IF_IFDESCR_MAP.put(10114, "GigabitEthernet0/14");
        SWITCH2_IF_MAC_MAP.put(10114, "0016c894aa8e");
        SWITCH2_IF_IFNAME_MAP.put(10119, "Gi0/19");
        SWITCH2_IF_IFDESCR_MAP.put(10119, "GigabitEthernet0/19");
        SWITCH2_IF_MAC_MAP.put(10119, "0016c894aa93");
        SWITCH2_IF_IFNAME_MAP.put(10105, "Gi0/5");
        SWITCH2_IF_IFDESCR_MAP.put(10105, "GigabitEthernet0/5");
        SWITCH2_IF_MAC_MAP.put(10105, "0016c894aa85");
        SWITCH2_IF_IFNAME_MAP.put(10121, "Gi0/21");
        SWITCH2_IF_IFDESCR_MAP.put(10121, "GigabitEthernet0/21");
        SWITCH2_IF_MAC_MAP.put(10121, "0016c894aa95");
        SWITCH2_IF_IFNAME_MAP.put(10116, "Gi0/16");
        SWITCH2_IF_IFDESCR_MAP.put(10116, "GigabitEthernet0/16");
        SWITCH2_IF_MAC_MAP.put(10116, "0016c894aa90");
        SWITCH3_IP_IF_MAP.put(InetAddressUtils.addr("172.16.10.3"), 10);
        SWITCH3_IP_MK_MAP.put(InetAddressUtils.addr("172.16.10.3"), InetAddressUtils.addr("255.255.255.0"));
        SWITCH3_IF_IFNAME_MAP.put(10008, "Fa0/8");
        SWITCH3_IF_IFDESCR_MAP.put(10008, "FastEthernet0/8");
        SWITCH3_IF_MAC_MAP.put(10008, "f4ea67ebdc08");
        SWITCH3_IF_IFNAME_MAP.put(10, "Vl10");
        SWITCH3_IF_IFDESCR_MAP.put(10, "Vlan10");
        SWITCH3_IF_MAC_MAP.put(10, "f4ea67ebdc41");
        SWITCH3_IF_IFNAME_MAP.put(10003, "Fa0/3");
        SWITCH3_IF_IFDESCR_MAP.put(10003, "FastEthernet0/3");
        SWITCH3_IF_MAC_MAP.put(10003, "f4ea67ebdc03");
        SWITCH3_IF_IFNAME_MAP.put(10021, "Fa0/21");
        SWITCH3_IF_IFDESCR_MAP.put(10021, "FastEthernet0/21");
        SWITCH3_IF_MAC_MAP.put(10021, "f4ea67ebdc15");
        SWITCH3_IF_IFNAME_MAP.put(10024, "Fa0/24");
        SWITCH3_IF_IFDESCR_MAP.put(10024, "FastEthernet0/24");
        SWITCH3_IF_MAC_MAP.put(10024, "f4ea67ebdc18");
        SWITCH3_IF_IFNAME_MAP.put(10001, "Fa0/1");
        SWITCH3_IF_IFDESCR_MAP.put(10001, "FastEthernet0/1");
        SWITCH3_IF_MAC_MAP.put(10001, "f4ea67ebdc01");
        SWITCH3_IF_IFNAME_MAP.put(10014, "Fa0/14");
        SWITCH3_IF_IFDESCR_MAP.put(10014, "FastEthernet0/14");
        SWITCH3_IF_MAC_MAP.put(10014, "f4ea67ebdc0e");
        SWITCH3_IF_IFNAME_MAP.put(10019, "Fa0/19");
        SWITCH3_IF_IFDESCR_MAP.put(10019, "FastEthernet0/19");
        SWITCH3_IF_MAC_MAP.put(10019, "f4ea67ebdc13");
        SWITCH3_IF_IFNAME_MAP.put(5001, "Po1");
        SWITCH3_IF_IFDESCR_MAP.put(5001, "Port-channel1");
        SWITCH3_IF_MAC_MAP.put(5001, "f4ea67ebdc13");
        SWITCH3_IF_IFNAME_MAP.put(10023, "Fa0/23");
        SWITCH3_IF_IFDESCR_MAP.put(10023, "FastEthernet0/23");
        SWITCH3_IF_MAC_MAP.put(10023, "f4ea67ebdc17");
        SWITCH3_IF_IFNAME_MAP.put(10012, "Fa0/12");
        SWITCH3_IF_IFDESCR_MAP.put(10012, "FastEthernet0/12");
        SWITCH3_IF_MAC_MAP.put(10012, "f4ea67ebdc0c");
        SWITCH3_IF_IFNAME_MAP.put(10018, "Fa0/18");
        SWITCH3_IF_IFDESCR_MAP.put(10018, "FastEthernet0/18");
        SWITCH3_IF_MAC_MAP.put(10018, "f4ea67ebdc12");
        SWITCH3_IF_IFNAME_MAP.put(10013, "Fa0/13");
        SWITCH3_IF_IFDESCR_MAP.put(10013, "FastEthernet0/13");
        SWITCH3_IF_MAC_MAP.put(10013, "f4ea67ebdc0d");
        SWITCH3_IF_IFNAME_MAP.put(10102, "Gi0/2");
        SWITCH3_IF_IFDESCR_MAP.put(10102, "GigabitEthernet0/2");
        SWITCH3_IF_MAC_MAP.put(10102, "f4ea67ebdc1a");
        SWITCH3_IF_IFNAME_MAP.put(1, "Vl1");
        SWITCH3_IF_IFDESCR_MAP.put(1, "Vlan1");
        SWITCH3_IF_MAC_MAP.put(1, "f4ea67ebdc40");
        SWITCH3_IF_IFNAME_MAP.put(10005, "Fa0/5");
        SWITCH3_IF_IFDESCR_MAP.put(10005, "FastEthernet0/5");
        SWITCH3_IF_MAC_MAP.put(10005, "f4ea67ebdc05");
        SWITCH3_IF_IFNAME_MAP.put(10016, "Fa0/16");
        SWITCH3_IF_IFDESCR_MAP.put(10016, "FastEthernet0/16");
        SWITCH3_IF_MAC_MAP.put(10016, "f4ea67ebdc10");
        SWITCH3_IF_IFNAME_MAP.put(10010, "Fa0/10");
        SWITCH3_IF_IFDESCR_MAP.put(10010, "FastEthernet0/10");
        SWITCH3_IF_MAC_MAP.put(10010, "f4ea67ebdc0a");
        SWITCH3_IF_IFNAME_MAP.put(10002, "Fa0/2");
        SWITCH3_IF_IFDESCR_MAP.put(10002, "FastEthernet0/2");
        SWITCH3_IF_MAC_MAP.put(10002, "f4ea67ebdc02");
        SWITCH3_IF_IFNAME_MAP.put(10022, "Fa0/22");
        SWITCH3_IF_IFDESCR_MAP.put(10022, "FastEthernet0/22");
        SWITCH3_IF_MAC_MAP.put(10022, "f4ea67ebdc16");
        SWITCH3_IF_IFNAME_MAP.put(10011, "Fa0/11");
        SWITCH3_IF_IFDESCR_MAP.put(10011, "FastEthernet0/11");
        SWITCH3_IF_MAC_MAP.put(10011, "f4ea67ebdc0b");
        SWITCH3_IF_IFNAME_MAP.put(10101, "Gi0/1");
        SWITCH3_IF_IFDESCR_MAP.put(10101, "GigabitEthernet0/1");
        SWITCH3_IF_MAC_MAP.put(10101, "f4ea67ebdc19");
        SWITCH3_IF_IFNAME_MAP.put(10020, "Fa0/20");
        SWITCH3_IF_IFDESCR_MAP.put(10020, "FastEthernet0/20");
        SWITCH3_IF_MAC_MAP.put(10020, "f4ea67ebdc14");
        SWITCH3_IF_IFNAME_MAP.put(10006, "Fa0/6");
        SWITCH3_IF_IFDESCR_MAP.put(10006, "FastEthernet0/6");
        SWITCH3_IF_MAC_MAP.put(10006, "f4ea67ebdc06");
        SWITCH3_IF_IFNAME_MAP.put(10501, "Nu0");
        SWITCH3_IF_IFDESCR_MAP.put(10501, "Null0");
        SWITCH3_IF_IFNAME_MAP.put(10009, "Fa0/9");
        SWITCH3_IF_IFDESCR_MAP.put(10009, "FastEthernet0/9");
        SWITCH3_IF_MAC_MAP.put(10009, "f4ea67ebdc09");
        SWITCH3_IF_IFNAME_MAP.put(10015, "Fa0/15");
        SWITCH3_IF_IFDESCR_MAP.put(10015, "FastEthernet0/15");
        SWITCH3_IF_MAC_MAP.put(10015, "f4ea67ebdc0f");
        SWITCH3_IF_IFNAME_MAP.put(10017, "Fa0/17");
        SWITCH3_IF_IFDESCR_MAP.put(10017, "FastEthernet0/17");
        SWITCH3_IF_MAC_MAP.put(10017, "f4ea67ebdc11");
        SWITCH3_IF_IFNAME_MAP.put(10007, "Fa0/7");
        SWITCH3_IF_IFDESCR_MAP.put(10007, "FastEthernet0/7");
        SWITCH3_IF_MAC_MAP.put(10007, "f4ea67ebdc07");
        SWITCH3_IF_IFNAME_MAP.put(10004, "Fa0/4");
        SWITCH3_IF_IFDESCR_MAP.put(10004, "FastEthernet0/4");
        SWITCH3_IF_MAC_MAP.put(10004, "f4ea67ebdc04");
        SWITCH4_IP_IF_MAP.put(InetAddressUtils.addr("172.16.50.2"), 50);
        SWITCH4_IP_MK_MAP.put(InetAddressUtils.addr("172.16.50.2"), InetAddressUtils.addr("255.255.255.0"));
        SWITCH4_IF_IFNAME_MAP.put(10019, "Fa0/19");
        SWITCH4_IF_IFDESCR_MAP.put(10019, "FastEthernet0/19");
        SWITCH4_IF_MAC_MAP.put(10019, "a4187504e413");
        SWITCH4_IF_IFNAME_MAP.put(10102, "Gi0/2");
        SWITCH4_IF_IFDESCR_MAP.put(10102, "GigabitEthernet0/2");
        SWITCH4_IF_MAC_MAP.put(10102, "a4187504e41a");
        SWITCH4_IF_IFNAME_MAP.put(10501, "Nu0");
        SWITCH4_IF_IFDESCR_MAP.put(10501, "Null0");
        SWITCH4_IF_IFNAME_MAP.put(10024, "Fa0/24");
        SWITCH4_IF_IFDESCR_MAP.put(10024, "FastEthernet0/24");
        SWITCH4_IF_MAC_MAP.put(10024, "a4187504e418");
        SWITCH4_IF_IFNAME_MAP.put(10014, "Fa0/14");
        SWITCH4_IF_IFDESCR_MAP.put(10014, "FastEthernet0/14");
        SWITCH4_IF_MAC_MAP.put(10014, "a4187504e40e");
        SWITCH4_IF_IFNAME_MAP.put(10002, "Fa0/2");
        SWITCH4_IF_IFDESCR_MAP.put(10002, "FastEthernet0/2");
        SWITCH4_IF_MAC_MAP.put(10002, "a4187504e402");
        SWITCH4_IF_IFNAME_MAP.put(10017, "Fa0/17");
        SWITCH4_IF_IFDESCR_MAP.put(10017, "FastEthernet0/17");
        SWITCH4_IF_MAC_MAP.put(10017, "a4187504e411");
        SWITCH4_IF_IFNAME_MAP.put(10016, "Fa0/16");
        SWITCH4_IF_IFDESCR_MAP.put(10016, "FastEthernet0/16");
        SWITCH4_IF_MAC_MAP.put(10016, "a4187504e410");
        SWITCH4_IF_IFNAME_MAP.put(10011, "Fa0/11");
        SWITCH4_IF_IFDESCR_MAP.put(10011, "FastEthernet0/11");
        SWITCH4_IF_MAC_MAP.put(10011, "a4187504e40b");
        SWITCH4_IF_IFNAME_MAP.put(10004, "Fa0/4");
        SWITCH4_IF_IFDESCR_MAP.put(10004, "FastEthernet0/4");
        SWITCH4_IF_MAC_MAP.put(10004, "a4187504e404");
        SWITCH4_IF_IFNAME_MAP.put(10009, "Fa0/9");
        SWITCH4_IF_IFDESCR_MAP.put(10009, "FastEthernet0/9");
        SWITCH4_IF_MAC_MAP.put(10009, "a4187504e409");
        SWITCH4_IF_IFNAME_MAP.put(10007, "Fa0/7");
        SWITCH4_IF_IFDESCR_MAP.put(10007, "FastEthernet0/7");
        SWITCH4_IF_MAC_MAP.put(10007, "a4187504e407");
        SWITCH4_IF_IFNAME_MAP.put(10006, "Fa0/6");
        SWITCH4_IF_IFDESCR_MAP.put(10006, "FastEthernet0/6");
        SWITCH4_IF_MAC_MAP.put(10006, "a4187504e406");
        SWITCH4_IF_IFNAME_MAP.put(10015, "Fa0/15");
        SWITCH4_IF_IFDESCR_MAP.put(10015, "FastEthernet0/15");
        SWITCH4_IF_MAC_MAP.put(10015, "a4187504e40f");
        SWITCH4_IF_IFNAME_MAP.put(1, "Vl1");
        SWITCH4_IF_IFDESCR_MAP.put(1, "Vlan1");
        SWITCH4_IF_MAC_MAP.put(1, "a4187504e440");
        SWITCH4_IF_IFNAME_MAP.put(10013, "Fa0/13");
        SWITCH4_IF_IFDESCR_MAP.put(10013, "FastEthernet0/13");
        SWITCH4_IF_MAC_MAP.put(10013, "a4187504e40d");
        SWITCH4_IF_IFNAME_MAP.put(10023, "Fa0/23");
        SWITCH4_IF_IFDESCR_MAP.put(10023, "FastEthernet0/23");
        SWITCH4_IF_MAC_MAP.put(10023, "a4187504e417");
        SWITCH4_IF_IFNAME_MAP.put(10021, "Fa0/21");
        SWITCH4_IF_IFDESCR_MAP.put(10021, "FastEthernet0/21");
        SWITCH4_IF_MAC_MAP.put(10021, "a4187504e415");
        SWITCH4_IF_IFNAME_MAP.put(50, "Vl50");
        SWITCH4_IF_IFDESCR_MAP.put(50, "Vlan50");
        SWITCH4_IF_MAC_MAP.put(50, "a4187504e441");
        SWITCH4_IF_IFNAME_MAP.put(10020, "Fa0/20");
        SWITCH4_IF_IFDESCR_MAP.put(10020, "FastEthernet0/20");
        SWITCH4_IF_MAC_MAP.put(10020, "a4187504e414");
        SWITCH4_IF_IFNAME_MAP.put(10010, "Fa0/10");
        SWITCH4_IF_IFDESCR_MAP.put(10010, "FastEthernet0/10");
        SWITCH4_IF_MAC_MAP.put(10010, "a4187504e40a");
        SWITCH4_IF_IFNAME_MAP.put(10012, "Fa0/12");
        SWITCH4_IF_IFDESCR_MAP.put(10012, "FastEthernet0/12");
        SWITCH4_IF_MAC_MAP.put(10012, "a4187504e40c");
        SWITCH4_IF_IFNAME_MAP.put(10001, "Fa0/1");
        SWITCH4_IF_IFDESCR_MAP.put(10001, "FastEthernet0/1");
        SWITCH4_IF_MAC_MAP.put(10001, "a4187504e401");
        SWITCH4_IF_IFNAME_MAP.put(10018, "Fa0/18");
        SWITCH4_IF_IFDESCR_MAP.put(10018, "FastEthernet0/18");
        SWITCH4_IF_MAC_MAP.put(10018, "a4187504e412");
        SWITCH4_IF_IFNAME_MAP.put(10022, "Fa0/22");
        SWITCH4_IF_IFDESCR_MAP.put(10022, "FastEthernet0/22");
        SWITCH4_IF_MAC_MAP.put(10022, "a4187504e416");
        SWITCH4_IF_IFNAME_MAP.put(10008, "Fa0/8");
        SWITCH4_IF_IFDESCR_MAP.put(10008, "FastEthernet0/8");
        SWITCH4_IF_MAC_MAP.put(10008, "a4187504e408");
        SWITCH4_IF_IFNAME_MAP.put(10003, "Fa0/3");
        SWITCH4_IF_IFDESCR_MAP.put(10003, "FastEthernet0/3");
        SWITCH4_IF_MAC_MAP.put(10003, "a4187504e403");
        SWITCH4_IF_IFNAME_MAP.put(10005, "Fa0/5");
        SWITCH4_IF_IFDESCR_MAP.put(10005, "FastEthernet0/5");
        SWITCH4_IF_MAC_MAP.put(10005, "a4187504e405");
        SWITCH4_IF_IFNAME_MAP.put(10101, "Gi0/1");
        SWITCH4_IF_IFDESCR_MAP.put(10101, "GigabitEthernet0/1");
        SWITCH4_IF_MAC_MAP.put(10101, "a4187504e419");
        SWITCH5_IP_IF_MAP.put(InetAddressUtils.addr("172.16.10.4"), 10);
        SWITCH5_IP_MK_MAP.put(InetAddressUtils.addr("172.16.10.4"), InetAddressUtils.addr("255.255.255.0"));
        SWITCH5_IF_IFNAME_MAP.put(10501, "Nu0");
        SWITCH5_IF_IFDESCR_MAP.put(10501, "Null0");
        SWITCH5_IF_IFNAME_MAP.put(10023, "Fa0/23");
        SWITCH5_IF_IFDESCR_MAP.put(10023, "FastEthernet0/23");
        SWITCH5_IF_MAC_MAP.put(10023, "f4ea67f82997");
        SWITCH5_IF_IFNAME_MAP.put(10005, "Fa0/5");
        SWITCH5_IF_IFDESCR_MAP.put(10005, "FastEthernet0/5");
        SWITCH5_IF_MAC_MAP.put(10005, "f4ea67f82985");
        SWITCH5_IF_IFNAME_MAP.put(10014, "Fa0/14");
        SWITCH5_IF_IFDESCR_MAP.put(10014, "FastEthernet0/14");
        SWITCH5_IF_MAC_MAP.put(10014, "f4ea67f8298e");
        SWITCH5_IF_IFNAME_MAP.put(10010, "Fa0/10");
        SWITCH5_IF_IFDESCR_MAP.put(10010, "FastEthernet0/10");
        SWITCH5_IF_MAC_MAP.put(10010, "f4ea67f8298a");
        SWITCH5_IF_IFNAME_MAP.put(10002, "Fa0/2");
        SWITCH5_IF_IFDESCR_MAP.put(10002, "FastEthernet0/2");
        SWITCH5_IF_MAC_MAP.put(10002, "f4ea67f82982");
        SWITCH5_IF_IFNAME_MAP.put(10021, "Fa0/21");
        SWITCH5_IF_IFDESCR_MAP.put(10021, "FastEthernet0/21");
        SWITCH5_IF_MAC_MAP.put(10021, "f4ea67f82995");
        SWITCH5_IF_IFNAME_MAP.put(10019, "Fa0/19");
        SWITCH5_IF_IFDESCR_MAP.put(10019, "FastEthernet0/19");
        SWITCH5_IF_MAC_MAP.put(10019, "f4ea67f82993");
        SWITCH5_IF_IFNAME_MAP.put(1, "Vl1");
        SWITCH5_IF_IFDESCR_MAP.put(1, "Vlan1");
        SWITCH5_IF_MAC_MAP.put(1, "f4ea67f829c0");
        SWITCH5_IF_IFNAME_MAP.put(10006, "Fa0/6");
        SWITCH5_IF_IFDESCR_MAP.put(10006, "FastEthernet0/6");
        SWITCH5_IF_MAC_MAP.put(10006, "f4ea67f82986");
        SWITCH5_IF_IFNAME_MAP.put(10001, "Fa0/1");
        SWITCH5_IF_IFDESCR_MAP.put(10001, "FastEthernet0/1");
        SWITCH5_IF_MAC_MAP.put(10001, "f4ea67f82981");
        SWITCH5_IF_IFNAME_MAP.put(10013, "Fa0/13");
        SWITCH5_IF_IFDESCR_MAP.put(10013, "FastEthernet0/13");
        SWITCH5_IF_MAC_MAP.put(10013, "f4ea67f8298d");
        SWITCH5_IF_IFNAME_MAP.put(10007, "Fa0/7");
        SWITCH5_IF_IFDESCR_MAP.put(10007, "FastEthernet0/7");
        SWITCH5_IF_MAC_MAP.put(10007, "f4ea67f82987");
        SWITCH5_IF_IFNAME_MAP.put(10011, "Fa0/11");
        SWITCH5_IF_IFDESCR_MAP.put(10011, "FastEthernet0/11");
        SWITCH5_IF_MAC_MAP.put(10011, "f4ea67f8298b");
        SWITCH5_IF_IFNAME_MAP.put(10017, "Fa0/17");
        SWITCH5_IF_IFDESCR_MAP.put(10017, "FastEthernet0/17");
        SWITCH5_IF_MAC_MAP.put(10017, "f4ea67f82991");
        SWITCH5_IF_IFNAME_MAP.put(10101, "Gi0/1");
        SWITCH5_IF_IFDESCR_MAP.put(10101, "GigabitEthernet0/1");
        SWITCH5_IF_MAC_MAP.put(10101, "f4ea67f82999");
        SWITCH5_IF_IFNAME_MAP.put(10012, "Fa0/12");
        SWITCH5_IF_IFDESCR_MAP.put(10012, "FastEthernet0/12");
        SWITCH5_IF_MAC_MAP.put(10012, "f4ea67f8298c");
        SWITCH5_IF_IFNAME_MAP.put(10009, "Fa0/9");
        SWITCH5_IF_IFDESCR_MAP.put(10009, "FastEthernet0/9");
        SWITCH5_IF_MAC_MAP.put(10009, "f4ea67f82989");
        SWITCH5_IF_IFNAME_MAP.put(10004, "Fa0/4");
        SWITCH5_IF_IFDESCR_MAP.put(10004, "FastEthernet0/4");
        SWITCH5_IF_MAC_MAP.put(10004, "f4ea67f82984");
        SWITCH5_IF_IFNAME_MAP.put(10018, "Fa0/18");
        SWITCH5_IF_IFDESCR_MAP.put(10018, "FastEthernet0/18");
        SWITCH5_IF_MAC_MAP.put(10018, "f4ea67f82992");
        SWITCH5_IF_IFNAME_MAP.put(10022, "Fa0/22");
        SWITCH5_IF_IFDESCR_MAP.put(10022, "FastEthernet0/22");
        SWITCH5_IF_MAC_MAP.put(10022, "f4ea67f82996");
        SWITCH5_IF_IFNAME_MAP.put(10008, "Fa0/8");
        SWITCH5_IF_IFDESCR_MAP.put(10008, "FastEthernet0/8");
        SWITCH5_IF_MAC_MAP.put(10008, "f4ea67f82988");
        SWITCH5_IF_IFNAME_MAP.put(10015, "Fa0/15");
        SWITCH5_IF_IFDESCR_MAP.put(10015, "FastEthernet0/15");
        SWITCH5_IF_MAC_MAP.put(10015, "f4ea67f8298f");
        SWITCH5_IF_IFNAME_MAP.put(10016, "Fa0/16");
        SWITCH5_IF_IFDESCR_MAP.put(10016, "FastEthernet0/16");
        SWITCH5_IF_MAC_MAP.put(10016, "f4ea67f82990");
        SWITCH5_IF_IFNAME_MAP.put(10102, "Gi0/2");
        SWITCH5_IF_IFDESCR_MAP.put(10102, "GigabitEthernet0/2");
        SWITCH5_IF_MAC_MAP.put(10102, "f4ea67f8299a");
        SWITCH5_IF_IFNAME_MAP.put(10024, "Fa0/24");
        SWITCH5_IF_IFDESCR_MAP.put(10024, "FastEthernet0/24");
        SWITCH5_IF_MAC_MAP.put(10024, "f4ea67f82998");
        SWITCH5_IF_IFNAME_MAP.put(10003, "Fa0/3");
        SWITCH5_IF_IFDESCR_MAP.put(10003, "FastEthernet0/3");
        SWITCH5_IF_MAC_MAP.put(10003, "f4ea67f82983");
        SWITCH5_IF_IFNAME_MAP.put(10, "Vl10");
        SWITCH5_IF_IFDESCR_MAP.put(10, "Vlan10");
        SWITCH5_IF_MAC_MAP.put(10, "f4ea67f829c1");
        SWITCH5_IF_IFNAME_MAP.put(10020, "Fa0/20");
        SWITCH5_IF_IFDESCR_MAP.put(10020, "FastEthernet0/20");
        SWITCH5_IF_MAC_MAP.put(10020, "f4ea67f82994");
    }

    public OnmsNode getRouter1() {
        return getNode(ROUTER1_NAME,ROUTER1_SYSOID,ROUTER1_IP,ROUTER1_IP_IF_MAP,ROUTER1_IF_IFNAME_MAP,ROUTER1_IF_MAC_MAP,ROUTER1_IF_IFDESCR_MAP,ROUTER1_IF_IFALIAS_MAP,ROUTER1_IP_MK_MAP);
    }    

    public OnmsNode getRouter2() {
        return getNode(ROUTER2_NAME,ROUTER2_SYSOID,ROUTER2_IP,ROUTER2_IP_IF_MAP,ROUTER2_IF_IFNAME_MAP,ROUTER2_IF_MAC_MAP,ROUTER2_IF_IFDESCR_MAP,ROUTER2_IF_IFALIAS_MAP,ROUTER2_IP_MK_MAP);
    }    

    public OnmsNode getRouter3() {
        return getNode(ROUTER3_NAME,ROUTER3_SYSOID,ROUTER3_IP,ROUTER3_IP_IF_MAP,ROUTER3_IF_IFNAME_MAP,ROUTER3_IF_MAC_MAP,ROUTER3_IF_IFDESCR_MAP,ROUTER3_IF_IFALIAS_MAP,ROUTER3_IP_MK_MAP);
    }    

    public OnmsNode getRouter4() {
        return getNode(ROUTER4_NAME,ROUTER4_SYSOID,ROUTER4_IP,ROUTER4_IP_IF_MAP,ROUTER4_IF_IFNAME_MAP,ROUTER4_IF_MAC_MAP,ROUTER4_IF_IFDESCR_MAP,ROUTER4_IF_IFALIAS_MAP,ROUTER4_IP_MK_MAP);
    }    

    public OnmsNode getSwitch1() {
        return getNode(SWITCH1_NAME,SWITCH1_SYSOID,SWITCH1_IP,SWITCH1_IP_IF_MAP,SWITCH1_IF_IFNAME_MAP,SWITCH1_IF_MAC_MAP,SWITCH1_IF_IFDESCR_MAP,SWITCH1_IF_IFALIAS_MAP,SWITCH1_IP_MK_MAP);
    }    

    public OnmsNode getSwitch2() {
        return getNode(SWITCH2_NAME,SWITCH2_SYSOID,SWITCH2_IP,SWITCH2_IP_IF_MAP,SWITCH2_IF_IFNAME_MAP,SWITCH2_IF_MAC_MAP,SWITCH2_IF_IFDESCR_MAP,SWITCH2_IF_IFALIAS_MAP,SWITCH2_IP_MK_MAP);
    }    

    public OnmsNode getSwitch3() {
       return getNode(SWITCH3_NAME,SWITCH3_SYSOID,SWITCH3_IP,SWITCH3_IP_IF_MAP,SWITCH3_IF_IFNAME_MAP,SWITCH3_IF_MAC_MAP,SWITCH3_IF_IFDESCR_MAP,SWITCH3_IF_IFALIAS_MAP,SWITCH3_IP_MK_MAP);
    }
   
    public OnmsNode getSwitch4() {
       return getNode(SWITCH4_NAME,SWITCH4_SYSOID,SWITCH4_IP,SWITCH4_IP_IF_MAP,SWITCH4_IF_IFNAME_MAP,SWITCH4_IF_MAC_MAP,SWITCH4_IF_IFDESCR_MAP,SWITCH4_IF_IFALIAS_MAP,SWITCH4_IP_MK_MAP);
    }
   
    public OnmsNode getSwitch5() {
       return getNode(SWITCH5_NAME,SWITCH5_SYSOID,SWITCH5_IP,SWITCH5_IP_IF_MAP,SWITCH5_IF_IFNAME_MAP,SWITCH5_IF_MAC_MAP,SWITCH5_IF_IFDESCR_MAP,SWITCH5_IF_IFALIAS_MAP,SWITCH5_IP_MK_MAP);
    }


}    

