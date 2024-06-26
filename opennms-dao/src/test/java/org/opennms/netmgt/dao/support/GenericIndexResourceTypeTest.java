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
package org.opennms.netmgt.dao.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opennms.netmgt.collection.api.StorageStrategy;
import org.opennms.netmgt.dao.api.NodeDao;
import org.opennms.netmgt.dao.api.ResourceDao;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.model.OnmsResource;
import org.opennms.netmgt.rrd.NullRrdStrategy;
import org.opennms.test.FileAnticipator;

import com.google.common.base.Throwables;

public class GenericIndexResourceTypeTest {

    private static final String RRD_FILE_NAME = "ds.nullRrd";

    private FileAnticipator m_fileAnticipator;

    private FilesystemResourceStorageDao m_resourceStorageDao;
    private StorageStrategy m_storageStrategy = mock(StorageStrategy.class);
    private NodeDao m_nodeDao = mock(NodeDao.class);
    private ResourceDao m_resourceDao = mock(ResourceDao.class);

    @Before
    public void setUp() throws IOException {
        m_fileAnticipator = new FileAnticipator();
        
        m_resourceStorageDao = new FilesystemResourceStorageDao();
        m_resourceStorageDao.setRrdDirectory(m_fileAnticipator.getTempDir());
        m_resourceStorageDao.setRrdStrategy(new NullRrdStrategy());
    }

    @After
    public void tearDown() {
        verifyNoMoreInteractions(m_storageStrategy);
        verifyNoMoreInteractions(m_nodeDao);
        verifyNoMoreInteractions(m_resourceDao);

        if (m_fileAnticipator.isInitialized()) {
            m_fileAnticipator.deleteExpected();
        }

        m_fileAnticipator.tearDown();
    }

    @Test
    public void testGetResourceByNodeAndIndexGetLabelPlain() throws IOException {
        GenericIndexResourceType rt = new GenericIndexResourceType(m_resourceStorageDao, "foo", "Foo Resource", "plain", m_storageStrategy);

        touch("snmp", "1", "foo", "1", RRD_FILE_NAME);

        OnmsResource resource = rt.getChildByName(getNodeResource(1), "1");

        assertNotNull("resource", resource);
        assertEquals("resource label", "plain", resource.getLabel());
    }

    @Test
    public void testGetResourceByNodeAndIndexGetLabelIndex() {
        GenericIndexResourceType rt = new GenericIndexResourceType(m_resourceStorageDao, "foo", "Foo Resource", "${index}", m_storageStrategy);

        touch("snmp", "1", "foo", "1", RRD_FILE_NAME);
        
        OnmsResource resource = rt.getChildByName(getNodeResource(1), "1");
        
        assertNotNull("resource", resource);
        assertEquals("resource label", "1", resource.getLabel());
    }

    @Test
    public void testGetResourceByNodeAndIndexGetLabelStringAttribute() throws Exception {
        GenericIndexResourceType rt = new GenericIndexResourceType(m_resourceStorageDao, "foo", "Foo Resource", "${stringAttribute}", m_storageStrategy);

        File rrd = touch("snmp", "1", "foo", "1", RRD_FILE_NAME);
        m_fileAnticipator.tempFile(rrd.getParentFile(), RrdResourceAttributeUtils.STRINGS_PROPERTIES_FILE_NAME, "stringAttribute=hello!!!!");

        OnmsResource resource = rt.getChildByName(getNodeResource(1), "1");
        
        assertNotNull("resource", resource);
        assertEquals("resource label", "hello!!!!", resource.getLabel());
    }

    @Test
    public void testGetResourceByNodeSourceAndIndexGetLabelStringAttribute() throws IOException {
        try {
            System.setProperty("org.opennms.rrd.storeByForeignSource", "true");

            GenericIndexResourceType rt = new GenericIndexResourceType(m_resourceStorageDao, "foo", "Foo Resource", "${stringAttribute}", m_storageStrategy);

            File rrd = touch("snmp", "fs", "source1", "123", "foo", "1", RRD_FILE_NAME);
            m_fileAnticipator.tempFile(rrd.getParentFile(), RrdResourceAttributeUtils.STRINGS_PROPERTIES_FILE_NAME, "stringAttribute=hello!!!!");

            OnmsResource resource = rt.getChildByName(getNodeResource("source1", "123"), "1");

            assertNotNull("resource", resource);
            assertEquals("resource label", "hello!!!!", resource.getLabel());
        } finally {
            System.setProperty("org.opennms.rrd.storeByForeignSource", "false");
        }
    }

    @Test
    public void testGetResourceByNodeAndIndexGetLabelIndexWithSubIndexNumber() {
        GenericIndexResourceType rt = new GenericIndexResourceType(m_resourceStorageDao, "foo", "Foo Resource", "${subIndex(3, 1)}", m_storageStrategy);

        touch("snmp", "1", "foo", "1.2.3.4", RRD_FILE_NAME);

        OnmsResource resource = rt.getChildByName(getNodeResource(1), "1.2.3.4");
        
        assertNotNull("resource", resource);
        assertEquals("resource label", "4", resource.getLabel());
    }

    @Test
    public void testGetResourceByNodeAndIndexGetLabelIndexWithSubIndexBogusArguments() {
        GenericIndexResourceType rt = new GenericIndexResourceType(m_resourceStorageDao, "foo", "Foo Resource", "${subIndex(absolutely bogus)}", m_storageStrategy);

        touch("snmp", "1", "foo", "1.2.3.4", RRD_FILE_NAME);

        OnmsResource resource = rt.getChildByName(getNodeResource(1), "1.2.3.4");
        
        assertNotNull("resource", resource);
        assertEquals("resource label", "${subIndex(absolutely bogus)}", resource.getLabel());
    }

    @Test
    public void testGetResourceByNodeAndIndexGetLabelIndexWithSubIndexBogusOffset() {
        GenericIndexResourceType rt = new GenericIndexResourceType(m_resourceStorageDao, "foo", "Foo Resource", "${subIndex(foo, 1)}", m_storageStrategy);

        touch("snmp", "1", "foo", "1.2.3.4", RRD_FILE_NAME);

        OnmsResource resource = rt.getChildByName(getNodeResource(1), "1.2.3.4");

        assertNotNull("resource", resource);
        assertEquals("resource label", "${subIndex(foo, 1)}", resource.getLabel());
    }

    @Test
    public void testGetResourceByNodeAndIndexGetLabelIndexWithSubIndexBadNumber() {
        GenericIndexResourceType rt = new GenericIndexResourceType(m_resourceStorageDao, "foo", "Foo Resource", "${subIndex(4, 1)}", m_storageStrategy);

        touch("snmp", "1", "foo", "1.2.3.4", RRD_FILE_NAME);

        OnmsResource resource = rt.getChildByName(getNodeResource(1), "1.2.3.4");

        assertNotNull("resource", resource);
        assertEquals("resource label", "${subIndex(4, 1)}", resource.getLabel());
    }

    @Test
    public void testGetResourceByNodeAndIndexGetLabelIndexWithSubIndexBeginning() {
        GenericIndexResourceType rt = new GenericIndexResourceType(m_resourceStorageDao, "foo", "Foo Resource", "${subIndex(1)}", m_storageStrategy);

        touch("snmp", "1", "foo", "1.2.3.4", RRD_FILE_NAME);

        OnmsResource resource = rt.getChildByName(getNodeResource(1), "1.2.3.4");
        
        assertNotNull("resource", resource);
        assertEquals("resource label", "2.3.4", resource.getLabel());
    }

    @Test
    public void testGetResourceByNodeAndIndexGetLabelIndexWithSubIndexEnding() {
        GenericIndexResourceType rt = new GenericIndexResourceType(m_resourceStorageDao, "foo", "Foo Resource", "${subIndex(0, 3)}", m_storageStrategy);

        touch("snmp", "1", "foo", "1.2.3.4", RRD_FILE_NAME);

        OnmsResource resource = rt.getChildByName(getNodeResource(1), "1.2.3.4");
        
        assertNotNull("resource", resource);
        assertEquals("resource label", "1.2.3", resource.getLabel());
    }

    @Test
    public void testGetResourceByNodeAndIndexGetLabelIndexWithSubIndexNoArguments() {
        GenericIndexResourceType rt = new GenericIndexResourceType(m_resourceStorageDao, "foo", "Foo Resource", "${subIndex()}", m_storageStrategy);

        touch("snmp", "1", "foo", "1.2.3.4", RRD_FILE_NAME);

        OnmsResource resource = rt.getChildByName(getNodeResource(1), "1.2.3.4");
        
        assertNotNull("resource", resource);
        assertEquals("resource label", "${subIndex()}", resource.getLabel());
    }

    @Test
    public void testGetResourceByNodeAndIndexGetLabelIndexWithSubIndexStartOutOfBounds() {
        GenericIndexResourceType rt = new GenericIndexResourceType(m_resourceStorageDao, "foo", "Foo Resource", "${subIndex(4)}", m_storageStrategy);

        touch("snmp", "1", "foo", "1.2.3.4", RRD_FILE_NAME);

        OnmsResource resource = rt.getChildByName(getNodeResource(1), "1.2.3.4");
        
        assertNotNull("resource", resource);
        assertEquals("resource label", "${subIndex(4)}", resource.getLabel());
    }

    @Test
    public void testGetResourceByNodeAndIndexGetLabelIndexWithSubIndexEndOutOfBounds() {
        GenericIndexResourceType rt = new GenericIndexResourceType(m_resourceStorageDao, "foo", "Foo Resource", "${subIndex(0, 5)}", m_storageStrategy);

        touch("snmp", "1", "foo", "1.2.3.4", RRD_FILE_NAME);

        OnmsResource resource = rt.getChildByName(getNodeResource(1), "1.2.3.4");
        
        assertNotNull("resource", resource);
        assertEquals("resource label", "${subIndex(0, 5)}", resource.getLabel());
    }

    @Test
    public void testGetResourceByNodeAndIndexGetLabelIndexWithHexConversion() {
        GenericIndexResourceType rt = new GenericIndexResourceType(m_resourceStorageDao, "foo", "Foo Resource", "${hex(index)}", m_storageStrategy);

        touch("snmp", "1", "foo", "1.2.3.4.14.15", RRD_FILE_NAME);

        OnmsResource resource = rt.getChildByName(getNodeResource(1), "1.2.3.4.14.15");
        
        assertNotNull("resource", resource);
        assertEquals("resource label", "01:02:03:04:0E:0F", resource.getLabel());
    }

    @Test
    public void testGetResourceByNodeAndIndexGetLabelIndexWithHexConversionBogusInteger() {
        GenericIndexResourceType rt = new GenericIndexResourceType(m_resourceStorageDao, "foo", "Foo Resource", "${hex(index)}", m_storageStrategy);

        touch("snmp", "1", "foo", "foo", RRD_FILE_NAME);

        OnmsResource resource = rt.getChildByName(getNodeResource(1), "foo");
        
        assertNotNull("resource", resource);
        assertEquals("resource label", "${hex(index)}", resource.getLabel());
    }

    /**
     * Test for enhancement in bug #2467.
     */
    @Test
    public void testGetResourceByNodeAndIndexGetLabelIndexWithSubStringAndHexConversion() {
        GenericIndexResourceType rt = new GenericIndexResourceType(m_resourceStorageDao, "foo", "Foo Resource", "MAC Address ${hex(subIndex(0, 6))} on interface ${subIndex(6, 1)}", m_storageStrategy);

        touch("snmp", "1", "foo", "0.21.109.80.9.66.4", RRD_FILE_NAME);

        OnmsResource resource = rt.getChildByName(getNodeResource(1), "0.21.109.80.9.66.4");
        
        assertNotNull("resource", resource);
        assertEquals("resource label", "MAC Address 00:15:6D:50:09:42 on interface 4", resource.getLabel());
    }

    @Test
    public void testGetResourceByNodeAndIndexGetLabelIndexWithSubStringOfDynamicLength() {
        GenericIndexResourceType rt = new GenericIndexResourceType(m_resourceStorageDao, "foo", "Foo Resource", "Easy as ${subIndex(0, n)}", m_storageStrategy);

        touch("snmp", "1", "foo", "5.1.2.3.4.5", RRD_FILE_NAME);

        OnmsResource resource = rt.getChildByName(getNodeResource(1), "5.1.2.3.4.5");
        
        assertNotNull("resource", resource);
        assertEquals("resource label", "Easy as 1.2.3.4.5", resource.getLabel());
    }

    @Test
    public void testGetResourceByNodeAndIndexGetLabelIndexWithThreeSubStringsOfDynamicLength() {
        GenericIndexResourceType rt = new GenericIndexResourceType(m_resourceStorageDao, "foo", "Foo Resource", "Easy as ${subIndex(0, n)} and ${subIndex(n, n)} and ${subIndex(n, n)}", m_storageStrategy);

        touch("snmp", "1", "foo", "1.1.2.1.2.3.1.2.3", RRD_FILE_NAME);

        OnmsResource resource = rt.getChildByName(getNodeResource(1), "1.1.2.1.2.3.1.2.3");
        
        assertNotNull("resource", resource);
        assertEquals("resource label", "Easy as 1 and 1.2 and 1.2.3", resource.getLabel());
    }

    @Test
    public void testGetResourceByNodeAndIndexGetLabelIndexWithSubStringAndDynSubStringAndDynSubStringAndSubStringToEnd() {
        GenericIndexResourceType rt = new GenericIndexResourceType(m_resourceStorageDao, "foo", "Foo Resource", "Easy as ${subIndex(0, 1)} and ${subIndex(1, n)} and ${subIndex(n, n)} and ${subIndex(n)}", m_storageStrategy);

        touch("snmp", "1", "foo", "3.3.1.2.3.3.4.5.6.0", RRD_FILE_NAME);

        OnmsResource resource = rt.getChildByName(getNodeResource(1), "3.3.1.2.3.3.4.5.6.0");
        
        assertNotNull("resource", resource);
        assertEquals("resource label", "Easy as 3 and 1.2.3 and 4.5.6 and 0", resource.getLabel());
    }

    @Test
    public void testGetResourceByNodeAndIndexGetLabelIndexWithDisplaySubStringOfDynamicLength() {
        GenericIndexResourceType rt = new GenericIndexResourceType(m_resourceStorageDao, "foo", "Foo Resource", "Easy as ${string(subIndex(0, n))}", m_storageStrategy);

        touch("snmp", "1", "foo", "3.112.105.101", RRD_FILE_NAME);

        OnmsResource resource = rt.getChildByName(getNodeResource(1), "3.112.105.101");
        
        assertNotNull("resource", resource);
        assertEquals("resource label", "Easy as pie", resource.getLabel());
    }

    @Test
    public void testGetResourceByNodeAndIndexGetLabelIndexWithSubStringAndTwoDisplaySubStringsOfDynamicLengthAndSubStringToEnd() {
        GenericIndexResourceType rt = new GenericIndexResourceType(m_resourceStorageDao, "foo", "Foo Resource", "Easy as ${subIndex(0, 1)} piece of ${string(subIndex(1, n))} or just under ${string(subIndex(n, n))} pieces of ${subIndex(n)}", m_storageStrategy);

        touch("snmp", "1", "foo", "1.3.112.105.101.2.80.105.3.1.4.1.5.9", RRD_FILE_NAME);

        OnmsResource resource = rt.getChildByName(getNodeResource(1), "1.3.112.105.101.2.80.105.3.1.4.1.5.9");
        
        assertNotNull("resource", resource);
        assertEquals("resource label", "Easy as 1 piece of pie or just under Pi pieces of 3.1.4.1.5.9", resource.getLabel());
    }

    @Test
    public void testGetResourceByNodeAndIndexGetLabelIndexWithBogusUseOfNforStartOfFirstSubIndex() {
        GenericIndexResourceType rt = new GenericIndexResourceType(m_resourceStorageDao, "foo", "Foo Resource", "Easy as ${subIndex(n, 3)}", m_storageStrategy);

        touch("snmp", "1", "foo", "3.1.2.3", RRD_FILE_NAME);

        OnmsResource resource = rt.getChildByName(getNodeResource(1), "3.1.2.3");
        
        assertNotNull("resource", resource);
        assertEquals("resource label", "Easy as ${subIndex(n, 3)}", resource.getLabel());
    }

    private OnmsResource getNodeResource(int nodeId) {
        final NodeResourceType nodeResourceType = new NodeResourceType(m_resourceDao, m_nodeDao);
        OnmsNode node = new OnmsNode();
        node.setId(nodeId);
        node.setLabel("Node"+ nodeId);
        return nodeResourceType.createResourceForNode(node);
    }

    private OnmsResource getNodeResource(String fs, String fid) {
        NodeResourceType nodeSourceResourceType = new NodeResourceType(m_resourceDao, m_nodeDao);
        OnmsNode node = new OnmsNode();
        node.setId(0);
        node.setLabel("Node");
        node.setForeignSource(fs);
        node.setForeignId(fid);
        return nodeSourceResourceType.createResourceForNode(node);
    }

    private File touch(String first, String... more) {
        Path parent = m_fileAnticipator.getTempDir().toPath();
        Path path = parent.resolve(Paths.get(first, more));
        File file = path.toFile();
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
        assertTrue(file.canRead());
        return file;
    }
}
