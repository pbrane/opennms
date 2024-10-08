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
package org.opennms.netmgt.jasper.measurement.remote;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.opennms.core.test.Level;
import org.opennms.core.test.LoggingEvent;
import org.opennms.core.test.MockLogAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.google.common.io.ByteStreams;

/**
 * Verifies that the {@link MeasurementApiClient} connects accordingly to the OpenNMS Measurement API and may
 * deal with OpenNMS specifics.
 */
@net.jcip.annotations.NotThreadSafe
public class MeasurementApiConnectorIT {

    private static final Logger LOG = LoggerFactory.getLogger(MeasurementApiClientTest.class);

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.wireMockConfig()
            .keystorePath(System.getProperty("javax.net.ssl.keyStore"))
            .keystorePassword(System.getProperty("javax.net.ssl.keyStorePassword"))
            .dynamicPort().dynamicHttpsPort());

    @BeforeClass
    public static void beforeClass() {
        String[] keys = new String[]{
                "ssl.debug",
                "javax.net.debug",
                "javax.net.ssl.keyStore",
                "javax.net.ssl.keyStorePassword",
                "javax.net.ssl.trustStore",
                "javax.net.ssl.trustStorePassword"};
        for (String eachKey : keys) {
            String value = eachKey.toLowerCase().contains("password") ? "*****" : System.getProperty(eachKey);
            LOG.error("{} = {}", eachKey, value);
        }
    }

    @Before
    public void before() {
        // OK Requests
        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/opennms/rest/measurements"))
                .withHeader("Accept", WireMock.equalTo("application/xml"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBody("<response>Some content</response>")));

        // Forward Requests
        WireMock.stubFor(WireMock.post(WireMock.urlMatching("/opennms/rest/forward/.*"))
                .willReturn(WireMock.aResponse()
                        .withStatus(302)));

        // 500 Requests
        WireMock.stubFor(WireMock.post(WireMock.urlMatching("/opennms/rest/bad/.*"))
                .willReturn(WireMock.aResponse()
                        .withStatus(500)
                        .withBody("This did not work as you might have expected, ugh?")));

        // Slow response
        WireMock.stubFor(WireMock.post(WireMock.urlMatching("/opennms/rest/measurements/slow-response"))
                .willReturn(WireMock.aResponse().withFixedDelay(5000)));

        // Map everything else to a 404 Response
        WireMock.stubFor(WireMock.any(WireMock.anyUrl())
                .atPriority(10)
                .willReturn(WireMock.aResponse()
                        .withStatus(404)
                        .withBody("{\"status\":\"Error\",\"message\":\"Endpoint not found\"}")));
    }

    @After
    public void tearDown() {
        WireMock.reset();
    }

    @Test
    public void test200() throws IOException {
        Result result = new MeasurementApiClient().execute(false, "http://localhost:" + wireMockRule.port() + "/opennms/rest/measurements", null, null, "<dummy request>");
        Assert.assertTrue(result.wasSuccessful());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteStreams.copy(result.getInputStream(), outputStream);
        Assert.assertEquals("<response>Some content</response>", outputStream.toString());

        verifyWiremock();
    }

    @Test
    public void test404() throws IOException {
        Result result = new MeasurementApiClient().execute(false, "http://localhost:" + wireMockRule.port() + "/opennms/rest/doesNotExist", null, null, "<dummy request>");
        Assert.assertFalse(result.wasSuccessful());
        Assert.assertEquals(404, result.getResponseCode());
        Assert.assertEquals("Not Found", result.getResponseMessage());

        verifyWiremock("/opennms/rest/doesNotExist");
    }

    /**
     * OpenNMS sometimes forwards to the index.jsp if not logged in, which would result in a success of the request
     * in general. We do not want that. This test checks that a 302 is not automatically forwarded.
     */
    @Test
    public void test302() throws IOException {
        Result result = new MeasurementApiClient().execute(false, "http://localhost:" + wireMockRule.port() + "/opennms/rest/forward/me", null, null, "<dummy request>");
        Assert.assertFalse(result.wasSuccessful());
        Assert.assertTrue(result.wasRedirection());
        Assert.assertEquals(302, result.getResponseCode());
        Assert.assertNull(result.getInputStream());
        Assert.assertNull(result.getErrorStream());

        verifyWiremock("/opennms/rest/forward/me");
    }

    @Test
    public void test500() throws IOException {
        Result result = new MeasurementApiClient().execute(false, "http://localhost:" + wireMockRule.port() + "/opennms/rest/bad/request", null, null, "<dummy request>");
        Assert.assertFalse(result.wasSuccessful());
        Assert.assertFalse(result.wasRedirection());
        Assert.assertEquals(500, result.getResponseCode());
        Assert.assertNull(result.getInputStream());
        Assert.assertNotNull(result.getErrorStream());

        verifyWiremock("/opennms/rest/bad/request");
    }

    @Test
    public void testAuthentication() throws IOException {
        Result result = new MeasurementApiClient().execute(false, "http://localhost:" + wireMockRule.port() + "/opennms/rest/measurements", "admin", "admin", "<dummy request>");
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertFalse(result.wasRedirection());
        Assert.assertEquals(200, result.getResponseCode());
        Assert.assertNotNull(result.getInputStream());
        Assert.assertNull(result.getErrorStream());

        RequestPatternBuilder requestPatternBuilder = createDefaultRequestPatternBuilder("/opennms/rest/measurements");
        requestPatternBuilder.withHeader("Authorization", WireMock.matching("Basic .*"));

        verifyWiremock(requestPatternBuilder);
    }

    // Tests a delay before receiving any result
    @Test
    public void testSlowConnection() throws IOException {
        Result result = new MeasurementApiClient().execute(false, "http://localhost:" + wireMockRule.port() + "/opennms/rest/measurements/slow-response", null, null, "<dummy request>");
        Assert.assertTrue(result.wasSuccessful());
    }

    // we connect to localhost on a wrong port to trigger a ConnectException
    @Test(expected=ConnectException.class)
    public void testConnectException() throws IOException {
        new MeasurementApiClient().execute(false, "http://localhost:1234/opennms/rest/measurements", null, null, "<dummy request>");
    }

    // We connect and expect a timeout.
    // We also verify that the timeout is as defined (including a tolerance)
    @Test(expected=SocketTimeoutException.class)
    public void testTimeout() throws IOException {
        WireMock.setGlobalFixedDelay(5000);
        long start = System.currentTimeMillis();
        try {
            new MeasurementApiClient(MeasurementApiClient.CONNECT_TIMEOUT, 2500).execute(false, "http://localhost:" + wireMockRule.port() + "/opennms/rest/measurements", null, null, "<dummy request>");
        } catch (SocketTimeoutException ex) {
            long diff = System.currentTimeMillis() - start;
            long offset = 500; // ms
            Assert.assertEquals(2500d, (double) diff, (double) offset);
            throw ex;
        } finally {
            WireMock.reset();
        }
    }

    // Verifies that a https call can be made
    @Test
    public void testHttpsOk() throws IOException, InterruptedException {
        Result result = new MeasurementApiClient().execute(true, "https://localhost:" + wireMockRule.httpsPort() + "/opennms/rest/measurements", null, null, "<dummy request>");
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertTrue(result.wasSecureConnection());
        Assert.assertNotNull(result.getInputStream());
        Assert.assertNull(result.getErrorStream());

        verifyWiremock();
    }

    // Verifies that a https call cannot be made to an unknown server (certificate)
    @Test(expected=SSLHandshakeException.class)
    public void testHttpsUnknown() throws IOException {
        new MeasurementApiClient().execute(true, "https://127.0.0.1:" + wireMockRule.httpsPort() + "/opennms/rest/measurements", null, null, "<dummy request>");
    }

    // Verifies that even if useSSL = false, when connecting to a valid https url the connection is secure
    @Test
    public void testHttpsUrlButUseSslNotSet() throws IOException {
        Result result = new MeasurementApiClient().execute(false, "https://localhost:" + wireMockRule.httpsPort() + "/opennms/rest/measurements", null, null, "<dummy request>");
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertTrue(result.wasSecureConnection());
        Assert.assertNotNull(result.getInputStream());
        Assert.assertNull(result.getErrorStream());

        LoggingEvent[] warnEvents = MockLogAppender.getEventsAtLevel(Level.WARN);
        boolean found = false;
        for (LoggingEvent eachEvent : warnEvents) {
            if (MeasurementApiClient.class.getName().equals(eachEvent.getLoggerName())) {
                Assert.assertTrue(eachEvent.getMessage().contains("A secure connection was established even if it was not intended."));
                Assert.assertTrue(eachEvent.getMessage().contains("Use SSL = false"));
                Assert.assertTrue(eachEvent.getMessage().contains("URL = https://localhost:" + wireMockRule.httpsPort() + "/opennms/rest/measurements"));
                found = true;
            }
        }
        Assert.assertTrue("Expected to have a warn message about using ssl even if it was not enabled manually", found);
    }

    // Verifies that if SSL is enabled and we connect to a HTTP connection, a SSLException is thrown by our client.
    @Test(expected=SSLException.class)
    public void testHttpUrlButUseSslSet() throws IOException {
        new MeasurementApiClient().execute(true, "http://localhost:" + wireMockRule.port() + "/opennms/rest/measurements", null, null, "<dummy request>");
    }

    // We do not need this test, but I leave it for now
    @Test(expected=SSLException.class)
    public void testConectToHttpPortUsingHttpsProtocol() throws IOException {
        new MeasurementApiClient().execute(true, "https://localhost:" + wireMockRule.port() + "/opennms/rest/measurements", null, null, "<dummy request>");
    }

    // Verify that an empty URL throws an expected IllegalArgumentException
    @Test(expected=IllegalArgumentException.class)
    public void testEmptyUrl() throws IOException {
        new MeasurementApiClient().execute(false, "", null, null, null);
    }

    // Verify that a null URL throws an expected IllegalArgumentException
    @Test(expected=IllegalArgumentException.class)
    public void testNullUrl() throws IOException {
        new MeasurementApiClient().execute(false, null, null, null, null);
    }

    // Verify that a null query throws an expected IllegalArgumentException
    @Test(expected=IllegalArgumentException.class)
    public void testNullQuery() throws IOException {
        new MeasurementApiClient().execute(false, "http://localhost", null, null, null);
    }

    // Verify that an empty query throws an expected IllegalArgumentException
    @Test(expected=IllegalArgumentException.class)
    public void testEmptyQuery() throws IOException {
        new MeasurementApiClient().execute(false, "http://localhost", null, null, "");
    }

    private void verifyWiremock() {
        verifyWiremock("/opennms/rest/measurements");
    }

    private void verifyWiremock(String url) {
        verifyWiremock(createDefaultRequestPatternBuilder(url));
    }

    private void verifyWiremock(RequestPatternBuilder builder) {
        WireMock.verify(builder);
    }

    private RequestPatternBuilder createDefaultRequestPatternBuilder(String url) {
        return WireMock.postRequestedFor(WireMock.urlMatching(url))
                .withRequestBody(WireMock.matching("<dummy request>"))
                .withHeader("Content-Type", WireMock.equalTo("application/xml"))
                .withHeader("Accept-Charset", WireMock.equalTo(StandardCharsets.UTF_8.name()));
    }
}
