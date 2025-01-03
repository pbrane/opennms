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
package org.opennms.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.opennms.core.sysprops.SystemProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Caches properties files in order to improve performance.
 *
 * @author <a href="mailto:brozow@opennms.org">Mathew Brozowski</a>
 * @version $Id: $
 */
public class PropertiesCache {
	
	private static final Logger LOG = LoggerFactory.getLogger(PropertiesCache.class);

    public static final String CHECK_LAST_MODIFY_STRING = "org.opennms.utils.propertiesCache.enableCheckFileModified";
    public static final String CACHE_TIMEOUT = "org.opennms.utils.propertiesCache.cacheTimeout";
    public static final int DEFAULT_CACHE_TIMEOUT = 3600;

    protected static class PropertiesHolder {
        private Properties m_properties;
        private final File m_file;
        private final Lock lock = new ReentrantLock();
        private long m_lastModify = 0;
        private boolean m_checkLastModify = Boolean.getBoolean(CHECK_LAST_MODIFY_STRING);

        PropertiesHolder(final File file) {
            m_file = file;
            m_properties = null;
        }
        
        private Properties read() throws IOException {
            if (!m_file.canRead()) {
                return null;
            }

            InputStream in = null;
            try {
                in = new FileInputStream(m_file);
                Properties prop = new Properties();
                prop.load(in);
                if (m_checkLastModify) {
                    m_lastModify = m_file.lastModified();
                }
                return prop;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // Ignore this exception
                    }
                }
            }
        }
        
        private void write() throws IOException {
            if(!m_file.getParentFile().mkdirs()) {
            	if(!m_file.getParentFile().exists()) {
            		LOG.warn("Could not make directory: {}", m_file.getParentFile().getPath());
            	}
            }
            OutputStream out = null;
            try {
                out = new FileOutputStream(m_file);
                m_properties.store(out, null);
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // Ignore this exception
                    }
                }
            }
        }

        public Properties get() throws IOException {
            lock.lock();
            try {
                if (m_properties == null) {
                    readWithDefault(new Properties());
                } else {
                    if (m_checkLastModify && m_file.canRead() && m_lastModify != m_file.lastModified()) {
                        m_properties = read();
                    }
                }
                return m_properties;
            } finally {
                lock.unlock();
            }
        }

        private void readWithDefault(final Properties deflt) throws IOException {
            // this is
            if (deflt == null && !m_file.canRead()) {
                // nothing to load so m_properties remains null no writing necessary
                // just return to avoid getting the write lock
                return;
            }
            
            if (m_properties == null) {
                m_properties = read();
                if (m_properties == null) {
                    m_properties = deflt;
                }
            }   
        }
        
        public void put(final Properties properties) throws IOException {
            lock.lock();
            try {
                m_properties = properties;
                write();
            } finally {
                lock.unlock();
            }
        }

        public void update(final Map<String, String> props) throws IOException {
            if (props == null) return;
            lock.lock();
            try {
                boolean save = false;
                for(Entry<String, String> e : props.entrySet()) {
                    if (!e.getValue().equals(get().get(e.getKey()))) {
                        get().put(e.getKey(), e.getValue());
                        save = true;
                    }
                }
                if (save) {
                    write();
                }
            } finally {
                lock.unlock();
            }
        }
        
        public void setProperty(final String key, final String value) throws IOException {
            lock.lock();
            try {
                // first we do get to make sure the properties are loaded
                get();
                if (!value.equals(get().get(key))) {
                    get().put(key, value);
                    write();
                }
            } finally {
                lock.unlock();
            }
        }

        public Properties find() throws IOException {
            lock.lock();
            try {
                if (m_properties == null) {
                    readWithDefault(null);
                }
                return m_properties;
            } finally {
                lock.unlock();
            }
        }

        public String getProperty(final String key) throws IOException {
            lock.lock();
            try {
                return get().getProperty(key);
            } finally {
                lock.unlock();
            }
            
        }
    }

    protected final Cache<String, PropertiesHolder> m_cache;

    public PropertiesCache() {
        this(CacheBuilder.newBuilder());
    }


    protected PropertiesCache(final CacheBuilder<Object, Object> cacheBuilder) {
        m_cache = cacheBuilder
                .expireAfterAccess(SystemProperties.getInteger(CACHE_TIMEOUT, DEFAULT_CACHE_TIMEOUT), TimeUnit.SECONDS)
                .build();
    }

    private PropertiesHolder getHolder(final File propFile) throws IOException {
        final String key = propFile.getCanonicalPath();

        try {
            return m_cache.get(key, new Callable<PropertiesHolder>() {
                @Override
                public PropertiesHolder call() throws Exception {
                    return new PropertiesHolder(propFile);
                }
            });
        } catch (final ExecutionException e) {
            throw new IOException("Error creating PropertyHolder instance", e);
        }
    }
    
    /**
     * <p>clear</p>
     */
    public void clear() {
        synchronized (m_cache) {
            m_cache.invalidateAll();
        }
    }

    /**
     * Get the current properties object from the cache loading it in memory
     *
     * @param propFile a {@link java.io.File} object.
     * @throws java.io.IOException if any.
     * @return a {@link java.util.Properties} object.
     */
    public Properties getProperties(final File propFile) throws IOException {
        return getHolder(propFile).get();
    }
    
    /**
     * <p>findProperties</p>
     *
     * @param propFile a {@link java.io.File} object.
     * @return a {@link java.util.Properties} object.
     * @throws java.io.IOException if any.
     */
    public Properties findProperties(final File propFile) throws IOException {
        return getHolder(propFile).find();
    }
    /**
     * <p>saveProperties</p>
     *
     * @param propFile a {@link java.io.File} object.
     * @param properties a {@link java.util.Properties} object.
     * @throws java.io.IOException if any.
     */
    public void saveProperties(final File propFile, final Properties properties) throws IOException {
        getHolder(propFile).put(properties);
    }

    public void saveProperties(final File propFile, final Map<String, String> attributeMappings) throws IOException {
        if (attributeMappings == null) return;
        final Properties properties = new Properties();
        properties.putAll(attributeMappings);
        saveProperties(propFile, properties);
    }

    /**
     * <p>updateProperties</p>
     *
     * @param propFile a {@link java.io.File} object.
     * @param props a {@link java.util.Map} object.
     * @throws java.io.IOException if any.
     */
    public void updateProperties(final File propFile, final Map<String, String> props) throws IOException {
        getHolder(propFile).update(props);
    }
    
    /**
     * <p>setProperty</p>
     *
     * @param propFile a {@link java.io.File} object.
     * @param key a {@link java.lang.String} object.
     * @param value a {@link java.lang.String} object.
     * @throws java.io.IOException if any.
     */
    public void setProperty(final File propFile, final String key, final String value) throws IOException {
        getHolder(propFile).setProperty(key, value);
    }
    
    /**
     * <p>getProperty</p>
     *
     * @param propFile a {@link java.io.File} object.
     * @param key a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     * @throws java.io.IOException if any.
     */
    public String getProperty(final File propFile, final String key) throws IOException {
        return getHolder(propFile).getProperty(key);
    }
}
