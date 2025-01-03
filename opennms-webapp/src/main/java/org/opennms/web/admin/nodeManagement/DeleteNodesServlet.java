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
package org.opennms.web.admin.nodeManagement;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opennms.core.db.DataSourceFactory;
import org.opennms.core.utils.DBUtils;
import org.opennms.core.utils.WebSecurityUtils;
import org.opennms.netmgt.dao.api.ResourceStorageDao;
import org.opennms.netmgt.events.api.EventConstants;
import org.opennms.netmgt.model.ResourcePath;
import org.opennms.netmgt.model.ResourceTypeUtils;
import org.opennms.netmgt.model.events.EventBuilder;
import org.opennms.netmgt.xml.event.Event;
import org.opennms.web.api.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * A servlet that handles deleting nodes from the database
 *
 * @author <A HREF="mailto:tarus@opennms.org">Tarus Balog </A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS </A>
 */
public class DeleteNodesServlet extends HttpServlet {

	private static final Logger LOG = LoggerFactory.getLogger(DeleteNodesServlet.class);

    private static final long serialVersionUID = 573510937493956121L;

    private ResourceStorageDao m_resourceStorageDao;

    /** {@inheritDoc} */
    @Override
    public void init() throws ServletException {
        WebApplicationContext webAppContext = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        m_resourceStorageDao = webAppContext.getBean("resourceStorageDao", ResourceStorageDao.class);
    }

    /** {@inheritDoc} */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Integer> nodeList = getList(request.getParameterValues("nodeCheck"));
        List<Integer> nodeDataList = getList(request.getParameterValues("nodeData"));

        for (Integer nodeId : nodeDataList) {
            // Get a list of response time IP address lists
            List<String> ipAddrs = getIpAddrsForNode(nodeId);

            ResourcePath nodeSnmpPath = new ResourcePath(
                    ResourceTypeUtils.SNMP_DIRECTORY,
                    Integer.toString(nodeId)
            );

            if (m_resourceStorageDao.exists(nodeSnmpPath, 0)) {
                LOG.debug("Attempting to delete node data directory: {}", nodeSnmpPath);
                if (m_resourceStorageDao.delete(nodeSnmpPath)) {
                    LOG.info("Node SNMP data directory deleted successfully: {}", nodeSnmpPath);
                } else {
                    LOG.warn("Node SNMP data directory *not* deleted successfully: {}", nodeSnmpPath);
                }
            }

            // Response time RRD directories
            for (String ipAddr : ipAddrs) {
                ResourcePath ifResponseTimePath = new ResourcePath(
                        ResourceTypeUtils.RESPONSE_DIRECTORY,
                        ipAddr
                );

                if (m_resourceStorageDao.exists(ifResponseTimePath, 0)) {
                    LOG.debug("Attempting to delete node response time data directory: {}", ifResponseTimePath);
                    if (m_resourceStorageDao.delete(ifResponseTimePath)) {
                        LOG.info("Node response time data directory deleted successfully: {}", ifResponseTimePath);
                    } else {
                        LOG.warn("Node response time data directory *not* deleted successfully: {}", ifResponseTimePath);
                    }
                }
            }
        }

        // Now, tell capsd to delete the node from the database
        for (Integer nodeId : nodeList) {
            sendDeleteNodeEvent(nodeId);
            LOG.debug("End of delete of node {}", nodeId);
        }

        // forward the request for proper display
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/admin/deleteNodesFinish.jsp");
        dispatcher.forward(request, response);
    }

    private List<String> getIpAddrsForNode(Integer nodeId) throws ServletException {
        List<String> ipAddrs = new ArrayList<>();
        final DBUtils d = new DBUtils(getClass());

        try {
            Connection conn = DataSourceFactory.getInstance().getConnection();
            d.watch(conn);

            PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT ipaddr FROM ipinterface WHERE nodeid=?");
            d.watch(stmt);
            stmt.setInt(1, nodeId);
            ResultSet rs = stmt.executeQuery();
            d.watch(rs);

            while (rs.next()) {
                ipAddrs.add(rs.getString("ipaddr"));
            }
        } catch (SQLException e) {
            throw new ServletException("There was a problem with the database connection: " + e, e);
        } finally {
            d.cleanUp();
        }
        return ipAddrs;
    }

    private void sendDeleteNodeEvent(int node) throws ServletException {
        EventBuilder bldr = new EventBuilder(EventConstants.DELETE_NODE_EVENT_UEI, "web ui");
        bldr.setNodeid(node);
        
        bldr.addParam(EventConstants.PARM_TRANSACTION_NO, "webUI");

        sendEvent(bldr.getEvent());
    }

    private void sendEvent(Event event) throws ServletException {
        try {
            Util.createEventProxy().send(event);
        } catch (Throwable e) {
            throw new ServletException("Could not send event " + event.getUei(), e);
        }
    }

    private List<Integer> getList(String[] array) {
        if (array == null) {
            return new ArrayList<Integer>(0);
        }
        
        List<Integer> list = new ArrayList<Integer>(array.length);
        for (String a : array) {
            list.add(WebSecurityUtils.safeParseInt(a));
        }
        return list;
    }

}
