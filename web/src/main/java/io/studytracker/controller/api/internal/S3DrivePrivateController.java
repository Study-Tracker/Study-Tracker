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
import io.studytracker.mapstruct.dto.response.StorageDriveDetailsDto;
import io.studytracker.mapstruct.mapper.StorageDriveMapper;
import io.studytracker.model.AwsIntegration;
import io.studytracker.model.S3BucketDetails;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDrive.DriveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/internal/drives/s3")
public class S3DrivePrivateController {

  public static final Logger LOGGER = LoggerFactory.getLogger(S3DrivePrivateController.class);

  @Autowired
  private AwsIntegrationService awsIntegrationService;

  @Autowired
  private StorageDriveMapper mapper;

  @GetMapping("")
  public List<StorageDriveDetailsDto> findRegisteredBuckets() {
    LOGGER.debug("Listing registered buckets");
    List<AwsIntegration> integrations = awsIntegrationService.findAll();
    List<StorageDrive> drives = new ArrayList<>();
    if (!integrations.isEmpty()) {
      drives.addAll(awsIntegrationService.findRegisteredBuckets(integrations.get(0)));
    }
    return mapper.toDetailsDto(drives);
  }

  @PostMapping("")
  public HttpEntity<StorageDriveDetailsDto> registerBucket(@Valid @RequestBody S3BucketFormDto dto) {
    LOGGER.info("Registering S3 bucket {}", dto.getDisplayName());
    AwsIntegration integration = awsIntegrationService.findAll().get(0);
    StorageDrive bucket = mapper.fromS3FormDto(dto);
    S3BucketDetails details = new S3BucketDetails();
    details.setBucketName(dto.getBucketName());
    details.setAwsIntegrationId(integration.getId());
    bucket.setDetails(details);
    bucket.setDriveType(DriveType.S3);
    bucket.setRootPath("");
    StorageDrive created = awsIntegrationService.registerBucket(bucket);
    return new ResponseEntity<>(mapper.toDetailsDto(created), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<StorageDriveDetailsDto> updateBucket(@PathVariable("id") Long id,
      @Valid @RequestBody S3BucketFormDto dto) {
    LOGGER.info("Updating S3 bucket {}", id);
    StorageDrive bucket = mapper.fromS3FormDto(dto);
    bucket.setId(id);
    S3BucketDetails details = new S3BucketDetails();
    details.setBucketName(dto.getBucketName());
    bucket.setDetails(details);
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
