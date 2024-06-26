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
package org.opennms.features.mibcompiler;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsmiparser.parser.SmiDefaultParser;
import org.jsmiparser.smi.SmiMib;
import org.jsmiparser.smi.SmiModule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opennms.core.test.MockLogAppender;
import org.opennms.features.mibcompiler.api.MibParser;
import org.opennms.features.mibcompiler.services.JsmiMibParser;
import org.opennms.netmgt.xml.eventconf.Events;

/**
 * The Test Class for <a href="https://issues.opennms.org/browse/NMS-10609">NMS-10609</a>
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a> 
 */
public class NMS10609Test {

    /** The Constant MIB_DIR. */
    protected static final File MIB_DIR = new File("src/test/resources");

    /** The MIB parser. */
    protected MibParser parser;

    /**
     * Sets up the tests.
     */
    @Before
    public void setUp() {
        MockLogAppender.setupLogging();
        parser = new JsmiMibParser();
        parser.setMibDirectory(MIB_DIR);
    }

    public void tearDown() {
        MockLogAppender.assertNoWarningsOrGreater();
    }

    /**
     * Test standard parse.
     * <p>This test is to verify that the problem is not JsmiParser.</p>
     * 
     * @throws Exception the exception
     */
    @Test
    public void testStandardParse() throws Exception {
        SmiDefaultParser parser = new SmiDefaultParser();
        List<URL> inputUrls = new ArrayList<>();
        try {
            // Base MIBs
            inputUrls.add(new File(MIB_DIR, "SNMPv2-SMI.txt").toURI().toURL());
            inputUrls.add(new File(MIB_DIR, "SNMPv2-TC.txt").toURI().toURL());
            inputUrls.add(new File(MIB_DIR, "SNMPv2-CONF.txt").toURI().toURL());
            inputUrls.add(new File(MIB_DIR, "SNMPv2-MIB.txt").toURI().toURL());
            inputUrls.add(new File(MIB_DIR, "IANAifType-MIB.txt").toURI().toURL());
            inputUrls.add(new File(MIB_DIR, "IF-MIB.txt").toURI().toURL());
            inputUrls.add(new File(MIB_DIR, "SNMP-FRAMEWORK-MIB.txt").toURI().toURL());
            inputUrls.add(new File(MIB_DIR, "INET-ADDRESS-MIB.txt").toURI().toURL());
            // Custom MIBs
            inputUrls.add(new File(MIB_DIR, "HC-PerfHist-TC-MIB.my").toURI().toURL());
            inputUrls.add(new File(MIB_DIR, "ADSL2-LINE-TC-MIB.my").toURI().toURL());
            inputUrls.add(new File(MIB_DIR, "ADSL2-LINE-MIB.my").toURI().toURL());
            inputUrls.add(new File(MIB_DIR, "VDSL2-LINE-TC-MIB.my").toURI().toURL());
            inputUrls.add(new File(MIB_DIR, "VDSL2-LINE-MIB.my").toURI().toURL());
            inputUrls.add(new File(MIB_DIR, "ENTITY-MIB.my").toURI().toURL());
            inputUrls.add(new File(MIB_DIR, "PerfHist-TC-MIB.my").toURI().toURL());
            inputUrls.add(new File(MIB_DIR, "CISCO-SMI.my").toURI().toURL());
            inputUrls.add(new File(MIB_DIR, "CISCO-CBP-TC-MIB.my").toURI().toURL());
            inputUrls.add(new File(MIB_DIR, "CISCO-DYNAMIC-TEMPLATE-TC-MIB.my").toURI().toURL());
            inputUrls.add(new File(MIB_DIR, "CISCO-SUBSCRIBER-IDENTITY-TC-MIB.my").toURI().toURL());
            inputUrls.add(new File(MIB_DIR, "CISCO-SUBSCRIBER-SESSION-TC-MIB.my").toURI().toURL());
            inputUrls.add(new File(MIB_DIR, "CISCO-SUBSCRIBER-SESSION-MIB.my").toURI().toURL());
            inputUrls.add(new File(MIB_DIR, "PRIV-VENDORDEF-MIB.my").toURI().toURL());
            inputUrls.add(new File(MIB_DIR, "BATM-SWITCH-MIB.my").toURI().toURL());
            inputUrls.add(new File(MIB_DIR, "BATM-DRY-CONTACTS-MIB.my").toURI().toURL());
        } catch (Exception e) {
            Assert.fail();
        }
        parser.getFileParserPhase().setInputUrls(inputUrls);
        SmiMib mib = parser.parse();
        if (parser.getProblemEventHandler().isOk()) {
            Assert.assertNotNull(mib);
            Assert.assertTrue(mibExist(mib, "ADSL2-LINE-MIB"));
            Assert.assertTrue(mibExist(mib, "VDSL2-LINE-MIB"));
            Assert.assertTrue(mibExist(mib, "CISCO-SUBSCRIBER-SESSION-MIB"));
            Assert.assertTrue(mibExist(mib, "BATM-DRY-CONTACTS-MIB"));
        } else {
            Assert.fail("Couldn't be compiled: " + parser.getProblemEventHandler().getTotalCount() + " problems encountered");
        }
    }

    /**
     * Test events.
     */
    @Test
    public void testEvents() {
        verifyEvents("ADSL2-LINE-MIB.my");
        verifyEvents("VDSL2-LINE-MIB.my");
        verifyEvents("CISCO-SUBSCRIBER-SESSION-MIB.my");
        verifyEvents("BATM-DRY-CONTACTS-MIB.my");      
    }

    /**
     * Verify events.
     *
     * @param mibName the MIB name
     */
    public void verifyEvents(String mibName) {
        if (! parser.parseMib(new File(MIB_DIR, mibName))) {
            Assert.fail("The MIB " + mibName + " must parse successfully");
        } else {
            Assert.assertTrue(parser.getMissingDependencies().isEmpty());
        }
        Events events = parser.getEvents("uei.opennms.org/" + mibName + "/");
        Assert.assertNotNull(events);
        Assert.assertFalse(events.getEvents().isEmpty());
    }

    /**
     * Checks if a specific MIB exist on the SMI Object.
     *
     * @param mib the SMI MIB Object
     * @param mibName the MIB name
     * @return true, if exist
     */
    private boolean mibExist(SmiMib mib, String mibName) {
        for (SmiModule m : mib.getModules()) {
            if (m.getId().equals(mibName))
                return true;
        }
        return false;
    }

}
