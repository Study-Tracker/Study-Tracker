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
    field.setFieldOrder(order);
    return this;
  }

  public IntegrationConfigurationSchemaField build() {
    try {
      Assert.notNull(field.getFieldName(), "Field name is required");
      Assert.notNull(field.getDisplayName(), "Display name is required");
      Assert.notNull(field.getType(), "Type is required");
      Assert.notNull(field.getFieldOrder(), "Order is required.");
    } catch (Exception e) {
      e.printStackTrace();
      throw new IllegalStateException("Invalid integration configuration field configuration");
    }
    return field;
  }

}
