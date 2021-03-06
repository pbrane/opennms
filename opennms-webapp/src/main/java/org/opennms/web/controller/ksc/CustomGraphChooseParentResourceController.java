/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2007-2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.web.controller.ksc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opennms.netmgt.model.OnmsResource;
import org.opennms.web.svclayer.ResourceService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * <p>CustomGraphChooseParentResourceController class.</p>
 *
 * @author ranger
 * @version $Id: $
 * @since 1.8.1
 */
public class CustomGraphChooseParentResourceController extends AbstractController implements InitializingBean {

    public enum Parameters {
        selectedResourceId
    }

    private ResourceService m_resourceService;

    /** {@inheritDoc} */
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        /*
        ModelAndView modelAndView = new ModelAndView("KSC/customGraphChooseParentResource");

        modelAndView.addObject("nodeResources", getResourceService().findNodeResources());
        modelAndView.addObject("nodeSourceResources", getResourceService().findNodeSourceResources());
        modelAndView.addObject("domainResources", getResourceService().findDomainResources());
        
        return modelAndView;
        */
        
        ModelAndView modelAndView = new ModelAndView("KSC/customGraphChooseResource");

        String selectedResourceId = request.getParameter(Parameters.selectedResourceId.toString());
        if (selectedResourceId != null) {
            OnmsResource selectedResource = m_resourceService.getResourceById(selectedResourceId);

            Map<String, OnmsResource> selectedResourceAndParents = new HashMap<String, OnmsResource>();
            OnmsResource r = selectedResource;
            while (r != null) {
                selectedResourceAndParents.put(r.getId(), r);
                r = r.getParent();
            }
            
            modelAndView.addObject("selectedResourceAndParents", selectedResourceAndParents);
        }

        
        /*
        OnmsResource resource = getResourceService().getResourceById(resourceId);
        modelAndView.addObject("parentResource", resource);
        
        modelAndView.addObject("parentResourcePrefabGraphs", m_resourceService.findPrefabGraphsForResource(resource));
        */

        //List<OnmsResource> childResources = getResourceService().findChildResources(resource);
        List<OnmsResource> nodeResources = getResourceService().findNodeResources();
        List<OnmsResource> nodeSourceResources = getResourceService().findNodeSourceResources();
        List<OnmsResource> domainResources = getResourceService().findDomainResources();
        
        List<OnmsResource> childResources = new ArrayList<OnmsResource>(nodeResources.size() + nodeSourceResources.size() + domainResources.size());
        childResources.addAll(nodeResources);
        childResources.addAll(nodeSourceResources);
        childResources.addAll(domainResources);

        modelAndView.addObject("resources", childResources);
        
        return modelAndView;
    }

    /**
     * <p>getResourceService</p>
     *
     * @return a {@link org.opennms.web.svclayer.ResourceService} object.
     */
    public ResourceService getResourceService() {
        return m_resourceService;
    }

    /**
     * <p>setResourceService</p>
     *
     * @param resourceService a {@link org.opennms.web.svclayer.ResourceService} object.
     */
    public void setResourceService(ResourceService resourceService) {
        m_resourceService = resourceService;
    }

    /**
     * <p>afterPropertiesSet</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.state(m_resourceService != null, "property resourceService must be set");
    }

}
