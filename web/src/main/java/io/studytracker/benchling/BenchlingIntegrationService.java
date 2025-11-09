/*
 * Copyright 2019-2025 the original author or authors.
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

package io.studytracker.benchling;

import io.studytracker.benchling.api.BenchlingElnRestClient;
import io.studytracker.benchling.api.entities.BenchlingCustomEntity;
import io.studytracker.benchling.api.entities.BenchlingCustomEntity.BenchlingCustomEntityList;
import io.studytracker.benchling.api.entities.BenchlingDropdown;
import io.studytracker.benchling.api.entities.BenchlingDropdown.BenchlingDropdownList;
import io.studytracker.benchling.api.entities.BenchlingEntryTemplateList;
import io.studytracker.eln.NotebookTemplate;
import io.studytracker.integration.IntegrationService;
import io.studytracker.model.BenchlingIntegration;
import io.studytracker.repository.BenchlingIntegrationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Service
public class BenchlingIntegrationService implements IntegrationService<BenchlingIntegration> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BenchlingIntegrationService.class);
    
    @Autowired
    private BenchlingIntegrationRepository benchlingIntegrationRepository;
    
    @Autowired
    private BenchlingClientFactory benchlingClientFactory;
    
    @Override
    public Optional<BenchlingIntegration> findById(Long id) {
        LOGGER.debug("Find BenchlingIntegration by id: {}", id);
        return benchlingIntegrationRepository.findById(id);
    }
    
    @Override
    public List<BenchlingIntegration> findAll() {
        LOGGER.debug("Find all BenchlingIntegration");
        return benchlingIntegrationRepository.findAll();
    }
    
    @Override
    @Transactional
    public BenchlingIntegration register(BenchlingIntegration instance) {
        LOGGER.info("Registering BenchlingIntegration");
        if (!validate(instance)) {
            throw new IllegalArgumentException("One or more required fields are missing.");
        }
        if (!test(instance)) {
            throw new IllegalArgumentException("Failed to connect to Benchling API with the provided credentials.");
        }
        instance.setActive(true);
        return benchlingIntegrationRepository.save(instance);
    }
    
    @Override
    @Transactional
    public BenchlingIntegration update(BenchlingIntegration instance) {
        LOGGER.info("Updating BenchlingIntegration: {}", instance.getId());
        if (!validate(instance)) {
            throw new IllegalArgumentException("One or more required fields are missing.");
        }
        if (!test(instance)) {
            throw new IllegalArgumentException("Failed to connect to Benchling API with the provided credentials.");
        }
        BenchlingIntegration i = benchlingIntegrationRepository.getById(instance.getId());
        i.setName(instance.getName());
        i.setTenantName(instance.getTenantName());
        i.setRootUrl(instance.getRootUrl());
        i.setClientId(instance.getClientId());
        i.setClientSecret(instance.getClientSecret());
        i.setUsername(instance.getUsername());
        i.setPassword(instance.getPassword());
        i.setActive(instance.isActive());
        return benchlingIntegrationRepository.save(i);
    }
    
    @Override
    public boolean validate(BenchlingIntegration instance) {
        try {
            Assert.hasText(instance.getTenantName(), "Tenant name is required.");
            Assert.hasText(instance.getRootUrl(), "Root URL is required.");
            Assert.hasText(instance.getClientId(), "Client ID is required.");
            Assert.hasText(instance.getClientSecret(), "Client secret is required.");
        } catch (Exception e) {
            LOGGER.warn("Benchling Integration validation failed: " + e.getMessage());
            return false;
        }
        return true;
    }
    
    @Override
    public boolean test(BenchlingIntegration instance) {
        try {
            BenchlingElnRestClient client = benchlingClientFactory.createBenchlingClient(instance);
            BenchlingEntryTemplateList list = client.findEntryTemplates(null);
            Assert.notNull(list, "Failed to connect to Benchling API with the provided credentials.");
        } catch (Exception e) {
            LOGGER.warn("Benchling Integration test failed: " + e.getMessage());
            return false;
        }
        return true;
    }
    
    @Override
    @Transactional
    public void remove(BenchlingIntegration instance) {
        LOGGER.info("Removing BenchlingIntegration: {}", instance.getId());
        BenchlingIntegration i = benchlingIntegrationRepository.getById(instance.getId());
        i.setActive(false);
        benchlingIntegrationRepository.save(i);
    }

  /**
   * Finds all Benchling dropdowns for the given integration.
   * @param instance
   * @return
   */
  public List<BenchlingDropdown> findDropdowns(BenchlingIntegration instance) {
    BenchlingElnRestClient client = benchlingClientFactory.createBenchlingClient(instance);
    List<BenchlingDropdown> dropdowns = new ArrayList<>();
    String nextToken = null;
    boolean hasNext = true;
    while (hasNext) {
      BenchlingDropdownList dropdownList = client.findDropdowns(nextToken);
      dropdowns.addAll(dropdownList.getDropdowns());
      nextToken = dropdownList.getNextToken();
      hasNext = StringUtils.hasText(nextToken);
    }
    return dropdowns;
  }

  /**
   * Finds a Benchling dropdown by ID.
   * @param instance
   * @param id
   * @return
   */
  public Optional<BenchlingDropdown> findDropdownById(BenchlingIntegration instance, String id) {
    BenchlingElnRestClient client = benchlingClientFactory.createBenchlingClient(instance);
    Optional<BenchlingDropdown> optional = client.findDropdownById(id);
    return optional;
  }

  /**
   * Finds all Benchling custom entities for the given integration and schema ID.
   * @param instance
   * @param schemaId
   * @return
   */
  public List<BenchlingCustomEntity> findCustomEntities(BenchlingIntegration instance,
      String schemaId, String nameIncludes) {
    BenchlingElnRestClient client = benchlingClientFactory.createBenchlingClient(instance);
    List<BenchlingCustomEntity> entities = new ArrayList<>();
    String token = null;
    boolean hasNext = true;
    while (hasNext) {
      BenchlingCustomEntityList list = client.findCustomEntities(token, schemaId, nameIncludes);
      entities.addAll(list.getCustomEntities());
      token = list.getNextToken();
      hasNext = StringUtils.hasText(token);
    }
    return entities;
  }

  /**
   * Finds a Benchling custom entity by ID.
   * @param integration
   * @param id
   * @return
   */
  public Optional<BenchlingCustomEntity> findCustomEntityById(BenchlingIntegration integration,
      String id) {
    BenchlingElnRestClient client = benchlingClientFactory.createBenchlingClient(integration);
    return client.findCustomEntityById(id);
  }

}
