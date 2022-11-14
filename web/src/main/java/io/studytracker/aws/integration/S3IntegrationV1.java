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

package io.studytracker.aws.integration;

import io.studytracker.integration.IntegrationConfigurationSchemaFieldBuilder;
import io.studytracker.integration.IntegrationDefinitionBuilder;
import io.studytracker.integration.IntegrationOptions;
import io.studytracker.integration.IntegrationType;
import io.studytracker.model.CustomEntityFieldType;
import io.studytracker.model.IntegrationDefinition;
import io.studytracker.model.IntegrationInstance;
import io.studytracker.model.IntegrationInstanceConfigurationValue;
import java.util.Set;
import lombok.Data;

@Data
public class S3IntegrationV1 implements S3IntegrationOptions, IntegrationOptions {

  public static final String REGION = "region";
  public static final String BUCKET_NAME = "bucket-name";
  public static final String DEFAUL_STUDY_STORAGE_LOCATION = "default-study-storage-location";

  private String region;
  private String bucketName;
  private String defaultStudyStorageLocation;
  private IntegrationInstance instance;

  public S3IntegrationV1(IntegrationInstance instance) {
    this.region = instance.getConfigurationValue(REGION)
        .orElseThrow(() -> new IllegalArgumentException("Missing required configuration value: " + REGION));
    this.bucketName = instance.getConfigurationValue(BUCKET_NAME)
        .orElseThrow(() -> new IllegalArgumentException("Missing required configuration value: " + BUCKET_NAME));
    if (instance.hasConfigurationValue(DEFAUL_STUDY_STORAGE_LOCATION)) {
      this.defaultStudyStorageLocation = instance.getConfigurationValue(DEFAUL_STUDY_STORAGE_LOCATION)
          .orElseThrow(() -> new IllegalArgumentException("Missing required configuration value: " + DEFAUL_STUDY_STORAGE_LOCATION));
    }
    this.instance = instance;
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
        .type(IntegrationType.AWS_S3)
        .version(1)
        .active(true)
        .configurationSchemaField(
            new IntegrationConfigurationSchemaFieldBuilder()
                .displayName("Region")
                .fieldName(REGION)
                .description("AWS region the bucket resides within.")
                .required(true)
                .type(CustomEntityFieldType.STRING)
                .order(1)
                .build()
        )
        .configurationSchemaField(
            new IntegrationConfigurationSchemaFieldBuilder()
                .displayName("Bucket Name")
                .fieldName(BUCKET_NAME)
                .description("Name of the S3 bucket.")
                .required(true)
                .type(CustomEntityFieldType.TEXT)
                .order(2)
                .build()
        )
        .build();
  }

}
