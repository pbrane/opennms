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
package org.opennms.protocols.json.collector;

import java.io.FileInputStream;

import net.sf.json.JSONObject;

import net.sf.json.JSONSerializer;
import org.apache.commons.io.IOUtils;

/**
 * The Mock Document Builder.
 *
 * @author <a href="mailto:ronald.roskens@gmail.com">Ronald Roskens</a>
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public class MockDocumentBuilder {

    /** The JSON file name. */
    public static String m_jsonFileName;

    /**
     * Instantiates a new mock document builder.
     */
    private MockDocumentBuilder() {}

    /**
     * Gets the JSON document.
     *
     * @return the JSON document
     */
    public static JSONObject getJSONDocument() {
        if (m_jsonFileName == null)
            return null;
        JSONObject json = null;
        
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(m_jsonFileName);
            String everything = IOUtils.toString(inputStream);
            json = AbstractJsonCollectionHandler.wrapArray(JSONSerializer.toJSON(everything));
        } catch (Exception e) {
        } finally {
            if (inputStream != null)
            IOUtils.closeQuietly(inputStream);
        }
        
        return json;
    }

    public static void setJSONFileName(String jsonFileName) {
        m_jsonFileName = jsonFileName;
    }
}

