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
package org.opennms.netmgt.config.discovery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.core.xml.ValidateUsing;

@XmlRootElement(name="definition")
@XmlAccessorType(XmlAccessType.FIELD)
@ValidateUsing("discovery-configuration.xsd")
public class Definition implements Serializable {

    private static final long serialVersionUID = 5369200192316960658L;

    @XmlAttribute(name = "location")
    private String location;

    /**
     * The number of times a ping is retried for this
     *  specific address. If there is no response after the first ping
     *  to an address, it is tried again for the specified number of
     *  retries. This retry count overrides the default.
     */
    @XmlAttribute(name = "retries")
    private Integer retries;

    /**
     * The timeout on each poll for this specific
     *  address. This timeout overrides the default.
     */
    @XmlAttribute(name = "timeout")
    private Long timeout;

    @XmlAttribute(name = "foreign-source")
    private String foreignSource;

    @XmlElementWrapper(name = "detectors")
    @XmlElement(name = "detector")
    private List<Detector> detectors = new ArrayList<>();
    /**
     * the specific addresses for discovery
     */
    @XmlElement(name = "specific")
    private List<Specific> specifics = new ArrayList<>();

    /**
     * the range of addresses for discovery
     */
    @XmlElement(name = "include-range")
    private List<IncludeRange> includeRanges = new ArrayList<>();

    /**
     * the range of addresses to be excluded from the
     * discovery
     */
    @XmlElement(name = "exclude-range")
    private List<ExcludeRange> excludeRanges = new ArrayList<>();

    /**
     * a file URL holding specific addresses to be
     *  polled
     */
    @XmlElement(name = "include-url")
    private List<IncludeUrl> includeUrls = new ArrayList<>();

    /**
     * a file URL holding specific addresses to be
     *  excluded
     */
    @XmlElement(name = "exclude-url")
    private List<ExcludeUrl> excludeUrls = new ArrayList<>();


    public Optional<String> getLocation() {
        return Optional.ofNullable(location);
    }

    public String getLocationName() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Optional<Integer> getRetries() {
        return Optional.ofNullable(this.retries);
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Optional<Long> getTimeout() {
        return Optional.ofNullable(this.timeout);
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public String getForeignSource() {
        return foreignSource;
    }

    public void setForeignSource(String foreignSource) {
        this.foreignSource = foreignSource;
    }

    public List<Specific> getSpecifics() {
        return specifics;
    }

    public void setSpecifics(List<Specific> specifics) {
        this.specifics = specifics;
    }

    public void addSpecific(Specific specific) {
        this.specifics.add(specific);
    }

    public List<IncludeRange> getIncludeRanges() {
        return includeRanges;
    }

    public void setIncludeRanges(List<IncludeRange> includeRanges) {
        this.includeRanges = includeRanges;
    }

    public void addIncludeRange(IncludeRange includeRange) {
        this.includeRanges.add(includeRange);
    }

    public List<ExcludeRange> getExcludeRanges() {
        return excludeRanges;
    }

    public void setExcludeRanges(List<ExcludeRange> excludeRanges) {
        this.excludeRanges = excludeRanges;
    }

    public void addExcludeRange(ExcludeRange excludeRange) {
        this.excludeRanges.add(excludeRange);
    }

    public List<Detector> getDetectors() {
        return detectors;
    }

    public void setDetectors(List<Detector> detectors) {
        this.detectors = detectors;
    }

    public void addDetector(Detector detector) {
        this.detectors.add(detector);
    }

    public List<IncludeUrl> getIncludeUrls() {
        return includeUrls;
    }

    public void setIncludeUrls(final List<IncludeUrl> includeUrls) {
        if (includeUrls == this.includeUrls) return;
        this.includeUrls.clear();
        if (includeUrls != null) this.includeUrls.addAll(includeUrls);
    }

    public void addIncludeUrl(final IncludeUrl includeUrl) {
        includeUrls.add(includeUrl);
    }

    public boolean removeIncludeUrl(final IncludeUrl includeUrl) {
        return includeUrls.remove(includeUrl);
    }

    public List<ExcludeUrl> getExcludeUrls() {
        return excludeUrls;
    }

    public void setExcludeUrls(final List<ExcludeUrl> excludeUrls) {
        if (excludeUrls.equals(this.excludeUrls)) return;
        this.excludeUrls.clear();
        if (excludeUrls != null) this.excludeUrls.addAll(excludeUrls);
    }

    public void addExcludeUrl(final ExcludeUrl excludeUrl) {
        excludeUrls.add(excludeUrl);
    }

    public boolean removeExcludeUrl(final ExcludeUrl excludeUrl) {
        return excludeUrls.remove(excludeUrl);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Definition that = (Definition) o;
        return Objects.equals(location, that.location) &&
                Objects.equals(specifics, that.specifics) &&
                Objects.equals(includeRanges, that.includeRanges) &&
                Objects.equals(excludeRanges, that.excludeRanges) &&
                Objects.equals(detectors, that.detectors) &&
                Objects.equals(includeUrls, that.includeUrls) &&
                Objects.equals(excludeUrls, that.excludeUrls);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, specifics, includeRanges, excludeRanges, detectors, includeUrls, excludeUrls);
    }
}
