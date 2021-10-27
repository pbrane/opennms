/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2021 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2021 The OpenNMS Group, Inc.
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

package org.opennms.web.rest.v1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.springframework.stereotype.Component;

@Component
@Path("filesystem")
public class FilesystemRestService {

    private static final List<String> FILES = Arrays.asList(
            "availability-reports.xml",
            "bsf-northbounder-configuration.xml",
            "collectd-configuration.xml",
            "discovery-configuration.xml",
            "drools-northbounder-configuration.xml",
            "http-datacollection-config.xml",
            "jdbc-datacollection-config.xml",
            "log4j2.xml",
            "poller-configuration.xml",
            "provisiond-configuration.xml",
            "rancid-configuration.xml",
            "snmp-asset-adapter-configuration.xml",
            "statsd-configuration.xml",
            "syslogd-configuration.xml",
            "telemetryd-configuration.xml",
            "trend-configuration.xml",
            "viewsdisplay.xml",
            "wmi-config.xml",
            "wsman-asset-adapter-configuration.xml",
            "wsman-config.xml",
            "snmp-config.xml",
            "notifd-configuration.xml",
            "snmptrap-northbounder-configuration.xml",
            "elastic-credentials.xml",
            "ksc-performance-reports.xml",
            "ifttt-config.xml",
            "rtc-configuration.xml",
            "enlinkd-configuration.xml",
            "microblog-configuration.xml",
            "snmp-metadata-adapter-configuration.xml",
            "datacollection-config.xml",
            "javamail-configuration.xml",
            "threshd-configuration.xml",
            "ackd-configuration.xml",
            "tl1d-configuration.xml",
            "log4j2-tools.xml",
            "eventd-configuration.xml",
            "surveillance-views.xml",
            "opennms-activemq.xml",
            "nsclient-config.xml",
            "trapd-configuration.xml",
            "vmware-datacollection-config.xml",
            "nsclient-datacollection-config.xml",
            "jmx-datacollection-config.xml",
            "users.xml",
            "destinationPaths.xml",
            "snmp-hardware-inventory-adapter-configuration.xml",
            "prometheus-datacollection.d/node-exporter.xml",
            "xmp-datacollection-config.xml",
            "thresholds.xml",
            "translator-configuration.xml",
            "vacuumd-configuration.xml",
            "eventconf.xml",
            "site-status-views.xml",
            "email-northbounder-configuration.xml",
            "notifications.xml",
            "xml-datacollection-config.xml",
            "remote-repository.xml",
            "jmx-config.xml",
            "vmware-config.xml",
            "ami-config.xml",
            "syslog-northbounder-configuration.xml",
            "service-configuration.xml",
            "notificationCommands.xml",
            "actiond-configuration.xml",
            "prometheus-datacollection-config.xml",
            "vmware-cim-datacollection-config.xml",
            "jasper-reports.xml",
            "scriptd-configuration.xml",
            "reportd-configuration.xml",
            "tca-datacollection-config.xml",
            "categories.xml",
            "database-reports.xml",
            "wsman-datacollection-config.xml",
            "poll-outages.xml",
            "rws-configuration.xml",
            "snmp-interface-poller-configuration.xml",
            "wmi-datacollection-config.xml",
            "chart-configuration.xml",
            "search-actions.xml",
            "xmp-config.xml",
            // non-xml
            "javamail-configuration.properties",
            "org.opennms.features.topology.app.cfg"
    ); static {
        FILES.sort(Comparator.naturalOrder());
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getFiles() {
        return FILES;
    }

    @GET
    @Path("/contents")
    public Response getFileContents(@QueryParam("f") String fileName) throws IOException {
        if (!FILES.contains(fileName)) {
            throw new RuntimeException("Unsupported filename: '" + fileName + "'");
        }
        final File file = Paths.get(System.getProperty("opennms.home"), "etc", fileName).toFile();
        final String mimeType = Files.probeContentType(file.toPath());
        return streamAll(file, mimeType, fileName);
    }

    @POST
    @Path("/contents")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String uploadFile(@QueryParam("f") String fileName,
                             @Multipart("upload") Attachment attachment) throws IOException {
        if (!FILES.contains(fileName)) {
            throw new RuntimeException("Unsupported filename: '" + fileName + "'");
        }

        final java.nio.file.Path path = Paths.get(System.getProperty("opennms.home"), "etc", fileName);
        InputStream in = attachment.getObject(InputStream.class);
        Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        return "uploaded " + fileName;
    }

    private Response streamAll(final File file, String mimeType, String fileName) {
        return Response.ok(file)
                .type(mimeType)
                .header(HttpHeaders.CONTENT_DISPOSITION, fileName)
                .header(HttpHeaders.CONTENT_LENGTH, file.length())
                .header(HttpHeaders.LAST_MODIFIED, new Date(file.lastModified()))
                .build();
    }

}
