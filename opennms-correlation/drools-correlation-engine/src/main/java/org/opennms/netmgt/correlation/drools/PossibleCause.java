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
package org.opennms.netmgt.correlation.drools;

import org.opennms.netmgt.xml.event.Event;

/**
 * <p>PossibleCause class.</p>
 *
 * @author <a href="mailto:brozow@opennms.org">Mathew Brozowski</a>
 * @version $Id: $
 */
public class PossibleCause extends Cause {
    private static final long serialVersionUID = 6081288061834584980L;

    /**
     * <p>Constructor for PossibleCause.</p>
     *
     * @param cause a {@link java.lang.Long} object.
     * @param symptom a {@link org.opennms.netmgt.xml.event.Event} object.
     */
    public PossibleCause(final Long cause, final Event symptom) {
        super(Type.POSSIBLE, cause, symptom);
    }

}
