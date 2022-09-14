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

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.opennms.core.db.DataSourceFactory;
import org.opennms.core.utils.ConfigFileConstants;

/**
 * <p>NotificationFactory class.</p>
 *
 * @author ranger
 * @version $Id: $
 */
public class NotificationFactory extends NotificationManager {
    /**
     * Singleton instance
     */
    private static NotificationFactory instance;

    /**
     * Boolean indicating if the init() method has been called
     */
    private static boolean initialized = false;

    /**
     * 
     */
    private File m_noticeConfFile;

    /**
     * 
     */
    private long m_lastModified;

    /**
     * 
     */
    private NotificationFactory() {
        super(NotifdConfigFactory.getInstance(), DataSourceFactory.getInstance());
    }

    /**
     * <p>Getter for the field <code>instance</code>.</p>
     *
     * @return a {@link org.opennms.netmgt.config.NotificationFactory} object.
     */
    static synchronized public NotificationFactory getInstance() {
        if (!initialized)
            return null;

        return instance;
    }

    /**
     * <p>init</p>
     *
     * @throws java.io.IOException if any.
     * @throws java.io.FileNotFoundException if any.
     * @throws java.lang.ClassNotFoundException if any.
     * @throws java.sql.SQLException if any.
     * @throws java.beans.PropertyVetoException if any.
     */
    public static synchronized void init() throws IOException, FileNotFoundException, ClassNotFoundException, SQLException, PropertyVetoException  {
        if (!initialized) {
            instance = new NotificationFactory();
            instance.reload();
            initialized = true;
        }
    }

    /**
     * <p>reload</p>
     *
     * @throws java.io.IOException if any.
     */
    public synchronized void reload() throws IOException {
        m_noticeConfFile = ConfigFileConstants.getFile(ConfigFileConstants.NOTIFICATIONS_CONF_FILE_NAME);

        try (InputStream configIn = new FileInputStream(m_noticeConfFile)) {
            m_lastModified = m_noticeConfFile.lastModified();
            parseXML(configIn);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void saveXML(String xmlString) throws IOException {
        if (xmlString != null) {
            try (Writer fileWriter = new OutputStreamWriter(new FileOutputStream(m_noticeConfFile), StandardCharsets.UTF_8)) {
                fileWriter.write(xmlString);
                fileWriter.flush();
            }
        }
    }

    /**
     * <p>update</p>
     *
     * @throws java.io.IOException if any.
     */
    @Override
    public void update() throws IOException {
        if (m_lastModified != m_noticeConfFile.lastModified()) {
            reload();
        }
    }
}
