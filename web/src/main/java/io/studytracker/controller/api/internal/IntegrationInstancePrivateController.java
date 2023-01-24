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

package io.studytracker.controller.api.internal;

import io.studytracker.controller.api.AbstractApiController;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.form.IntegrationInstanceFormDto;
import io.studytracker.mapstruct.dto.response.IntegrationInstanceDetailsDto;
import io.studytracker.mapstruct.mapper.IntegrationInstanceMapper;
import io.studytracker.model.IntegrationDefinition;
import io.studytracker.model.IntegrationInstance;
import io.studytracker.service.IntegrationsService;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/internal/integrations")
@RestController
public class IntegrationInstancePrivateController extends AbstractApiController {

  private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationInstancePrivateController.class);

  @Autowired
  private IntegrationsService integrationsService;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private IntegrationInstanceMapper integrationInstanceMapper;

  @GetMapping("")
  public List<IntegrationInstanceDetailsDto> findAllInstances() {
    LOGGER.debug("findAll()");
    return integrationInstanceMapper.toDetailsList(integrationsService.findAllInstances());
  }

  @GetMapping("/{id}")
  public IntegrationInstanceDetailsDto findById(@PathVariable("id") Long id) {
    LOGGER.debug("findById({})", id);
    IntegrationInstance instance = integrationsService.findInstanceById(id)
        .orElseThrow(() -> new RecordNotFoundException("Integration instance record not found: " + id));
    return integrationInstanceMapper.toDetails(instance);
  }

  @PostMapping("")
  public HttpEntity<IntegrationInstanceDetailsDto> createInstance(@Valid @RequestBody IntegrationInstanceFormDto dto) {
    LOGGER.info("Registering new integration instance: {}", dto);
    IntegrationInstance instance = integrationInstanceMapper.fromForm(dto);
    IntegrationDefinition definition = integrationsService.findDefinitionById(dto.getIntegrationDefinitionId())
        .orElseThrow(() -> new RecordNotFoundException("Integration definition record not found: " + dto.getIntegrationDefinitionId()));
    IntegrationInstance created = integrationsService.createInstance(instance, definition);
    return new ResponseEntity<>(integrationInstanceMapper.toDetails(created), HttpStatus.CREATED);

  }

  @PutMapping("/{id}")
  public HttpEntity<IntegrationInstanceDetailsDto> updateInstance(@PathVariable("id") Long id,
      @Valid @RequestBody IntegrationInstanceFormDto dto) {
    LOGGER.info("Updating existing file storage location: {} {}", id, dto);
    if (integrationsService.findInstanceById(id).isPresent()) {
      IntegrationInstance instance = integrationInstanceMapper.fromForm(dto);
      IntegrationInstance updated = integrationsService.updateInstance(instance);
      return new ResponseEntity<>(integrationInstanceMapper.toDetails(updated), HttpStatus.OK);
    } else {
      throw new RecordNotFoundException("File storage location record not found: " + id);
    }

  }

}
