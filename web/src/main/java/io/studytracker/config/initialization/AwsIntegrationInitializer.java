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
import io.studytracker.model.Organization;
import io.studytracker.model.S3Bucket;
import io.studytracker.model.S3BucketFolder;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDrive.DriveType;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.repository.S3BucketFolderRepository;
import io.studytracker.repository.S3BucketRepository;
import io.studytracker.repository.StorageDriveRepository;
import io.studytracker.service.OrganizationService;
import java.util.ArrayList;
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

  @Autowired
  private StorageDriveRepository storageDriveRepository;

  @Autowired
  private S3BucketFolderRepository s3BucketFolderRepository;

  @Autowired
  private S3BucketRepository s3BucketRepository;

  private AwsIntegration registerAwsIntegrationInstances(Organization organization) throws InvalidConfigurationException {

    AwsIntegration awsIntegration = null;
    AWSProperties awsProperties = properties.getAws();

    // Check to see if AWS config properties are present
    if (awsProperties != null && StringUtils.hasText(awsProperties.getRegion())) {

      List<AwsIntegration> integrations = awsIntegrationService.findByOrganization(organization);

      // If yes, update the record
      if (integrations.size() > 0) {

        // Has the record already been updated?
        AwsIntegration existing = integrations.get(0);
        if (existing.getCreatedAt().equals(existing.getUpdatedAt())) {
          LOGGER.info("AWS integration for organization {} has already been initialized.", organization.getName());
          return existing;
        }

        // If not, update the record
        LOGGER.info("Updating AWS integration for organization {}", organization.getName());
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
        newIntegration.setActive(true);
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
   * Registers an {@link S3Bucket} object for AWS S3 instances, if they are being used, as
   *   well as {@link StorageDrive} instances for each provided bucket.
   *
   * @throws InvalidConfigurationException if required environment variables are missing
   */
  private List<S3Bucket> registerS3Buckets(AwsIntegration awsIntegration, Organization organization) throws InvalidConfigurationException {

    if (awsIntegration == null) {
      LOGGER.warn("AWS integration is not configured. Skipping S3 bucket registration.");
      return null;
    }

    List<S3Bucket> buckets = null;

    S3Properties s3Properties = properties.getAws().getS3();
    if (s3Properties != null && StringUtils.hasText(s3Properties.getBuckets())) {
      buckets = new ArrayList<>();
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
            buckets.add(awsIntegrationService.registerBucket(bucket));
          }
        }
      }
    }
    return buckets;

  }

  private void registerRootFolders(List<S3Bucket> buckets) throws InvalidConfigurationException {

    String defaultStudyBucket = null;
    String defaultStudyPath = null;
    if (StringUtils.hasText(properties.getAws().getS3().getDefaultStudyLocation())) {
      String defaultStudyLocation = properties.getAws().getS3().getDefaultStudyLocation();
      String[] bits = defaultStudyLocation.split("/", 2);
      defaultStudyBucket = bits[0];
      defaultStudyPath = bits.length > 1 ? bits[1] : "";
    }

    for (S3Bucket b: buckets) {

      S3Bucket bucket = s3BucketRepository.findById(b.getId())
          .orElseThrow(() -> new InvalidConfigurationException("S3 bucket not found: " + b.getName()));
      StorageDrive drive = storageDriveRepository.findById(bucket.getStorageDrive().getId())
          .orElseThrow(() -> new InvalidConfigurationException("Storage drive not found: " + b.getName()));

      // Check to see if the root folder has already been registered
      boolean exists = s3BucketFolderRepository.findByStorageDriveId(drive.getId())
          .stream()
          .anyMatch(f -> f.getStorageDriveFolder().getPath().equals("")
              && f.getStorageDriveFolder().isBrowserRoot());

      if (!exists) {

        LOGGER.info("Registering root folder for bucket {}.", bucket.getName());

        StorageDriveFolder rootFolder = new StorageDriveFolder();
        rootFolder.setStudyRoot(false);
        rootFolder.setBrowserRoot(true);
        rootFolder.setWriteEnabled(true);
        rootFolder.setName(drive.getDisplayName() + " Root Folder");
        rootFolder.setStorageDrive(drive);
        rootFolder.setPath("");

        S3BucketFolder bucketFolder = new S3BucketFolder();
        bucketFolder.setS3Bucket(bucket);
        bucketFolder.setStorageDriveFolder(rootFolder);
        bucketFolder.setKey("");

        s3BucketFolderRepository.save(bucketFolder);

        if (bucket.getName().equals(defaultStudyBucket)) {

          StorageDriveFolder studyRootFolder = new StorageDriveFolder();
          studyRootFolder.setStudyRoot(true);
          studyRootFolder.setBrowserRoot(true);
          studyRootFolder.setWriteEnabled(true);
          studyRootFolder.setName(drive.getDisplayName() + " Root Folder");
          studyRootFolder.setStorageDrive(drive);
          studyRootFolder.setPath(defaultStudyPath);

          S3BucketFolder studyBucketFolder = new S3BucketFolder();
          studyBucketFolder.setS3Bucket(bucket);
          studyBucketFolder.setStorageDriveFolder(studyRootFolder);
          studyBucketFolder.setKey("");

          s3BucketFolderRepository.save(studyBucketFolder);
        }

      } else {
        LOGGER.info("Root folder for bucket {} has already been registered.", bucket.getName());
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
        List<S3Bucket> buckets = registerS3Buckets(awsIntegration, organization);
        registerRootFolders(buckets);
      }

    } catch (Exception e) {
      LOGGER.error("Failed to initialize AWS integrations", e);
      e.printStackTrace();
      throw new InvalidConfigurationException(e);
    }

  }

}
