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
package org.opennms.netmgt.config.surveillanceViews;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.core.xml.ValidateUsing;
import org.opennms.netmgt.config.utils.ConfigUtils;

@XmlRootElement(name = "column-def")
@XmlAccessorType(XmlAccessType.FIELD)
@ValidateUsing("surveillance-views.xsd")
public class ColumnDef implements Def, Serializable {
    private static final long serialVersionUID = 3L;

    @XmlAttribute(name = "label", required = true)
    private String m_label;

    @XmlAttribute(name = "report-category")
    private String m_reportCategory;

    /**
     * This element is used to specify OpenNMS specific categories. Note:
     * currently, these categories are defined in a separate configuration file
     * and are
     *  related directly to monitored services. I have separated out this element
     * so that it can be referenced by other entities (nodes, interfaces, etc.)
     *  however, they will be ignored until the domain model is changed and the
     * service layer is adapted for this behavior.
     *  
     */
    @XmlElement(name = "category", required = true)
    private List<Category> m_categories = new ArrayList<>();

    public ColumnDef() {
    }

    public ColumnDef(final String label, final String... categories) {
        setLabel(label);
        for (final String category : categories) {
            addCategory(category);
        }
    }

    public String getLabel() {
        return m_label;
    }

    public void setLabel(final String label) {
        m_label = ConfigUtils.assertNotEmpty(label, "label");
    }

    public Optional<String> getReportCategory() {
        return Optional.ofNullable(m_reportCategory);
    }

    public void setReportCategory(final String reportCategory) {
        m_reportCategory = ConfigUtils.normalizeString(reportCategory);
    }

    public List<Category> getCategories() {
        return m_categories;
    }

    public void setCategories(final List<Category> categories) {
        ConfigUtils.assertMinimumSize(categories, 1, "category");
        if (categories == m_categories) return;
        m_categories.clear();
        if (categories != null) m_categories.addAll(categories);
    }

    public void addCategory(final Category vCategory) {
        m_categories.add(vCategory);
    }

    public void addCategory(final String category) {
        m_categories.add(new Category(category));
    }

    public boolean removeCategory(final Category category) {
        return m_categories.remove(category);
    }

    @Override
    public Set<String> getCategoryNames() {
        return getCategories().stream().map(cat -> {
            return cat.getName();
        }).collect(Collectors.toSet());
    }

    @Override
    public boolean containsCategory(final String name) {
        return getCategories().stream().anyMatch(cat -> {
            return name.equals(cat.getName());
        });
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_label, 
                            m_reportCategory, 
                            m_categories);
    }

    @Override
    public boolean equals(final Object obj) {
        if ( this == obj ) {
            return true;
        }

        if (obj instanceof ColumnDef) {
            final ColumnDef that = (ColumnDef)obj;
            return Objects.equals(this.m_label, that.m_label)
                    && Objects.equals(this.m_reportCategory, that.m_reportCategory)
                    && Objects.equals(this.m_categories, that.m_categories);
        }
        return false;
    }

}
