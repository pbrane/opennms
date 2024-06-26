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
package org.opennms.features.topology.app.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import org.opennms.core.sysprops.SystemProperties;
import org.opennms.features.topology.api.Graph;
import org.opennms.features.topology.api.GraphContainer;
import org.opennms.features.topology.api.GraphVisitor;
import org.opennms.features.topology.api.Layout;
import org.opennms.features.topology.api.Point;
import org.opennms.features.topology.api.SelectionManager;
import org.opennms.features.topology.api.support.hops.VertexHopCriteria;
import org.opennms.features.topology.api.topo.Criteria;
import org.opennms.features.topology.api.topo.Edge;
import org.opennms.features.topology.api.topo.EdgeRef;
import org.opennms.features.topology.api.topo.Status;
import org.opennms.features.topology.api.topo.Vertex;
import org.opennms.features.topology.api.topo.VertexRef;
import org.opennms.features.topology.app.internal.gwt.client.SharedEdge;
import org.opennms.features.topology.app.internal.gwt.client.SharedVertex;
import org.opennms.features.topology.app.internal.gwt.client.TopologyComponentState;
import org.opennms.features.topology.app.internal.support.IconRepositoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.vaadin.server.PaintException;

public class GraphPainter implements GraphVisitor {

    public static final int DEFAULT_EDGE_PATH_OFFSET = SystemProperties.getInteger("org.opennms.features.topology.api.topo.defaultEdgePathOffset", 20);

    private final GraphContainer m_graphContainer;
    private final IconRepositoryManager m_iconRepoManager;
    private final Layout m_layout;
    private final TopologyComponentState m_componentState;
    private final List<SharedVertex> m_vertices = new ArrayList<>();
    private final List<SharedEdge> m_edges = new ArrayList<>();
    private static final Logger s_log = LoggerFactory.getLogger(VEProviderGraphContainer.class);
    private final Map<VertexRef,Status> m_statusMap = new HashMap<VertexRef, Status>();
    private final Map<EdgeRef,Status> m_edgeStatusMap = new HashMap<EdgeRef, Status>();
    private Set<VertexRef> m_focusVertices = new HashSet<>();


    GraphPainter(GraphContainer graphContainer, Layout layout, IconRepositoryManager iconRepoManager, TopologyComponentState componentState) {
        m_graphContainer = graphContainer;
        m_layout = layout;
        m_iconRepoManager = iconRepoManager;
        m_componentState = componentState;
    }

    @Override
    public void visitGraph(Graph graph) throws PaintException {
        m_focusVertices.clear();
        Criteria[] criterias = m_graphContainer.getCriteria();
        for(Criteria criteria : criterias){
            try{
                VertexHopCriteria c = (VertexHopCriteria) criteria;
                m_focusVertices.addAll(c.getVertices());
            }catch(ClassCastException e){}
        }
        m_statusMap.clear();
        m_statusMap.putAll(graph.getVertexStatus());
        m_edgeStatusMap.clear();
        m_edgeStatusMap.putAll(graph.getEdgeStatus());
    }

    @Override
    public void visitVertex(Vertex vertex) throws PaintException {
        boolean selected = isSelected(m_graphContainer.getSelectionManager(), vertex);
        Point initialLocation = m_layout.getInitialLocation(vertex);
        Point location = m_layout.getLocation(vertex);
        SharedVertex v = new SharedVertex();
        v.setKey(vertex.getKey());
        //TODO cast to int for now
        v.setInitialX((int)initialLocation.getX());
        v.setInitialY((int)initialLocation.getY());
        v.setX((int)location.getX());
        v.setY((int) location.getY());
        v.setSelected(selected);
        v.setStatus(getStatus(vertex));
        v.setStatusCount(getStatusCount(vertex));
        v.setSVGIconId(getIconId(vertex));
        v.setLabel(vertex.getLabel());
        v.setTooltipText(getTooltipText(vertex));
        v.setStyleName(getVertexStyle(vertex, selected));
        v.setTargets(getTargets(vertex));
        v.setEdgePathOffset(getEdgePathOffset(vertex));
        m_vertices.add(v);
    }

    private static int getEdgePathOffset(Vertex vertex) {
        if (vertex.getEdgePathOffset() != null) {
            return vertex.getEdgePathOffset();
        } else {
            return DEFAULT_EDGE_PATH_OFFSET;
        }
    }

    private String getIconId(Vertex vertex) {
        return m_iconRepoManager.getSVGIconId(vertex);
    }

    private String getVertexStyle(Vertex vertex, boolean selected) {
        final StringBuilder style = new StringBuilder();
        style.append("vertex");
        if(selected) {
            style.append(" selected");
        }
        if(m_componentState.isHighlightFocus()) {
            if(!m_focusVertices.contains(vertex)) {
                style.append(" opacity-40");
            }
        }
        return style.toString();

    }

    /**
     * Determines if the given vertex has "links" to vertices from other layers.
     *
     * @param vertex The vertex to check
     * @return True if links to other layers exists, false otherwise
     */
    private boolean getTargets(Vertex vertex) {
        return !m_graphContainer.getTopologyServiceClient().getOppositeVertices(vertex).isEmpty();
    }

    private String getStatusCount(Vertex vertex) {
        Status status = m_statusMap.get(vertex);
        Map<String, String> statusProperties = status != null ? status.getStatusProperties() : new HashMap<>();
        return statusProperties.get("statusCount") == null ? "" : statusProperties.get("statusCount");
    }

    private String getStatus(Vertex vertex) {
        return m_statusMap.get(vertex) != null ? m_statusMap.get(vertex).computeStatus() : "";
    }

    private static String getTooltipText(Vertex vertex) {
        String tooltipText = vertex.getTooltipText();
        // If the tooltip text is null, use the label
        tooltipText = (tooltipText == null ? vertex.getLabel() : tooltipText);
        // If the label is null, use a blank string
        return (tooltipText == null ? "" : tooltipText);
    }

    @Override
    public void visitEdge(Edge edge) throws PaintException {
        String sourceKey = getSourceKey(edge);
        String targetKey = getTargetKey(edge);
        if (sourceKey == null) {
            s_log.debug("Discarding edge with no source vertex in the base topology: {}", edge);
        } else if (targetKey == null) {
            s_log.debug("Discarding edge with no target vertex in the base topology: {}", edge);
        } else {
            SharedEdge e = new SharedEdge();
            e.setKey(edge.getKey());
            e.setSourceKey(sourceKey);
            e.setTargetKey(targetKey);
            e.setSelected(isSelected(m_graphContainer.getSelectionManager(), edge));
            e.setStatus(getEdgeStatus(edge));
            if (m_edgeStatusMap.get(edge) != null) {
                e.setAdditionalStyling(m_edgeStatusMap.get(edge).getStyleProperties());
            }
            if(m_componentState.isHighlightFocus()){
                e.setCssClass(getStyleName(edge) + " opacity-50");
            }else{
                e.setCssClass(getStyleName(edge));
            }

            e.setTooltipText(getTooltipText(edge));
            m_edges.add(e);
        }
    }

    private String getEdgeStatus(Edge edge) {
        Status status = m_edgeStatusMap.get(edge);
        return status != null ? status.computeStatus() : "";
    }

    /**
     * Cannot return null
     */
    private static String getTooltipText(Edge edge) {
        String tooltipText = edge.getTooltipText();
        // If the tooltip text is null, use the label
        tooltipText = (tooltipText == null ? edge.getLabel() : tooltipText);
        // If the label is null, use a blank string
        return (tooltipText == null ? "" : tooltipText);
    }

    @Override
    public void completeGraph(Graph graph) throws PaintException {
        m_componentState.setVertices(m_vertices);
        m_componentState.setEdges(m_edges);
    }

    private String getSourceKey(Edge edge) {
        if (edge == null) return null;
        final VertexRef edgeVertex = edge.getSource().getVertex();
        final Vertex vertex = m_graphContainer.getTopologyServiceClient().getVertex(edgeVertex, m_graphContainer.getCriteria());
        if (vertex == null) return null;
        return vertex.getKey();
    }

    private String getTargetKey(Edge edge) {
        if (edge == null) return null;
        final VertexRef edgeVertex = edge.getTarget().getVertex();
        final Vertex vertex = m_graphContainer.getTopologyServiceClient().getVertex(edgeVertex, m_graphContainer.getCriteria());
        if (vertex == null) return null;
        return vertex.getKey();
    }

    /**
     * Cannot return null
     */
    private String getStyleName(Edge edge) {
        final String styleName = edge.getStyleName();
        final StringJoiner stringJoiner = new StringJoiner(" ");
        if (!Strings.isNullOrEmpty(styleName)) {
            stringJoiner.add(styleName);
        }
        if (isSelected(m_graphContainer.getSelectionManager(), edge)) {
            stringJoiner.add("selected");
        }
        String status = getEdgeStatus(edge);
        if (!Strings.isNullOrEmpty(status)) {
            stringJoiner.add(status);
        }
        return stringJoiner.toString();
    }

    private static boolean isSelected(SelectionManager selectionManager, Vertex vertex) {
        return selectionManager.isVertexRefSelected(vertex);
    }

    private static boolean isSelected(SelectionManager selectionManager, Edge edge) {
        return selectionManager.isEdgeRefSelected(edge);
    }

}
