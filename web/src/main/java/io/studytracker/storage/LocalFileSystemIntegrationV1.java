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

package io.studytracker.storage;

import io.studytracker.integration.IntegrationConfigurationSchemaFieldBuilder;
import io.studytracker.integration.IntegrationDefinitionBuilder;
import io.studytracker.integration.IntegrationType;
import io.studytracker.model.CustomEntityFieldType;
import io.studytracker.model.IntegrationDefinition;
import io.studytracker.model.IntegrationInstance;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocalFileSystemIntegrationV1 {

  public static final String ROOT_PATH = "root-path";

  private String rootPath;

  public LocalFileSystemIntegrationV1(IntegrationInstance instance) {
    this.rootPath = instance.getConfigurationValue(ROOT_PATH)
        .orElseThrow(() -> new IllegalArgumentException("Root path must be specified."));
  }

  public static IntegrationDefinition getDefinition() {
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
