package io.studytracker.egnyte.integration;

import io.studytracker.integration.IntegrationType;
import io.studytracker.model.IntegrationInstance;

public class EgnyteIntegrationOptionsFactory {

  public static EgnyteIntegrationOptions create(IntegrationInstance instance) {
    if (!instance.getDefinition().getType().equals(IntegrationType.EGNYTE)) {
      throw new IllegalArgumentException("Integration type is not Egnyte");
    }

    if (instance.getDefinition().getVersion() == 1) {
      return new EgnyteIntegrationV1(instance);
    }
    throw new IllegalArgumentException("Unsupported Egnyte integration version");
  }

}
