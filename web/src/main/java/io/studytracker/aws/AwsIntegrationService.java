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
import io.studytracker.model.S3Bucket;
import io.studytracker.repository.AwsIntegrationRepository;
import io.studytracker.repository.S3BucketRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;

@Service
public class AwsIntegrationService implements IntegrationService<AwsIntegration> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AwsIntegrationService.class);

  private final AwsIntegrationRepository awsIntegrationRepository;
  private final S3BucketRepository s3BucketRepository;

  public AwsIntegrationService(AwsIntegrationRepository awsIntegrationRepository,
      S3BucketRepository s3BucketRepository) {
    this.awsIntegrationRepository = awsIntegrationRepository;
    this.s3BucketRepository = s3BucketRepository;
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
    return awsIntegrationRepository.save(awsIntegration);
  }

  @Transactional
  @Override
  public AwsIntegration update(AwsIntegration awsIntegration) {
    LOGGER.info("Updating AWS integration: {}", awsIntegration);
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
    return false;
  }

  @Override
  public boolean test(AwsIntegration instance) {
    return false;
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

  public List<S3Bucket> findRegisteredBuckets(AwsIntegration integration) {
    return s3BucketRepository.findByAwsIntegrationId(integration.getId());
  }

  public boolean bucketIsRegistered(AwsIntegration integration, String bucketName) {
    return s3BucketRepository.findByIntegrationAndName(integration.getId(), bucketName).isPresent();
  }

  @Transactional
  public S3Bucket registerBucket(S3Bucket bucket) {
    LOGGER.info("Registering bucket {}", bucket.getName());
    if (!this.bucketExists(bucket.getAwsIntegration(), bucket.getName())) {
      throw new IllegalArgumentException("Bucket does not exist: " + bucket.getName());
    }
    return s3BucketRepository.save(bucket);
  }

  @Transactional
  public void removeBucket(Long id) {
    S3Bucket b = s3BucketRepository.getById(id);
    b.getStorageDrive().setActive(false);
    s3BucketRepository.save(b);
  }

}
