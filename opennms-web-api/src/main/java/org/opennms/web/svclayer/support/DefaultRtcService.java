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
package org.opennms.web.svclayer.support;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.opennms.netmgt.dao.api.MonitoredServiceDao;
import org.opennms.netmgt.dao.api.OutageDao;
import org.opennms.netmgt.model.OnmsCriteria;
import org.opennms.netmgt.model.OnmsMonitoredService;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.model.OnmsOutage;
import org.opennms.web.svclayer.RtcService;
import org.opennms.web.svclayer.model.RtcNode;
import org.opennms.web.svclayer.model.RtcNodeModel;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;


/**
 * <p>DefaultRtcService class.</p>
 *
 * @author <a href="mailto:dj@opennms.org">DJ Gregor</a>
 * @version $Id: $
 * @since 1.8.1
 */
public class DefaultRtcService implements RtcService, InitializingBean {
    private MonitoredServiceDao m_monitoredServiceDao;
    private OutageDao m_outageDao;

    /**
     * <p>getNodeList</p>
     *
     * @return a {@link org.opennms.web.svclayer.model.RtcNodeModel} object.
     */
    @Override
    public RtcNodeModel getNodeList() {
        OnmsCriteria serviceCriteria = createServiceCriteria();
        OnmsCriteria outageCriteria = createOutageCriteria();
        
        return getNodeListForCriteria(serviceCriteria, outageCriteria);
    }
    
    /** {@inheritDoc} */
    @Override
    public RtcNodeModel getNodeListForCriteria(OnmsCriteria serviceCriteria, OnmsCriteria outageCriteria) {
        serviceCriteria.addOrder(Order.asc("node.label"));
        serviceCriteria.addOrder(Order.asc("node.id"));
        serviceCriteria.addOrder(Order.asc("ipInterface.ipAddress"));
        serviceCriteria.addOrder(Order.asc("serviceType.name"));

        Date periodEnd = new Date(System.currentTimeMillis());
        Date periodStart = new Date(periodEnd.getTime() - (24 * 60 * 60 * 1000));
        
        Disjunction disjunction = Restrictions.disjunction();
        disjunction.add(Restrictions.isNull("ifRegainedService"));
        disjunction.add(Restrictions.ge("ifLostService", periodStart));
        disjunction.add(Restrictions.ge("ifRegainedService", periodStart));
        outageCriteria.add(disjunction);
        
        outageCriteria.addOrder(Order.asc("monitoredService"));
        outageCriteria.addOrder(Order.asc("ifLostService"));
        
        List<OnmsMonitoredService> services = m_monitoredServiceDao.findMatching(serviceCriteria);
        List<OnmsOutage> outages = m_outageDao.findMatching(outageCriteria);
        
        Map<OnmsMonitoredService, Long> serviceDownTime = calculateServiceDownTime(periodEnd, periodStart, outages);
        
        RtcNodeModel model = new RtcNodeModel();
        
        OnmsNode lastNode = null;
        int serviceCount = 0;
        int serviceDownCount = 0;
        long downMillisCount = 0;
        for (OnmsMonitoredService service : services) {
            if (!service.getIpInterface().getNode().equals(lastNode) && lastNode != null) {
                Double availability = calculateAvailability(serviceCount, downMillisCount, periodEnd.getTime() - periodStart.getTime());
                
                model.addNode(new RtcNode(lastNode, serviceCount, serviceDownCount, availability));
                
                serviceCount = 0;
                serviceDownCount = 0;
                downMillisCount = 0;
            }
            
            serviceCount++;
            if (service.isDown()) {
                serviceDownCount++;
            }
            
            Long downMillis = serviceDownTime.get(service);
            if  (downMillis == null) {
                // This service had 100% availability over the period, no downtime
            } else {
                downMillisCount += downMillis;
            }
            
            lastNode = service.getIpInterface().getNode();
        }
        if (lastNode != null) {
            Double availability = calculateAvailability(serviceCount, downMillisCount, periodEnd.getTime() - periodStart.getTime());
            
            model.addNode(new RtcNode(lastNode, serviceCount, serviceDownCount, availability));
        }
        
        return model;
    }

    /**
     * <p>Create a {@link OnmsCriteria} that will select {@link OnmsOutage} objects.</p>
     * 
     * <p>CAUTION: This criteria must contain a JOIN alias to the node table called 'node' so that the
     * {@link org.opennms.dashboard.server.CriteriaAddingVisitor.addCriteriaForCategories(OnmsCriteria, String...)}
     * function can add a category-based restriction on matching node IDs to the criteria.</p>
     *
     * @return a {@link org.opennms.netmgt.model.OnmsCriteria} object.
     */
    @Override
    public OnmsCriteria createOutageCriteria() {
        OnmsCriteria outageCriteria = new OnmsCriteria(OnmsOutage.class, "outage");

        outageCriteria.createAlias("monitoredService", "monitoredService", OnmsCriteria.INNER_JOIN);
        outageCriteria.add(Restrictions.eq("monitoredService.status", "A"));
        outageCriteria.createAlias("monitoredService.ipInterface", "ipInterface", OnmsCriteria.INNER_JOIN);
        outageCriteria.add(Restrictions.ne("ipInterface.isManaged", "D"));
        // Required for {@link org.opennms.dashboard.server.CriteriaAddingVisitor.addCriteriaForCategories(OnmsCriteria, String...)}
        // to work properly.
        outageCriteria.createAlias("monitoredService.ipInterface.node", "node", OnmsCriteria.INNER_JOIN);
        outageCriteria.add(Restrictions.ne("node.type", "D"));
        outageCriteria.add(Restrictions.isNull("perspective"));

        return outageCriteria;
    }

    /**
     * <p>Create a {@link OnmsCriteria} that will select {@link OnmsMonitoredService} objects.</p>
     * 
     * <p>CAUTION: This criteria must contain a JOIN alias to the node table called 'node' so that the
     * {@link org.opennms.dashboard.server.CriteriaAddingVisitor.addCriteriaForCategories(OnmsCriteria, String...)}
     * function can add a category-based restriction on matching node IDs to the criteria.</p>
     *
     * @return a {@link org.opennms.netmgt.model.OnmsCriteria} object.
     */
    @Override
    public OnmsCriteria createServiceCriteria() {
        OnmsCriteria serviceCriteria = new OnmsCriteria(OnmsMonitoredService.class, "monitoredService");

        serviceCriteria.add(Restrictions.eq("monitoredService.status", "A"));
        serviceCriteria.createAlias("ipInterface", "ipInterface", OnmsCriteria.INNER_JOIN);
        serviceCriteria.add(Restrictions.ne("ipInterface.isManaged", "D"));
        // Required for {@link org.opennms.dashboard.server.CriteriaAddingVisitor.addCriteriaForCategories(OnmsCriteria, String...)}
        // to work properly.
        serviceCriteria.createAlias("ipInterface.node", "node", OnmsCriteria.INNER_JOIN);
        serviceCriteria.add(Restrictions.ne("node.type", "D"));
        serviceCriteria.createAlias("serviceType", "serviceType", OnmsCriteria.INNER_JOIN);
        serviceCriteria.createAlias("currentOutages", "currentOutages", OnmsCriteria.INNER_JOIN);
        
        return serviceCriteria;
    }

    private static Map<OnmsMonitoredService, Long> calculateServiceDownTime(Date periodEnd, Date periodStart, List<OnmsOutage> outages) {
        Map<OnmsMonitoredService, Long> map = new HashMap<OnmsMonitoredService, Long>();
        for (OnmsOutage outage : outages) {
            if (map.get(outage.getMonitoredService()) == null) {
                map.put(outage.getMonitoredService(), 0L);
            }
            
            Date begin;
            if (outage.getIfLostService().before(periodStart)) {
                begin = periodStart;
            } else if (outage.getIfLostService().after(periodEnd)) {
                LoggerFactory.getLogger(DefaultRtcService.class).warn("Outage beginning is after period end {}, discarding outage: {}", periodEnd, outage.toString());
                continue;
            } else {
                begin = outage.getIfLostService();
            }
            
            Date end;
            if (outage.getIfRegainedService() == null) {
                // If the outage hasn't ended yet, use the end of the period as the end time
                end = periodEnd;
            } else if (outage.getIfRegainedService().after(periodEnd)) {
                // If the outage ended after the end of the period, use the end of the period as the end time
                end = periodEnd;
            } else {
                end = outage.getIfRegainedService();
            }

            if (begin.after(end)) {
                LoggerFactory.getLogger(DefaultRtcService.class).warn("Outage beginning is after outage end inside period {} to {}, discarding outage: {}", periodStart, periodEnd, outage.toString());
                continue;
            } else {
                Long count = map.get(outage.getMonitoredService());
                count += (end.getTime() - begin.getTime());
                map.put(outage.getMonitoredService(), count);
            }
        }
        return map;
    }

    private static Double calculateAvailability(int serviceCount, long downMillisCount, long periodLength) {
        long upMillis = ((long)serviceCount * periodLength) - downMillisCount;

        return ((double) upMillis / (double) (serviceCount * periodLength));
    }
    
    /**
     * <p>afterPropertiesSet</p>
     */
    @Override
    public void afterPropertiesSet() {
        Assert.state(m_monitoredServiceDao != null, "property monitoredServiceDao must be set and non-null");
        Assert.state(m_outageDao != null, "property outageDao must be set and non-null");
    }
    
    /**
     * <p>getMonitoredServiceDao</p>
     *
     * @return a {@link org.opennms.netmgt.dao.api.MonitoredServiceDao} object.
     */
    public MonitoredServiceDao getMonitoredServiceDao() {
        return m_monitoredServiceDao;
    }
    /**
     * <p>setMonitoredServiceDao</p>
     *
     * @param monitoredServiceDao a {@link org.opennms.netmgt.dao.api.MonitoredServiceDao} object.
     */
    public void setMonitoredServiceDao(MonitoredServiceDao monitoredServiceDao) {
        m_monitoredServiceDao = monitoredServiceDao;
    }
    /**
     * <p>getOutageDao</p>
     *
     * @return a {@link org.opennms.netmgt.dao.api.OutageDao} object.
     */
    public OutageDao getOutageDao() {
        return m_outageDao;
    }
    /**
     * <p>setOutageDao</p>
     *
     * @param outageDao a {@link org.opennms.netmgt.dao.api.OutageDao} object.
     */
    public void setOutageDao(OutageDao outageDao) {
        m_outageDao = outageDao;
    }
}
