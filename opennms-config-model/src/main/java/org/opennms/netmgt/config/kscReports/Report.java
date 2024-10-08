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
package org.opennms.netmgt.config.kscReports;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.core.xml.ValidateUsing;
import org.opennms.netmgt.config.utils.ConfigUtils;

@XmlRootElement(name = "Report")
@XmlAccessorType(XmlAccessType.FIELD)
@ValidateUsing("ksc-performance-reports.xsd")
public class Report implements java.io.Serializable {
    private static final long serialVersionUID = 3L;

    @XmlAttribute(name = "id")
    private Integer m_id;

    @XmlAttribute(name = "title", required = true)
    private String m_title;

    @XmlAttribute(name = "show_timespan_button")
    private Boolean m_showTimespanButton;

    @XmlAttribute(name = "show_graphtype_button")
    private Boolean m_showGraphtypeButton;

    @XmlAttribute(name = "graphs_per_line")
    private Integer m_graphsPerLine;

    @XmlElement(name = "Graph")
    private List<Graph> m_graphs = new ArrayList<>();

    public Integer getId() {
        return m_id;
    }

    public void setId(final Integer id) {
        m_id = id;
    }

    public String getTitle() {
        return m_title;
    }

    public void setTitle(final String title) {
        m_title = ConfigUtils.assertNotEmpty(title, "title");
    }

    public Optional<Boolean> getShowTimespanButton() {
        return Optional.ofNullable(m_showTimespanButton);
    }

    public void setShowTimespanButton(final Boolean showTimespanButton) {
        m_showTimespanButton = showTimespanButton;
    }

    public Optional<Boolean> getShowGraphtypeButton() {
        return Optional.ofNullable(m_showGraphtypeButton);
    }

    public void setShowGraphtypeButton(final Boolean showGraphtypeButton) {
        m_showGraphtypeButton = showGraphtypeButton;
    }

    public List<Graph> getGraphs() {
        return m_graphs;
    }

    public void setGraphs(final List<Graph> graphs) {
        if (graphs == m_graphs) return;
        m_graphs.clear();
        if (graphs != null) m_graphs.addAll(graphs);
    }

    public void addGraph(final Graph graph) {
        m_graphs.add(graph);
    }

    public void addGraph(final int index, final Graph graph) {
        m_graphs.add(index, graph);
    }

    public boolean removeGraph(final Graph graph) {
        return m_graphs.remove(graph);
    }

    public Optional<Integer> getGraphsPerLine() {
        return Optional.ofNullable(m_graphsPerLine);
    }

    public void setGraphsPerLine(final Integer graphsPerLine) {
        m_graphsPerLine = ConfigUtils.assertMinimumInclusive(graphsPerLine, 0, "graphs_per_line");
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_id, 
                            m_title, 
                            m_showTimespanButton, 
                            m_showGraphtypeButton, 
                            m_graphsPerLine, 
                            m_graphs);
    }

    @Override
    public boolean equals(final Object obj) {
        if ( this == obj ) {
            return true;
        }

        if (obj instanceof Report) {
            final Report that = (Report)obj;
            return Objects.equals(this.m_id, that.m_id)
                    && Objects.equals(this.m_title, that.m_title)
                    && Objects.equals(this.m_showTimespanButton, that.m_showTimespanButton)
                    && Objects.equals(this.m_showGraphtypeButton, that.m_showGraphtypeButton)
                    && Objects.equals(this.m_graphsPerLine, that.m_graphsPerLine)
                    && Objects.equals(this.m_graphs, that.m_graphs);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Report [id=" + m_id + ", m_title=" + m_title
                + ", showTimespanButton=" + m_showTimespanButton
                + ", showGraphtypeButton=" + m_showGraphtypeButton
                + ", graphsPerLine=" + m_graphsPerLine + ", graphs="
                + m_graphs + "]";
    }

}
