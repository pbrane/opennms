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
package org.opennms.netmgt.trapd;

import static org.opennms.core.utils.InetAddressUtils.str;

import java.net.InetAddress;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import org.opennms.core.utils.InetAddressUtils;
import org.opennms.netmgt.config.api.EventConfDao;
import org.opennms.netmgt.dao.api.InterfaceToNodeCache;
import org.opennms.netmgt.dao.api.MonitoringLocationDao;
import org.opennms.netmgt.events.api.EventConstants;
import org.opennms.netmgt.model.events.EventBuilder;
import org.opennms.netmgt.model.events.snmp.SyntaxToEvent;
import org.opennms.netmgt.snmp.SnmpObjId;
import org.opennms.netmgt.snmp.SnmpResult;
import org.opennms.netmgt.snmp.SnmpValue;
import org.opennms.netmgt.xml.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EventCreator {
	
	private static final Logger LOG = LoggerFactory.getLogger(EventCreator.class);

    private final InterfaceToNodeCache cache;
    private final EventConfDao eventConfDao;

    public EventCreator(InterfaceToNodeCache cache, EventConfDao eventConfDao) {
        this.cache = Objects.requireNonNull(cache);
        this.eventConfDao = Objects.requireNonNull(eventConfDao);
    }

    public Event createEventFrom(final TrapDTO trapDTO, final String systemId, final String location, final InetAddress trapAddress) {
        LOG.debug("{} trap - trapInterface: {}", trapDTO.getVersion(), trapDTO.getAgentAddress());

        // Set event data
        final InetAddress sourceTrapAddress = Optional.ofNullable(trapDTO.getTrapAddress())
                .orElse(trapAddress);

        final EventBuilder eventBuilder = new EventBuilder(null, "trapd");
        eventBuilder.setTime(new Date(trapDTO.getCreationTime()));
        eventBuilder.setCommunity(trapDTO.getCommunity());
        eventBuilder.setSnmpTimeStamp(trapDTO.getTimestamp());
        eventBuilder.setSnmpVersion(trapDTO.getVersion());
        eventBuilder.setSnmpHost(str(sourceTrapAddress));
        eventBuilder.setInterface(sourceTrapAddress);
        eventBuilder.setHost(InetAddressUtils.toIpAddrString(trapDTO.getAgentAddress()));

        // Handle trap identity
        final TrapIdentityDTO trapIdentity = trapDTO.getTrapIdentity();
        if (trapIdentity != null) {
            LOG.debug("Trap Identity {}", trapIdentity);
            eventBuilder.setGeneric(trapIdentity.getGeneric());
            eventBuilder.setSpecific(trapIdentity.getSpecific());
            eventBuilder.setEnterpriseId(trapIdentity.getEnterpriseId());
            eventBuilder.setTrapOID(trapIdentity.getTrapOID());
        }

        // Handle var bindings
        for (SnmpResult eachResult : trapDTO.getResults()) {
            final SnmpObjId name = eachResult.getBase();
            final SnmpValue value = eachResult.getValue();
            eventBuilder.addParam(SyntaxToEvent.processSyntax(name.toString(), value));
            if (EventConstants.OID_SNMP_IFINDEX.isPrefixOf(name)) {
                eventBuilder.setIfIndex(value.toInt());
            }
        }

        // Resolve Node id and set, if known by OpenNMS
        resolveNodeId(location, sourceTrapAddress )
                .ifPresent(eventBuilder::setNodeid);

        // If there was no systemId in the trap message, assume that
        // it was generated by this system. Eventd will fill in the
        // systemId of the local system if it remains null here.
        if (systemId != null) {
            eventBuilder.setDistPoller(systemId);
        }

        // Get event template and set uei, if unknown
        final Event event = eventBuilder.getEvent();
        final org.opennms.netmgt.xml.eventconf.Event econf = eventConfDao.findByEvent(event);
        if (econf == null || econf.getUei() == null) {
            event.setUei("uei.opennms.org/default/trap");
        } else {
            event.setUei(econf.getUei());
        }
        return event;
    }

    private Optional<Integer> resolveNodeId(String location, InetAddress trapAddress) {
        // If there was no location in the trap message, assume that
        // it was generated in the default location
        if (location == null) {
            return cache.getFirstNodeId(MonitoringLocationDao.DEFAULT_MONITORING_LOCATION_ID, trapAddress);
        }
        return cache.getFirstNodeId(location, trapAddress);
    }
}
