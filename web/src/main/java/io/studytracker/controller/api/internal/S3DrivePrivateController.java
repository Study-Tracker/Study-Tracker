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
import io.studytracker.mapstruct.dto.form.StorageDriveFormDto;
import io.studytracker.mapstruct.dto.response.StorageDriveDetailsDto;
import io.studytracker.mapstruct.mapper.StorageDriveMapper;
import io.studytracker.model.AwsIntegration;
import io.studytracker.model.Organization;
import io.studytracker.model.S3BucketDetails;
import io.studytracker.model.StorageDrive;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/drives/s3")
public class S3DrivePrivateController {

  public static final Logger LOGGER = LoggerFactory.getLogger(S3DrivePrivateController.class);

  @Autowired
  private AwsIntegrationService awsIntegrationService;

  @Autowired
  private OrganizationService organizationService;

  @Autowired
  private StorageDriveMapper mapper;

  @GetMapping("")
  public List<StorageDriveDetailsDto> findRegisteredBuckets() {
    LOGGER.debug("Listing registered buckets");
    Organization organization = organizationService.getCurrentOrganization();
    AwsIntegration integration = awsIntegrationService.findByOrganization(organization).get(0);
    return mapper.toDetailsDto(awsIntegrationService.findRegisteredBuckets(integration));
  }

  @PostMapping("")
  public HttpEntity<StorageDriveDetailsDto> registerBucket(@Valid @RequestBody StorageDriveFormDto dto) {
    LOGGER.info("Registering S3 bucket {}", dto.getDisplayName());
    StorageDrive bucket = mapper.fromFormDto(dto);
    Organization organization = organizationService.getCurrentOrganization();
    AwsIntegration integration = awsIntegrationService.findByOrganization(organization).get(0);
    ((S3BucketDetails) bucket.getDetails()).setAwsIntegrationId(integration.getId());
    bucket.setOrganization(organization);
    bucket.setRootPath("");
    StorageDrive created = awsIntegrationService.registerBucket(bucket);
    return new ResponseEntity<>(mapper.toDetailsDto(created), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<StorageDriveDetailsDto> updateBucket(@PathVariable("id") Long id,
      @Valid @RequestBody StorageDriveFormDto dto) {
    LOGGER.info("Updating S3 bucket {}", id);
    StorageDrive bucket = mapper.fromFormDto(dto);
    bucket.setId(id);
    StorageDrive updated = awsIntegrationService.updateBucket(bucket);
    return new ResponseEntity<>(mapper.toDetailsDto(updated), HttpStatus.OK);
  }

  @PatchMapping("/{id}")
  public HttpEntity<?> updateBucketStatus(@PathVariable("id") Long bucketId,
      @RequestParam("active") boolean active) {
    LOGGER.info("Updating S3 bucket status {}", active);
    StorageDrive bucket = awsIntegrationService.findBucketById(bucketId)
            .orElseThrow(() -> new IllegalArgumentException("Bucket not found"));
    awsIntegrationService.updateBucketStatus(bucket, active);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> removeBucket(@PathVariable("id") Long id) {
    LOGGER.info("Unregistering S3 bucket {}", id);
    StorageDrive bucket = awsIntegrationService.findBucketById(id)
        .orElseThrow(() -> new IllegalArgumentException("Bucket not found"));
    awsIntegrationService.removeBucket(bucket);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
