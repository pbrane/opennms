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
package org.opennms.netmgt.notifd;

import static org.opennms.core.web.HttpClientWrapperConfigHelper.PARAMETER_KEYS.useSystemProxy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.opennms.core.db.DataSourceFactory;
import org.opennms.core.utils.MatchTable;
import org.opennms.core.utils.PropertiesUtils;
import org.opennms.core.web.HttpClientWrapper;
import org.opennms.netmgt.config.NotificationManager;
import org.opennms.netmgt.model.notifd.Argument;
import org.opennms.netmgt.model.notifd.NotificationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * <p>HttpNotificationStrategy class.</p>
 *
 * @author <a href="mailto:david@opennms.org">David Hustace</a>
 * @version $Id: $
 */
public class HttpNotificationStrategy implements NotificationStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(HttpNotificationStrategy.class);

    private List<Argument> m_arguments;

    /* (non-Javadoc)
     * @see org.opennms.netmgt.notifd.NotificationStrategy#send(java.util.List)
     */
    /** {@inheritDoc} */
    @Override
    public int send(List<Argument> arguments) {

        m_arguments = arguments;

        String url = getUrl();
        if (url == null) {
            LOG.warn("send: url argument is null, HttpNotification requires a URL");
            return 1;
        }

        final HttpClientWrapper clientWrapper = HttpClientWrapper.create()
                .setConnectionTimeout(3000)
                .setSocketTimeout(3000);
        if(getUseSystemProxy()) {
             clientWrapper.useSystemProxySettings();
        }
        HttpUriRequest method = null;
        final List<NameValuePair> posts = getPostArguments();

        if (posts == null) {
            method = new HttpGet(url);
            LOG.info("send: No \"post-\" arguments..., continuing with an HTTP GET using URL: {}", url);
        } else {
            LOG.info("send: Found \"post-\" arguments..., continuing with an HTTP POST using URL: {}", url);
            for (final NameValuePair post : posts) {
                LOG.debug("send: post argument: {} = {}", post.getName(), post.getValue());
            }
            method = new HttpPost(url);
            final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(posts, StandardCharsets.UTF_8);
            ((HttpPost)method).setEntity(entity);
        }

        String contents = null;
        int statusCode = -1;
        try {
            CloseableHttpResponse response = clientWrapper.execute(method);
            statusCode = response.getStatusLine().getStatusCode();
            contents = EntityUtils.toString(response.getEntity());
            LOG.info("send: Contents is: {}", contents);
        } catch (IOException e) {
            LOG.error("send: IO problem with HTTP post/response: {}", e);
            throw new RuntimeException("Problem with HTTP post: "+e.getMessage());
        } finally {
            IOUtils.closeQuietly(clientWrapper);
        }

        doSql(contents);

        return statusCode;
    }

    void setArguments(List<Argument> arguments){
        m_arguments = arguments;
    }

    private void doSql(String contents) {
        if (getSql() == null) {
            LOG.info("send: optional sql argument is null.");
            return;
        }

        if (contents == null) {
            LOG.info("doSql: HTTP reply is null");
            return;
        }

        LOG.debug("send: compiling expression: {}", getSwitchValue("result-match"));
        Pattern p = Pattern.compile(getSwitchValue("result-match"));
        Matcher m = p.matcher(contents);
        if (m.matches()) {
            LOG.debug("send: compiled expression ready to run sql: {}", getSql());
            MatchTable matches = new MatchTable(m);
            String sqlString = PropertiesUtils.substitute(getSql(), matches);
            LOG.debug("send: running sql: {}", sqlString);
            JdbcTemplate template = new JdbcTemplate(DataSourceFactory.getInstance());
            template.execute(sqlString);
        } else {
            LOG.info("send: result didn't match, not running sql");
        }
    }

    private List<NameValuePair> getPostArguments() {
        List<Argument> args = getArgsByPrefix("post-");
        List<NameValuePair> retval = new ArrayList<>();
        for (Argument arg : args) {
            String argSwitch = arg.getSwitch().substring("post-".length());
            if (arg.getValue() == null) {
                arg.setValue("");
            }
            retval.add(new BasicNameValuePair(argSwitch, getValue(arg.getValue())));
        }
        return retval;
    }

    private String getValue(String argValue) {
        if (argValue.equals(NotificationManager.PARAM_DESTINATION))
            return getNotificationValue(NotificationManager.PARAM_DESTINATION);
        if (argValue.equals(NotificationManager.PARAM_EMAIL))
            return getNotificationValue(NotificationManager.PARAM_EMAIL);
        if (argValue.equals(NotificationManager.PARAM_HOME_PHONE))
            return getNotificationValue(NotificationManager.PARAM_HOME_PHONE);
        if (argValue.equals(NotificationManager.PARAM_INTERFACE))
            return getNotificationValue(NotificationManager.PARAM_INTERFACE);
        if (argValue.equals(NotificationManager.PARAM_MICROBLOG_USERNAME))
            return getNotificationValue(NotificationManager.PARAM_MICROBLOG_USERNAME);
        if (argValue.equals(NotificationManager.PARAM_MOBILE_PHONE))
            return getNotificationValue(NotificationManager.PARAM_MOBILE_PHONE);
        if (argValue.equals(NotificationManager.PARAM_NODE))
            return getNotificationValue(NotificationManager.PARAM_NODE);
        if (argValue.equals(NotificationManager.PARAM_NUM_MSG))
            return getNotificationValue(NotificationManager.PARAM_NUM_MSG);
        if (argValue.equals(NotificationManager.PARAM_NUM_PAGER_PIN))
            return getNotificationValue(NotificationManager.PARAM_NUM_PAGER_PIN);
        if (argValue.equals(NotificationManager.PARAM_PAGER_EMAIL))
            return getNotificationValue(NotificationManager.PARAM_PAGER_EMAIL);
        if (argValue.equals(NotificationManager.PARAM_RESPONSE))
            return getNotificationValue(NotificationManager.PARAM_RESPONSE);
        if (argValue.equals(NotificationManager.PARAM_SERVICE))
            return getNotificationValue(NotificationManager.PARAM_SERVICE);
        if (argValue.equals(NotificationManager.PARAM_SUBJECT))
            return getNotificationValue(NotificationManager.PARAM_SUBJECT);
        if (argValue.equals(NotificationManager.PARAM_TEXT_MSG))
            return getNotificationValue(NotificationManager.PARAM_TEXT_MSG);
        if (argValue.equals(NotificationManager.PARAM_TEXT_PAGER_PIN))
            return getNotificationValue(NotificationManager.PARAM_TEXT_PAGER_PIN);
        if (argValue.equals(NotificationManager.PARAM_TUI_PIN))
            return getNotificationValue(NotificationManager.PARAM_TUI_PIN);
        if (argValue.equals(NotificationManager.PARAM_TYPE))
            return getNotificationValue(NotificationManager.PARAM_TYPE);
        if (argValue.equals(NotificationManager.PARAM_WORK_PHONE))
            return getNotificationValue(NotificationManager.PARAM_WORK_PHONE);
        if (argValue.equals(NotificationManager.PARAM_XMPP_ADDRESS))
            return getNotificationValue(NotificationManager.PARAM_XMPP_ADDRESS);

        return argValue;
    }

    private String getNotificationValue(final String notificationManagerParamString) {
        String message = "no notification text message defined for the \""+notificationManagerParamString+"\" switch.";
        for (Iterator<Argument> it = m_arguments.iterator(); it.hasNext();) {
            Argument arg = it.next();
            if (arg.getSwitch().equals(notificationManagerParamString))
                message = arg.getValue();
        }
        LOG.debug("getNotificationValue: {}", message);
        return message;
    }

    private List<Argument> getArgsByPrefix(String argPrefix) {
        List<Argument> args = new ArrayList<>();
        for (Iterator<Argument> it = m_arguments.iterator(); it.hasNext();) {
            Argument arg = it.next();
            if (arg.getSwitch().startsWith(argPrefix)) {
                args.add(arg) ;
            }
        }
        return args;
    }

    private String getSql() {
        return getSwitchValue("sql");
    }

    boolean getUseSystemProxy(){
        return Boolean.parseBoolean(extractValue(useSystemProxy.name()));
    }

    String getUrl() {
        return extractValue("url");
    }

    private String extractValue(String name) {
        String value = getSwitchValue(name);
        if ( value == null )
            value = getValueAsPrefix(name);
        return value;
    }

    private String getValueAsPrefix(String argPrefix) {
        String value = null;
        for (Argument arg: getArgsByPrefix(argPrefix)) {
            LOG.debug("Found {} switch: {} with value: {}", argPrefix, arg.getSwitch(), arg.getValue());
            value = arg.getValue();
        }
        return value;
    }

    /**
     * Helper method to look into the Argument list and return the associated value.
     * If the value is an empty String, this method returns null.
     * @param argSwitch
     * @return
     */
    private String getSwitchValue(String argSwitch) {
        String value = null;
        for (Iterator<Argument> it = m_arguments.iterator(); it.hasNext();) {
            Argument arg = it.next();
            if (arg.getSwitch().equals(argSwitch)) {
                value = arg.getValue();
            }
        }
        if (value != null && value.equals(""))
            value = null;

        return value;
    }
}
