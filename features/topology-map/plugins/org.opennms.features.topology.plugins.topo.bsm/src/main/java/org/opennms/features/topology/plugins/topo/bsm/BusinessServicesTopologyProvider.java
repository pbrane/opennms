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

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.opennms.core.criteria.CriteriaBuilder;
import org.opennms.core.criteria.restrictions.Restrictions;
import org.opennms.features.topology.api.browsers.ContentType;
import org.opennms.features.topology.api.browsers.SelectionChangedListener;
import org.opennms.features.topology.api.support.hops.DefaultVertexHopCriteria;
import org.opennms.features.topology.api.topo.AbstractTopologyProvider;
import org.opennms.features.topology.api.topo.Defaults;
import org.opennms.features.topology.api.topo.Edge;
import org.opennms.features.topology.api.topo.GraphProvider;
import org.opennms.features.topology.api.topo.VertexRef;
import org.opennms.features.topology.plugins.topo.bsm.AbstractBusinessServiceVertex.Type;
import org.opennms.netmgt.bsm.service.BusinessServiceManager;
import org.opennms.netmgt.bsm.service.model.BusinessService;
import org.opennms.netmgt.bsm.service.model.graph.BusinessServiceGraph;
import org.opennms.netmgt.bsm.service.model.graph.GraphEdge;
import org.opennms.netmgt.bsm.service.model.graph.GraphVertex;
import org.opennms.netmgt.vaadin.core.TransactionAwareBeanProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class BusinessServicesTopologyProvider extends AbstractTopologyProvider implements GraphProvider {

    public static final String TOPOLOGY_NAMESPACE = "bsm";

    private static final Logger LOG = LoggerFactory.getLogger(BusinessServicesTopologyProvider.class);

    private BusinessServiceManager businessServiceManager;

    private final TransactionAwareBeanProxyFactory transactionAwareBeanProxyFactory;

    public BusinessServicesTopologyProvider(TransactionAwareBeanProxyFactory transactionAwareBeanProxyFactory) {
        super(new org.opennms.features.topology.plugins.topo.bsm.BusinessServiceGraph());
        this.transactionAwareBeanProxyFactory = Objects.requireNonNull(transactionAwareBeanProxyFactory);
        LOG.debug("Creating a new {} with namespace {}", getClass().getSimpleName(), TOPOLOGY_NAMESPACE);
    }

    private void load() {
        graph.resetContainer();
        BusinessServiceGraph graph = businessServiceManager.getGraph();
        for (GraphVertex topLevelBusinessService : graph.getVerticesByLevel(0)) {
            addVertex(graph, topLevelBusinessService, null);
        }
    }

    private void addVertex(BusinessServiceGraph bsmGraph, GraphVertex graphVertex, AbstractBusinessServiceVertex topologyVertex) {
        if (topologyVertex == null) {
            // Create a topology vertex for the current vertex
            topologyVertex = createTopologyVertex(graphVertex);
            graph.addVertices(topologyVertex);
        }

        for (GraphEdge graphEdge : bsmGraph.getOutEdges(graphVertex)) {
            GraphVertex childVertex = bsmGraph.getOpposite(graphVertex, graphEdge);

            // Create a topology vertex for the child vertex

            AbstractBusinessServiceVertex childTopologyVertex = createTopologyVertex(childVertex);
            bsmGraph.getInEdges(childVertex).stream()
                    .map(GraphEdge::getFriendlyName)
                    .filter(s -> !Strings.isNullOrEmpty(s))
                    .findFirst()
                    .ifPresent(childTopologyVertex::setLabel);

            graph.addVertices(childTopologyVertex);

            // Connect the two
            childTopologyVertex.setParent(topologyVertex);
            Edge edge = new BusinessServiceEdge(graphEdge, topologyVertex, childTopologyVertex);
            graph.addEdges(edge);

            // Recurse
            addVertex(bsmGraph, childVertex, childTopologyVertex);
        }
    }

    private AbstractBusinessServiceVertex createTopologyVertex(GraphVertex graphVertex) {
        return GraphVertexToTopologyVertexConverter.createTopologyVertex(graphVertex);
    }

    public void setBusinessServiceManager(BusinessServiceManager businessServiceManager) {
        Objects.requireNonNull(businessServiceManager);
        this.businessServiceManager = transactionAwareBeanProxyFactory.createProxy(businessServiceManager);
    }

    @Override
    public void refresh() {
        load();
    }

    @Override
    public Defaults getDefaults() {
        return new Defaults()
                .withPreferredLayout("Hierarchy Layout")
                .withCriteria(() -> {
                    // Grab the business service with the smallest id
                    List<BusinessService> businessServices = businessServiceManager.findMatching(new CriteriaBuilder(BusinessService.class).orderBy("id", true).limit(1).toCriteria());
                    // If one was found, use it for the default focus
                    if (!businessServices.isEmpty()) {
                        BusinessService businessService = businessServices.iterator().next();
                        BusinessServiceVertex businessServiceVertex = new BusinessServiceVertex(businessService, 0);
                        return Lists.newArrayList(new DefaultVertexHopCriteria(businessServiceVertex));
                    }
                    return null;
                });
    }

    @Override
    public SelectionChangedListener.Selection getSelection(List<VertexRef> selectedVertices, ContentType contentType) {
        // only consider vertices of our namespace and of the correct type
        final Set<AbstractBusinessServiceVertex> filteredSet = selectedVertices.stream()
                .filter(e -> TOPOLOGY_NAMESPACE.equals(e.getNamespace()))
                .filter(e -> e instanceof AbstractBusinessServiceVertex)
                .map(e -> (AbstractBusinessServiceVertex) e)
                .collect(Collectors.toSet());
        switch (contentType) {
            case Alarm:
                // show alarms with reduction keys associated with the current selection.
                final Set<String> reductionKeys = filteredSet.stream()
                        .map(AbstractBusinessServiceVertex::getReductionKeys)
                        .flatMap(Set::stream)
                        .collect(Collectors.toSet());
                return () -> {
                    if (reductionKeys != null && !reductionKeys.isEmpty()) {
                        return Lists.newArrayList(Restrictions.in("reductionKey", reductionKeys));
                    }
                    return Lists.newArrayList(Restrictions.isNull("id")); // is always false, so nothing is shown
                };
            case BusinessService:
                final Set<Long> businessServiceIds = Sets.newHashSet();

                // Business Service
                filteredSet.stream()
                        .filter(v -> v.getType() == Type.BusinessService)
                        .forEach(v -> businessServiceIds.add(((BusinessServiceVertex) v).getServiceId()));

                // Ip Service
                filteredSet.stream()
                        .filter(v -> v.getType() == Type.IpService)
                        .forEach(v -> businessServiceIds.add(((BusinessServiceVertex) v.getParent()).getServiceId()));

                // Application
                filteredSet.stream()
                        .filter(v -> v.getType() == Type.Application)
                        .forEach(v -> businessServiceIds.add(((BusinessServiceVertex) v.getParent()).getServiceId()));

                // Reduction keys (Only consider children of Business Services)
                filteredSet.stream()
                    .filter(v -> v.getType() == Type.ReductionKey
                            && ((AbstractBusinessServiceVertex) v.getParent()).getType() == Type.BusinessService ) // we ignore children of ip services
                    .forEach(v -> ((BusinessServiceVertex) v.getParent()).getServiceId());
                return new SelectionChangedListener.IdSelection<>(businessServiceIds);
            case Node:
                final Set<Integer> nodeIds = filteredSet.stream()
                        .filter(v -> v.getType() == Type.IpService)
                        .map(v -> businessServiceManager.getIpServiceById(((IpServiceVertex) v).getIpServiceId()).getNodeId())
                        .collect(Collectors.toSet());

                filteredSet.stream()
                        .filter(v -> v.getType() == Type.Application)
                        .forEach(v -> nodeIds.addAll(businessServiceManager.getApplicationById(((ApplicationVertex) v).getApplicationId()).getNodeIds()));

                return new SelectionChangedListener.IdSelection<>(nodeIds);
            default:
                // pass
        }
        throw new IllegalArgumentException(getClass().getSimpleName() + " does not support filtering vertices for contentType " + contentType);
    }

    @Override
    public boolean contributesTo(ContentType type) {
        return Sets.newHashSet(ContentType.Alarm, ContentType.Node, ContentType.BusinessService).contains(type);
    }
}
