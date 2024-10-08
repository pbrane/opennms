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
package org.opennms.protocols.json.collector;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.opennms.netmgt.collection.api.CollectionAgent;
import org.opennms.netmgt.collection.support.builder.CollectionSetBuilder;
import org.opennms.netmgt.collection.support.builder.Resource;
import org.opennms.protocols.xml.collector.AbstractXmlCollectionHandler;
import org.opennms.protocols.xml.collector.UrlFactory;
import org.opennms.protocols.xml.config.Request;
import org.opennms.protocols.xml.config.XmlGroup;
import org.opennms.protocols.xml.config.XmlObject;
import org.opennms.protocols.xml.config.XmlSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * The Abstract Class JSON Collection Handler.
 * <p>All JsonCollectionHandler should extend this class.</p>
 * 
 * @author <a href="mailto:ronald.roskens@gmail.com">Ronald Roskens</a>
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public abstract class AbstractJsonCollectionHandler extends AbstractXmlCollectionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractJsonCollectionHandler.class);

    /**
     * Fill collection set.
     *
     * @param agent the agent
     * @param collectionSet the collection set
     * @param source the source
     * @param json the JSON Object
     * @throws ParseException the parse exception
     */
    @SuppressWarnings("unchecked")
    protected void fillCollectionSet(CollectionAgent agent, CollectionSetBuilder builder, XmlSource source, JSONObject json) throws ParseException {
        JXPathContext context = JXPathContext.newContext(json);
        for (XmlGroup group : source.getXmlGroups()) {
            LOG.debug("fillCollectionSet: getting resources for XML group {} using XPATH {}", group.getName(), group.getResourceXpath());
            Date timestamp = getTimeStamp(context, group);
            Iterator<Pointer> itr = context.iteratePointers(group.getResourceXpath());
            while (itr.hasNext()) {
                JXPathContext relativeContext = context.getRelativeContext(itr.next());
                String resourceName = getResourceName(relativeContext, group);
                LOG.debug("fillCollectionSet: processing XML resource {} of type {}", resourceName, group.getResourceType());
                final Resource collectionResource = getCollectionResource(agent, resourceName, group.getResourceType(), timestamp);
                LOG.debug("fillCollectionSet: processing resource {}", collectionResource);
                for (XmlObject object : group.getXmlObjects()) {
                    try {
                        final Object obj = object.map((String) relativeContext.getValue(object.getXpath(), String.class));
                        if (obj != null) {
                            builder.withAttribute(collectionResource, group.getName(), object.getName(), obj.toString(), object.getDataType());
                        }
                    } catch (JXPathException ex) {
                        LOG.warn("Unable to get value for {}: {}", object.getXpath(), ex.getMessage());
                    }
                }
                processXmlResource(builder, collectionResource, resourceName, group.getName());
            }
        }
    }

    /**
     * Gets the resource name.
     *
     * @param context the JXpath context
     * @param group the group
     * @return the resource name
     */
    private String getResourceName(JXPathContext context, XmlGroup group) {
        // Processing multiple-key resource name.
        if (group.hasMultipleResourceKey()) {
            List<String> keys = new ArrayList<>();
            for (String key : group.getXmlResourceKey().getKeyXpathList()) {
                LOG.debug("getResourceName: getting key for resource's name using {}", key);
                String keyName = (String)context.getValue(key);
                keys.add(keyName);
            }
            return StringUtils.join(keys, "_");
        }
        // If key-xpath doesn't exist or not found, a node resource will be assumed.
        if (group.getKeyXpath() == null) {
            return "node";
        }
        // Processing single-key resource name.
        LOG.debug("getResourceName: getting key for resource's name using {}", group.getKeyXpath());
        return (String)context.getValue(group.getKeyXpath());
    }

    /**
     * Gets the time stamp.
     * 
     * @param context the JXPath context
     * @param group the group
     * @return the time stamp
     */
    protected Date getTimeStamp(JXPathContext context, XmlGroup group) {
        if (group.getTimestampXpath() == null) {
            return null;
        }
        String pattern = group.getTimestampFormat() == null ? "yyyy-MM-dd HH:mm:ss" : group.getTimestampFormat();
        LOG.debug("getTimeStamp: retrieving custom timestamp to be used when updating RRDs using XPATH {} and pattern {}", group.getTimestampXpath(), pattern);
        Date date = null;
        String value = (String)context.getValue(group.getTimestampXpath());
        try {
            DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
            DateTime dateTime = dtf.parseDateTime(value);
            date = dateTime.toDate();
        } catch (Exception e) {
            LOG.warn("getTimeStamp: can't convert custom timestamp {} using pattern {}", value, pattern);
        }
        return date;
    }

    /**
     * Gets the JSON object.
     *
     * @param urlString the URL string
     * @param request the request
     * @return the JSON object
     * @throws Exception the exception
     */
    protected JSONObject getJSONObject(String urlString, Request request) throws Exception {
        InputStream is = null;
        URLConnection c = null;
        try {
            URL url = UrlFactory.getUrl(urlString, request);
            c = url.openConnection();
            is = c.getInputStream();
            StringWriter writer = new StringWriter();
            IOUtils.copy(is, writer);

            return wrapArray(JSONSerializer.toJSON(writer.toString()));

        } finally {
            IOUtils.closeQuietly(is);
            UrlFactory.disconnect(c);
        }
    }

    protected static JSONObject wrapArray(final JSON json) {
        if (json.isArray()) {
            final JSONObject wrapper = new JSONObject();
            wrapper.put("elements", json);
            return wrapper;

        } else {
            return (JSONObject) json;
        }
    }
}
