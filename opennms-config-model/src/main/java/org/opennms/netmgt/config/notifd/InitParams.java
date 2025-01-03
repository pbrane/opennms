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
package org.opennms.netmgt.config.notifd;


import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.core.xml.ValidateUsing;
import org.opennms.netmgt.config.utils.ConfigUtils;

@XmlRootElement(name = "init-params")
@XmlAccessorType(XmlAccessType.FIELD)
@ValidateUsing("notifd-configuration.xsd")
public class InitParams implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "param-name", required = true)
    private String m_paramName;

    @XmlElement(name = "param-value", required = true)
    private String m_paramValue;

    public InitParams() {
    }

    public String getParamName() {
        return m_paramName;
    }

    public void setParamName(final String paramName) {
        m_paramName = ConfigUtils.assertNotEmpty(paramName, "param-name");
    }

    public String getParamValue() {
        return m_paramValue;
    }

    public void setParamValue(final String paramValue) {
        m_paramValue = ConfigUtils.assertNotEmpty(paramValue, "param-value");
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_paramName, m_paramValue);
    }

    @Override
    public boolean equals(final Object obj) {
        if ( this == obj ) {
            return true;
        }

        if (obj instanceof InitParams) {
            final InitParams that = (InitParams)obj;
            return Objects.equals(this.m_paramName, that.m_paramName)
                    && Objects.equals(this.m_paramValue, that.m_paramValue);
        }
        return false;
    }

}
