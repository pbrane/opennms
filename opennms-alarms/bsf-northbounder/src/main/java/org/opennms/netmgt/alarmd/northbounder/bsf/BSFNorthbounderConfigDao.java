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
package org.opennms.netmgt.alarmd.northbounder.bsf;

import java.io.FileWriter;
import java.io.IOException;

import org.opennms.core.xml.AbstractJaxbConfigDao;
import org.opennms.core.xml.JaxbUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class BSFNorthbounderConfigDao.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public class BSFNorthbounderConfigDao extends AbstractJaxbConfigDao<BSFNorthbounderConfig, BSFNorthbounderConfig> {

    /** The Constant LOG. */
    public static final Logger LOG = LoggerFactory.getLogger(BSFNorthbounderConfigDao.class);

    /**
     * Instantiates a new BSF northbounder configuration DAO.
     */
    public BSFNorthbounderConfigDao() {
        super(BSFNorthbounderConfig.class, "Config for BSF Northbounder");
    }

    /* (non-Javadoc)
     * @see org.opennms.core.xml.AbstractJaxbConfigDao#translateConfig(java.lang.Object)
     */
    @Override
    protected BSFNorthbounderConfig translateConfig(BSFNorthbounderConfig config) {
        return config;
    }

    /**
     * Gets the BSF northbounder configuration.
     *
     * @return the configuration object
     */
    public BSFNorthbounderConfig getConfig() {
        return getContainer().getObject();
    }

    /**
     * Reload.
     */
    public void reload() {
        getContainer().reload();
    }

    /**
     * Save.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void save() throws IOException {
        JaxbUtils.marshal(getConfig(), new FileWriter(getConfigResource().getFile()));
    }

}
