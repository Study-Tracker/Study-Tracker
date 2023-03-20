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

import io.studytracker.egnyte.EgnyteIntegrationService;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.form.EgnyteIntegrationFormDto;
import io.studytracker.mapstruct.dto.response.EgnyteIntegrationDetailsDto;
import io.studytracker.mapstruct.mapper.EgnyteIntegrationMapper;
import io.studytracker.model.EgnyteIntegration;
import io.studytracker.model.Organization;
import io.studytracker.service.OrganizationService;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/integrations/egnyte")
public class EgnyteIntegrationPrivateController {

  public static final Logger LOGGER = LoggerFactory.getLogger(EgnyteIntegrationPrivateController.class);

  @Autowired
  private EgnyteIntegrationService egnyteIntegrationService;

  @Autowired
  private EgnyteIntegrationMapper egnyteIntegrationMapper;

  @Autowired
  private OrganizationService organizationService;

  @GetMapping("")
  public List<EgnyteIntegrationDetailsDto> findAllIntegrations() {
    Organization organization = organizationService.getCurrentOrganization();
    LOGGER.debug("Finding all egnyte integrations for organization: {}", organization.getId());
    List<EgnyteIntegration> integrations = egnyteIntegrationService.findByOrganization(organization);
    return egnyteIntegrationMapper.toDetailsDto(integrations);
  }

  @PostMapping("")
  public HttpEntity<EgnyteIntegrationDetailsDto> registerIntegration(
      @Valid @RequestBody EgnyteIntegrationFormDto dto) {;
    Organization organization = organizationService.getCurrentOrganization();
    LOGGER.info("Registering egnyte integration for organization: {}", organization.getId());
    EgnyteIntegration integration = egnyteIntegrationMapper.fromFormDto(dto);
    integration.setOrganization(organization);
    EgnyteIntegration created = egnyteIntegrationService.register(integration);
    return new ResponseEntity<>(egnyteIntegrationMapper.toDetailsDto(created), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<EgnyteIntegrationDetailsDto> updateRegistration(@PathVariable("id") Long id,
      @Valid @RequestBody EgnyteIntegrationFormDto dto) {
    Organization organization = organizationService.getCurrentOrganization();
    EgnyteIntegration existing = egnyteIntegrationService.findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Egnyte integration not found: " + id));
    if (!existing.getOrganization().equals(organization)) {
      throw new RecordNotFoundException("Egnyte integration not found: " + id);
    }
    EgnyteIntegration updated = egnyteIntegrationService
        .update(egnyteIntegrationMapper.fromFormDto(dto));
    return new ResponseEntity<>(egnyteIntegrationMapper.toDetailsDto(updated), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> unregisterIntegration(@PathVariable("id") Long id) {
    LOGGER.info("Unregistering egnyte integration: {}", id);
    Organization organization = organizationService.getCurrentOrganization();
    EgnyteIntegration existing = egnyteIntegrationService.findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Egnyte integration not found: " + id));
    if (!existing.getOrganization().equals(organization)) {
      throw new RecordNotFoundException("Egnyte integration not found: " + id);
    }
    egnyteIntegrationService.remove(existing);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
