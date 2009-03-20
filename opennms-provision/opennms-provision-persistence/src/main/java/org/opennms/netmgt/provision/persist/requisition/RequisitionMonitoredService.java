//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.01.29 at 01:15:48 PM EST 
//


package org.opennms.netmgt.provision.persist.requisition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder = { "m_categories" })
@XmlRootElement(name="monitored-service")
public class RequisitionMonitoredService {

    @XmlElement(name="category")
    protected List<RequisitionCategory> m_categories = new ArrayList<RequisitionCategory>();;
    
    @XmlAttribute(name="service-name", required=true)
    protected String m_serviceName;

    @XmlTransient
    public int getCategoryCount() {
        return (m_categories == null) ? 0 : m_categories.size();
    }

    /* backwards compatibility with ModelImport */
    @XmlTransient
    public RequisitionCategory[] getCategory() {
        return m_categories.toArray(new RequisitionCategory[] {});
    }

    public List<RequisitionCategory> getCategories() {
        if (m_categories == null) {
            m_categories = new ArrayList<RequisitionCategory>();
        }
        return m_categories;
    }

    public void setCategories(List<RequisitionCategory> categories) {
        m_categories = categories;
    }

    public void deleteCategory(RequisitionCategory category) {
        if (m_categories != null) {
            Iterator<RequisitionCategory> i = m_categories.iterator();
            while (i.hasNext()) {
                RequisitionCategory cat = i.next();
                if (cat.getName().equals(category.getName())) {
                    i.remove();
                    break;
                }
            }
        }
    }

    public void deleteCategory(String category) {
        if (m_categories != null) {
            Iterator<RequisitionCategory> i = m_categories.iterator();
            while (i.hasNext()) {
                RequisitionCategory cat = i.next();
                if (cat.getName().equals(category)) {
                    i.remove();
                    break;
                }
            }
        }
    }

    public void insertCategory(RequisitionCategory category) {
        Iterator<RequisitionCategory> iterator = m_categories.iterator();
        while (iterator.hasNext()) {
            RequisitionCategory existing = iterator.next();
            if (existing.getName().equals(category.getName())) {
                iterator.remove();
            }
        }
        m_categories.add(0, category);
    }

    public String getServiceName() {
        return m_serviceName;
    }

    public void setServiceName(String value) {
        m_serviceName = value;
    }

}
