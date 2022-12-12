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
import java.util.Collection;
import org.springframework.util.Assert;

public class IntegrationDefinitionBuilder {

  private IntegrationDefinition integration;

  public IntegrationDefinitionBuilder() {
    integration = new IntegrationDefinition();
  }

  public IntegrationDefinitionBuilder type(IntegrationType type) {
    integration.setType(type);
    return this;
  }

  public IntegrationDefinitionBuilder version(Integer version) {
    integration.setVersion(version);
    return this;
  }

  public IntegrationDefinitionBuilder active(Boolean active) {
    integration.setActive(active);
    return this;
  }

  public IntegrationDefinitionBuilder configurationSchemaFields(
      Collection<IntegrationConfigurationSchemaField> fields) {
    for (IntegrationConfigurationSchemaField field : fields) {
      field.setIntegrationDefinition(integration);
      integration.addConfigurationSchemaField(field);
    }
    return this;
  }

  public IntegrationDefinitionBuilder configurationSchemaField(
      IntegrationConfigurationSchemaField field) {
    field.setIntegrationDefinition(integration);
    integration.getConfigurationSchemaFields().add(field);
    return this;
  }

  public IntegrationDefinition build() {
    try {
      Assert.notNull(integration.getType(), "Type is required");
      Assert.notNull(integration.getVersion(), "Version is required");
    } catch (Exception e) {
      e.printStackTrace();
      throw new IllegalStateException("Invalid integration configuration");
    }
    return integration;
  }

}
