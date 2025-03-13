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
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/integrations/egnyte")
public class EgnyteIntegrationPrivateController {

  public static final Logger LOGGER = LoggerFactory.getLogger(EgnyteIntegrationPrivateController.class);

  @Autowired
  private EgnyteIntegrationService egnyteIntegrationService;

  @Autowired
  private EgnyteIntegrationMapper egnyteIntegrationMapper;

  @GetMapping("")
  public List<EgnyteIntegrationDetailsDto> findAllIntegrations() {
    LOGGER.debug("Finding all egnyte integrations");
    List<EgnyteIntegration> integrations = egnyteIntegrationService.findAll();
    return egnyteIntegrationMapper.toDetailsDto(integrations);
  }

  @PostMapping("")
  public HttpEntity<EgnyteIntegrationDetailsDto> registerIntegration(
      @Valid @RequestBody EgnyteIntegrationFormDto dto) {;
    LOGGER.info("Registering egnyte integration");
    EgnyteIntegration integration = egnyteIntegrationMapper.fromFormDto(dto);
    EgnyteIntegration created = egnyteIntegrationService.register(integration);
    egnyteIntegrationService.registerDefaultDrive(created);
    return new ResponseEntity<>(egnyteIntegrationMapper.toDetailsDto(created), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<EgnyteIntegrationDetailsDto> updateRegistration(@PathVariable("id") Long id,
      @Valid @RequestBody EgnyteIntegrationFormDto dto) {
    egnyteIntegrationService.findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Egnyte integration not found: " + id));
    EgnyteIntegration updated = egnyteIntegrationService
        .update(egnyteIntegrationMapper.fromFormDto(dto));
    return new ResponseEntity<>(egnyteIntegrationMapper.toDetailsDto(updated), HttpStatus.OK);
  }

  @PatchMapping("/{id}")
  public HttpEntity<?> toggleIntegrationStatus(@RequestParam("active") boolean active,
      @PathVariable("id") Long id) {
    LOGGER.info("Updating Egnyte integration {} status to {}", id, active);
    Optional<EgnyteIntegration> optional = egnyteIntegrationService.findById(id);
    if (optional.isEmpty()) {
      throw new RecordNotFoundException("Egnyte integration not found");
    }
    EgnyteIntegration integration = optional.get();
    integration.setActive(active);
    egnyteIntegrationService.update(integration);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> unregisterIntegration(@PathVariable("id") Long id) {
    LOGGER.info("Unregistering egnyte integration: {}", id);
    EgnyteIntegration existing = egnyteIntegrationService.findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Egnyte integration not found: " + id));
    egnyteIntegrationService.remove(existing);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  // Drives

  @PostMapping("/{id}/drives")
  public HttpEntity<?> registerDefaultDrive(@PathVariable("id") Long id) {
    LOGGER.info("Registering default drive for Egnyte integration: {}", id);
    EgnyteIntegration integration = egnyteIntegrationService.findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Egnyte integration not found: " + id));
    egnyteIntegrationService.registerDefaultDrive(integration);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }
}
