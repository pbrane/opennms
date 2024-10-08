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
package org.opennms.enlinkd.generator.protocol.bridge;

import java.net.InetAddress;
import java.util.Date;
import java.util.Optional;

import org.opennms.netmgt.enlinkd.model.BridgeBridgeLink;
import org.opennms.netmgt.enlinkd.model.BridgeElement;
import org.opennms.netmgt.enlinkd.model.BridgeMacLink;
import org.opennms.netmgt.enlinkd.model.IpNetToMedia;
import org.opennms.netmgt.model.OnmsIpInterface;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.model.OnmsSnmpInterface;

public class BridgeBuilder {

    /* this is the vlan id for all the bridgebridgelinks and bridgemaclinks */
    private final static int VLAN_ID = 1;

    private OnmsNode node;
    private int bridgePortCounter;
    private BridgeBuilderContext context;

    public BridgeBuilder(OnmsNode node, int bridgePortCounter, BridgeBuilderContext context) {
        this.node = node;
        this.bridgePortCounter = bridgePortCounter;
        this.context = context;
        createAndSaveBridgeElement(node);
    }

    public BridgeBuilder connectToNewBridge(OnmsNode targetNode, int targetNodePortCounter) {
        this.bridgePortCounter ++;
        createAndPersistBridgeBridgeLink(targetNode, targetNodePortCounter, this.node, this.bridgePortCounter);
        return new BridgeBuilder(targetNode, targetNodePortCounter , this.context);
    }

    private void createAndSaveBridgeElement(OnmsNode node) {
        BridgeElement bridgeElement = createBridgeElement(node);
        this.context.getTopologyPersister().persist(bridgeElement);
    }

    private BridgeElement createBridgeElement(OnmsNode node) {
        BridgeElement bridge = new BridgeElement();
        bridge.setNode(node);
        bridge.setBaseBridgeAddress(this.context.getNextMacAddress());
        bridge.setBaseType(BridgeElement.BridgeDot1dBaseType.DOT1DBASETYPE_TRANSPARENT_ONLY);
        bridge.setBridgeNodeLastPollTime(new Date());
        bridge.setBaseNumPorts(3);
        bridge.setVlan(VLAN_ID);
        bridge.setVlanname("default");
        return bridge;
    }

    private void createAndPersistBridgeBridgeLink(OnmsNode bridge, Integer port, OnmsNode designated, Integer designatedPort) {
        createAndPersistBridgeBridgeLink(bridge, port, true, designated, designatedPort, true);
    }

    private void createAndPersistBridgeBridgeLink(OnmsNode bridge, Integer port, boolean createSnmpInterface, OnmsNode designated, Integer designatedPort, boolean createdesignatedsnmpInterface) {
        if (createSnmpInterface) {
            context.getTopologyPersister().persist(
                    createSnmpInterface(port, bridge)
            );
        }
        if (createdesignatedsnmpInterface) {
            context.getTopologyPersister().persist(
                    createSnmpInterface(designatedPort, designated)
            );
        }
        context.getTopologyPersister().persist(
                createBridgeBridgeLink(bridge, designated, port, designatedPort)
        );
    }

    public void createAndPersistCloud(int macaddresses, int ipaddresses) {
        for (int i=0; i<ipaddresses;i++) {
            String nextMac = context.getNextMacAddress();
            context.getTopologyPersister().persist(
                    createIpNetToMedia(null, null, nextMac, context.getNextInetAddress(), this.node)
            );
            context.getTopologyPersister().persist(
                    createBridgeMacLink(this.node, this.bridgePortCounter, nextMac));
        }

        for (int i=0; i<macaddresses;i++) {
            context.getTopologyPersister().persist(createBridgeMacLink(this.node, this.bridgePortCounter, context.getNextMacAddress()));
        }

    }

    public void createAndPersistBridgeMacLink(OnmsNode host, Integer hostPort, OnmsNode gateway) {
        createAndPersistBridgeMacLink(true, host, hostPort, gateway);
    }

    public void createAndPersistBridgeMacLink(boolean createsnmpinterface, OnmsNode host, Integer hostPort, OnmsNode gateway) {
        String mac=context.getNextMacAddress();
        InetAddress ip= context.getNextInetAddress();
        if (createsnmpinterface) {
            context.getTopologyPersister().persist(
                    createSnmpInterface(this.bridgePortCounter, this.node)
            );
        }
        if (hostPort != null) {
            OnmsSnmpInterface snmpInterface = createSnmpInterface(hostPort, host);
            context.getTopologyPersister().persist(
                    snmpInterface
            );
            OnmsIpInterface ipInterface = createIpInterface(snmpInterface, ip);
            context.getTopologyPersister().persist(ipInterface);
        } else {
            OnmsIpInterface ipInterface = createIpInterface(null,ip);
            ipInterface.setNode(host);
            context.getTopologyPersister().persist(ipInterface);
        }
        context.getTopologyPersister().persist(
                createIpNetToMedia(host, hostPort, mac, ip, gateway)
        );
        context.getTopologyPersister().persist(
                createBridgeMacLink(this.node, this.bridgePortCounter, mac)
        );
    }

    protected OnmsIpInterface createIpInterface(OnmsSnmpInterface snmp, InetAddress inetAddress) {
        OnmsIpInterface ip = new OnmsIpInterface();
        ip.setSnmpInterface(snmp);
        ip.setIpLastCapsdPoll(new Date());
        ip.setNode(Optional.ofNullable(snmp).map(OnmsSnmpInterface::getNode).orElse(null));
        ip.setIpAddress(inetAddress);
        return ip;
    }

    private BridgeMacLink createBridgeMacLink(OnmsNode bridge, Integer bridgePort, String mac){
        BridgeMacLink bridgeMacLink = new BridgeMacLink();
        bridgeMacLink.setNode(bridge);
        bridgeMacLink.setBridgePort(bridgePort);
        bridgeMacLink.setBridgePortIfIndex(bridgePort);
        bridgeMacLink.setLinkType(BridgeMacLink.BridgeMacLinkType.BRIDGE_LINK);
        bridgeMacLink.setMacAddress(mac);
        bridgeMacLink.setVlan(VLAN_ID);
        bridgeMacLink.setBridgeMacLinkLastPollTime(new Date());
        return bridgeMacLink;
    }

    protected OnmsSnmpInterface createSnmpInterface(int ifIndex, OnmsNode node) {
        OnmsSnmpInterface onmsSnmpInterface = new OnmsSnmpInterface();
        onmsSnmpInterface.setNode(node);
        onmsSnmpInterface.setIfIndex(ifIndex);
        onmsSnmpInterface.setIfType(4);
        onmsSnmpInterface.setIfSpeed(5L);
        onmsSnmpInterface.setIfAdminStatus(6);
        onmsSnmpInterface.setIfOperStatus(7);
        onmsSnmpInterface.setLastCapsdPoll(new Date());
        onmsSnmpInterface.setLastSnmpPoll(new Date());

        return onmsSnmpInterface;
    }

    private BridgeBridgeLink createBridgeBridgeLink(OnmsNode node, OnmsNode designatedNode, int port, int designatedPort) {
        BridgeBridgeLink link = new BridgeBridgeLink();
        link.setBridgePortIfIndex(port);
        link.setBridgePort(port);
        link.setVlan(1);
        link.setNode(node);
        link.setDesignatedNode(designatedNode);
        link.setDesignatedPort(designatedPort);
        link.setDesignatedPortIfIndex(designatedPort);
        link.setDesignatedVlan(VLAN_ID);
        link.setBridgeBridgeLinkLastPollTime(new Date());
        return link;
    }

    private IpNetToMedia createIpNetToMedia(OnmsNode node, Integer ifindex, String mac, InetAddress inetAddress, OnmsNode sourceNode){
        IpNetToMedia ipNetToMedia = new IpNetToMedia();
        ipNetToMedia.setCreateTime(new Date());
        if (node != null && ifindex == null ) {
            ipNetToMedia.setIfIndex(-1);
        } else {
            ipNetToMedia.setIfIndex(ifindex);
        }
        ipNetToMedia.setLastPollTime(new Date());
        ipNetToMedia.setIpNetToMediaType(IpNetToMedia.IpNetToMediaType.IPNETTOMEDIA_TYPE_DYNAMIC);
        ipNetToMedia.setNetAddress(inetAddress);
        ipNetToMedia.setPhysAddress(mac);
        ipNetToMedia.setNode(node);
        ipNetToMedia.setSourceIfIndex(0);
        ipNetToMedia.setSourceNode(sourceNode);
        return ipNetToMedia;
    }

    public void increasePortCounter() {
        this.bridgePortCounter++;
    }
}
