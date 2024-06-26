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
package org.opennms.netmgt.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennms.core.spring.BeanUtils;
import org.opennms.core.test.OpenNMSJUnit4ClassRunner;
import org.opennms.core.test.db.annotations.JUnitTemporaryDatabase;
import org.opennms.netmgt.dao.api.NodeDao;
import org.opennms.netmgt.dao.api.SnmpInterfaceDao;
import org.opennms.netmgt.model.OnmsSnmpInterface;
import org.opennms.test.JUnitConfigurationEnvironment;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * UpsertTest
 *
 * @author brozow
 */
@RunWith(OpenNMSJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:/META-INF/opennms/applicationContext-soa.xml",
        "classpath:/META-INF/opennms/applicationContext-dao.xml",
        "classpath:/META-INF/opennms/applicationContext-mockConfigManager.xml",
        "classpath*:/META-INF/opennms/component-dao.xml",
        "classpath:/META-INF/opennms/applicationContext-databasePopulator.xml",
        "classpath:/upsertTest-context.xml",
        "classpath:/META-INF/opennms/applicationContext-commonConfigs.xml",
        "classpath:/META-INF/opennms/applicationContext-minimal-conf.xml"
})
@JUnitConfigurationEnvironment
@JUnitTemporaryDatabase
public class UpsertIT implements InitializingBean {
    
    @Autowired
    UpsertService m_upsertService;

    @Autowired
    NodeDao m_nodeDao;
    
    @Autowired
    SnmpInterfaceDao m_snmpIfaceDao;
    
    @Autowired
    JdbcTemplate m_jdbcTemplate;
    
    @Autowired
    DatabasePopulator m_populator;
    
    @Autowired
    TransactionTemplate m_transTemplate;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        BeanUtils.assertAutowiring(this);
    }

    @Before
    public void setUp() {
        m_populator.populateDatabase();
    }
    
    @Test
    @JUnitTemporaryDatabase
    public void testInsert() {
        String newIfName = "newIf0";
        assertEquals(0, countIfs(m_populator.getNode1().getId(), 1001, newIfName));

        // add non existent snmpiface
        OnmsSnmpInterface snmpIface = new OnmsSnmpInterface();
        snmpIface.setNode(m_populator.getNode1());
        snmpIface.setIfIndex(1001);
        snmpIface.setIfName(newIfName);
        
        m_upsertService.upsert(m_populator.getNode1().getId() /* nodeid */, snmpIface, 0);
        
        assertEquals(1, countIfs(m_populator.getNode1().getId(), 1001, newIfName));
    }
    
    private int countIfs(int nodeId, int ifIndex, String ifName) {
        return m_jdbcTemplate.queryForObject("select count(*) from snmpInterface where nodeid=? and snmpifindex=? and snmpifname=?", new Object[] { nodeId, ifIndex, ifName }, Integer.class);
    }
    
    @Test
    @JUnitTemporaryDatabase
    public void testUpdate() {
        String oldIfName = "eth0";
        String newIfName = "newIf0";
        assertEquals(1, countIfs(m_populator.getNode1().getId(), 2, oldIfName));
        assertEquals(0, countIfs(m_populator.getNode1().getId(), 2, newIfName));
        
        // add non existent snmpiface
        OnmsSnmpInterface snmpIface = new OnmsSnmpInterface();
        snmpIface.setIfIndex(2);
        snmpIface.setIfName(newIfName);
        
        m_upsertService.upsert(m_populator.getNode1().getId(), snmpIface, 0);

        assertEquals(0, countIfs(m_populator.getNode1().getId(), 2, oldIfName));
        assertEquals(1, countIfs(m_populator.getNode1().getId(), 2, newIfName));
    }
    
    @Test
    @JUnitTemporaryDatabase
    public void testConcurrentInsert() throws InterruptedException {
        Inserter one = new Inserter(m_upsertService, m_populator.getNode1().getId(), 1001, "ifName1");
        Inserter two = new Inserter(m_upsertService, m_populator.getNode1().getId(), 1001, "ifName2");
        
        one.start();
        two.start();
        
        one.join();
        two.join();
        
        assertNull("Exception on upsert two "+two.getThrowable(), two.getThrowable());
        assertNull("Exception on upsert one "+one.getThrowable(), one.getThrowable());
    }

    private static class Inserter extends Thread {
        private final UpsertService m_upsertService;
        private final int m_nodeId;
        private final int m_ifIndex;
        private final String m_ifName;
        private AtomicReference<Throwable> m_throwable = new AtomicReference<>();
        
        public Inserter(UpsertService upsertService, int nodeId, int ifIndex, String ifName) {
            m_upsertService = upsertService;
            m_nodeId = nodeId;
            m_ifIndex = ifIndex;
            m_ifName = ifName;
        }
        
        @Override
        public void run() {
            try {
                OnmsSnmpInterface snmpIface = new OnmsSnmpInterface();
                snmpIface.setIfIndex(m_ifIndex);
                snmpIface.setIfName(m_ifName);
                m_upsertService.upsert(m_nodeId, snmpIface, 1000);
            } catch(Throwable t) {
                t.printStackTrace();
                m_throwable.set(t);
            }
        }
        
        public Throwable getThrowable() {
            return m_throwable.get();
        }
    }
}
