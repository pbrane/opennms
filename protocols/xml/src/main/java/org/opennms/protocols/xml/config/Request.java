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
package org.opennms.protocols.xml.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class Request.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
@XmlRootElement(name="request")
@XmlAccessorType(XmlAccessType.FIELD)
public class Request implements Cloneable {

    /** The method. */
    @XmlAttribute
    private String method = "GET";

    /** The parameters. */
    @XmlElement(name="parameter")
    private List<Parameter> parameters = new ArrayList<>();

    /** The headers. */
    @XmlElement(name="header")
    private List<Header> headers = new ArrayList<>();

    /** The content. */
    @XmlElement
    private Content content;

    public Request() { }

    public Request(Request copy) {
        method = copy.method;
        copy.parameters.stream().forEach(p -> parameters.add(p.clone()));
        copy.headers.stream().forEach(h -> headers.add(h.clone()));
        content = copy.content != null ? copy.content.clone() : null;
    }

    /**
     * Gets the method.
     *
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Gets the parameters.
     *
     * @return the parameters
     */
    public List<Parameter> getParameters() {
        return parameters;
    }

    /**
     * Gets the value of a specific parameter.
     *
     * @param name the name
     * @return the parameter
     */
    public String getParameter(String name) {
        for (Parameter p : parameters) {
            if (p.getName().equals(name)) {
                return p.getValue();
            }
        }
        return null;
    }

    /**
     * Gets the parameter as integer.
     *
     * @param name the name
     * @return the parameter value as integer
     */
    public int getParameterAsInt(String name) {
        try {
            return Integer.parseInt(getParameter(name));
        } catch (NumberFormatException e) {
            return -1;
        }

    }

    public boolean getParameterAsBoolean(String name) {
        return Boolean.valueOf(getParameter(name));
    }

    /**
     * Gets the headers.
     *
     * @return the headers
     */
    public List<Header> getHeaders() {
        return headers;
    }

    /**
     * Gets the value of a specific header.
     *
     * @param name the name
     * @return the header value
     */
    public String getHeader(String name) {
        for (Header h : headers) {
            if (h.getName().equals(name)) {
                return h.getValue();
            }
        }
        return null;
    }

    /**
     * Gets the content.
     *
     * @return the content
     */
    public Content getContent() {
        return content;
    }

    /**
     * Sets the method.
     *
     * @param method the new method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Sets the parameters.
     *
     * @param parameters the new parameters
     */
    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    /**
     * Adds the parameter.
     *
     * @param name the name
     * @param value the value
     */
    public void addParameter(String name, String value) {
        getParameters().add(new Parameter(name, value));
    }

    /**
     * Sets the headers.
     *
     * @param headers the new headers
     */
    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    /**
     * Adds the header.
     *
     * @param name the name
     * @param value the value
     */
    public void addHeader(String name, String value) {
        getHeaders().add(new Header(name, value));
    }

    /**
     * Sets the content.
     *
     * @param content the new content
     */
    public void setContent(Content content) {
        this.content = content;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Request [method=" + method + ", parameters=" + parameters + ", headers=" + headers + ", content=" + content + "]";
    }

    @Override
    public Request clone() {
        return new Request(this);
    }
}