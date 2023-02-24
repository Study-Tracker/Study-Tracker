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
import io.studytracker.aws.S3Service;
import io.studytracker.exception.InvalidRequestException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.form.AwsIntegrationFormDto;
import io.studytracker.mapstruct.dto.form.S3BucketFormDto;
import io.studytracker.mapstruct.dto.response.AwsIntegrationDetailsDto;
import io.studytracker.mapstruct.dto.response.S3BucketDetailsDto;
import io.studytracker.mapstruct.mapper.AwsIntegrationMapper;
import io.studytracker.mapstruct.mapper.S3BucketMapper;
import io.studytracker.model.AwsIntegration;
import io.studytracker.model.Organization;
import io.studytracker.model.S3Bucket;
import io.studytracker.service.OrganizationService;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/internal/aws")
public class AWSIntegrationPrivateController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AWSIntegrationPrivateController.class);

  @Autowired(required = false)
  private S3Service s3Service;

  @Autowired
  private AwsIntegrationService awsIntegrationService;

  @Autowired
  private OrganizationService organizationService;

  @Autowired
  private AwsIntegrationMapper awsIntegrationMapper;

  @Autowired
  private S3BucketMapper s3BucketMapper;

  @GetMapping("/integrations")
  public List<AwsIntegrationDetailsDto> fetchAwsIntegrations() {
    LOGGER.debug("Fetching AWS integrations");
    Organization organization = organizationService.getCurrentOrganization();
    return awsIntegrationMapper.toDetailsDto(awsIntegrationService.findByOrganization(organization));
  }

  @PostMapping("/integrations")
  public HttpEntity<AwsIntegrationDetailsDto> registerIntegration(@Valid @RequestBody AwsIntegrationFormDto dto) {
    LOGGER.info("Registering AWS integration for organization: {}", dto.getName());
    AwsIntegration integration = awsIntegrationMapper.fromFormDto(dto);
    Organization organization = organizationService.getCurrentOrganization();
    if (organization.getId() != dto.getOrganizationId()) {
      throw new InvalidRequestException("Organization ID mismatch");
    }
    integration.setOrganization(organization);
    AwsIntegration created = awsIntegrationService.register(integration);
    return new ResponseEntity<>(awsIntegrationMapper.toDetailsDto(created), HttpStatus.CREATED);
  }

  @PutMapping("/integrations/{id}")
  public HttpEntity<AwsIntegrationDetailsDto> registerIntegration(@PathVariable("id") Long id,
      @Valid @RequestBody AwsIntegrationFormDto dto) {
    LOGGER.info("Updating AWS integration {} for organization: {}", id, dto.getName());
    AwsIntegration integration = awsIntegrationMapper.fromFormDto(dto);
    Organization organization = organizationService.getCurrentOrganization();
    if (organization.getId() != dto.getOrganizationId()) {
      throw new InvalidRequestException("Organization ID mismatch");
    }
    integration.setOrganization(organization);
    AwsIntegration updated = awsIntegrationService.update(integration);
    return new ResponseEntity<>(awsIntegrationMapper.toDetailsDto(updated), HttpStatus.OK);
  }

  @DeleteMapping("/integrations/{id}")
  public HttpEntity<?> deleteIntegration(@PathVariable("id") Long id) {
    LOGGER.info("Deleting AWS integration {}", id);
    Optional<AwsIntegration> optional = awsIntegrationService.findById(id);
    if (optional.isEmpty()) {
      throw new RecordNotFoundException("AWS integration not found");
    }
    awsIntegrationService.remove(optional.get());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  // S3

  @GetMapping("/s3/available-buckets")
  public List<String> listAvailableBuckets() {
    LOGGER.debug("Listing available buckets");
    return s3Service.listAvailableBuckets();
  }

  @GetMapping("/s3/buckets")
  public List<S3BucketDetailsDto> findRegisteredBuckets() {
    LOGGER.debug("Listing registered buckets");
    return s3BucketMapper.toDto(s3Service.findRegisteredBuckets());
  }

  @PostMapping("/s3/buckets")
  public S3BucketDetailsDto registerBucket(@Valid @RequestBody S3BucketFormDto dto) {
    LOGGER.info("Registering S3 bucket {}", dto.getName());
    S3Bucket bucket = s3BucketMapper.fromFormDto(dto);
    return s3BucketMapper.toDto(s3Service.registerBucket(bucket));
  }

  @DeleteMapping("/s3/buckets/{id}")
  public HttpEntity<?> updateBucket(@PathVariable("id") Long id) {
    LOGGER.info("Unregistering S3 bucket {}", id);
    s3Service.removeBucket(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
