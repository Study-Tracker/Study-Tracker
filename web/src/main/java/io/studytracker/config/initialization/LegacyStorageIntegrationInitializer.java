package io.studytracker.config.initialization;

import io.studytracker.egnyte.EgnyteIntegrationDefinition;
import io.studytracker.integration.FileStorageLocationBuilder;
import io.studytracker.integration.IntegrationInstanceBuilder;
import io.studytracker.model.FileStorageLocation;
import io.studytracker.model.IntegrationInstance;
import io.studytracker.model.SupportedIntegration;
import io.studytracker.repository.FileStorageLocationRepository;
import io.studytracker.service.IntegrationsService;
import io.studytracker.storage.StorageLocationType;
import io.studytracker.storage.StoragePermissions;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class LegacyStorageIntegrationInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(LegacyStorageIntegrationInitializer.class);

  @Autowired private Environment env;

  @Autowired private IntegrationsService integrationsService;

  @Autowired private FileStorageLocationRepository fileStorageLocationRepository;

  @PostConstruct
  public void initializeIntegrations() {

    FileStorageLocation defaultLocation = null;
    boolean updateFlag = false;

    //// Egnyte

    // Are all Egnyte integration definitions registered?
    List<SupportedIntegration> egnyteIntegrations =
        integrationsService.findSupportedIntegrationsByName(EgnyteIntegrationDefinition.INTEGRATION_NAME);
    if (egnyteIntegrations.size() < EgnyteIntegrationDefinition.integrationDefinitions().size()) {
      for (SupportedIntegration integration: EgnyteIntegrationDefinition.integrationDefinitions()) {
        Optional<SupportedIntegration> optional = integrationsService
            .findSupportedIntegrationByNameAndVersion(integration.getName(), integration.getVersion());
        if (optional.isEmpty()) {
          LOGGER.info("Registering Egnyte integration definition: {}", integration);
          integrationsService.registerSupportedIntegration(integration);
        }
      }
    }

    // Is Egnyte being used?
    if (env.containsProperty("storage.mode")
        && env.getRequiredProperty("storage.mode").equals("egnyte")) {

      // Is there an active Egnyte integration instance?
      List<IntegrationInstance> egnyteInstances = integrationsService
          .findActiveIntegrationsInstanceByParentName(EgnyteIntegrationDefinition.INTEGRATION_NAME);
      if (egnyteInstances.isEmpty()) {

        // Get the latest supported integration definition for Egnyte
        Optional<SupportedIntegration> supportedIntegrationOptional = integrationsService
            .findLatestSupportedIntegrationByName(EgnyteIntegrationDefinition.INTEGRATION_NAME);
        if (supportedIntegrationOptional.isEmpty()) {
          throw new IllegalStateException("Could not find a suitable Egnyte integration to "
              + "initialize legacy storage location");
        }
        SupportedIntegration egnyteDef = supportedIntegrationOptional.get();

        // Create an integration instance
        IntegrationInstanceBuilder builder = new IntegrationInstanceBuilder()
            .name(env.getRequiredProperty("egnyte.tenant-name") + "-egnyte")
            .displayName("Egnyte")
            .supportedIntegration(egnyteDef)
            .active(true)
            .configurationValue(EgnyteIntegrationDefinition.TENANT_NAME, env.getRequiredProperty("egnyte.tenant-name"))
            .configurationValue(EgnyteIntegrationDefinition.API_TOKEN, env.getRequiredProperty("egnyte.api-token"))
            .configurationValue(EgnyteIntegrationDefinition.ROOT_PATH, env.getRequiredProperty("egnyte.root-path"));
        if (env.containsProperty("egnyte.root-url")) {
          builder.configurationValue(EgnyteIntegrationDefinition.ROOT_URL, env.getRequiredProperty("egnyte.root-url"));
        } else {
          builder.configurationValue(EgnyteIntegrationDefinition.ROOT_URL, "https://" + env.getRequiredProperty("egnyte.tenant-name") + ".egnyte.com");
        }
        IntegrationInstance egnyteInstance = integrationsService.createIntegrationInstance(builder.build());

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
        FileStorageLocation egnyteLocation = fileStorageLocationRepository.save(locationBuilder.build());
        defaultLocation = egnyteLocation;
        updateFlag = true;

      }

    }

    // Local file system
    else {
      LOGGER.info("Initializing legacy local file storage location...");

      // Create the IntegrationInstance

    }
  }

}
