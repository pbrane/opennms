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
package org.opennms.netmgt.model;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Type;
import org.opennms.core.network.InetAddressXmlAdapter;
import org.opennms.netmgt.model.monitoringLocations.OnmsMonitoringLocation;

import com.google.common.base.MoreObjects;


/**
 * <p>OnmsOutage class.</p>
 *
 * @hibernate.class table="outages"
 */
@XmlRootElement(name="outage")
@Entity
@Table(name="outages")
@Filter(name=FilterManager.AUTH_FILTER_NAME, condition="exists (select distinct x.nodeid from node x join category_node cn on x.nodeid = cn.nodeid join category_group cg on cn.categoryId = cg.categoryId join ipInterface on x.nodeid = ipInterface.nodeid join ifServices on ipInterface.id = ifServices.ipInterfaceId where ifServices.id = ifServiceId and cg.groupId in (:userGroups))")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OnmsOutage implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3846398168228820151L;

    /** identifier field */
    private Integer m_id;

    /** persistent field */
    private Date m_ifLostService;

    /** nullable persistent field */
    private Date m_ifRegainedService;

    /** persistent field */
    private OnmsEvent m_serviceRegainedEvent;

    /** persistent field */
    private OnmsEvent m_serviceLostEvent;

    /** persistent field */
    private OnmsMonitoredService m_monitoredService;
    
    /** persistent field */
    private Date m_suppressTime;
    
    /** persistent field */
    private String m_suppressedBy;

    /** persistent field */
    private OnmsMonitoringLocation m_perspective;

    /**
     * full constructor
     *
     * @param ifLostService a {@link java.util.Date} object.
     * @param ifRegainedService a {@link java.util.Date} object.
     * @param eventBySvcRegainedEvent a {@link org.opennms.netmgt.model.OnmsEvent} object.
     * @param eventBySvcLostEvent a {@link org.opennms.netmgt.model.OnmsEvent} object.
     * @param monitoredService a {@link org.opennms.netmgt.model.OnmsMonitoredService} object.
     * @param suppressTime a {@link java.util.Date} object.
     * @param suppressedBy a {@link java.lang.String} object.
     */
    public OnmsOutage(Date ifLostService, Date ifRegainedService, OnmsEvent eventBySvcRegainedEvent, OnmsEvent eventBySvcLostEvent, OnmsMonitoredService monitoredService, Date suppressTime, String suppressedBy) {
        m_ifLostService = ifLostService;
        m_ifRegainedService = ifRegainedService;
        m_serviceRegainedEvent = eventBySvcRegainedEvent;
        m_serviceLostEvent = eventBySvcLostEvent;
        m_monitoredService = monitoredService;
        m_suppressTime = suppressTime;
        m_suppressedBy = suppressedBy;
        
    }

    /**
     * default constructor
     */
    public OnmsOutage() {
    }

    /**
     */
    public OnmsOutage(Date ifLostService, OnmsMonitoredService monitoredService) {
        m_ifLostService = ifLostService;
        m_monitoredService = monitoredService;
    }

    public OnmsOutage(Date ifLostService, Date ifRegainedService, OnmsMonitoredService monitoredService) {
        m_ifLostService = ifLostService;
        m_ifRegainedService = ifRegainedService;
        m_monitoredService = monitoredService;
    }

    /**
     * minimal constructor
     *
     * @param ifLostService a {@link java.util.Date} object.
     * @param eventBySvcLostEvent a {@link org.opennms.netmgt.model.OnmsEvent} object.
     * @param monitoredService a {@link org.opennms.netmgt.model.OnmsMonitoredService} object.
     */
    public OnmsOutage(Date ifLostService, OnmsEvent eventBySvcLostEvent, OnmsMonitoredService monitoredService) {
        m_ifLostService = ifLostService;
        m_serviceLostEvent = eventBySvcLostEvent;
        m_monitoredService = monitoredService;
    }

    /**
     * <p>getId</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    @Id
    @XmlAttribute(name="id")
    @Column(name="outageId", nullable=false)
    @SequenceGenerator(name="outageSequence", sequenceName="outageNxtId")
    @GeneratedValue(generator="outageSequence")
    public Integer getId() {
        return m_id;
    }

    /**
     * <p>setId</p>
     *
     * @param outageId a {@link java.lang.Integer} object.
     */
    public void setId(Integer outageId) {
        m_id = outageId;
    }

    // @XmlTransient
    /**
     * <p>getMonitoredService</p>
     *
     * @return a {@link org.opennms.netmgt.model.OnmsMonitoredService} object.
     */
    @ManyToOne
    @JoinColumn(name="ifserviceId")
    public OnmsMonitoredService getMonitoredService() {
        return m_monitoredService;
    }

    /**
     * <p>setMonitoredService</p>
     *
     * @param monitoredService a {@link org.opennms.netmgt.model.OnmsMonitoredService} object.
     */
    public void setMonitoredService(OnmsMonitoredService monitoredService) {
        m_monitoredService = monitoredService;
    }

    
    /**
     * <p>getIfLostService</p>
     *
     * @return a {@link java.util.Date} object.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ifLostService", nullable=false)
    public Date getIfLostService() {
        return m_ifLostService;
    }

    /**
     * <p>setIfLostService</p>
     *
     * @param ifLostService a {@link java.util.Date} object.
     */
    public void setIfLostService(Date ifLostService) {
        m_ifLostService = ifLostService;
    }

    /**
     * <p>getServiceLostEvent</p>
     *
     * @return a {@link org.opennms.netmgt.model.OnmsEvent} object.
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="svcLostEventId")
    public OnmsEvent getServiceLostEvent() {
        return m_serviceLostEvent;
    }

    /**
     * <p>setServiceLostEvent</p>
     *
     * @param svcLostEvent a {@link org.opennms.netmgt.model.OnmsEvent} object.
     */
    public void setServiceLostEvent(OnmsEvent svcLostEvent) {
        m_serviceLostEvent = svcLostEvent;
    }


    /**
     * <p>getIfRegainedService</p>
     *
     * @return a {@link java.util.Date} object.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ifRegainedService")
    public Date getIfRegainedService() {
        return m_ifRegainedService;
    }
    
    /**
     * <p>setIfRegainedService</p>
     *
     * @param ifRegainedService a {@link java.util.Date} object.
     */
    public void setIfRegainedService(Date ifRegainedService) {
        m_ifRegainedService = ifRegainedService;
    }

    /**
     * <p>getServiceRegainedEvent</p>
     *
     * @return a {@link org.opennms.netmgt.model.OnmsEvent} object.
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="svcRegainedEventId")
    public OnmsEvent getServiceRegainedEvent() {
        return m_serviceRegainedEvent;
    }

    /**
     * <p>setServiceRegainedEvent</p>
     *
     * @param svcRegainedEvent a {@link org.opennms.netmgt.model.OnmsEvent} object.
     */
    public void setServiceRegainedEvent(OnmsEvent svcRegainedEvent) {
        m_serviceRegainedEvent = svcRegainedEvent;
    }

    /**
     * <p>getSuppressTime</p>
     *
     * @return a {@link java.util.Date} object.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="suppressTime")
    public Date getSuppressTime(){
    	return m_suppressTime;
    }
    
    /**
     * <p>setSuppressTime</p>
     *
     * @param timeToSuppress a {@link java.util.Date} object.
     */
    public void setSuppressTime(Date timeToSuppress){
    	m_suppressTime = timeToSuppress;
    }
    
    
    /**
     * <p>getSuppressedBy</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Column(name="suppressedBy")
    public String getSuppressedBy(){
    	return m_suppressedBy;
    }
    
    /**
     * <p>setSuppressedBy</p>
     *
     * @param suppressorMan a {@link java.lang.String} object.
     */
    public void setSuppressedBy(String suppressorMan){
    	m_suppressedBy = suppressorMan;
    }


    /**
     * This method is necessary for CXF to be able to introspect
     * the type of {@link OnmsNode} parameters.
     *
     * @return a {@link OnmsNode} object.
     */
    @Transient
    @XmlTransient
    @JsonIgnore
    public OnmsNode getNode() {
        return getMonitoredService().getIpInterface().getNode();
    }

    /**
     * This method is necessary for CXF to be able to introspect
     * the type of {@link OnmsNode} parameters.
     */
    public void setNode(OnmsNode node) {
        OnmsMonitoredService service = getMonitoredService();
        if (service == null) {
            service = new OnmsMonitoredService();
            setMonitoredService(service);
        }
        OnmsIpInterface intf = service.getIpInterface();
        if (intf == null) {
            intf = new OnmsIpInterface();
            service.setIpInterface(intf);
        }
        intf.setNode(node);
    }

    /**
     * <p>getNodeId</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    @Transient
    @XmlElement(name="nodeId")
    public Integer getNodeId(){
    	return getMonitoredService().getNodeId();
    }

    /**
     * <p>getNodeLabel</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Transient
    @XmlElement(name="nodeLabel")
    public String getNodeLabel(){
        return getMonitoredService().getIpInterface().getNode().getLabel();
    }

    /**
     * <p>getForeignSource</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Transient
    @XmlElement(name="foreignSource")
    public String getForeignSource(){
        return getMonitoredService().getIpInterface().getNode().getForeignSource();
    }

    /**
     * <p>getForeignId</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Transient
    @XmlElement(name="foreignId")
    public String getForeignId(){
        return getMonitoredService().getIpInterface().getNode().getForeignId();
    }

    /**
     * <p>getLocationName</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Transient
    @XmlElement(name="locationName")
    public String getLocationName(){
        return getMonitoredService().getIpInterface().getNode().getLocation().getLocationName();
    }

    /**
     * <p>getIpAddress</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Transient
    @XmlElement(name="ipAddress")
    @Type(type="org.opennms.netmgt.model.InetAddressUserType")
    @XmlJavaTypeAdapter(InetAddressXmlAdapter.class)
    public InetAddress getIpAddress() {
        return getMonitoredService().getIpAddress();
    }

    /**
     * <p>getIpAddressAsString</p>
     *
     * @return a {@link java.lang.String} object.
     * @deprecated use getIpAddress
     */
    @Transient
    @XmlTransient
    @JsonIgnore
    public String getIpAddressAsString() {
        return getMonitoredService().getIpAddressAsString();
    }

    /**
     * <p>getServiceId</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    @Transient
    public Integer getServiceId() {
    	return getMonitoredService().getServiceId();
    }

    /**
     * This method is necessary for CXF to be able to introspect
     * the type of {@link OnmsNode} parameters.
     *
     * @return a {@link OnmsServiceType} object.
     */
    @Transient
    @XmlTransient
    @JsonIgnore
    public OnmsServiceType getServiceType() {
        return getMonitoredService().getServiceType();
    }

    /**
     * This method is necessary for CXF to be able to introspect
     * the type of {@link OnmsServiceType} parameters.
     */
    public void setServiceType(OnmsServiceType type) {
        OnmsMonitoredService service = getMonitoredService();
        if (service == null) {
            service = new OnmsMonitoredService();
            setMonitoredService(service);
        }
        service.setServiceType(type);
    }

    /**
     * Monitoring perspective that this outage is associated with.
     */
    @JsonSerialize(using=MonitoringLocationJsonSerializer.class)
    @JsonDeserialize(using=MonitoringLocationJsonDeserializer.class)
    @XmlElement(name="perspective")
    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="perspective")
    @XmlJavaTypeAdapter(MonitoringLocationIdAdapter.class)
    public OnmsMonitoringLocation getPerspective() {
        return m_perspective;
    }

    /**
     * Set the monitoring perspective for this outage.
     */
    public void setPerspective(OnmsMonitoringLocation perspective) {
        m_perspective = perspective;
    }

    /**
     * <p>toString</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("outageId", m_id)
            .add("ifLostService", m_ifLostService)
            .add("ifRegainedService", m_ifRegainedService)
            .add("ifRegainedServiceEvent", m_serviceRegainedEvent)
            .add("service", m_monitoredService)
            .add("suppressedBy", m_suppressedBy)
            .add("suppressTime", m_suppressTime)
            .add("perspective", m_perspective)
            .toString();
    }

}
