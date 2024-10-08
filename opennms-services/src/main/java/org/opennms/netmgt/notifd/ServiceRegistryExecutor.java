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
package org.opennms.netmgt.notifd;

import java.util.List;

import org.opennms.core.soa.lookup.ServiceLookup;
import org.opennms.core.soa.lookup.ServiceLookupBuilder;
import org.opennms.core.soa.lookup.ServiceRegistryLookup;
import org.opennms.core.soa.support.DefaultServiceRegistry;
import org.opennms.netmgt.model.notifd.Argument;
import org.opennms.netmgt.model.notifd.NotificationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceRegistryExecutor implements ExecutorStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceRegistryExecutor.class);

    private static final ServiceLookup<Class<?>, String> SERVICE_LOOKUP = new ServiceLookupBuilder(new ServiceRegistryLookup(DefaultServiceRegistry.INSTANCE))
            .blocking()
            .build();

    @Override
    public int execute(String filter, List<Argument> arguments) {
        LOG.debug("Searching for notification strategy matching filter: {}", filter);
        final NotificationStrategy ns = getNotificationStrategy(filter);
        if (ns == null) {
            LOG.error("No notification strategy found matching filter: {}", filter);
            return 1;
        }
        LOG.debug("Found notification strategy: {}", ns);
        return ns.send(arguments);
    }

    private NotificationStrategy getNotificationStrategy(String filter) {
        return SERVICE_LOOKUP.lookup(NotificationStrategy.class, filter);
    }
}
