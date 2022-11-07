/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.studytracker.service;

import io.studytracker.model.IntegrationDefinition;
import io.studytracker.model.IntegrationInstance;
import io.studytracker.repository.IntegrationConfigurationSchemaFieldRepository;
import io.studytracker.repository.IntegrationDefinitionRepository;
import io.studytracker.repository.IntegrationInstanceConfigurationValueRepository;
import io.studytracker.repository.IntegrationInstanceRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IntegrationsService {

  private IntegrationDefinitionRepository integrationDefinitionRepository;

  private IntegrationInstanceRepository integrationInstanceRepository;

  private IntegrationConfigurationSchemaFieldRepository schemaFieldRepository;

  private IntegrationInstanceConfigurationValueRepository configurationValueRepository;

  public static final Logger LOGGER = LoggerFactory.getLogger(IntegrationsService.class);

  /* Supported Integrations */

  public Optional<IntegrationDefinition> findLatestSupportedIntegrationByName(String name) {
    return integrationDefinitionRepository.findLatestByName(name);
  }

  public List<IntegrationDefinition> findSupportedIntegrationsByName(String name) {
    return integrationDefinitionRepository.findByName(name).stream()
        .sorted(Comparator.comparing(IntegrationDefinition::getVersion))
        .collect(Collectors.toList());
  }

  public Optional<IntegrationDefinition> findSupportedIntegrationByNameAndVersion(String name, Integer version) {
    return integrationDefinitionRepository.findByNameAndVersion(name, version);
  }

  @Transactional
  public IntegrationDefinition registerSupportedIntegration(IntegrationDefinition integration) {
    return integrationDefinitionRepository.save(integration);
  }


  /* Integration instances */

  /**
   * Returns a list of active integration instances for a given supported integration, defined by
   *   name. This list will include integrations for all parent versions, regardless of whether they
   *   are the latest definition or active.
   *
   * @param name
   * @return
   */
  public List<IntegrationInstance> findActiveIntegrationsInstanceByParentName(String name) {
    return integrationInstanceRepository.findBySupportedIntegrationName(name)
        .stream()
        .filter(IntegrationInstance::isActive)
        .collect(Collectors.toList());
  }

  /***
   * Registers a new {@link IntegrationInstance} with the system.
   *
   * @param instance the instance to register
   * @return the registered instance
   */
  @Transactional
  public IntegrationInstance createIntegrationInstance(IntegrationInstance instance) {
    return integrationInstanceRepository.save(instance);
  }

  /* Getters and setters */

  @Autowired
  public void setSupportedIntegrationRepository(
      IntegrationDefinitionRepository integrationDefinitionRepository) {
    this.integrationDefinitionRepository = integrationDefinitionRepository;
  }

  @Autowired
  public void setIntegrationInstanceRepository(
      IntegrationInstanceRepository integrationInstanceRepository) {
    this.integrationInstanceRepository = integrationInstanceRepository;
  }

  @Autowired
  public void setSchemaFieldRepository(
      IntegrationConfigurationSchemaFieldRepository schemaFieldRepository) {
    this.schemaFieldRepository = schemaFieldRepository;
  }

  @Autowired
  public void setConfigurationValueRepository(
      IntegrationInstanceConfigurationValueRepository configurationValueRepository) {
    this.configurationValueRepository = configurationValueRepository;
  }
}
