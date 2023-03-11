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

package io.studytracker.config.initialization;

import io.studytracker.aws.AwsIntegrationService;
import io.studytracker.config.properties.AWSProperties;
import io.studytracker.config.properties.AWSProperties.S3Properties;
import io.studytracker.config.properties.StudyTrackerProperties;
import io.studytracker.exception.InvalidConfigurationException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.AwsIntegration;
import io.studytracker.model.FileStorageLocation;
import io.studytracker.model.IntegrationInstance;
import io.studytracker.model.Organization;
import io.studytracker.model.S3Bucket;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDrive.DriveType;
import io.studytracker.service.OrganizationService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Initializes AWS service configurations and captures records in the database.
 *
 * @author Will Oemler
 * @since 0.9.0
 */
@Component
public class AwsIntegrationInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(AwsIntegrationInitializer.class);

  @Autowired
  private StudyTrackerProperties properties;

  @Autowired
  private AwsIntegrationService awsIntegrationService;

  @Autowired
  private OrganizationService organizationService;

  private AwsIntegration registerAwsIntegrationInstances(Organization organization) throws InvalidConfigurationException {

    AwsIntegration awsIntegration = null;
    AWSProperties awsProperties = properties.getAws();

    // Check to see if AWS config properties are present
    if (awsProperties != null && StringUtils.hasText(awsProperties.getRegion())) {

      List<AwsIntegration> integrations = awsIntegrationService.findByOrganization(organization);

      // If yes, update the record
      if (integrations.size() > 0) {
        LOGGER.info("Updating AWS integration for organization {}", organization.getName());
        AwsIntegration existing = integrations.get(0);
        if (StringUtils.hasText(awsProperties.getAccessKeyId())
            && StringUtils.hasText(awsProperties.getSecretAccessKey())) {
          existing.setAccessKeyId(awsProperties.getAccessKeyId());
          existing.setSecretAccessKey(awsProperties.getSecretAccessKey());
          existing.setUseIam(false);
        } else {
          existing.setAccessKeyId(null);
          existing.setSecretAccessKey(null);
          existing.setUseIam(true);
        }
        awsIntegration = awsIntegrationService.update(existing);
      }
      // If no, create a new record
      else {
        LOGGER.info("Creating new AWS integration for organization {}", organization.getName());
        AwsIntegration newIntegration = new AwsIntegration();
        newIntegration.setName("Default AWS Integration");
        newIntegration.setOrganization(organization);
        newIntegration.setRegion(awsProperties.getRegion());
        if (StringUtils.hasText(awsProperties.getAccessKeyId())
            && StringUtils.hasText(awsProperties.getSecretAccessKey())) {
          newIntegration.setAccessKeyId(awsProperties.getAccessKeyId());
          newIntegration.setSecretAccessKey(awsProperties.getSecretAccessKey());
          newIntegration.setUseIam(false);
        } else {
          newIntegration.setAccessKeyId(null);
          newIntegration.setSecretAccessKey(null);
          newIntegration.setUseIam(true);
        }
        awsIntegration = awsIntegrationService.register(newIntegration);
      }

    }

    return awsIntegration;

  }

  /**
   * Registers an {@link IntegrationInstance} for AWS S2 instances, if they are being used, as
   *   well as {@link FileStorageLocation} instances for each provided bucket.
   *
   * @throws InvalidConfigurationException if required environment variables are missing
   */
  private void registerS3Buckets(AwsIntegration awsIntegration, Organization organization) throws InvalidConfigurationException {

    if (awsIntegration == null) {
      LOGGER.warn("AWS integration is not configured. Skipping S3 bucket registration.");
      return;
    }

    S3Properties s3Properties = properties.getAws().getS3();
    if (s3Properties != null && StringUtils.hasText(s3Properties.getBuckets())) {
      for (String bucketName: s3Properties.getBuckets().split(",")) {
        if (StringUtils.hasText(bucketName)) {
          if (!awsIntegrationService.bucketIsRegistered(awsIntegration, bucketName)) {
            LOGGER.info("Registering S3 bucket: " + bucketName);
            StorageDrive storageDrive = new StorageDrive();
            storageDrive.setOrganization(organization);
            storageDrive.setActive(true);
            storageDrive.setDisplayName("S3: " + bucketName);
            storageDrive.setDriveType(DriveType.S3);
            storageDrive.setRootPath("");
            S3Bucket bucket = new S3Bucket();
            bucket.setName(bucketName);
            bucket.setAwsIntegration(awsIntegration);
            bucket.setStorageDrive(storageDrive);
            awsIntegrationService.registerBucket(bucket);
          }
        }
      }
    }
  }

  @Transactional
  public void initializeIntegrations() throws InvalidConfigurationException {

    Organization organization;

    // Check to see if the AWS integration is already registered
    try {
      organization = organizationService.getCurrentOrganization();
    } catch (RecordNotFoundException e) {
      e.printStackTrace();
      LOGGER.error("No organization found. Skipping AWS integration initialization.");
      return;
    }

    try {

      // Register integration definitiions
      AwsIntegration awsIntegration = registerAwsIntegrationInstances(organization);
      if (awsIntegration != null) {
        registerS3Buckets(awsIntegration, organization);
      }

    } catch (Exception e) {
      LOGGER.error("Failed to initialize AWS integrations", e);
      e.printStackTrace();
      throw new InvalidConfigurationException(e);
    }

  }

}
