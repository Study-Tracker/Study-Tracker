package io.studytracker.integration;

import io.studytracker.model.IntegrationConfigurationSchemaField;
import io.studytracker.model.IntegrationInstance;
import io.studytracker.model.IntegrationInstanceConfigurationValue;
import io.studytracker.model.SupportedIntegration;
import java.util.Collection;
import org.springframework.util.Assert;

public class IntegrationInstanceBuilder {

  private IntegrationInstance instance;

  public IntegrationInstanceBuilder() {
    instance = new IntegrationInstance();
  }

  public IntegrationInstanceBuilder name(String name) {
    instance.setName(name);
    return this;
  }

  public IntegrationInstanceBuilder displayName(String displayName) {
    instance.setDisplayName(displayName);
    return this;
  }

  public IntegrationInstanceBuilder active(Boolean active) {
    instance.setActive(active);
    return this;
  }

  public IntegrationInstanceBuilder supportedIntegration(SupportedIntegration supportedIntegration) {
    instance.setSupportedIntegration(supportedIntegration);
    return this;
  }

  public IntegrationInstanceBuilder configurationValues(
      Collection<IntegrationInstanceConfigurationValue> values) {
    instance.getConfigurationValues().addAll(values);
    return this;
  }

  public IntegrationInstanceBuilder configurationValue(IntegrationInstanceConfigurationValue value) {
    instance.getConfigurationValues().add(value);
    return this;
  }

  public IntegrationInstanceBuilder configurationValue(String fieldName, String value) {
    IntegrationInstanceConfigurationValue configurationValue = new IntegrationInstanceConfigurationValue();
    configurationValue.setFieldName(fieldName);
    configurationValue.setValue(value);
    instance.addConfigurationValue(configurationValue);
    return this;
  }

  public IntegrationInstance build() {
    try {
      Assert.notNull(instance.getName(), "Name is required");
      Assert.notNull(instance.getDisplayName(), "Display name is required");
      Assert.notNull(instance.getSupportedIntegration(), "Supported integration is required");

      // Make sure required field are present
      for (IntegrationConfigurationSchemaField schemaField: instance.getSupportedIntegration()
          .getConfigurationSchemaFields()) {
        if (schemaField.isRequired()) {
          Assert.isTrue(instance.getConfigurationValues().stream()
                  .anyMatch(value -> value.getFieldName().equals(schemaField.getFieldName())),
              "Missing configuration value for field " + schemaField.getFieldName());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new IllegalStateException("Invalid integration instance configuration");
    }
    return instance;
  }

}
