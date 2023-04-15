/*
 * Copyright 2019-2023 the original author or authors.
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
import io.studytracker.repository.IntegrationDefinitionRepository;
import io.studytracker.repository.IntegrationInstanceRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Deprecated
@Service
public class IntegrationsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationsService.class);

  @Autowired
  private IntegrationInstanceRepository integrationInstanceRepository;

  @Autowired
  private IntegrationDefinitionRepository integrationDefinitionRepository;

  public List<IntegrationInstance> findAllInstances() {
    return integrationInstanceRepository.findAll();
  }

  public List<IntegrationDefinition> findAllDefinitions() {
    return integrationDefinitionRepository.findAll();
  }

  public Optional<IntegrationInstance> findInstanceById(Long id) {
    return integrationInstanceRepository.findById(id);
  }

  public Optional<IntegrationDefinition> findDefinitionById(Long id) {
    return integrationDefinitionRepository.findById(id);
  }

  @Transactional
  public IntegrationInstance createInstance(IntegrationInstance instance, IntegrationDefinition definition) {
    instance.setDefinition(definition);
    return integrationInstanceRepository.save(instance);
  }

  @Transactional
  public IntegrationInstance updateInstance(IntegrationInstance instance) {
    IntegrationInstance i = integrationInstanceRepository.getById(instance.getId());
    i.setName(instance.getName());
    i.setActive(instance.isActive());
    i.setDisplayName(instance.getDisplayName());
    i.setConfigurationValues(instance.getConfigurationValues());
    return integrationInstanceRepository.save(i);
  }

}
