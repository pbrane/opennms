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
package org.opennms.features.status.api;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.opennms.core.criteria.Criteria;
import org.opennms.core.criteria.CriteriaBuilder;
import org.opennms.web.utils.QueryParameters;

public abstract class AbstractStatusService<T, Q extends Query> {

    public List<StatusEntity<T>> getStatus(Q query) {
        final QueryParameters queryParameters = query.getParameters();
        final SeverityFilter filter = query.getSeverityFilter();
        final CriteriaBuilder criteriaBuilder = getCriteriaBuilder(queryParameters);

        // The implementors do not know anything about status/severity,
        // therefore it is not supported to order by severityon dao level.
        if (isSeverityRelatedQuery(query)) {
            criteriaBuilder.offset(null);
            criteriaBuilder.limit(null);

            // no column "severity" exists, clear it to avoid hibernate complaining about it
            if (queryParameters.getOrder().getColumn().equals("severity")) {
                criteriaBuilder.clearOrder();
            }
        }

        // Query and apply filters
        List<StatusEntity<T>> collect = findMatching(query, criteriaBuilder);
        collect = apply(collect, filter);

        // sort manually if required
        if (queryParameters.getOrder() != null && queryParameters.getOrder().getColumn().equals("severity")) {
            Comparator<StatusEntity<T>> comparator = Comparator.comparing(StatusEntity::getStatus);
            if (queryParameters.getOrder().isDesc()) {
                comparator = comparator.reversed();
            }
            collect.sort(comparator);
            collect = subList(collect, queryParameters);
        }

        return collect;
    }

    private List<StatusEntity<T>> subList(List<StatusEntity<T>> list, QueryParameters queryParameters) {
        return queryParameters.getPage().apply(list);
    }

    public int count(Q query) {
        final QueryParameters queryParameters = query.getParameters();
        final SeverityFilter filter = query.getSeverityFilter();
        final CriteriaBuilder builder = getCriteriaBuilder(queryParameters);

        // Remove limit, offset and ordering to fetch count
        builder.limit(null);
        builder.offset(null);
        builder.clearOrder();

        // If a severity is given, we must count manually!
        if (filter != null && filter.getSeverities() != null && !filter.getSeverities().isEmpty()) {
            List<StatusEntity<T>> collect = findMatching(query, builder);
            collect = apply(collect, filter);
            return collect.size();
        } else {
            return countMatching(builder.toCriteria());
        }
    }

    protected abstract int countMatching(Criteria criteria);

    protected abstract List<StatusEntity<T>> findMatching(Q query, CriteriaBuilder criteriaBuilder);

    protected abstract CriteriaBuilder getCriteriaBuilder(QueryParameters queryParameters);

    private List<StatusEntity<T>> apply(List<StatusEntity<T>> statusList, SeverityFilter severityFilter) {
        if (!statusList.isEmpty() && severityFilter != null && !severityFilter.getSeverities().isEmpty()) {
            return statusList.stream()
                    .filter(statusEntity -> severityFilter.getSeverities().contains(statusEntity.getStatus()))
                    .collect(Collectors.toList());
        }
        return statusList; // don't filter
    }

    // A severity related query is a query having a order column set to severity or a severity filter of any kind
    private boolean isSeverityRelatedQuery(Q query) {
        boolean severityRelatedQuery = query.getSeverityFilter() != null && !query.getSeverityFilter().getSeverities().isEmpty()
                || (query.getParameters().getOrder() != null && query.getParameters().getOrder().getColumn().equals("severity"));
        return severityRelatedQuery;
    }
}
