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
package org.opennms.netmgt.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.opennms.core.utils.ConfigFileConstants;
import org.opennms.core.xml.JaxbUtils;
import org.opennms.netmgt.config.httpdatacollection.HttpCollection;
import org.opennms.netmgt.config.httpdatacollection.HttpDatacollectionConfig;
import org.opennms.netmgt.rrd.RrdRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>HttpCollectionConfigFactory class.</p>
 *
 * @author <a href="mailto:david@opennms.org">David Hustace</a>
 * @version $Id: $
 */
public class HttpCollectionConfigFactory {
    private static final Logger LOG = LoggerFactory.getLogger(HttpCollectionConfigFactory.class);
    /** The singleton instance. */
    private static HttpCollectionConfigFactory m_instance;

    private static boolean m_loadedFromFile = false;

    /** Boolean indicating if the init() method has been called. */
    protected boolean initialized = false;

    /** Timestamp of the http collection config, used to know when to reload from disk. */
    protected static long m_lastModified;

    private static HttpDatacollectionConfig m_config;

    /**
     * <p>Constructor for HttpCollectionConfigFactory.</p>
     *
     * @param configFile a {@link java.lang.String} object.
     * @throws java.io.IOException if any.
     */
    public HttpCollectionConfigFactory(String configFile) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(configFile);
            initialize(is);
        } finally {
            if (is != null) {
                IOUtils.closeQuietly(is);
            }
        }
    }

    /**
     * <p>Constructor for HttpCollectionConfigFactory.</p>
     *
     * @param stream a {@link java.io.InputStream} object.
     * @throws IOException
     */
    public HttpCollectionConfigFactory(InputStream stream) throws IOException {
        initialize(stream);
    }

    private void initialize(InputStream stream) throws IOException {
        LOG.debug("initialize: initializing http collection config factory.");
        try (InputStreamReader isr = new InputStreamReader(stream)) {
            m_config = JaxbUtils.unmarshal(HttpDatacollectionConfig.class, isr);
        }
    }

    /**
     * Be sure to call this method before calling getInstance().
     *
     * @throws java.io.IOException if any.
     * @throws java.io.FileNotFoundException if any.
     */
    public static synchronized void init() throws IOException, FileNotFoundException {
        
        if (m_instance == null) {
            File cfgFile = ConfigFileConstants.getFile(ConfigFileConstants.HTTP_COLLECTION_CONFIG_FILE_NAME);
            m_instance = new HttpCollectionConfigFactory(cfgFile.getPath());
            m_lastModified = cfgFile.lastModified();
            m_loadedFromFile = true;
        }
    }

    /**
     * Singleton static call to get the only instance that should exist
     *
     * @return the single factory instance
     * @throws java.lang.IllegalStateException
     *             if init has not been called
     */
    public static synchronized HttpCollectionConfigFactory getInstance() {
        
        if (m_instance == null) {
            throw new IllegalStateException("You must call HttpCollectionConfigFactory.init() before calling getInstance().");
        }
        return m_instance;
    }
    
    /**
     * <p>setInstance</p>
     *
     * @param instance a {@link org.opennms.netmgt.config.HttpCollectionConfigFactory} object.
     */
    public static synchronized void setInstance(HttpCollectionConfigFactory instance) {
        m_instance = instance;
        m_loadedFromFile = false;
    }

    /**
     * <p>reload</p>
     *
     * @throws java.io.IOException if any.
     * @throws java.io.FileNotFoundException if any.
     */
    public synchronized void reload() throws IOException, FileNotFoundException {
        m_instance = null;
        init();
    }


    /**
     * Reload the http-datacollection-config.xml file if it has been changed since we last
     * read it.
     *
     * @throws java.io.IOException if any.
     */
    protected void updateFromFile() throws IOException {
        if (m_loadedFromFile) {
            File surveillanceViewsFile = ConfigFileConstants.getFile(ConfigFileConstants.HTTP_COLLECTION_CONFIG_FILE_NAME);
            if (m_lastModified != surveillanceViewsFile.lastModified()) {
                this.reload();
            }
        }
    }

    /**
     * <p>getConfig</p>
     *
     * @return a {@link org.opennms.netmgt.config.httpdatacollection.HttpDatacollectionConfig} object.
     */
    public static synchronized HttpDatacollectionConfig getConfig() {
        return m_config;
    }

    /**
     * <p>setConfig</p>
     *
     * @param m_config a {@link org.opennms.netmgt.config.httpdatacollection.HttpDatacollectionConfig} object.
     */
    public static synchronized void setConfig(HttpDatacollectionConfig m_config) {
        HttpCollectionConfigFactory.m_config = m_config;
    }

    /**
     * <p>getHttpCollection</p>
     *
     * @param collectionName a {@link java.lang.String} object.
     * @return a {@link org.opennms.netmgt.config.httpdatacollection.HttpCollection} object.
     */
    public HttpCollection getHttpCollection(String collectionName) {
        try {
            updateFromFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<HttpCollection> collections = m_config.getHttpCollection();
        HttpCollection collection = null;
        for (HttpCollection coll : collections) {
            if (coll.getName().equalsIgnoreCase(collectionName)) {
                collection = coll;
                break;
            }
        }
        if (collection == null) {
            throw new IllegalArgumentException("getHttpCollection: collection name: "
                    +collectionName+" specified in collectd configuration not found in http collection configuration.");
        }
        return collection;
    }

    /**
     * <p>getRrdRepository</p>
     *
     * @param collectionName a {@link java.lang.String} object.
     * @return a {@link org.opennms.netmgt.rrd.RrdRepository} object.
     */
    public RrdRepository getRrdRepository(String collectionName) {
        RrdRepository repo = new RrdRepository();
        repo.setRrdBaseDir(new File(getRrdPath()));
        repo.setRraList(getRRAList(collectionName));
        repo.setStep(getStep(collectionName));
        repo.setHeartBeat((2 * getStep(collectionName)));
        return repo;
    }
    
    /**
     * <p>getStep</p>
     *
     * @param cName a {@link java.lang.String} object.
     * @return a int.
     */
    public int getStep(String cName) {
        HttpCollection collection = getHttpCollection(cName);
        if (collection != null)
            return collection.getRrd().getStep();
        else
            return -1;
    }
    
    /**
     * <p>getRRAList</p>
     *
     * @param cName a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    public List<String> getRRAList(String cName) {
       HttpCollection collection = getHttpCollection(cName);
        if (collection != null)
            return collection.getRrd().getRra();
        else
            return null;

    }
    
    /**
     * <p>getRrdPath</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getRrdPath() {
        String rrdPath = m_config.getRrdRepository();
        if (rrdPath == null) {
            throw new RuntimeException("Configuration error, failed to "
                    + "retrieve path to RRD repository.");
        }
    
        /*
         * TODO: make a path utils class that has the below in it strip the
         * File.separator char off of the end of the path.
         */
        if (rrdPath.endsWith(File.separator)) {
            rrdPath = rrdPath.substring(0, (rrdPath.length() - File.separator.length()));
        }
        
        return rrdPath;
    }

}
