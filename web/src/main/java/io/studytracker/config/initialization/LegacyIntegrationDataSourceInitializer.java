package io.studytracker.config.initialization;

import io.studytracker.egnyte.EgnyteIntegrationAttributes;
import io.studytracker.model.IntegrationInstance;
import io.studytracker.model.SupportedIntegration;
import io.studytracker.service.IntegrationsService;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class LegacyIntegrationDataSourceInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(LegacyIntegrationDataSourceInitializer.class);

  @Autowired private Environment env;

  @Autowired private IntegrationsService integrationsService;

  @PostConstruct
  public void initializeLegacyDataSources() {

    // Egnyte
    if (env.containsProperty("storage.mode")
        && env.getRequiredProperty("storage.mode").equals("egnyte")) {
      LOGGER.info("Initializing legacy Egnyte storage location...");

      // Get the latest supported integration definition for Egnyte
      Optional<SupportedIntegration> supportedIntegrationOptional = integrationsService
          .findLatestSupportedIntegrationByName(EgnyteIntegrationAttributes.INTEGRATION_NAME);
      if (supportedIntegrationOptional.isEmpty()) {
        throw new IllegalStateException("Could not find a suitable Egnyte integration to "
            + "initialize legacy storage location");
      }
      SupportedIntegration egnyteDef = supportedIntegrationOptional.get();

      IntegrationInstance egnyteInstance = new IntegrationInstance();
      egnyteInstance.setDisplayName(EgnyteIntegrationAttributes.INTEGRATION_NAME);
      egnyteInstance.setName(env.getRequiredProperty("egnyte.tenant-name") + "-egnyte");
      egnyteInstance.setActive(true);


    }

    // Local file system
    else {
      LOGGER.info("Initializing legacy local file storage location...");

      // Create the IntegrationInstance

    }
  }

}
