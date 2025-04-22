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

import io.studytracker.aws.AwsIntegrationService;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.form.AwsIntegrationFormDto;
import io.studytracker.mapstruct.dto.response.AwsIntegrationDetailsDto;
import io.studytracker.mapstruct.mapper.AwsIntegrationMapper;
import io.studytracker.model.AwsIntegration;
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
@RequestMapping("/api/internal/integrations/aws")
public class AWSIntegrationPrivateController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AWSIntegrationPrivateController.class);

  @Autowired
  private AwsIntegrationService awsIntegrationService;

  @Autowired
  private AwsIntegrationMapper awsIntegrationMapper;

  @GetMapping("")
  public List<AwsIntegrationDetailsDto> fetchAwsIntegrations() {
    LOGGER.debug("Fetching AWS integrations");
    return awsIntegrationMapper.toDetailsDto(awsIntegrationService.findAll());
  }

  @PostMapping("")
  public HttpEntity<AwsIntegrationDetailsDto> registerIntegration(@Valid @RequestBody AwsIntegrationFormDto dto) {
    LOGGER.info("Registering AWS integration: {}", dto.getName());
    AwsIntegration integration = awsIntegrationMapper.fromFormDto(dto);
    AwsIntegration created = awsIntegrationService.register(integration);
    return new ResponseEntity<>(awsIntegrationMapper.toDetailsDto(created), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<AwsIntegrationDetailsDto> updateIntegration(@PathVariable("id") Long id,
      @Valid @RequestBody AwsIntegrationFormDto dto) {
    LOGGER.info("Updating AWS integration {}", id);
    AwsIntegration integration = awsIntegrationMapper.fromFormDto(dto);
    AwsIntegration updated = awsIntegrationService.update(integration);
    return new ResponseEntity<>(awsIntegrationMapper.toDetailsDto(updated), HttpStatus.OK);
  }

  @PatchMapping("/{id}")
  public HttpEntity<?> toggleIntegrationStatus(@RequestParam("active") boolean active,
      @PathVariable("id") Long id) {
    LOGGER.info("Updating AWS integration {} status to {}", id, active);
    Optional<AwsIntegration> optional = awsIntegrationService.findById(id);
    if (optional.isEmpty()) {
      throw new RecordNotFoundException("AWS integration not found");
    }
    AwsIntegration integration = optional.get();
    integration.setActive(active);
    awsIntegrationService.update(integration);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> deleteIntegration(@PathVariable("id") Long id) {
    LOGGER.info("Deleting AWS integration {}", id);
    Optional<AwsIntegration> optional = awsIntegrationService.findById(id);
    if (optional.isEmpty()) {
      throw new RecordNotFoundException("AWS integration not found");
    }
    awsIntegrationService.remove(optional.get());
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
