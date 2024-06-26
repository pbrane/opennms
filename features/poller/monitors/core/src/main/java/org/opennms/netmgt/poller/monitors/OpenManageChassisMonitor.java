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

import org.opennms.core.utils.InetAddressUtils;
import org.opennms.core.utils.ParameterMap;
import org.opennms.netmgt.poller.MonitoredService;
import org.opennms.netmgt.poller.PollStatus;
import org.opennms.netmgt.snmp.SnmpAgentConfig;
import org.opennms.netmgt.snmp.SnmpObjId;
import org.opennms.netmgt.snmp.SnmpUtils;
import org.opennms.netmgt.snmp.SnmpValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class is used to monitor Dell OpenManage chassis. The specific OIDs
 * referenced to "SNMP Reference Guide", available from
 * http://support.dell.com/support/edocs/software/svradmin/6.1/en
 * </p>
 * <p>
 * This does SNMP and therefore relies on the SNMP configuration so it is not distributable.
 * </p>
 *
 * @author <A HREF="mailto:r.trommer@open-factory.org">Ronny Trommer</A>
 */
final public class OpenManageChassisMonitor extends SnmpMonitorStrategy {

    public static final Logger LOG = LoggerFactory.getLogger(OpenManageChassisMonitor.class);

    /**
     * Defines the status of the chassis.
     */
    private static final String CHASSIS_STATUS_OID = ".1.3.6.1.4.1.674.10892.1.300.10.1.4.1";

    /**
     * Defines the overall status of this chassis (ESM) event log.
     */
    private static final String EVENT_LOG_STATUS_OID = ".1.3.6.1.4.1.674.10892.1.200.10.1.41.1";

    /**
     * Defines the manufacturer's name for this chassis.
     */
    private static final String MANUFACTURER_OID = ".1.3.6.1.4.1.674.10892.1.300.10.1.8.1";

    /**
     * Defines the status of the chassis.
     */
    private static final String MODEL_NAME_OID = "1.3.6.1.4.1.674.10892.1.300.10.1.9.1";

    /**
     * Defines the service tag name for this chassis.
     */
    private static final String SERVICE_TAG_OID = ".1.3.6.1.4.1.674.10892.1.300.10.1.11.1";

    /**
     * Implement the dell status
     */
    private enum DELL_STATUS {
        OTHER(1), UNKNOWN(2), OK(3), NON_CRITICAL(4), CRITICAL(5), NON_RECOVERABLE(6);

        private final int state; // state code

        DELL_STATUS(int s) {
            this.state = s;
        }

        private int value() {
            return this.state;
        }
    };

    /**
     * {@inheritDoc}
     *
     * <P>
     * The poll() method is responsible for polling the specified address for
     * SNMP service availability.
     * </P>
     * @exception RuntimeException
     *                Thrown for any uncrecoverable errors.
     */
    @Override
    public PollStatus poll(MonitoredService svc, Map<String,Object> parameters) {
        String returnValue = "";

        PollStatus status = PollStatus.unavailable();
        InetAddress ipaddr = svc.getAddress();

        // Initialize the messages if the session is down
        String eventLogStatusTxt = "N/A";
        String manufacturerName = "N/A";
        String modelName = "N/A";
        String serviceTagTxt = "N/A";
        String chassisStatusTxt = "N/A";

        // Retrieve this interface's SNMP peer object
        //
        final SnmpAgentConfig agentConfig = getAgentConfig(svc, parameters);
        final String hostAddress = InetAddressUtils.str(ipaddr);
		LOG.debug("poll: setting SNMP peer attribute for interface {}", hostAddress);

        // set timeout and retries on SNMP peer object
        //
        agentConfig.setTimeout(ParameterMap.getKeyedInteger(parameters, "timeout", agentConfig.getTimeout()));
        agentConfig.setRetries(ParameterMap.getKeyedInteger(parameters, "retry", ParameterMap.getKeyedInteger(parameters, "retries", agentConfig.getRetries())));
        agentConfig.setPort(ParameterMap.getKeyedInteger(parameters, "port", agentConfig.getPort()));

        // Establish SNMP session with interface
        //
        try {
            LOG.debug("poll: SnmpAgentConfig address: {}", agentConfig);

            // Get the chassis status
            SnmpObjId chassisStatusSnmpObject = SnmpObjId.get(CHASSIS_STATUS_OID);
            SnmpValue chassisStatus = SnmpUtils.get(agentConfig, chassisStatusSnmpObject);

            // If no chassis status is received or SNMP is not possible,
            // service is down
            if (chassisStatus == null) {
                LOG.warn("No chassis status received!");
                return status;
            } else {
                LOG.debug("poll: chassis status: {}", chassisStatus);
            }

            /*
             * Do no unnecessary SNMP requests, if chassis status is OK,
             * return with service available and go away.
             */
            if (chassisStatus.toInt() == DELL_STATUS.OK.value()) {
                LOG.debug("poll: chassis status: {}", chassisStatus.toInt());
                return PollStatus.available();
            } else {
                LOG.debug("poll: chassis status: {}", chassisStatus.toInt());
                chassisStatusTxt = resolveDellStatus(chassisStatus.toInt());
            }

            // Chassis status is not OK gather some information
            SnmpObjId eventLogStatusSnmpObject = SnmpObjId.get(EVENT_LOG_STATUS_OID);
            SnmpValue eventLogStatus = SnmpUtils.get(agentConfig, eventLogStatusSnmpObject);
            // Check correct MIB-Support
            if (eventLogStatus == null) {
                LOG.warn("Cannot receive eventLogStatus.");
            } else {
                LOG.debug("poll: eventLogStatus: {}", eventLogStatus);
                eventLogStatusTxt = resolveDellStatus(eventLogStatus.toInt());
            }

            SnmpObjId manufacturerSnmpObject = SnmpObjId.get(MANUFACTURER_OID);
            SnmpValue manufacturer = SnmpUtils.get(agentConfig, manufacturerSnmpObject);
            // Check correct MIB-Support
            if (manufacturer == null) {
                LOG.warn("Cannot receive manufacturer.");
            } else {
                LOG.debug("poll: manufacturer: {}", manufacturer);
                manufacturerName = manufacturer.toString();
            }

            SnmpObjId modelSnmpObject = SnmpObjId.get(MODEL_NAME_OID);
            SnmpValue model = SnmpUtils.get(agentConfig, modelSnmpObject);
            // Check correct MIB-Support
            if (model == null) {
                LOG.warn("Cannot receive model name.");
            } else {
                LOG.debug("poll: model name: {}", model);
                modelName = model.toString();
            }

            SnmpObjId serviceTagSnmpObject = SnmpObjId.get(SERVICE_TAG_OID);
            SnmpValue serviceTag = SnmpUtils.get(agentConfig, serviceTagSnmpObject);
            // Check correct MIB-Support
            if (serviceTag == null) {
                LOG.warn("Cannot receive service tag");
            } else {
                LOG.debug("poll: service tag: {}", serviceTag);
                serviceTagTxt = serviceTag.toString();
            }

            returnValue = "Chassis status from " + manufacturerName + " " + modelName + " with service tag " + serviceTagTxt + " is " + chassisStatusTxt
                    + ". Last event log status is " + eventLogStatusTxt + ". For further information, check your OpenManage website!";
            // Set service down and return gathered information
            status = PollStatus.unavailable(returnValue);

        } catch (NullPointerException e) {
            String reason = "Unexpected error during SNMP poll of interface " + hostAddress;
            LOG.debug(reason, e);
            status = PollStatus.unavailable(reason);
        } catch (NumberFormatException e) {
            String reason = "Number operator used on a non-number " + e.getMessage();
            LOG.debug(reason);
            status = PollStatus.unavailable(reason);
        } catch (IllegalArgumentException e) {
            String reason = "Invalid SNMP Criteria: " + e.getMessage();
            LOG.debug(reason);
            status = PollStatus.unavailable(reason);
        } catch (Throwable t) {
            String reason = "Unexpected exception during SNMP poll of interface " + hostAddress;
            LOG.debug(reason, t);
            status = PollStatus.unavailable(reason);
        }

        // If matchAll is set to true, then the status is set to available
        // above with a single match.
        // Otherwise, the service will be unavailable.
        return status;
    }

    /**
     * Method to resolve a given Dell status to human readable string.
     * 
     * @param sc
     *            Dell status code
     * @return Human readable Dell status
     */
    private String resolveDellStatus(int sc) {
        String name = "N/A";
        if (DELL_STATUS.OTHER.value() == sc)
            name = DELL_STATUS.OTHER.name();
        if (DELL_STATUS.UNKNOWN.value() == sc)
            name = DELL_STATUS.UNKNOWN.name();
        if (DELL_STATUS.OK.value() == sc)
            name = DELL_STATUS.OK.name();
        if (DELL_STATUS.NON_CRITICAL.value() == sc)
            name = DELL_STATUS.NON_CRITICAL.name();
        if (DELL_STATUS.CRITICAL.value() == sc)
            name = DELL_STATUS.CRITICAL.name();
        if (DELL_STATUS.NON_RECOVERABLE.value() == sc)
            name = DELL_STATUS.NON_RECOVERABLE.name();
        return name;
    }
}
