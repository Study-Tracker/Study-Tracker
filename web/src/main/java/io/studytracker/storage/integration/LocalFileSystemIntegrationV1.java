/*
 * Copyright 2019-2023 the original author or authors.
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

package io.studytracker.storage.integration;

import io.studytracker.integration.IntegrationConfigurationSchemaFieldBuilder;
import io.studytracker.integration.IntegrationDefinitionBuilder;
import io.studytracker.integration.IntegrationType;
import io.studytracker.model.CustomEntityFieldType;
import io.studytracker.model.IntegrationDefinition;
import io.studytracker.model.IntegrationInstance;
import io.studytracker.model.IntegrationInstanceConfigurationValue;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocalFileSystemIntegrationV1 implements LocalFileSystemOptions {

  public static final String ROOT_PATH = "root-path";
  public static final String OVERWRITE_EXISTING = "overwrite-existing";
  public static final String USE_EXISTING = "use-existing";

  private IntegrationInstance instance;
  private String rootPath;
  private boolean overwriteExisting = false;
  private boolean useExisting = true;

  public LocalFileSystemIntegrationV1(IntegrationInstance instance) {
    this.instance = instance;
    this.rootPath = instance.getConfigurationValue(ROOT_PATH)
        .orElseThrow(() -> new IllegalArgumentException("Missing configuration value for " + ROOT_PATH));
    if (instance.hasConfigurationValue(OVERWRITE_EXISTING)) {
      this.overwriteExisting = Boolean.parseBoolean(instance.getConfigurationValue(OVERWRITE_EXISTING)
          .orElseThrow(() -> new IllegalArgumentException("Missing configuration value for " + OVERWRITE_EXISTING)));
    }
    if (instance.hasConfigurationValue(USE_EXISTING)) {
      this.useExisting = Boolean.parseBoolean(instance.getConfigurationValue(USE_EXISTING)
          .orElseThrow(() -> new IllegalArgumentException("Missing configuration value for " + USE_EXISTING)));
    }
  }

  @Override
  public IntegrationDefinition getDefinition() {
    return instance.getDefinition();
  }

  @Override
  public String getDisplayName() {
    return instance.getDisplayName();
  }

  @Override
  public String getName() {
    return instance.getName();
  }

  @Override
  public boolean isActive() {
    return instance.isActive();
  }

  @Override
  public Set<IntegrationInstanceConfigurationValue> getConfigurationValues() {
    return instance.getConfigurationValues();
  }

  public static IntegrationDefinition getIntegrationDefinition() {
    return new IntegrationDefinitionBuilder()
        .type(IntegrationType.LOCAL_FILE_SYSTEM)
        .version(1)
        .active(true)
        .configurationSchemaField(
            new IntegrationConfigurationSchemaFieldBuilder()
                .displayName("Root path")
                .fieldName(ROOT_PATH)
                .description("Root folder path in the file system for all study files to be stored.")
                .type(CustomEntityFieldType.STRING)
                .active(true)
                .required(true)
                .order(1)
                .build()
        )
        .build();
  }

}
