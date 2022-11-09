/*
 * Copyright 2022 the original author or authors.
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

import io.studytracker.egnyte.integration.EgnyteIntegrationV1;
import io.studytracker.exception.InvalidConfigurationException;
import io.studytracker.integration.FileStorageLocationBuilder;
import io.studytracker.integration.IntegrationInstanceBuilder;
import io.studytracker.integration.IntegrationType;
import io.studytracker.model.FileStorageLocation;
import io.studytracker.model.FileStoreFolder;
import io.studytracker.model.IntegrationDefinition;
import io.studytracker.model.IntegrationInstance;
import io.studytracker.repository.FileStorageLocationRepository;
import io.studytracker.repository.FileStoreFolderRepository;
import io.studytracker.repository.IntegrationDefinitionRepository;
import io.studytracker.repository.IntegrationInstanceRepository;
import io.studytracker.storage.StorageLocationType;
import io.studytracker.storage.StoragePermissions;
import io.studytracker.storage.integration.LocalFileSystemIntegrationV1;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class IntegrationInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationInitializer.class);

  public static final List<IntegrationDefinition> INTEGRATION_DEFINITIONS = List.of(
      EgnyteIntegrationV1.getIntegrationDefinition(),
      LocalFileSystemIntegrationV1.getIntegrationDefinition()
  );

  @Autowired
  private Environment env;

  @Autowired
  private IntegrationDefinitionRepository integrationDefinitionRepository;

  @Autowired
  private FileStorageLocationRepository fileStorageLocationRepository;

  @Autowired
  private FileStoreFolderRepository fileStoreFolderRepository;

  @Autowired
  private IntegrationInstanceRepository integrationInstanceRepository;

  @PostConstruct
  @Transactional
  public void initializeIntegrations() throws InvalidConfigurationException {

    try {

      // Register integration definitiions
      for (IntegrationDefinition integration : INTEGRATION_DEFINITIONS) {
        Optional<IntegrationDefinition> integrationOptional = integrationDefinitionRepository
            .findByTypeAndVersion(integration.getType(), integration.getVersion());

        // Register new integration option
        if (integrationOptional.isEmpty()) {
          LOGGER.info("Initializing integration: {}", integration);
          integrationDefinitionRepository.save(integration);
        }

        // Update existing option, if necessary
        else {
          IntegrationDefinition existingIntegration = integrationOptional.get();
          if (existingIntegration.isActive() != integration.isActive()) {
            LOGGER.info("Updating integration: {}", integration);
            existingIntegration.setActive(integration.isActive());
            integrationDefinitionRepository.save(existingIntegration);
          }
        }
      }

      // Register integration instances for legacy configurations
      FileStorageLocation defaultLocation = null;
      boolean updateFlag = false; // for triggering update of existing folder records

      // Is Egnyte being used?
      if (env.containsProperty("storage.mode")
          && env.getRequiredProperty("storage.mode").equals("egnyte")) {

        // Is there an active Egnyte integration instance?
        List<IntegrationInstance> egnyteInstances = integrationInstanceRepository
            .findByIntegrationType(IntegrationType.EGNYTE);

        if (egnyteInstances.isEmpty()) {

          // Get the latest integration definition for Egnyte
          Optional<IntegrationDefinition> definitionOptional
              = integrationDefinitionRepository.findLatestByType(IntegrationType.EGNYTE);
          if (definitionOptional.isEmpty()) {
            throw new InvalidConfigurationException("Could not find a suitable Egnyte integration to "
                + "initialize legacy storage location");
          }
          IntegrationDefinition egnyteDef = definitionOptional.get();

          // Create an integration instance
          IntegrationInstanceBuilder builder = new IntegrationInstanceBuilder()
              .name(env.getRequiredProperty("egnyte.tenant-name") + "-egnyte")
              .displayName("Egnyte")
              .integrationDefinition(egnyteDef)
              .active(true)
              .configurationValue(EgnyteIntegrationV1.TENANT_NAME,
                  env.getRequiredProperty("egnyte.tenant-name"))
              .configurationValue(EgnyteIntegrationV1.API_TOKEN,
                  env.getRequiredProperty("egnyte.api-token"))
              .configurationValue(EgnyteIntegrationV1.ROOT_PATH,
                  env.getRequiredProperty("egnyte.root-path"));
          if (env.containsProperty("egnyte.root-url")) {
            builder.configurationValue(EgnyteIntegrationV1.ROOT_URL,
                env.getRequiredProperty("egnyte.root-url"));
          } else {
            builder.configurationValue(EgnyteIntegrationV1.ROOT_URL,
                "https://" + env.getRequiredProperty("egnyte.tenant-name") + ".egnyte.com");
          }
          IntegrationInstance egnyteInstance = integrationInstanceRepository.save(builder.build());

          // Create the default file storage location
          FileStorageLocationBuilder locationBuilder = new FileStorageLocationBuilder()
              .integrationInstance(egnyteInstance)
              .type(StorageLocationType.EGNYTE_API)
              .displayName("Egnyte Study Folder")
              .name("egnyte-study-folder")
              .permissions(StoragePermissions.READ_WRITE)
              .rootFolderPath(env.getRequiredProperty("egnyte.root-path"))
              .defaultStudyLocation(true)
              .defaultDataLocation(false);
          defaultLocation = fileStorageLocationRepository.save(locationBuilder.build());
          updateFlag = true;

        } else {
          LOGGER.info("Egnyte integration instance already exists");
        }

      }

      // Local file system
      else {

        // Is there an active local file system integration instance?
        List<IntegrationInstance> localFileSystemInstances = integrationInstanceRepository
            .findByIntegrationType(IntegrationType.LOCAL_FILE_SYSTEM);

        if (localFileSystemInstances.isEmpty()) {

          LOGGER.info("Initializing legacy local file storage location...");

          // Get the latest integration definition for Egnyte
          Optional<IntegrationDefinition> definitionOptional
              = integrationDefinitionRepository.findLatestByType(IntegrationType.LOCAL_FILE_SYSTEM);
          if (definitionOptional.isEmpty()) {
            throw new InvalidConfigurationException("Could not find a suitable Egnyte integration to "
                + "initialize legacy storage location");
          }
          IntegrationDefinition localFileSystemDef = definitionOptional.get();

          // Build the instance record
          IntegrationInstanceBuilder builder = new IntegrationInstanceBuilder()
              .name("local-file-system")
              .displayName("Local File System")
              .integrationDefinition(localFileSystemDef)
              .active(true)
              .configurationValue(LocalFileSystemIntegrationV1.ROOT_PATH,
                  env.getRequiredProperty("storage.local-dir"));
          IntegrationInstance localFileSystemInstance
              = integrationInstanceRepository.save(builder.build());

          // Create the default file storage location
          FileStorageLocationBuilder locationBuilder = new FileStorageLocationBuilder()
              .integrationInstance(localFileSystemInstance)
              .type(StorageLocationType.LOCAL_FILE_SYSTEM)
              .displayName("Local Study Folder")
              .name("local-study-folder")
              .permissions(StoragePermissions.READ_WRITE)
              .rootFolderPath(env.getRequiredProperty("storage.local-dir"))
              .defaultStudyLocation(true)
              .defaultDataLocation(false);
          defaultLocation = fileStorageLocationRepository.save(locationBuilder.build());
          updateFlag = true;
        } else {
          LOGGER.info("Local file system integration instance already exists");
        }

      }

      // Update existing folder records
      if (updateFlag) {
        LOGGER.info("Updating existing folder records...");
        int counter = 0;
        for (FileStoreFolder folder: fileStoreFolderRepository.findFoldersWithPlaceholderLocations()) {
          folder.setFileStorageLocation(defaultLocation);
          fileStoreFolderRepository.save(folder);
          counter++;
        }
        LOGGER.info("Legacy folder record update complete. Updated {} records.", counter);
      }

    } catch (Exception e) {
      LOGGER.error("Failed to initialize integrations", e);
      e.printStackTrace();
      throw new InvalidConfigurationException(e);
    }

  }

}
