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
package org.opennms.netmgt.dao.mock;

import java.util.concurrent.atomic.AtomicInteger;

import org.opennms.netmgt.dao.api.ResourceReferenceDao;
import org.opennms.netmgt.model.ResourceReference;

public class MockResourceReferenceDao extends AbstractMockDao<ResourceReference, Integer> implements ResourceReferenceDao {
    private AtomicInteger m_id = new AtomicInteger(0);

    @Override
    public ResourceReference getByResourceId(final String resourceId) {
        for (final ResourceReference reference : findAll()) {
            if (resourceId.equals(reference.getResourceId())) {
                return reference;
            }
        }
        return null;
    }

    @Override
    protected Integer getId(final ResourceReference reference) {
        return reference.getId();
    }

    @Override
    protected void generateId(final ResourceReference reference) {
        reference.setId(m_id.incrementAndGet());
    }

}
