/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2016-2017 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2017 The OpenNMS Group, Inc.
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

package org.opennms.features.graphml.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Properties;

import org.graphdrawing.graphml.GraphmlType;
import org.json.JSONObject;
import org.opennms.core.xml.JaxbUtils;
import org.opennms.features.config.service.api.JsonAsString;
import org.opennms.features.graphml.service.GraphmlRepository;
import org.opennms.features.config.service.api.ConfigurationManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class GraphmlRepositoryImpl implements GraphmlRepository {

    protected static final String TOPOLOGY_CFG_FILE_PREFIX = "org.opennms.features.topology.plugins.topo.graphml";
    protected static final String GRAPH_CFG_FILE_PREFIX = "org.opennms.netmgt.graph.provider.graphml";
    protected static final String TOPOLOGY_LOCATION = "topologyLocation";
    protected static final String GRAPH_LOCATION = "graphLocation";
    protected static final String LABEL = "label";

    private static final Logger LOG = LoggerFactory.getLogger(GraphmlRepositoryImpl.class);
    
    private ConfigurationManagerService cm;

    public GraphmlRepositoryImpl(ConfigurationManagerService cm) {
        Preconditions.checkState(System.getProperty("opennms.home") != null, "No opennms.home defined. Bailing out...");
        this.cm = Objects.requireNonNull(cm);
    }

    @Override
    public GraphmlType findByName(String name) throws IOException {
        Objects.requireNonNull(name);
        if (!exists(name)) {
            throw new NoSuchElementException("No GraphML file found with name  " + name);
        }
        GraphmlType graphmlType = JaxbUtils.unmarshal(GraphmlType.class, new File(buildGraphmlFilepath(name)));
        return graphmlType;
    }

    @Override
    public void save(String name, String label, GraphmlType graphmlType) throws IOException {
        LOG.debug("Saving GraphML file {} with label {}", name, label);
        Objects.requireNonNull(name);
        Objects.requireNonNull(label);
        Objects.requireNonNull(graphmlType);
        if (exists(name)) {
            LOG.warn("GraphML file with name {} already exists", name);
            throw new IOException(name + " already exists");
        }

        File graphFile = new File(buildGraphmlFilepath(name));
        File topologyCfgFile = new File(buildTopologyCfgFilepath(name));
        LOG.debug("GraphML xml location: {}", graphFile);
        LOG.debug("GraphML topology cfg location: {}", topologyCfgFile);
        LOG.debug("GraphML graph cfg in cm: {}-{}", GRAPH_CFG_FILE_PREFIX, name);

        // create directories if not yet created
        graphFile.getParentFile().mkdirs();
        topologyCfgFile.getParentFile().mkdirs();

        // Write file to disk
        JaxbUtils.marshal(graphmlType, graphFile);

        // create Cfg file for Topology
        Properties properties = new Properties();
        properties.put(TOPOLOGY_LOCATION, graphFile.toString());
        properties.put(LABEL, label);
        properties.store(new FileWriter(topologyCfgFile), "Generated by " + getClass().getSimpleName() + ". DO NOT EDIT!");

        // Create config for Graph Service
        JSONObject json = new JSONObject();
        json.put(GRAPH_LOCATION, graphFile.toString());
        cm.registerConfiguration(GRAPH_CFG_FILE_PREFIX, name, new JsonAsString(json.toString()));
    }

    @Override
    public void delete(String name) throws IOException {
        LOG.debug("Delete GraphML file with name {}", name);
        Objects.requireNonNull(name);
        findByName(name);
        Files.delete(Paths.get(buildTopologyCfgFilepath(name)));
        cm.unregisterConfiguration(GRAPH_CFG_FILE_PREFIX, name);
        Files.delete(Paths.get(buildGraphmlFilepath(name)));
    }

    @Override
    public boolean exists(String name) {
        String filename = buildGraphmlFilepath(name);
        return new File(filename).exists() && new File(filename).isFile();
    }

    protected static String buildCfgFilepath(final String filename, final String cfgFilePrefix) {
        return Paths.get(System.getProperty("opennms.home"), "etc", cfgFilePrefix + "-" + filename + ".cfg").toString();
    }

    protected static String buildTopologyCfgFilepath(String filename) {
        return buildCfgFilepath(filename, TOPOLOGY_CFG_FILE_PREFIX);
    }

    protected static String buildGraphmlFilepath(String filename) {
        return Paths.get(System.getProperty("opennms.home"), "etc", "graphml", filename + ".xml").toString();
    }
}
