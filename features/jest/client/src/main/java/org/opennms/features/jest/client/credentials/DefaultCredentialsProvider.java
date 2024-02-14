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
package org.opennms.features.jest.client.credentials;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXB;

public class DefaultCredentialsProvider implements CredentialsProvider {

    private final Path configPath;

    public DefaultCredentialsProvider(String configName) {
        Path configPath = Paths.get(System.getProperty("opennms.home"), "etc", configName);
        this.configPath = configPath;
    }

    @Override
    public List<CredentialsScope> getCredentials() {
        if (Files.exists(configPath)) {
            final ElasticCredentials credentialsWrapper = JAXB.unmarshal(configPath.toFile(), ElasticCredentials.class);
            return credentialsWrapper.getCredentialsScopes();
        }
        return new ArrayList<>();
    }
}
