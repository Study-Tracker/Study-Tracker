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
import io.studytracker.model.*;
import io.studytracker.model.StorageDrive.DriveType;
import io.studytracker.repository.StorageDriveFolderRepository;
import io.studytracker.repository.StorageDriveRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

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
  private StorageDriveRepository storageDriveRepository;

  @Autowired
  private StorageDriveFolderRepository folderRepository;

  private AwsIntegration registerAwsIntegrationInstances() throws InvalidConfigurationException {

    AwsIntegration awsIntegration = null;
    AWSProperties awsProperties = properties.getAws();

    // Check to see if AWS config properties are present
    if (awsProperties != null && StringUtils.hasText(awsProperties.getRegion())) {

      List<AwsIntegration> integrations = awsIntegrationService.findAll();

      // If yes, update the record
      if (integrations.size() > 0) {

        // Has the record already been updated?
        AwsIntegration existing = integrations.get(0);
        if (!existing.getCreatedAt().equals(existing.getUpdatedAt())) {
          LOGGER.info("AWS integration has already been initialized.");
          return existing;
        }

        // If not, update the record
        LOGGER.info("Updating AWS integration");
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
        existing.setActive(true);

        awsIntegration = awsIntegrationService.update(existing);

      }
      // If no, create a new record
      else {
        LOGGER.info("Creating new AWS integration");
        AwsIntegration newIntegration = new AwsIntegration();
        newIntegration.setName("Default AWS Integration");
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
   * Registers an {@link S3BucketDetails} object for AWS S3 instances, if they are being used, as
   *   well as {@link StorageDrive} instances for each provided bucket.
   *
   * @throws InvalidConfigurationException if required environment variables are missing
   */
  private List<StorageDrive> registerS3Buckets(AwsIntegration awsIntegration) throws InvalidConfigurationException {

    if (awsIntegration == null || awsIntegration.getId() == null) {
      LOGGER.warn("AWS integration is not configured. Skipping S3 bucket registration.");
      return null;
    }

    List<StorageDrive> buckets = null;

    S3Properties s3Properties = properties.getAws().getS3();
    if (s3Properties != null && StringUtils.hasText(s3Properties.getBuckets())) {
      buckets = new ArrayList<>();
      for (String bucketName: s3Properties.getBuckets().split(",")) {
        if (StringUtils.hasText(bucketName) && !awsIntegrationService.bucketIsRegistered(awsIntegration, bucketName)) {
          LOGGER.info("Registering S3 bucket: " + bucketName);
          StorageDrive storageDrive = new StorageDrive();
          storageDrive.setActive(true);
          storageDrive.setDisplayName("S3: " + bucketName);
          storageDrive.setDriveType(DriveType.S3);
          storageDrive.setRootPath("");
          S3BucketDetails details = new S3BucketDetails();
          details.setBucketName(bucketName);
          details.setAwsIntegrationId(awsIntegration.getId());
          storageDrive.setDetails(details);
          buckets.add(awsIntegrationService.registerBucket(storageDrive));
        }
      }
    }
    return buckets;

  }

  private void registerRootFolders(List<StorageDrive> buckets) throws InvalidConfigurationException {

    String defaultStudyBucket = null;
    String defaultStudyPath = null;
    if (StringUtils.hasText(properties.getAws().getS3().getDefaultStudyLocation())) {
      String defaultStudyLocation = properties.getAws().getS3().getDefaultStudyLocation();
      String[] bits = defaultStudyLocation.split("/", 2);
      defaultStudyBucket = bits[0];
      defaultStudyPath = bits.length > 1 ? bits[1] : "";
    }

    for (StorageDrive drive: buckets) {

      S3BucketDetails bucketDetails = (S3BucketDetails) drive.getDetails();

      // Check to see if the root folder has already been registered
      boolean exists = folderRepository.findByStorageDriveId(drive.getId())
          .stream()
          .anyMatch(f -> f.getPath().equals("") && f.isBrowserRoot());

      if (!exists) {

        LOGGER.info("Registering root folder for bucket {}.", bucketDetails.getBucketName());

        StorageDriveFolder rootFolder = new StorageDriveFolder();
        rootFolder.setStudyRoot(false);
        rootFolder.setBrowserRoot(true);
        rootFolder.setWriteEnabled(true);
        rootFolder.setName(drive.getDisplayName() + " Root Folder");
        rootFolder.setStorageDrive(drive);
        rootFolder.setPath("");

        S3FolderDetails details = new S3FolderDetails();
        details.setKey("");
        rootFolder.setDetails(details);

        folderRepository.save(rootFolder);

        if (bucketDetails.getBucketName().equals(defaultStudyBucket)) {

          StorageDriveFolder studyRootFolder = new StorageDriveFolder();
          studyRootFolder.setStudyRoot(true);
          studyRootFolder.setBrowserRoot(true);
          studyRootFolder.setWriteEnabled(true);
          studyRootFolder.setName(drive.getDisplayName() + " Root Folder");
          studyRootFolder.setStorageDrive(drive);
          studyRootFolder.setPath(defaultStudyPath);

          S3FolderDetails folderDetails = new S3FolderDetails();
          folderDetails.setKey("");
          studyRootFolder.setDetails(folderDetails);

          folderRepository.save(studyRootFolder);
        }

      } else {
        LOGGER.info("Root folder for bucket {} has already been registered.", drive.getDisplayName());
      }

    }

  }

  @Transactional
  public void initializeIntegrations() throws InvalidConfigurationException {

    try {

      // Register integration definitiions
      AwsIntegration awsIntegration = registerAwsIntegrationInstances();
      if (awsIntegration != null) {
        List<StorageDrive> buckets = registerS3Buckets(awsIntegration);
        if (buckets != null) {
          registerRootFolders(buckets);
        }
      }

    } catch (Exception e) {
      LOGGER.error("Failed to initialize AWS integrations", e);
      e.printStackTrace();
      throw new InvalidConfigurationException(e);
    }

  }

}
