/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2002-2022 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2022 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.opennms.core.utils.ConfigFileConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the singleton class used to load the configuration for the OpenNMS
 * Poller service from the poller-configuration XML file.
 *
 * A mapping of the configured URLs to the IP list they contain is built at
 * init() time so as to avoid numerous file reads.
 *
 * <strong>Note: </strong>Users of this class should make sure the
 * <em>init()</em> is called before calling any other method to ensure the
 * config is loaded before accessing other convenience methods.
 *
 * @author <a href="mailto:jamesz@opennms.com">James Zuo </a>
 * @author <a href="mailto:mike@opennms.org">Mike Davidson </a>
 * @author <a href="mailto:sowmya@opennms.org">Sowmya Nataraj </a>
 * @author <a href="http://www.opennms.org/">OpenNMS </a>
 * @version $Id: $
 */
public final class SnmpInterfacePollerConfigFactory extends SnmpInterfacePollerConfigManager {
    private static final Logger LOG = LoggerFactory.getLogger(SnmpInterfacePollerConfigFactory.class);
    /**
     * The singleton instance of this factory
     */
    private static SnmpInterfacePollerConfig m_singleton = null;

    /**
     * This member is set to true if the configuration file has been loaded.
     */
    private static boolean m_loaded = false;
    
    /**
     * Loaded version
     */
    private long m_currentVersion = -1L;

    /**
     * <p>Constructor for SnmpInterfacePollerConfigFactory.</p>
     *
     * @param currentVersion a long.
     * @param stream a {@link java.io.InputStream} object.
     * @throws java.io.IOException if any.
     */
    public SnmpInterfacePollerConfigFactory(long currentVersion, InputStream stream) throws IOException {
        super(stream);
        m_currentVersion = currentVersion;
    }

    /**
     * Load the config from the default config file and create the singleton
     * instance of this factory.
     *
     * @exception java.io.IOException
     *                Thrown if the specified config file cannot be read
     * @throws java.io.IOException if any.
     */
    public static synchronized void init() throws IOException {
        if (m_loaded) {
            // init already called - return
            // to reload, reload() will need to be called
            return;
        }

        File cfgFile = ConfigFileConstants.getFile(ConfigFileConstants.SNMP_INTERFACE_POLLER_CONFIG_FILE_NAME);

        LOG.debug("init: config file path: {}", cfgFile.getPath());

        try (InputStream stream = new FileInputStream(cfgFile)) {
            SnmpInterfacePollerConfigFactory config = new SnmpInterfacePollerConfigFactory(cfgFile.lastModified(), stream);
            setInstance(config);
        }
    }

    /**
     * Reload the config from the default config file
     *
     * @exception java.io.IOException
     *                Thrown if the specified config file cannot be read/loaded
     * @throws java.io.IOException if any.
     */
    public static synchronized void reload() throws IOException {
        init();
        getInstance().update();
    }

    /** {@inheritDoc} */
    @Override
    protected synchronized void saveXml(String xml) throws IOException {
        if (xml != null) {
            long timestamp = System.currentTimeMillis();
            File cfgFile = ConfigFileConstants.getFile(ConfigFileConstants.SNMP_INTERFACE_POLLER_CONFIG_FILE_NAME);
            LOG.debug("saveXml: saving config file at {}: {}",timestamp, cfgFile.getPath());
            try (Writer fileWriter = new OutputStreamWriter(new FileOutputStream(cfgFile), StandardCharsets.UTF_8)) {
                fileWriter.write(xml);
                fileWriter.flush();
                fileWriter.close();
                LOG.debug("saveXml: finished saving config file: {}", cfgFile.getPath());
            }
        }
    }

    /**
     * Return the singleton instance of this factory.
     *
     * @return The current factory instance.
     * @throws java.lang.IllegalStateException
     *             Thrown if the factory has not yet been initialized.
     */
    public static synchronized SnmpInterfacePollerConfig getInstance() {
        if (!m_loaded) {
            throw new IllegalStateException("The factory has not been initialized");
        }

        return m_singleton;
    }
    
    /**
     * <p>setInstance</p>
     *
     * @param instance a {@link org.opennms.netmgt.config.SnmpInterfacePollerConfig} object.
     */
    public static synchronized void setInstance(SnmpInterfacePollerConfig instance) {
        m_singleton = instance;
        m_loaded = true;
    }

    /**
     * <p>update</p>
     *
     * @throws java.io.IOException if any.
     */
    @Override
    public synchronized void update() throws IOException {

        File cfgFile = ConfigFileConstants.getFile(ConfigFileConstants.SNMP_INTERFACE_POLLER_CONFIG_FILE_NAME);
        if (cfgFile.lastModified() > m_currentVersion) {
            m_currentVersion = cfgFile.lastModified();
            LOG.debug("init: config file path: {}", cfgFile.getPath());
            try (InputStream stream = new FileInputStream(cfgFile)) {
                reloadXML(stream);
            }
            LOG.debug("init: finished loading config file: {}", cfgFile.getPath());
        }
    }

}
