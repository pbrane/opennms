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
package org.opennms.netmgt.rtc;

import org.opennms.core.utils.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Provides convenience methods for use the HTTP POST method.
 *
 * @author <A HREF="mailto:larry@opennms.org">Lawrence Karnowski </A>
 */
public abstract class HttpUtils {
	private static final Logger LOG = LoggerFactory.getLogger(HttpUtils.class);

    /** Default buffer size for reading data. (Default is one kilobyte.) */
    public final static int DEFAULT_POST_BUFFER_SIZE = 1024;

    public final static int DEFAULT_CONNECT_TIMEOUT = -1;

    /**
     * Post a given <code>InputStream</code> s data to a URL.
     *
     * @param url
     *            the <code>URL</code> to post to
     * @param dataStream
     *            an input stream containing the data to send
     * @return An <code>InputStream</code> that the programmer can read from
     * to get the HTTP server's response.
     * @throws java.io.IOException if any.
     */
    public static InputStream post(URL url, InputStream dataStream) throws IOException {
        return (post(url, dataStream, null, null, DEFAULT_POST_BUFFER_SIZE, DEFAULT_CONNECT_TIMEOUT));
    }

    /**
     * Post a given <code>InputStream</code> s data to a URL using BASIC
     * authentication and the given username and password.
     *
     * @param url
     *            the <code>URL</code> to post to
     * @param dataStream
     *            an input stream containing the data to send
     * @param username
     *            the username to use in the BASIC authentication
     * @param password
     *            the password to use in the BASIC authentication
     * @return An <code>InputStream</code> that the programmer can read from
     * to get the HTTP server's response.
     * @throws java.io.IOException if any.
     */
    public static InputStream post(URL url, InputStream dataStream, String username, String password) throws IOException {
        return (post(url, dataStream, username, password, DEFAULT_POST_BUFFER_SIZE, DEFAULT_CONNECT_TIMEOUT));
    }

    /**
     * Post a given <code>InputStream</code> s data to a URL using BASIC
     * authentication, the given username and password, and a buffer size.
     *
     * @param url
     *            the <code>URL</code> to post to
     * @param dataStream
     *            an input stream containing the data to send
     * @param username
     *            the username to use in the BASIC authentication
     * @param password
     *            the password to use in the BASIC authentication
     * @param bufSize
     *            the size of the buffer to read from <code>dataStream</code>
     *            and write to the HTTP server
     * @return An <code>InputStream</code> that the programmer can read from
     * to get the HTTP server's response.
     * @throws java.io.IOException if any.
     */
    public static InputStream post(URL url, InputStream dataStream, String username, String password, int bufSize, int timeout) throws IOException {
        if (url == null || dataStream == null) {
            throw new IllegalArgumentException("Cannot take null parameters.");
        }

        if (bufSize < 1) {
            throw new IllegalArgumentException("Cannot use zero or negative buffer size.");
        }

        if (!"http".equals(url.getProtocol())) {
            throw new IllegalArgumentException("Cannot use non-HTTP URLs.");
        }

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // in a post we both write output and read input
        conn.setDoOutput(true);
        conn.setDoInput(true);
        if (timeout > 0) {
            conn.setConnectTimeout(timeout);
        }

        try {
            // the name of this method is post after all
            conn.setRequestMethod("POST");
        } catch (java.net.ProtocolException e) {
            // this would really really really bad... when can you not use POST
            // in HTTP?
            throw new IllegalStateException("Could not set a HttpURLConnection's method to POST.");
        }

        // add the authorization header if the username and password were given
        if (username != null && password != null) {
            byte[] authBytes = (username + ":" + password).getBytes();
            String authString = new String(Base64.encodeBase64(authBytes));
            conn.setRequestProperty("Authorization", "Basic " + authString);
        }

        // get the out-going HTTP connection
        OutputStream ostream = conn.getOutputStream();

        // initialize a buffer to use to read and write
        byte[] b = new byte[bufSize];

        // write the given data stream over the out-going HTTP connection
        int bytesRead = dataStream.read(b, 0, bufSize);
        while (bytesRead > 0) {
            ostream.write(b, 0, bytesRead);
            bytesRead = dataStream.read(b, 0, bufSize);
        }

        // close the out-going HTTP connection
        ostream.close();

        // return the in-coming HTTP connection so the programmer can read the
        // response
        return (conn.getInputStream());
    }

    /**
     * Post a given <code>Reader</code> s data to a URL using BASIC
     * authentication, the given username and password, and a buffer size.
     *
     * @param url
     *            the <code>URL</code> to post to
     * @param dataReader
     *            an input reader containing the data to send
     * @param username
     *            the username to use in the BASIC authentication
     * @param password
     *            the password to use in the BASIC authentication
     * @param bufSize
     *            the size of the buffer to read from <code>dataStream</code>
     *            and write to the HTTP server
     * @return An <code>InputStream</code> that the programmer can read from
     * to get the HTTP server's response.
     * @throws java.io.IOException if any.
     */
    public static InputStream post(URL url, Reader dataReader, String username, String password, int bufSize, int timeout) throws IOException {
        if (url == null || dataReader == null) {
            throw new IllegalArgumentException("Cannot take null parameters.");
        }

        if (bufSize < 1) {
            throw new IllegalArgumentException("Cannot use zero or negative buffer size.");
        }

        if (!"http".equals(url.getProtocol())) {
            throw new IllegalArgumentException("Cannot use non-HTTP URLs.");
        }

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // in a post we both write output and read input
        conn.setDoOutput(true);
        conn.setDoInput(true);
        if (timeout > 0) {
            conn.setConnectTimeout(timeout);
        }

        try {
            // the name of this method is post after all
            conn.setRequestMethod("POST");
        } catch (java.net.ProtocolException e) {
            // this would really really really bad... when can you not use POST
            // in HTTP?
            throw new IllegalStateException("Could not set a HttpURLConnection's method to POST.");
        }

        // add the authorization header if the username and password were given
        if (username != null && password != null) {
            byte[] authBytes = (username + ":" + password).getBytes();
            String authString = new String(Base64.encodeBase64(authBytes));
            conn.setRequestProperty("Authorization", "Basic " + authString);
        }
        
        // set the mime type
        conn.setRequestProperty("Content-type", "text/xml; charset=\"utf-8\"");

        // get the out-going HTTP connection
        OutputStreamWriter ostream = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.US_ASCII);

        // log data
        LOG.debug("HTTP Post: Current time: {}", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.GregorianCalendar().getTime()));
        LOG.debug("Data posted:");

        // initialize a buffer to use to read and write
        char[] b = new char[bufSize];

        // write the given data stream over the out-going HTTP connection
        int bytesRead = dataReader.read(b, 0, bufSize);
        if (bytesRead > 0 && LOG.isDebugEnabled())
            LOG.debug(new String(b, 0, bytesRead));

        while (bytesRead > 0) {
            ostream.write(b, 0, bytesRead);
            bytesRead = dataReader.read(b, 0, bufSize);

            if (bytesRead > 0 && LOG.isDebugEnabled())
                LOG.debug(new String(b, 0, bytesRead));
        }

        // close the out-going HTTP connection
        ostream.close();

        // return the in-coming HTTP connection so the programmer can read the
        // response
        return (conn.getInputStream());
    }

}
