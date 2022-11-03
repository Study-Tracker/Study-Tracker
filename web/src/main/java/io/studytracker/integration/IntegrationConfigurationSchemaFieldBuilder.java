package io.studytracker.integration;

import io.studytracker.model.CustomEntityFieldType;
import io.studytracker.model.IntegrationConfigurationSchemaField;
import org.springframework.util.Assert;

public class IntegrationConfigurationSchemaFieldBuilder {

  private IntegrationConfigurationSchemaField field;

  public IntegrationConfigurationSchemaFieldBuilder() {
    field = new IntegrationConfigurationSchemaField();
  }

  public IntegrationConfigurationSchemaFieldBuilder fieldName(String name) {
    field.setFieldName(name);
    return this;
  }

  public IntegrationConfigurationSchemaFieldBuilder displayName(String displayName) {
    field.setDisplayName(displayName);
    return this;
  }

  public IntegrationConfigurationSchemaFieldBuilder description(String description) {
    field.setDescription(description);
    return this;
  }

  public IntegrationConfigurationSchemaFieldBuilder type(CustomEntityFieldType type) {
    field.setType(type);
    return this;
  }

  public IntegrationConfigurationSchemaFieldBuilder required(Boolean required) {
    field.setRequired(required);
    return this;
  }

  public IntegrationConfigurationSchemaFieldBuilder active(Boolean active) {
    field.setActive(active);
    return this;
  }

  public IntegrationConfigurationSchemaFieldBuilder order(Integer order) {
    field.setOrder(order);
    return this;
  }

  public IntegrationConfigurationSchemaField build() {
    try {
      Assert.notNull(field.getFieldName(), "Field name is required");
      Assert.notNull(field.getDisplayName(), "Display name is required");
      Assert.notNull(field.getType(), "Type is required");
      Assert.notNull(field.getOrder(), "Order is required.");
    } catch (Exception e) {
      e.printStackTrace();
      throw new IllegalStateException("Invalid integration configuration field configuration");
    }
    return field;
  }

}
