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
package org.opennms.features.topology.plugins.topo.asset;

import java.util.List;
import java.util.Objects;

import org.graphdrawing.graphml.GraphmlType;
import org.opennms.features.graphml.model.GraphML;
import org.opennms.features.graphml.model.GraphMLWriter;
import org.opennms.features.graphml.service.GraphmlRepository;
import org.opennms.netmgt.events.api.EventIpcManager;
import org.opennms.netmgt.events.api.EventListener;
import org.opennms.netmgt.events.api.model.IEvent;
import org.opennms.netmgt.model.events.EventUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionOperations;

import com.google.common.collect.Lists;

public class AssetGraphMLProvider implements EventListener {

	private static final Logger LOG = LoggerFactory.getLogger(AssetGraphMLProvider.class);

	public static final String CREATE_ASSET_TOPOLOGY = "uei.opennms.plugins/assettopology/create";
	public static final String REMOVE_ASSET_TOPOLOGY = "uei.opennms.plugins/assettopology/remove";
	public static final String REGENERATE_ASSET_TOPOLOGY = "uei.opennms.plugins/assettopology/regenerate";
	public static final String REGENERATE_ALL_ASSET_TOPOLOGIES = "uei.opennms.plugins/assettopology/regenerateall";

	private static final List<String> ueiList = Lists.newArrayList(CREATE_ASSET_TOPOLOGY, 
			REMOVE_ASSET_TOPOLOGY,REGENERATE_ASSET_TOPOLOGY,REGENERATE_ALL_ASSET_TOPOLOGIES);

	private final EventIpcManager eventIpcManager;

	private final GraphmlRepository graphmlRepository;

	private final TransactionOperations transactionOperations;

	private final NodeProvider nodeProvider;

	private final AssetGraphDefinitionRepository assetGraphDefinitionRepository;

	public AssetGraphMLProvider(GraphmlRepository repository,
			EventIpcManager eventIpcManager, NodeProvider nodeProvider,
			TransactionOperations transactionOperations,
			AssetGraphDefinitionRepositoryImpl assetGraphDefinitionRepository) {
		this.graphmlRepository = Objects.requireNonNull(repository);
		this.eventIpcManager = Objects.requireNonNull(eventIpcManager);
		this.nodeProvider = Objects.requireNonNull(nodeProvider);
		this.transactionOperations=transactionOperations;
		this.assetGraphDefinitionRepository = Objects.requireNonNull(assetGraphDefinitionRepository);
	}

	/**
	 * Generates and installs a new AssetTopology defined by the config
	 * @param config 
	 */
	public synchronized void createAssetTopology(GeneratorConfig config){
		Objects.requireNonNull(config);
		try {
			LOG.debug("Creating Asset Topology providerId: {}, label: {}, config: {}", config.getProviderId(), config.getLabel(), config);
			if (graphmlRepository.exists(config.getProviderId())) {
				throw new IllegalStateException(String.format("GraphML Provider with id '%s' (label: %s) already exists", config.getProviderId(), config.getLabel()));
			}
			if (assetGraphDefinitionRepository.exists(config.getProviderId())) {
				throw new IllegalStateException(String.format("Asset Graph Definition with id '%s' (label: %s) already exists", config.getProviderId(), config.getLabel()));
			}

			final GraphML graphML = transactionOperations.execute(status -> new AssetGraphGenerator(nodeProvider).generateGraphs(config));
			final GraphmlType graphmlType = GraphMLWriter.convert(graphML);

			assetGraphDefinitionRepository.addConfigDefinition(config);
			graphmlRepository.save(config.getProviderId(), config.getLabel(), graphmlType);
		} catch (Exception ex){
			LOG.error("Could not create Asset Topology", ex);
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Removes the AssetTopology defined by the config
	 * @param providerId The providerId to delete.
	 */
	public synchronized void removeAssetTopology(String providerId){
		Objects.requireNonNull(providerId);
		try {
			LOG.debug("Removing Asset Topology providerId: {}", providerId);
			if (!assetGraphDefinitionRepository.exists(providerId)) {
				throw new IllegalStateException(String.format("Asset Graph Definition with id '%s' cannot be removed, because it does not exist", providerId));
			} else 	{
				assetGraphDefinitionRepository.removeConfigDefinition(providerId);
			}
			if (graphmlRepository.exists(providerId)) {
				graphmlRepository.delete(providerId);
			}
		} catch (Exception ex){
			LOG.error("problem removing asset topology ", ex);
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Regenerates the AssetTopology defined by the providerId
	 * @param providerId The providerId to regenerate.
	 */
	public synchronized void regenerateAssetTopology(String providerId){
		Objects.requireNonNull(providerId);
		try {
			LOG.debug("Regenerating Asset Topology providerId: {}", providerId);
			if (!assetGraphDefinitionRepository.exists(providerId)) 
				throw new IllegalStateException(String.format("Asset Graph Definition with id '%s' cannot be regenerated, because it does not exist", providerId));

			GeneratorConfig config = assetGraphDefinitionRepository.getConfigDefinition(providerId);
			final GraphML graphML = transactionOperations.execute(status -> new AssetGraphGenerator(nodeProvider).generateGraphs(config));
			final GraphmlType graphmlType = GraphMLWriter.convert(graphML);

			if (graphmlRepository.exists(providerId)) graphmlRepository.delete(providerId);
			graphmlRepository.save(config.getProviderId(), config.getLabel(), graphmlType);

		} catch (Exception ex){
			LOG.error("problem regenerating asset topology ", ex);
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Makes a best effort to regenerated all of the asset topologies defined in the assetGraphDefinitionRepository
	 * Throws a runtime exception if all the topologies are not regenerated
	 */
	public synchronized void regenerateAllAssetTopologies(){
		GeneratorConfigList configList = assetGraphDefinitionRepository.getAllConfigDefinitions();
		final StringBuilder logmsg = new StringBuilder("Regenerating All Asset Topologies succeeded for providerIds: ");
		final StringBuilder errmsg = new StringBuilder("Regenerate All Asset Topologies failed for providerIds: ");
		boolean failed=false;
		for(GeneratorConfig config : configList.getConfigs()){
			final String providerId = config.getProviderId();
			try {
				regenerateAssetTopology(providerId);
				logmsg.append("["+providerId+"]");
			} catch (Exception ex){
				errmsg.append("["+providerId+"]");
				failed=true;
			}
		}
		LOG.debug(logmsg.toString());
		if(failed) {
			LOG.error(errmsg.toString());
			throw new RuntimeException(errmsg.toString());
		}

	}

	public void init() {
		eventIpcManager.addEventListener(this, ueiList);
		LOG.info("asset topology provider started");
	}

	public void destroy() {
		eventIpcManager.removeEventListener(this, ueiList);
		LOG.info("asset topology provider stopped");
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public void onEvent(IEvent e) {
		try {
			if (CREATE_ASSET_TOPOLOGY.equals(e.getUei())) {
				final GeneratorConfig config = GeneratorConfigBuilder.buildFrom(e);
				createAssetTopology(config);
			} else if (REMOVE_ASSET_TOPOLOGY.equals(e.getUei())) {
				final String providerId = EventUtils.getParm(e, EventParameterNames.PROVIDER_ID);
				removeAssetTopology(providerId);
			} else if (REGENERATE_ASSET_TOPOLOGY.equals(e.getUei())) {
				final String providerId = EventUtils.getParm(e, EventParameterNames.PROVIDER_ID);
				regenerateAssetTopology(providerId);
			} else if (REGENERATE_ALL_ASSET_TOPOLOGIES.equals(e.getUei())){
				regenerateAllAssetTopologies();
			}
		} catch (Exception ex) {
			LOG.error("asset topology provider problem processing event " +e.getUei(), ex);
		}
	}

}