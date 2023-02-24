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

import io.studytracker.model.S3Bucket;
import io.studytracker.repository.AwsIntegrationRepository;
import io.studytracker.repository.S3BucketRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;

public class S3Service {

  private static final Logger LOGGER = LoggerFactory.getLogger(S3Service.class);

  private final S3Client s3Client;
  private final S3BucketRepository s3BucketRepository;
  private final AwsIntegrationRepository awsIntegrationRepository;

  public S3Service(
      S3Client s3Client,
      S3BucketRepository s3BucketRepository,
      AwsIntegrationRepository awsIntegrationRepository
  ) {
    this.s3Client = s3Client;
    this.s3BucketRepository = s3BucketRepository;
    this.awsIntegrationRepository = awsIntegrationRepository;
  }

  public List<String> listAvailableBuckets() {
    LOGGER.debug("Listing available buckets");
    return s3Client.listBuckets().buckets().stream()
        .map(Bucket::name)
        .collect(Collectors.toList());
  }

  public boolean bucketExists(String bucketName) {
    return this.listAvailableBuckets().contains(bucketName);
  }

  public List<S3Bucket> findRegisteredBuckets() {
    return s3BucketRepository.findAll();
  }

  public boolean bucketIsRegistered(String bucketName) {
    return s3BucketRepository.findByName(bucketName).isPresent();
  }

  @Transactional
  public S3Bucket registerBucket(S3Bucket bucket) {
    LOGGER.info("Registering bucket {}", bucket.getName());
    if (!this.bucketExists(bucket.getName())) {
      throw new IllegalArgumentException("Bucket does not exist");
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
