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
package org.opennms.features.topology.plugins.topo.bsm;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.opennms.features.topology.api.topo.Criteria;
import org.opennms.features.topology.api.topo.DefaultStatus;
import org.opennms.features.topology.api.topo.EdgeRef;
import org.opennms.features.topology.api.topo.EdgeStatusProvider;
import org.opennms.features.topology.api.topo.Status;
import org.opennms.features.topology.api.topo.StatusProvider;
import org.opennms.features.topology.api.topo.VertexRef;
import org.opennms.features.topology.api.topo.BackendGraph;
import org.opennms.features.topology.plugins.topo.bsm.simulate.SimulationAwareStateMachineFactory;
import org.opennms.netmgt.bsm.service.BusinessServiceManager;
import org.opennms.netmgt.bsm.service.BusinessServiceStateMachine;
import org.opennms.netmgt.bsm.service.model.graph.BusinessServiceGraph;
import org.opennms.netmgt.bsm.service.model.graph.GraphEdge;
import org.opennms.netmgt.bsm.service.model.graph.GraphVertex;

public class BusinessServicesStatusProvider implements StatusProvider, EdgeStatusProvider {

    private BusinessServiceManager businessServiceManager;

    @Override
    public Map<VertexRef, Status> getStatusForVertices(BackendGraph graph, Collection<VertexRef> vertices, Criteria[] criteria) {
        final BusinessServiceStateMachine stateMachine = SimulationAwareStateMachineFactory.createStateMachine(businessServiceManager, criteria);
        return vertices.stream()
            .filter(v -> contributesTo(v.getNamespace()) && v instanceof AbstractBusinessServiceVertex)
            .map(v -> (AbstractBusinessServiceVertex) v)
            .collect(Collectors.toMap(v -> v, v -> {
                org.opennms.netmgt.bsm.service.model.Status status = getStatus(stateMachine, v);
                // Status can be null
                return status != null ? new DefaultStatus(status.getLabel(), 0) : null;
            }));
    }

    @Override
    public Map<EdgeRef, Status> getStatusForEdges(BackendGraph graph, Collection<EdgeRef> edges, Criteria[] criteria) {
        final BusinessServiceStateMachine stateMachine = SimulationAwareStateMachineFactory.createStateMachine(businessServiceManager, criteria);
        return edges.stream()
                .filter(edge -> contributesTo(edge.getNamespace()) && edge instanceof BusinessServiceEdge)
                .map(edge -> (BusinessServiceEdge) edge)
                .collect(Collectors.toMap(edge -> edge, edge -> {
                    org.opennms.netmgt.bsm.service.model.Status status = getStatus(stateMachine, edge);
                    // Status can be null
                    return status != null ? new DefaultStatus(status.getLabel(), 0) : null;
                }));
    }

    public static org.opennms.netmgt.bsm.service.model.Status getStatus(BusinessServiceStateMachine stateMachine, AbstractBusinessServiceVertex vertex) {
        final GraphVertex graphVertex = getGraphVertex(vertex, stateMachine.getGraph());
        return graphVertex != null ? graphVertex.getStatus() : null;
    }

    public static org.opennms.netmgt.bsm.service.model.Status getStatus(BusinessServiceStateMachine stateMachine, BusinessServiceEdge edge) {
        final BusinessServiceGraph graph = stateMachine.getGraph();
        // We need both the source and target vertices to find the edge in the graph
        final GraphVertex source = getGraphVertex(edge.getBusinessServiceSource(), stateMachine.getGraph());
        final GraphVertex target = getGraphVertex(edge.getBusinessServiceTarget(), stateMachine.getGraph());
        final GraphEdge graphEdge = graph.findEdge(source, target);
        return graphEdge != null ? graphEdge.getStatus() : null;
    }

    private static GraphVertex getGraphVertex(AbstractBusinessServiceVertex vertex, BusinessServiceGraph graph) {
        final AtomicReference<GraphVertex> graphVertex = new AtomicReference<>();
        vertex.accept(new BusinessServiceVertexVisitor<Void>() {
            @Override
            public Void visit(BusinessServiceVertex vertex) {
                graphVertex.set(graph.getVertexByBusinessServiceId(vertex.getServiceId()));
                return null;
            }

            @Override
            public Void visit(IpServiceVertex vertex) {
                graphVertex.set(graph.getVertexByIpServiceId(vertex.getIpServiceId()));
                return null;
            }

            @Override
            public Void visit(ReductionKeyVertex vertex) {
                graphVertex.set(graph.getVertexByReductionKey(vertex.getReductionKey()));
                return null;
            }

            @Override
            public Void visit(ApplicationVertex vertex) {
                graphVertex.set(graph.getVertexByApplicationId(vertex.getApplicationId()));
                return null;
            }
        });
        return graphVertex.get();
    }

    @Override
    public String getNamespace() {
        return BusinessServicesTopologyProvider.TOPOLOGY_NAMESPACE;
    }

    @Override
    public boolean contributesTo(String namespace) {
        return getNamespace().equals(namespace);
    }

    public void setBusinessServiceManager(BusinessServiceManager businessServiceManager) {
        this.businessServiceManager = Objects.requireNonNull(businessServiceManager);
    }
}
