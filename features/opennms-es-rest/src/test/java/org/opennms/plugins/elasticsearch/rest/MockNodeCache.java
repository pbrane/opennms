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
package org.opennms.plugins.elasticsearch.rest;

import java.util.HashMap;
import java.util.Map;

public class MockNodeCache implements NodeCache {

	@Override
	public Map getEntry(Long key) {
		Map<String,String> body = new HashMap<String,String>();
		
        body.put("nodelabel", "nodelabel_"+key);
        body.put("nodesysname", "nodesysname_"+key);
        body.put("nodesyslocation", "nodesyslocation_"+key);
        body.put("foreignsource", "mock_foreignsource");
        body.put("foreignid", "foreignid_"+key);
        body.put("operatingsystem", "linux");
        body.put("categories", "cat1,cat2,cat3,cat4");
        
        return body;
	}

	@Override
	public void refreshEntry(Long key) {

	}

}
