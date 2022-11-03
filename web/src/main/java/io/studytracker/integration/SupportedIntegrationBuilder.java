package io.studytracker.integration;

import io.studytracker.model.IntegrationConfigurationSchemaField;
import io.studytracker.model.SupportedIntegration;
import java.util.Collection;
import org.springframework.util.Assert;

public class SupportedIntegrationBuilder {

  private SupportedIntegration integration;

  public SupportedIntegrationBuilder() {
    integration = new SupportedIntegration();
  }

  public SupportedIntegrationBuilder name(String name) {
    integration.setName(name);
    return this;
  }

  public SupportedIntegrationBuilder version(Integer version) {
    integration.setVersion(version);
    return this;
  }

  public SupportedIntegrationBuilder active(Boolean active) {
    integration.setActive(active);
    return this;
  }

  public SupportedIntegrationBuilder configurationSchemaFields(
      Collection<IntegrationConfigurationSchemaField> fields) {
    integration.getConfigurationSchemaFields().addAll(fields);
    return this;
  }

  public SupportedIntegrationBuilder configurationSchemaField(
      IntegrationConfigurationSchemaField field) {
    integration.getConfigurationSchemaFields().add(field);
    return this;
  }

  public SupportedIntegration build() {
    try {
      Assert.notNull(integration.getName(), "Name is required");
      Assert.notNull(integration.getVersion(), "Version is required");
    } catch (Exception e) {
      e.printStackTrace();
      throw new IllegalStateException("Invalid integration configuration");
    }
    return integration;
  }

}
