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
import io.studytracker.mapstruct.dto.form.S3BucketFormDto;
import io.studytracker.mapstruct.dto.response.S3BucketDetailsDto;
import io.studytracker.mapstruct.mapper.S3BucketMapper;
import io.studytracker.model.AwsIntegration;
import io.studytracker.model.Organization;
import io.studytracker.model.S3Bucket;
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
  private S3BucketMapper s3BucketMapper;

  @GetMapping("")
  public List<S3BucketDetailsDto> findRegisteredBuckets() {
    LOGGER.debug("Listing registered buckets");
    Organization organization = organizationService.getCurrentOrganization();
    AwsIntegration integration = awsIntegrationService.findByOrganization(organization).get(0);
    return s3BucketMapper.toDto(awsIntegrationService.findRegisteredBuckets(integration));
  }

  @PostMapping("")
  public HttpEntity<S3BucketDetailsDto> registerBucket(@Valid @RequestBody S3BucketFormDto dto) {
    LOGGER.info("Registering S3 bucket {}", dto.getBucketName());
    S3Bucket bucket = s3BucketMapper.fromFormDto(dto);
    Organization organization = organizationService.getCurrentOrganization();
    AwsIntegration integration = awsIntegrationService.findByOrganization(organization).get(0);
    bucket.setAwsIntegration(integration);
    bucket.getStorageDrive().setOrganization(organization);
    bucket.getStorageDrive().setRootPath("");
    S3Bucket created = awsIntegrationService.registerBucket(bucket);
    return new ResponseEntity<>(s3BucketMapper.toDto(created), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<S3BucketDetailsDto> updateBucket(@PathVariable("id") Long id, @Valid @RequestBody S3BucketFormDto dto) {
    LOGGER.info("Updating S3 bucket {}", id);
    S3Bucket bucket = s3BucketMapper.fromFormDto(dto);
    bucket.setId(id);
    S3Bucket updated = awsIntegrationService.updateBucket(bucket);
    return new ResponseEntity<>(s3BucketMapper.toDto(updated), HttpStatus.OK);
  }

  @PatchMapping("/{id}")
  public HttpEntity<?> updateBucketStatus(@PathVariable("id") Long bucketId,
      @RequestParam("active") boolean active) {
    LOGGER.info("Updating S3 bucket status {}", active);
    S3Bucket bucket = awsIntegrationService.findBucketById(bucketId)
            .orElseThrow(() -> new IllegalArgumentException("Bucket not found"));
    awsIntegrationService.updateBucketStatus(bucket, active);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> removeBucket(@PathVariable("id") Long id) {
    LOGGER.info("Unregistering S3 bucket {}", id);
    awsIntegrationService.removeBucket(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
