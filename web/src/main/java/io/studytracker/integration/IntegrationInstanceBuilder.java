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

import io.studytracker.model.IntegrationConfigurationSchemaField;
import io.studytracker.model.IntegrationDefinition;
import io.studytracker.model.IntegrationInstance;
import io.studytracker.model.IntegrationInstanceConfigurationValue;
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

  public IntegrationInstanceBuilder integrationDefinition(
      IntegrationDefinition integrationDefinition) {
    instance.setDefinition(integrationDefinition);
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
      Assert.notNull(instance.getDefinition(), "Integration definition is required");

      // Make sure required field are present
      for (IntegrationConfigurationSchemaField schemaField: instance.getDefinition()
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
