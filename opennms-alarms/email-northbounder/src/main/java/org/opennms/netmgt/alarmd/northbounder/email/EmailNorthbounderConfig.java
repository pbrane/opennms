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
package org.opennms.netmgt.alarmd.northbounder.email;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Configuration for Email NBI implementation.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
@XmlRootElement(name = "email-northbounder-config")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmailNorthbounderConfig implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The enabled. */
    @XmlElement(name = "enabled", required = false, defaultValue = "false")
    private Boolean m_enabled;

    /** The nagles delay. */
    @XmlElement(name = "nagles-delay", required = false, defaultValue = "1000")
    private Integer m_naglesDelay;

    /** The batch size. */
    @XmlElement(name = "batch-size", required = false, defaultValue = "100")
    private Integer m_batchSize;

    /** The queue size. */
    @XmlElement(name = "queue-size", required = false, defaultValue = "300000")
    private Integer m_queueSize;

    /** The Email destination. */
    @XmlElement(name = "destination")
    private List<EmailDestination> m_destinations = new ArrayList<>();

    /** The UEIs. */
    @XmlElement(name = "uei", required = false)
    private List<String> m_ueis;

    /**
     * Gets the Email destinations.
     *
     * @return the Email destinations
     */
    public List<EmailDestination> getEmailDestinations() {
        return m_destinations;
    }

    /**
     * Sets the Email destinations.
     *
     * @param destinations the new Email destinations
     */
    public void setDestinations(List<EmailDestination> destinations) {
        m_destinations = destinations;
    }

    /**
     * Gets the UEIs.
     *
     * @return the UEIs
     */
    public List<String> getUeis() {
        return m_ueis;
    }

    /**
     * Sets the UEIs.
     *
     * @param ueis the new UEIs
     */
    public void setUeis(List<String> ueis) {
        m_ueis = ueis;
    }

    /**
     * Gets the nagles delay.
     *
     * @return the nagles delay
     */
    public Integer getNaglesDelay() {
        return m_naglesDelay == null ? 1000 : m_naglesDelay;
    }

    /**
     * Sets the nagles delay.
     *
     * @param naglesDelay the new nagles delay
     */
    public void setNaglesDelay(Integer naglesDelay) {
        m_naglesDelay = naglesDelay;
    }

    /**
     * Gets the batch size.
     *
     * @return the batch size
     */
    public Integer getBatchSize() {
        return m_batchSize == null ? 100 : m_batchSize;
    }

    /**
     * Sets the batch size.
     *
     * @param batchSize the new batch size
     */
    public void setBatchSize(Integer batchSize) {
        m_batchSize = batchSize;
    }

    /**
     * Gets the queue size.
     *
     * @return the queue size
     */
    public Integer getQueueSize() {
        return m_queueSize == null ? 300000 : m_queueSize;
    }

    /**
     * Sets the queue size.
     *
     * @param alarmQueueSize the new queue size
     */
    public void setQueueSize(Integer alarmQueueSize) {
        m_queueSize = alarmQueueSize;
    }

    /**
     * Checks if is enabled.
     *
     * @return the boolean
     */
    public Boolean isEnabled() {
        return m_enabled == null ? Boolean.FALSE : m_enabled;
    }

    /**
     * Sets the enabled.
     *
     * @param enabled the new enabled
     */
    public void setEnabled(Boolean enabled) {
        m_enabled = enabled;
    }

    /**
     * Gets a specific Email destination.
     *
     * @param destinationName the Email destination name
     * @return the Email destination object
     */
    public EmailDestination getEmailDestination(String destinationName) {
        for (EmailDestination destination : m_destinations) {
            if (destination.getName().equals(destinationName)) {
                return destination;
            }
        }
        return null;
    }

    /**
     * Adds a specific email destination.
     * <p>If there is a destination with the same name, the existing one will be overridden.</p>
     *
     * @param emailDestination the Email destination object
     */
    public void addEmailDestination(EmailDestination emailDestination) {
        int index = -1;
        for (int i = 0; i < m_destinations.size(); i++) {
            if (m_destinations.get(i).getName().equals(emailDestination.getName())) {
                index = i;
                break;
            }
        }
        if (index > -1) {
            m_destinations.remove(index);
            m_destinations.add(index, emailDestination);
        } else {
            m_destinations.add(emailDestination);
        }
    }

    /**
     * Removes a specific email destination.
     *
     * @param destinationName the destination name
     * @return true, if successful
     */
    public boolean removeEmailDestination(String destinationName) {
        int index = -1;
        for (int i = 0; i < m_destinations.size(); i++) {
            if (m_destinations.get(i).getName().equals(destinationName)) {
                index = i;
                break;
            }
        }
        if (index > -1) {
            m_destinations.remove(index);
            return true;
        }
        return false;
    }

}