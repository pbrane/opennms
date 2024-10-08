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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.opennms.core.utils.ConfigFileConstants;
import org.opennms.core.xml.JaxbUtils;
import org.opennms.netmgt.config.vacuumd.Action;
import org.opennms.netmgt.config.vacuumd.ActionEvent;
import org.opennms.netmgt.config.vacuumd.AutoEvent;
import org.opennms.netmgt.config.vacuumd.Automation;
import org.opennms.netmgt.config.vacuumd.Statement;
import org.opennms.netmgt.config.vacuumd.Trigger;
import org.opennms.netmgt.config.vacuumd.VacuumdConfiguration;
import org.springframework.util.Assert;

/**
 * This is the singleton class used to load the configuration for the OpenNMS
 * Vacuumd process from the vacuumd-configuration xml file.
 *
 * <strong>Note: </strong>Users of this class should make sure the
 * <em>setReader()</em> method is called before calling any other method to ensure the
 * config is loaded before accessing other convenience methods.
 *
 * @author <a href="mailto:david@opennms.com">David Hustace </a>
 * @author <a href="mailto:brozow@opennms.com">Mathew Brozowski </a>
 * @author <a href="http://www.opennms.org/">OpenNMS </a>
 */
public final class VacuumdConfigFactory {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    /**
     * The singleton instance of this factory
     */
    private static VacuumdConfigFactory m_singleton = null;

    private static boolean m_loadedFromFile = false;

    /**
     * The config class loaded from the config file
     */
    private VacuumdConfiguration m_config;

    /**
     * <p>Constructor for VacuumdConfigFactory.</p>
     *
     * @param stream a {@link java.io.InputStream} object.
     */
    public VacuumdConfigFactory(InputStream stream) {
        m_config = JaxbUtils.unmarshal(VacuumdConfiguration.class, new InputStreamReader(stream));
    }

    /**
     * <p>
     * Constructor for VacuumdConfigFactory.
     * </p>
     *
     * Calling reload() on a instance created with method will have no effect.
     *
     * @param config
     *          The configuration the use.
     */
    public VacuumdConfigFactory(VacuumdConfiguration config) {
        m_config = config;
        m_loadedFromFile = false;
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
        if (m_singleton != null) {
            /*
             * The init method has already called, so return.
             * To reload, reload() will need to be called.
             */
            return;
        }

        InputStream is = null;

        try {
            is = new FileInputStream(ConfigFileConstants.getFile(ConfigFileConstants.VACUUMD_CONFIG_FILE_NAME));
            setInstance(new VacuumdConfigFactory(is));
        } finally {
            if (is != null) {
                IOUtils.closeQuietly(is);
            }
        }
        
        m_loadedFromFile = true;
    }

    /**
     * Reload the config from the default config file
     *
     * @exception java.io.IOException
     *                Thrown if the specified config file cannot be read/loaded
     * @throws java.io.IOException if any.
     */
    public static synchronized void reload() throws IOException {
        if (m_loadedFromFile) {
            setInstance(null);

            init();
        }
    }

    /**
     * Return the singleton instance of this factory.
     *
     * @return The current factory instance.
     * @throws java.lang.IllegalStateException
     *             Thrown if the factory has not yet been initialized.
     */
    public static synchronized VacuumdConfigFactory getInstance() {
        Assert.state(m_singleton != null, "The factory has not been initialized");

        return m_singleton;
    }
    
    /**
     * Set the singleton instance of this factory.
     *
     * @param instance The factory instance to set.
     */
    public static synchronized void setInstance(VacuumdConfigFactory instance) {
        m_singleton = instance;
    }
    
    /**
     * Returns a Collection of automations defined in the config
     *
     * @return a {@link java.util.Collection} object.
     */
    public synchronized Collection<Automation> getAutomations() {
        return m_config.getAutomations();
    }
    
	/**
	 * Returns a Collection of triggers defined in the config
	 *
	 * @return a {@link java.util.Collection} object.
	 */
	public synchronized Collection<Trigger> getTriggers() {
        return m_config.getTriggers();
    }

    /**
     * Returns a Collection of actions defined in the config
     *
     * @return a {@link java.util.Collection} object.
     */
    public synchronized Collection<Action> getActions() {
        return m_config.getActions();
    }

    /**
     * Returns a Collection of named events to that may have
     * been configured to be sent after an automation has run.
     *
     * @return a {@link java.util.Collection} object.
     */
    public synchronized Collection<AutoEvent> getAutoEvents() {
        return m_config.getAutoEvents();
    }

    /**
     * <p>getActionEvents</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public synchronized Collection<ActionEvent> getActionEvents() {
        return m_config.getActionEvents();
    }

    /**
     * <p>getPeriod</p>
     *
     * @return a int.
     */
    public synchronized int getPeriod() {
        return m_config.getPeriod();
    }

    /**
     * Returns a Trigger with a name matching the string parameter
     *
     * @param triggerName a {@link java.lang.String} object.
     * @return a {@link org.opennms.netmgt.config.vacuumd.Trigger} object.
     */
    public synchronized Trigger getTrigger(String triggerName) {
        for (Trigger trig : getTriggers()) {
            if (trig.getName().equals(triggerName)) {
                return trig;
            }
        }
        return null;
    }
    
    /**
     * Returns an Action with a name matching the string parmater
     *
     * @param actionName a {@link String} object.
     * @return a {@link org.opennms.netmgt.config.vacuumd.Action} object.
     */
    public synchronized Optional<Action> getAction(String actionName) {
        for (Action act : getActions()) {
            if (act.getName().equals(actionName)) {
                return Optional.of(act);
            }
        }
        return Optional.empty();
    }
    
    /**
     * Returns an Automation with a name matching the string parameter
     *
     * @param autoName a {@link java.lang.String} object.
     * @return a {@link org.opennms.netmgt.config.vacuumd.Automation} object.
     */
    public synchronized Automation getAutomation(String autoName) {
        for (Automation auto : getAutomations()) {
            if (auto.getName().equals(autoName)) {
                return auto;
            }
        }
        return null;
    }
    
    /**
     * Returns the AutoEvent associated with the auto-event-name
     *
     * @deprecated Use {@link ActionEvent} objects instead. Access these objects with {@link #getActionEvent(String)}.
     * @param name a {@link java.lang.String} object.
     * @return a {@link org.opennms.netmgt.config.vacuumd.AutoEvent} object.
     */
    public synchronized AutoEvent getAutoEvent(String name) {
        for (AutoEvent ae : getAutoEvents()) {
            if (ae.getName().equals(name)) {
                return ae;
            }
        }
        return null;
    }

    /**
     * <p>getSqlStatements</p>
     *
     * @return an array of {@link java.lang.String} objects.
     */
    public synchronized String[] getSqlStatements() {
        return m_config.getStatements().parallelStream()
            .map(Statement::getContent)
            .collect(Collectors.toList()).toArray(EMPTY_STRING_ARRAY);
    }
    
    /**
     * <p>getStatements</p>
     *
     * @return a {@link java.util.List} object.
     */
    public synchronized List<Statement> getStatements() {
    	return m_config.getStatements();
    }

    /**
     * <p>getActionEvent</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link org.opennms.netmgt.config.vacuumd.ActionEvent} object.
     */
    public ActionEvent getActionEvent(String name) {
        for (ActionEvent actionEvent : getActionEvents()) {
            if (actionEvent.getName().equals(name)) {
                return actionEvent;
            }
        }
        return null;
    }
}
