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

import static org.opennms.netmgt.events.api.EventConstants.APPLICATION_CHANGED_EVENT_UEI;
import static org.opennms.netmgt.events.api.EventConstants.APPLICATION_DELETED_EVENT_UEI;
import static org.opennms.netmgt.events.api.EventConstants.PARM_APPLICATION_ID;
import static org.opennms.netmgt.events.api.EventConstants.PARM_APPLICATION_NAME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.opennms.core.utils.WebSecurityUtils;
import org.opennms.netmgt.dao.api.ApplicationDao;
import org.opennms.netmgt.dao.api.MonitoredServiceDao;
import org.opennms.netmgt.dao.api.MonitoringLocationDao;
import org.opennms.netmgt.events.api.EventConstants;
import org.opennms.netmgt.events.api.EventProxy;
import org.opennms.netmgt.events.api.EventProxyException;
import org.opennms.netmgt.model.OnmsApplication;
import org.opennms.netmgt.model.OnmsMonitoredService;
import org.opennms.netmgt.model.monitoringLocations.OnmsMonitoringLocation;
import org.opennms.netmgt.model.events.EventBuilder;
import org.opennms.netmgt.xml.event.Event;
import org.opennms.web.svclayer.AdminApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Ordering;

/**
 * <p>DefaultAdminApplicationService class.</p>
 *
 * @author <a href="mailto:dj@opennms.org">DJ Gregor</a>
 * @version $Id: $
 * @since 1.8.1
 */
public class DefaultAdminApplicationService implements AdminApplicationService {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultAdminApplicationService.class);

    private ApplicationDao m_applicationDao;
    private MonitoredServiceDao m_monitoredServiceDao;
    private EventProxy m_eventProxy;
    private MonitoringLocationDao m_monitoringLocationDao;

    /** {@inheritDoc} */
    @Override
    public ApplicationAndMemberServices getApplication(String applicationIdString) {
        if (applicationIdString == null) {
            throw new IllegalArgumentException("applicationIdString must not be null");
        }

        final OnmsApplication application = findApplication(applicationIdString);
        
        final Collection<OnmsMonitoredService> memberServices = m_monitoredServiceDao.findByApplication(application);

        for (OnmsMonitoredService service : memberServices) {
            m_applicationDao.initialize(service.getIpInterface());
            m_applicationDao.initialize(service.getIpInterface().getNode());
        }

        return new ApplicationAndMemberServices(application, memberServices, application.getPerspectiveLocations());
    }

    /**
     * <p>findAllMonitoredServices</p>
     *
     * @return a {@link java.util.List} object.
     */
    @Override
    public List<OnmsMonitoredService> findAllMonitoredServices() {
        List<OnmsMonitoredService> list =
            new ArrayList<OnmsMonitoredService>(m_monitoredServiceDao.findAll());
        Collections.sort(list);
        
        return list;
    }

    @Override
    public List<OnmsMonitoringLocation> findAllMonitoringLocations() {
        return m_monitoringLocationDao.findAll().stream().sorted(Ordering.natural().nullsFirst().onResultOf(OnmsMonitoringLocation::getLocationName)).collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    public EditModel findApplicationAndAllMonitoredServices(String applicationIdString) {
        final ApplicationAndMemberServices app = getApplication(applicationIdString);
        return new EditModel(
                app.getApplication(),
                findAllMonitoredServices(),
                app.getMemberServices(),
                findAllMonitoringLocations(),
                app.getMemberLocations());
    }

    /**
     * <p>getApplicationDao</p>
     *
     * @return a {@link org.opennms.netmgt.dao.api.ApplicationDao} object.
     */
    public ApplicationDao getApplicationDao() {
        return m_applicationDao;
    }

    /**
     * <p>setApplicationDao</p>
     *
     * @param dao a {@link org.opennms.netmgt.dao.api.ApplicationDao} object.
     */
    public void setApplicationDao(ApplicationDao dao) {
        m_applicationDao = dao;
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

    public void setMonitoringLocationDao(MonitoringLocationDao monitoringLocationDao) {
        m_monitoringLocationDao = monitoringLocationDao;
    }

    public EventProxy getEventProxy() {
        return m_eventProxy;
    }

    public void setEventProxy(EventProxy eventProxy) {
        this.m_eventProxy = eventProxy;
    }

    /**
     * <p>performEdit</p>
     *
     * @param applicationIdString a {@link java.lang.String} object.
     * @param editAction a {@link java.lang.String} object.
     * @param toAdd an array of {@link java.lang.String} objects.
     * @param toDelete an array of {@link java.lang.String} objects.
     */
    @Override
    public void performEditServices(String applicationIdString, String editAction, String[] toAdd, String[] toDelete) {
        if (applicationIdString == null) {
            throw new IllegalArgumentException("applicationIdString cannot be null");
        }
        if (editAction == null) {
            throw new IllegalArgumentException("editAction cannot be null");
        }
        
        OnmsApplication application = findApplication(applicationIdString); 
       
        if (editAction.contains("Add")) { // @i18n
            if (toAdd == null || toAdd.length == 0) {
                return;
            }
           
            for (String idString : toAdd) {
                Integer id;
                try {
                    id = WebSecurityUtils.safeParseInt(idString);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("toAdd element '"
                                                       + idString
                                                       + "' is not an integer");
                }
                OnmsMonitoredService service = m_monitoredServiceDao.get(id);
                if (service == null) {
                    throw new IllegalArgumentException("monitored service with "
                                                       + "id of " + id
                                                       + "could not be found");
                }
                if (service.getApplications().contains(application)) {
                    throw new IllegalArgumentException("monitored service with "
                                                       + "id of " + id
                                                       + "is already a member of "
                                                       + "application "
                                                       + application.getName());
                }
                
                service.addApplication(application);
                m_monitoredServiceDao.save(service);
                sendEvent(application, EventConstants.APPLICATION_CHANGED_EVENT_UEI);
            }
       } else if (editAction.contains("Remove")) { // @i18n
            if (toDelete == null || toDelete.length == 0) {
                return;
            }
            
            for (String idString : toDelete) {
                Integer id;
                try {
                    id = WebSecurityUtils.safeParseInt(idString);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("toDelete element '"
                                                       + idString
                                                       + "' is not an integer");
                }
                OnmsMonitoredService service = m_monitoredServiceDao.get(id);
                if (service == null) {
                    throw new IllegalArgumentException("monitored service with "
                                                       + "id of " + id
                                                       + "could not be found");
                }
                if (!service.getApplications().contains(application)) {
                    throw new IllegalArgumentException("monitored service with "
                                                       + "id of " + id
                                                       + "is not a member of "
                                                       + "application "
                                                       + application.getName());
                }
                
                service.removeApplication(application);
                m_monitoredServiceDao.save(service);
            }

            m_applicationDao.save(application);
            sendEvent(application, EventConstants.APPLICATION_CHANGED_EVENT_UEI);
       } else {
           throw new IllegalArgumentException("editAction of '"
                                              + editAction
                                              + "' is not allowed");
       }
    }

    public void performEditLocations(final String applicationIdString, final String editAction, final String[] locationAdds, final String[] locationDeletes) {
        if (applicationIdString == null) {
            throw new IllegalArgumentException("applicationIdString cannot be null");
        }
        if (editAction == null) {
            throw new IllegalArgumentException("editAction cannot be null");
        }

        OnmsApplication application = findApplication(applicationIdString);

        if (editAction.contains("Add")) {
            if (locationAdds == null) {
                return;
            }

            for (final String locationName : locationAdds) {
                if (locationName == null) {
                    continue;
                }

                final OnmsMonitoringLocation location = m_monitoringLocationDao.get(locationName);

                if (location == null) {
                    throw new IllegalArgumentException("location with "
                            + "name of " + locationName
                            + "could not be found");
                }

                application.addPerspectiveLocation(location);
            }

            m_applicationDao.save(application);
            sendEvent(application, EventConstants.APPLICATION_CHANGED_EVENT_UEI);

        } else if (editAction.contains("Remove")) {
            if (locationDeletes == null) {
                return;
            }

            for (final String locationName : locationDeletes) {
                if (locationName == null) {
                    continue;
                }

                final OnmsMonitoringLocation location = m_monitoringLocationDao.get(locationName);

                if (location == null) {
                    throw new IllegalArgumentException("location with "
                            + "name of " + locationName
                            + "could not be found");
                }

                application.getPerspectiveLocations().remove(location);
            }

            m_applicationDao.save(application);
            sendEvent(application, EventConstants.APPLICATION_CHANGED_EVENT_UEI);

        } else {
            throw new IllegalArgumentException("editAction of '"
                    + editAction
                    + "' is not allowed");
        }
    }

    private void sendEvent(final OnmsApplication application, final String uei) {
        final Event event = new EventBuilder(uei, "Web UI")
                .addParam(PARM_APPLICATION_ID, application.getId())
                .addParam(PARM_APPLICATION_NAME, application.getName())
                .getEvent();
        try {
            m_eventProxy.send(event);
        } catch (final EventProxyException e) {
            LOG.warn("Failed to send event {}: {}", event.getUei(), e.getMessage(), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public OnmsApplication addNewApplication(String name) {
        OnmsApplication application = new OnmsApplication();
        application.setName(name);
        m_applicationDao.save(application);
        sendEvent(application, EventConstants.APPLICATION_CREATED_EVENT_UEI);
        return application;
    }

    /**
     * <p>findAllApplications</p>
     *
     * @return a {@link java.util.List} object.
     */
    @Override
    public List<OnmsApplication> findAllApplications() {
        Collection<OnmsApplication> applications = m_applicationDao.findAll();
        List<OnmsApplication> sortedApplications =
            new ArrayList<OnmsApplication>(applications);
        Collections.sort(sortedApplications);

        return sortedApplications;
    }

    /** {@inheritDoc} */
    @Override
    public void removeApplication(String applicationIdString) {
        OnmsApplication application = findApplication(applicationIdString);
        m_applicationDao.delete(application);
        sendEvent(application, APPLICATION_DELETED_EVENT_UEI);
    }

    /** {@inheritDoc} */
    @Override
    public List<OnmsApplication> findByMonitoredService(int id) {
        OnmsMonitoredService service = m_monitoredServiceDao.get(id);
        if (service == null) {
            throw new IllegalArgumentException("monitored service with "
                                               + "id of " + id
                                               + " could not be found");
        }
        
        List<OnmsApplication> sortedApplications =
            new ArrayList<OnmsApplication>(service.getApplications());
        Collections.sort(sortedApplications);
        
        return sortedApplications;
    }

    /**
     * <p>performServiceEdit</p>
     *
     * @param ifServiceIdString a {@link java.lang.String} object.
     * @param editAction a {@link java.lang.String} object.
     * @param toAdd an array of {@link java.lang.String} objects.
     * @param toDelete an array of {@link java.lang.String} objects.
     */
    @Override
    public void performServiceEdit(String ifServiceIdString, String editAction,
            String[] toAdd, String[] toDelete) {
        if (ifServiceIdString == null) {
            throw new IllegalArgumentException("ifServiceIdString cannot be null");
        }
        if (editAction == null) {
            throw new IllegalArgumentException("editAction cannot be null");
        }
        
        OnmsMonitoredService service = findService(ifServiceIdString);

        if (editAction.contains("Add")) { // @i18n
            if (toAdd == null) {
                return;
            }
            List<OnmsApplication> changedApplications = new ArrayList<>();
            for (String idString : toAdd) {
                Integer id;
                try {
                    id = WebSecurityUtils.safeParseInt(idString);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("toAdd element '"
                                                       + idString
                                                       + "' is not an integer");
                }
                OnmsApplication application = m_applicationDao.get(id);
                if (application == null) {
                    throw new IllegalArgumentException("application with "
                                                       + "id of " + id
                                                       + " could not be found");
                }
                if (service.getApplications().contains(application)) {
                    throw new IllegalArgumentException("application with "
                                                       + "id of " + id
                                                       + " is already a member of "
                                                       + "service "
                                                       + service.getServiceName());
                }
                service.getApplications().add(application);
                changedApplications.add(application);
            }
            
            m_monitoredServiceDao.save(service);
            changedApplications.forEach(a -> this.sendEvent(a, APPLICATION_CHANGED_EVENT_UEI));
       } else if (editAction.contains("Remove")) { // @i18n
            if (toDelete == null) {
                return;
                //throw new IllegalArgumentException("toDelete cannot be null if editAction is 'Remove'");
            }
            List<OnmsApplication> changedApplications = new ArrayList<>();
            for (String idString : toDelete) {
                Integer id;
                try {
                    id = WebSecurityUtils.safeParseInt(idString);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("toDelete element '"
                                                       + idString
                                                       + "' is not an integer");
                }
                OnmsApplication application = m_applicationDao.get(id);
                if (application == null) {
                    throw new IllegalArgumentException("application with "
                                                       + "id of " + id
                                                       + " could not be found");
                }
                if (!service.getApplications().contains(application)) {
                    throw new IllegalArgumentException("application with "
                                                       + "id of " + id
                                                       + " is not a member of "
                                                       + "service "
                                                       + service.getServiceName());
                }
                service.getApplications().add(application);
                changedApplications.add(application);
            }

            m_monitoredServiceDao.save(service);
            changedApplications.forEach(a -> this.sendEvent(a, APPLICATION_CHANGED_EVENT_UEI));
       } else {
           throw new IllegalArgumentException("editAction of '"
                                              + editAction
                                              + "' is not allowed");
       }
    }


    /** {@inheritDoc} */
    @Override
    public ServiceEditModel findServiceApplications(String ifServiceIdString) {
        if (ifServiceIdString == null) {
            throw new IllegalArgumentException("ifServiceIdString must not be null");
        }

        OnmsMonitoredService service = findService(ifServiceIdString);
        List<OnmsApplication> applications = findAllApplications();
        
        m_monitoredServiceDao.initialize(service.getIpInterface());
        m_monitoredServiceDao.initialize(service.getIpInterface().getNode());
        
        return new ServiceEditModel(service, applications);
    }

    /**
     * <p>findApplication</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link org.opennms.netmgt.model.OnmsApplication} object.
     */
    public OnmsApplication findApplication(String name) {
        int applicationId = -1;
        try {
            applicationId = WebSecurityUtils.safeParseInt(name);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("parameter 'applicationid' "
                                               + "with value '"
                                               + name
                                               + "' could not be parsed "
                                               + "as an integer");
        }

        OnmsApplication application = m_applicationDao.get(applicationId);
        if (application == null) {
            throw new IllegalArgumentException("Could not find application "
                                               + "with application ID "
                                               + applicationId);
        }
        return application;
        }



    private OnmsMonitoredService findService(String ifServiceIdString) {
        int ifServiceId;
        
        try {
            ifServiceId = WebSecurityUtils.safeParseInt(ifServiceIdString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("parameter ifserviceid '"
                                               + ifServiceIdString
                                               + "' is not an integer");
        }
        
        OnmsMonitoredService service = m_monitoredServiceDao.get(ifServiceId);
        if (service == null) {
            throw new IllegalArgumentException("monitored service with "
                                               + "id of " + ifServiceId
                                               + " could not be found");
        }
        
        return service;
    }

    public static class ApplicationAndMemberServices {
        private OnmsApplication m_application;
        private Collection<OnmsMonitoredService> m_memberServices;
        private Collection<OnmsMonitoringLocation> m_memberLocations;

        public ApplicationAndMemberServices(OnmsApplication application, Collection<OnmsMonitoredService> memberServices, Collection<OnmsMonitoringLocation> memberLocations) {
            m_application = application;
            m_memberServices = memberServices;
            m_memberLocations = memberLocations;
        }

        public OnmsApplication getApplication() {
            return m_application;
        }

        public Collection<OnmsMonitoredService> getMemberServices() {
            return m_memberServices;
        }
        public Collection<OnmsMonitoringLocation> getMemberLocations() {
            return m_memberLocations;
        }
    }

    public static class EditModel {
        private OnmsApplication m_application;
        private List<OnmsMonitoredService> m_monitoredServices;
        private List<OnmsMonitoredService> m_sortedMemberServices;
        private List<OnmsMonitoringLocation> m_monitoringLocations;
        private List<OnmsMonitoringLocation> m_sortedMemberLocations;

        public EditModel(OnmsApplication application,
                List<OnmsMonitoredService> monitoredServices,
                Collection<OnmsMonitoredService> memberServices,
                List<OnmsMonitoringLocation> monitoringLocations,
                Collection<OnmsMonitoringLocation> memberLocations) {
            m_application = application;
            m_monitoredServices = monitoredServices;
            m_monitoringLocations = monitoringLocations;

            m_monitoredServices.removeAll(memberServices);
            m_monitoringLocations.removeAll(memberLocations);

            m_sortedMemberServices = new ArrayList<OnmsMonitoredService>(memberServices);
            Collections.sort(m_sortedMemberServices);

            m_sortedMemberLocations = new ArrayList<OnmsMonitoringLocation>(memberLocations);
            Collections.sort(m_sortedMemberLocations, Ordering.natural().nullsFirst().onResultOf(OnmsMonitoringLocation::getLocationName));
        }

        public OnmsApplication getApplication() {
            return m_application;
        }

        public List<OnmsMonitoredService> getMonitoredServices() {
            return m_monitoredServices;
        }

        public List<OnmsMonitoredService> getSortedMemberServices() {
            return m_sortedMemberServices;
        }

        public List<OnmsMonitoringLocation> getMonitoringLocations() {
            return m_monitoringLocations;
        }

        public List<OnmsMonitoringLocation> getSortedMemberLocations() {
            return m_sortedMemberLocations;
        }
    }


    public static class ServiceEditModel {
        private OnmsMonitoredService m_service;
        private List<OnmsApplication> m_applications;
        private List<OnmsApplication> m_sortedApplications;

        public ServiceEditModel(OnmsMonitoredService service, List<OnmsApplication> applications) {
            m_service = service;
            m_applications = applications;
            
            for (OnmsApplication application : service.getApplications()) {
                m_applications.remove(application);
            }
            
            m_sortedApplications =
                new ArrayList<OnmsApplication>(m_service.getApplications());
            Collections.sort(m_sortedApplications);
        }
        
        public OnmsMonitoredService getService() {
            return m_service;
        }

        public List<OnmsApplication> getApplications() {
            return m_applications;
        }

        public List<OnmsApplication> getSortedApplications() {
            return m_sortedApplications;
        }
        
    }

}
