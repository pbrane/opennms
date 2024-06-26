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
package org.opennms.reporting.availability;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.opennms.core.db.DataSourceFactory;
import org.opennms.core.test.MockLogAppender;
import org.opennms.core.test.db.MockDatabase;
import org.opennms.core.xml.JaxbUtils;
import org.opennms.netmgt.config.CategoryFactory;
import org.opennms.netmgt.config.DatabaseSchemaConfigFactory;
import org.opennms.netmgt.dao.api.MonitoringLocationDao;
import org.opennms.netmgt.filter.FilterDaoFactory;
import org.opennms.netmgt.mock.MockCategoryFactory;
import org.opennms.reporting.availability.svclayer.LegacyAvailabilityDataService;

import junit.framework.TestCase;

public class AvailabilityReportIT extends TestCase {

    protected MockDatabase m_db;
    protected Categories m_categories; 
    protected Calendar calendar;
    protected MockCategoryFactory m_catFactory;

    @Override
    protected void setUp() throws Exception {
        System.err.println("------------------- begin "+getName()+" ---------------------");

        // Reset the FilterDaoFactory so we don't get screwed by having the JdbcFilterDao be connected to an older database
        FilterDaoFactory.setInstance(null);

        super.setUp();
        Locale.setDefault(Locale.US);
        calendar = new GregorianCalendar();
        //date for report run is 18th May 2005
        calendar.set(2005, 4, 18);
        MockLogAppender.setupLogging();
        m_categories = new Categories();

        m_db = new MockDatabase();
        DataSourceFactory.setInstance(m_db);

        DatabaseSchemaConfigFactory.init();

        m_catFactory = new MockCategoryFactory();
        CategoryFactory.setInstance(m_catFactory);
        m_db.update("insert into node (location, nodeID, nodelabel, nodeCreateTime, nodeType) values ('" + MonitoringLocationDao.DEFAULT_MONITORING_LOCATION_ID + "', 1,'test1.availability.opennms.org','2004-03-01 09:00:00','A')");
        m_db.update("insert into node (location, nodeID, nodelabel, nodeCreateTime, nodeType) values ('" + MonitoringLocationDao.DEFAULT_MONITORING_LOCATION_ID + "', 2,'test2.availability.opennms.org','2004-03-01 09:00:00','A')");

        m_db.update("insert into service (serviceid, servicename) values\n" +
        "(1, 'ICMP');");
        m_db.update("insert into service (serviceid, servicename) values\n" +	
        "(2, 'HTTP');");
        m_db.update("insert into service (serviceid, servicename) values\n" +	
        "(3, 'SNMP');");

        m_db.update("insert into ipinterface (id, nodeid, ipaddr, ismanaged) values\n" +
        "(1, 1,'192.168.100.1','M');");
        m_db.update("insert into ipinterface (id, nodeid, ipaddr, ismanaged) values\n" +		
        "(2, 2,'192.168.100.2','M');");
        m_db.update("insert into ipinterface (id, nodeid, ipaddr, ismanaged) values\n" +		
        "(3, 2,'192.168.100.3','M');");

        m_db.update("insert into ifservices (id, serviceid, status, ipInterfaceId) values " +
        "(1,1,'A',1);"); // 1:192.168.100.1:1
        m_db.update("insert into ifservices (id, serviceid, status, ipInterfaceId) values " +
        "(2,1,'A',2);"); // 2:192.168.100.2:1
        /*
        m_db.update("insert into ifservices (id, serviceid, status, ipInterfaceId) values " +
        "(3,2,'A',2);"); // 2:192.168.100.2:2
        */
        m_db.update("insert into ifservices (id, serviceid, status, ipInterfaceId) values " +
        "(4,1,'A',3);"); // 2:192.168.100.3:1

        m_db.update("insert into outages (outageid, ifServiceId, ifLostService, ifRegainedService) values " +
        "(1,1,'2005-05-01 09:00:00','2005-05-01 09:30:00');");
        m_db.update("insert into outages (outageid, ifServiceId, ifLostService, ifRegainedService) values " +
        "(2,2,'2005-05-01 10:00:00','2005-05-02 10:00:00');");

        // test data for LastMonthsDailyAvailability report

        // insert 30 minute outage on one node - 99.3056% availability
        m_db.update("insert into outages (outageid, ifServiceId, ifLostService, ifRegainedService) values " +
        "(3,1,'2005-04-02 10:00:00','2005-04-02 10:30:00');");
        // insert 60 minute outage on one interface and 59 minute outages on another - 97.2454
        m_db.update("insert into outages (outageid, ifServiceId, ifLostService, ifRegainedService) values " +
        "(4,1,'2005-04-03 11:30:00','2005-04-03 12:30:00');");
        m_db.update("insert into outages (outageid, ifServiceId, ifLostService, ifRegainedService) values " +
        "(5,2,'2005-04-03 23:00:00','2005-04-03 23:59:00');");
        // test an outage that spans 60 minutes across midnight - 99.3056% on each day, well, not exactly
        // its 29 minutes 99.3059 on the fist day and 31 minutes 99.3052 on the second.
        m_db.update("insert into outages (outageid, ifServiceId, ifLostService, ifRegainedService) values " +
        "(6,4,'2005-04-04 23:30:00','2005-04-05 00:30:00');");

    }

    private Section getSectionByName (Category category, String sectionName) {

        Section match = null;

        CatSections[] catSections = category.getCatSections();
        for(int i= 0; i < catSections.length; i++) {
            Section[] section = catSections[i].getSection();
            for(int j= 0; j < section.length; j++) {
                if ( sectionName.equals(section[j].getSectionName()) ) {
                    match = section[j];
                }
            }
        }			

        return match;
    }


    private Day getCalSectionDay(Category category, String title, int row, int col) {

        Section calSection = getSectionByName(category, title);
        CalendarTable table = calSection.getCalendarTable();
        Week week = table.getWeek(row);
        return week.getDay(col);

    }


    // helper method to round to 4 decimal places.

    private double fourDec(double number) {
        return (Math.round(number * 10000.0)) / 10000.0;
    }

    /*
    private int numRowsWithValue(Section section, String title, String data){

        int rowMatched = 0;
        boolean titlematch;
        boolean datamatch;

        ClassicTable table = section.getClassicTable();
        Rows rows = table.getRows();
        Row[] row = rows.getRow();
        for(int j= 0; j < row.length; j++) {
            Value[] value = row[j].getValue();
            titlematch = false;
            datamatch = false;
            for(int k= 0; k < value.length; k++){
                if (value[k].getType().equals("title") &&
                        value[k].getContent().equals(title))
                    titlematch = true;	
                if (value[k].getType().equals("data") &&
                        value[k].getContent().equals(data))
                    datamatch = true;
                if (datamatch && titlematch)
                    rowMatched++;
            }	
        }			
        return rowMatched; 
    }
    */
    
    private Report buildReport(Calendar calendar, String calFormat){


        Report report = new Report();
        report.setLogo("wherever");
        ViewInfo viewInfo = new ViewInfo();
        report.setViewInfo(viewInfo);
        report.setCategories(m_categories);
        // AvailabilityData availData = null;
        try {
            /* it seems we just initialize this to make sure it works
             * availData =
             */
            AvailabilityData reportSource = new AvailabilityData();
            reportSource.setAvailabilityDataService(new LegacyAvailabilityDataService());
            reportSource.fillReport("Network Interfaces", report, "HTML", calFormat, "4", "18", "2005");
        } catch (Throwable e) {
            throw new UndeclaredThrowableException(e); 
        }
        return report;
    }

    public void testMyDatabase () {
        assertEquals("node DB count", 2, m_db.countRows("select * from node"));
        assertEquals("service DB count", 3, m_db.countRows("select * from service"));
        assertEquals("ip interface DB count", 3, m_db.countRows("select * from ipinterface"));	
        assertEquals("interface services DB count", 3, m_db.countRows("select * from ifservices"));
//      assertEquals("outages DB count", 3, m_db.countRows("select * from outages"));
        assertEquals("ip interface where ipaddr = 192.168.100.1 count", 1, m_db.countRows("select * from ipinterface where ipaddr = '192.168.100.1'"));
        assertEquals("number of interfaces returned from IPLIKE",3, m_db.countRows("select * from ipinterface where iplike(ipaddr,'192.168.100.*')"));

    }

    public void testBuiltClassicReport () throws IOException {

        Report report = buildReport(calendar,"classic");
        
        assertNotNull("report", report);

        Writer fileWriter = new OutputStreamWriter(System.out, StandardCharsets.UTF_8);
        JaxbUtils.marshal(report, fileWriter);

        Categories categories = report.getCategories();
        assertNotNull("report categories", report.getCategories());
        
        Category category = categories.getCategory(0);
        assertEquals("category count", 1,categories.getCategoryCount());

        // basic tests
        assertEquals("category node count", Integer.valueOf(2),category.getNodeCount());
        assertEquals("category ip address count", Integer.valueOf(3), category.getIpaddrCount());
        assertEquals("category service count", Integer.valueOf(3), category.getServiceCount());

        Section section = getSectionByName(category,"LastMonthsDailyAvailability");
        assertNull("calendar table", section.getCalendarTable());


    }
    public void testBuiltCalendarReport () {

        Calendar calendar = new GregorianCalendar(2005,4,20);
        long oneHundred = 100;
        Day day;
        
        Report report = buildReport(calendar,"calendar");
        assertNotNull("report", report);
        
        Categories categories = report.getCategories();
        assertNotNull("report categories", report.getCategories());
        
        Category category = categories.getCategory(0);
        assertEquals("category count", 1,categories.getCategoryCount());

        assertEquals("category node count", Integer.valueOf(2),category.getNodeCount());
        assertEquals("category ip address count", Integer.valueOf(3),category.getIpaddrCount());
        assertEquals("category service count", Integer.valueOf(3),category.getServiceCount());

        // Section calSection = getSectionByName(category,"LastMonthsDailyAvailability");

        // First four days in month are invisible for US...

        day = getCalSectionDay(category,"LastMonthsDailyAvailability",0,0);
        assertNotNull("day 0,0 object", day);
        assertFalse("day 0,0 visibility", day.getVisible());
        
        day = getCalSectionDay(category,"LastMonthsDailyAvailability",0,1);
        assertNotNull("day 0,1 object", day);
        assertFalse("day 0,1 visibility", day.getVisible());
        
        day = getCalSectionDay(category,"LastMonthsDailyAvailability",0,2);
        assertNotNull("day 0,2 object", day);
        assertFalse("day 0,2 visibility", day.getVisible());
        
        day = getCalSectionDay(category,"LastMonthsDailyAvailability",0,4);
        assertNotNull("day 0,4 object", day);
        assertFalse("day 0,4 visibility", day.getVisible());

        day = getCalSectionDay(category,"LastMonthsDailyAvailability",0,5);
        assertNotNull("day 0,5 object", day);
        assertEquals("day 0,5 percentage value", oneHundred, day.getPctValue(), 0);
        assertTrue("day 0,5 visibility", day.getVisible());
        assertEquals("day 0,5 date", Integer.valueOf(1),day.getDate());

        day = getCalSectionDay(category,"LastMonthsDailyAvailability",0,6);
        assertNotNull("day 0,6 object", day);
        assertEquals("day 0,6 percentage value", 99.3056, fourDec(day.getPctValue()), 0);
        assertTrue("day 0,6 visibility", day.getVisible());
        assertEquals("day 0,6 date", Integer.valueOf(2),day.getDate());

        day = getCalSectionDay(category,"LastMonthsDailyAvailability",1,0);
        assertNotNull("day 1,0 object", day);
        assertEquals("day 1,0 percentage value", 97.2454, fourDec(day.getPctValue()), 0);
        assertTrue("day 1,0 visibility", day.getVisible());
        assertEquals("day 1,0 date", Integer.valueOf(3),day.getDate());

        day = getCalSectionDay(category,"LastMonthsDailyAvailability",1,1);
        assertNotNull("day 1,1 object", day);
        assertEquals("day 1,1 percentage value", 99.3056, fourDec(day.getPctValue()), 0);
        assertTrue("day 1,1 visibility", day.getVisible());
        assertEquals("day 1,1 date", Integer.valueOf(4),day.getDate());

        day = getCalSectionDay(category,"LastMonthsDailyAvailability",1,2);
        assertNotNull("day 1,2 object", day);
        assertEquals("day 1,2 percentage value", 99.3056, fourDec(day.getPctValue()), 0);
        assertTrue("day 1,2 visibility", day.getVisible());
        assertEquals("day 1,2 date", Integer.valueOf(5),day.getDate());

    }




    @Override
    protected void tearDown() throws Exception {
        m_db.drop();
        super.tearDown();

        System.err.println("------------------- end "+getName()+" -----------------------");
    }

}
