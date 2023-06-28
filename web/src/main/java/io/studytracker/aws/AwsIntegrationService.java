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

package io.studytracker.aws;

import io.studytracker.integration.IntegrationService;
import io.studytracker.model.AwsIntegration;
import io.studytracker.model.Organization;
import io.studytracker.model.S3BucketDetails;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDrive.DriveType;
import io.studytracker.repository.AwsIntegrationRepository;
import io.studytracker.repository.StorageDriveRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

@Service
public class AwsIntegrationService implements IntegrationService<AwsIntegration> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AwsIntegrationService.class);

  private final AwsIntegrationRepository awsIntegrationRepository;
  private final StorageDriveRepository storageDriveRepository;

  public AwsIntegrationService(AwsIntegrationRepository awsIntegrationRepository,
      StorageDriveRepository storageDriveRepository) {
    this.awsIntegrationRepository = awsIntegrationRepository;
    this.storageDriveRepository = storageDriveRepository;
  }

  @Override
  public Optional<AwsIntegration> findById(Long id) {
    return awsIntegrationRepository.findById(id);
  }

  @Override
  public List<AwsIntegration> findByOrganization(Organization organization) {
    LOGGER.debug("Finding AWS integrations for organization: {}", organization);
    return awsIntegrationRepository.findByOrganizationId(organization.getId());
  }

  @Transactional
  @Override
  public AwsIntegration register(AwsIntegration awsIntegration) {
    LOGGER.info("Creating AWS integration: {}", awsIntegration);
    if (!validate(awsIntegration)) {
      throw new IllegalArgumentException("One or more required fields are missing.");
    }
    if (!test(awsIntegration)) {
      throw new IllegalArgumentException("Failed to connect to AWS with the provided credentials.");
    }
    awsIntegration.setActive(true);
    return awsIntegrationRepository.save(awsIntegration);
  }

  @Transactional
  @Override
  public AwsIntegration update(AwsIntegration awsIntegration) {
    LOGGER.info("Updating AWS integration: {}", awsIntegration);
    if (!validate(awsIntegration)) {
      throw new IllegalArgumentException("One or more required fields are missing.");
    }
    if (!test(awsIntegration)) {
      throw new IllegalArgumentException("Failed to connect to AWS with the provided credentials.");
    }
    AwsIntegration i = awsIntegrationRepository.getById(awsIntegration.getId());
    i.setName(awsIntegration.getName());
    i.setAccountNumber(awsIntegration.getAccountNumber());
    i.setActive(awsIntegration.isActive());
    i.setAccessKeyId(awsIntegration.getAccessKeyId());
    i.setSecretAccessKey(awsIntegration.getSecretAccessKey());
    i.setUseIam(awsIntegration.isUseIam());
    return awsIntegrationRepository.save(i);
  }

  @Transactional
  @Override
  public void remove(AwsIntegration integration) {
    LOGGER.info("Removing AWS integration: {}", integration.getId());
    AwsIntegration i = awsIntegrationRepository.getById(integration.getId());
    i.setAccountNumber(null);
    i.setActive(false);
    i.setAccessKeyId(null);
    i.setSecretAccessKey(null);
    i.setUseIam(false);
    awsIntegrationRepository.save(i);
  }

  @Override
  public boolean validate(AwsIntegration instance) {
    if (!StringUtils.hasText(instance.getName())) return false;
    if (!StringUtils.hasText(instance.getRegion())) return false;
    if (!instance.isUseIam()) {
      if (!StringUtils.hasText(instance.getAccessKeyId())) return false;
      if (!StringUtils.hasText(instance.getSecretAccessKey())) return false;
    }
    return true;
  }

  @Override
  public boolean test(AwsIntegration instance) {
    try {
      S3Client s3Client = AWSClientFactory.createS3Client(instance);
      ListBucketsResponse response = s3Client.listBuckets();
      return response.buckets() != null;
    } catch (Exception e) {
      LOGGER.error("Failed to connect to AWS S3 with the provided credentials.", e);
      return false;
    }
  }

  // S3

  public List<String> listAvailableBuckets(AwsIntegration integration) {
    LOGGER.debug("Listing available buckets");
    S3Client s3Client = AWSClientFactory.createS3Client(integration);
    return s3Client.listBuckets().buckets().stream()
        .map(Bucket::name)
        .collect(Collectors.toList());
  }

  public boolean bucketExists(AwsIntegration integration, String bucketName) {
    return this.listAvailableBuckets(integration).contains(bucketName);
  }

  public List<StorageDrive> findRegisteredBuckets(AwsIntegration integration) {
    Organization organization = integration.getOrganization();
    return storageDriveRepository.findByOrganizationAndDriveType(organization.getId(), DriveType.S3)
        .stream()
        .filter(drive -> drive.getDetails() instanceof S3BucketDetails
            && ((S3BucketDetails) drive.getDetails()).getAwsIntegrationId().equals(integration.getId()))
        .collect(Collectors.toList());
  }

  public boolean bucketIsRegistered(AwsIntegration integration, String bucketName) {
    return storageDriveRepository.findByOrganizationAndDriveType(
        integration.getOrganization().getId(), DriveType.S3)
        .stream()
        .anyMatch(drive -> drive.getDetails() instanceof S3BucketDetails
            && ((S3BucketDetails) drive.getDetails()).getAwsIntegrationId().equals(integration.getId())
            && ((S3BucketDetails) drive.getDetails()).getBucketName().equals(bucketName)
        );
  }

  public Optional<StorageDrive> findBucketById(Long id) {
    Optional<StorageDrive> optional = storageDriveRepository.findById(id);
    if (optional.isPresent()) {
      StorageDrive drive = optional.get();
      if (drive.getDriveType() == DriveType.S3) {
        return optional;
      } else {
        throw new IllegalArgumentException("Drive is not an S3 bucket: " + id);
      }
    } else {
      return Optional.empty();
    }
  }

  @Transactional
  public StorageDrive registerBucket(StorageDrive bucket) {
    S3BucketDetails bucketDetails = (S3BucketDetails) bucket.getDetails();
    AwsIntegration integration = awsIntegrationRepository.findById(bucketDetails.getAwsIntegrationId())
        .orElseThrow(() -> new IllegalArgumentException("Integration does not exist: " + bucketDetails.getAwsIntegrationId()));
    LOGGER.info("Registering bucket {}", bucketDetails.getBucketName());
    bucket.setActive(true);
    bucket.setDriveType(DriveType.S3);
    if (!this.bucketExists(integration, bucketDetails.getBucketName())) {
      throw new IllegalArgumentException("Bucket does not exist: " + bucketDetails.getBucketName());
    }
    return storageDriveRepository.save(bucket);
  }

  @Transactional
  public StorageDrive updateBucket(StorageDrive bucket) {
    LOGGER.info("Updating bucket {}", bucket.getId());
    StorageDrive b = storageDriveRepository.getById(bucket.getId());
    b.setDisplayName(bucket.getDisplayName());
    b.setActive(bucket.isActive());
    return storageDriveRepository.save(b);
  }

  @Transactional
  public void updateBucketStatus(StorageDrive bucket, boolean active) {
    StorageDrive b = storageDriveRepository.getById(bucket.getId());
    b.setActive(active);
    storageDriveRepository.save(b);
  }

  @Transactional
  public void removeBucket(StorageDrive drive) {
    StorageDrive b = storageDriveRepository.getById(drive.getId());
    b.setActive(false);
    storageDriveRepository.save(b);
  }

}
