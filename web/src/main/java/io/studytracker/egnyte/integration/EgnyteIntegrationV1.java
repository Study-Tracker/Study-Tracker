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

package io.studytracker.egnyte.integration;

import io.studytracker.exception.StudyTrackerException;
import io.studytracker.integration.IntegrationConfigurationSchemaFieldBuilder;
import io.studytracker.integration.IntegrationDefinitionBuilder;
import io.studytracker.integration.IntegrationInstanceBuilder;
import io.studytracker.integration.IntegrationType;
import io.studytracker.model.CustomEntityFieldType;
import io.studytracker.model.IntegrationDefinition;
import io.studytracker.model.IntegrationInstance;
import io.studytracker.model.IntegrationInstanceConfigurationValue;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Deprecated
@Getter
@Setter
public final class EgnyteIntegrationV1 implements EgnyteIntegrationOptions {

  public static final String TENANT_NAME = "tenant-name";
  public static final String ROOT_URL = "root-url";
  public static final String API_TOKEN = "api-token";
  public static final String ROOT_PATH = "root-path";
  public static final String QPS = "qps";
  public static final String USE_EXISTING = "use-existing";

  private String name;
  private String displayName;
  private boolean active;
  private IntegrationDefinition definition;
  private Set<IntegrationInstanceConfigurationValue> configurationValues = new HashSet<>();
  private String tenantName;
  private URL rootUrl;
  private String rootPath;
  private String token;

  private Integer qps = 1;

  private boolean useExisting = true;

  public EgnyteIntegrationV1(IntegrationInstance instance) {
    this.name = instance.getName();
    this.displayName = instance.getDisplayName();
    this.active = instance.isActive();
    this.definition = instance.getDefinition();
    this.configurationValues = instance.getConfigurationValues();
    this.tenantName = instance.getConfigurationValue(TENANT_NAME)
        .orElseThrow(() -> new IllegalArgumentException("Missing tenant name"));
    this.rootPath = instance.getConfigurationValue(ROOT_PATH)
        .orElseThrow(() -> new IllegalArgumentException("Missing root path"));
    this.token = instance.getConfigurationValue(API_TOKEN)
        .orElseThrow(() -> new IllegalArgumentException("Missing API token"));
    try {
      if (instance.getConfigurationValue(ROOT_URL).isPresent()) {
        this.rootUrl = new URL(instance.getConfigurationValue(ROOT_URL).get());
      } else {
        this.rootUrl = new URL("https://" + tenantName + ".egnyte.com");
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new StudyTrackerException("Invalid root URL", e);
    }
    if (instance.hasConfigurationValue(QPS)) {
      this.qps = Integer.parseInt(instance.getConfigurationValue(QPS)
          .orElseThrow(() -> new IllegalArgumentException("Missing QPS")));
    }
    if (instance.hasConfigurationValue(USE_EXISTING)) {
      this.useExisting = Boolean.parseBoolean(instance.getConfigurationValue(USE_EXISTING)
          .orElseThrow(() -> new IllegalArgumentException("Missing use existing")));
    }
  }

  public IntegrationInstance toIntegrationInstance() {
    return new  IntegrationInstanceBuilder()
        .name(name)
        .displayName(displayName)
        .active(active)
        .integrationDefinition(definition)
        .configurationValues(configurationValues)
        .build();
  }

  public static IntegrationDefinition getIntegrationDefinition() {
    return new IntegrationDefinitionBuilder()
        .type(IntegrationType.EGNYTE)
        .version(1)
        .active(true)
        .configurationSchemaField(
            new IntegrationConfigurationSchemaFieldBuilder()
                .displayName("Tenant Name")
                .fieldName(TENANT_NAME)
                .type(CustomEntityFieldType.STRING)
                .active(true)
                .description("Tenant name, as it appears in your Egnyte URL. For example, if you access Egnyte at https://myorg.egnyte.com, then your tenant name is: \"myorg\"")
                .required(true)
                .order(1)
                .build()
        )
        .configurationSchemaField(
            new IntegrationConfigurationSchemaFieldBuilder()
                .displayName("API Token")
                .fieldName(API_TOKEN)
                .type(CustomEntityFieldType.STRING)
                .active(true)
                .description("API token for making requests to the Egnyte API. This can be generated in the Developer portal.")
                .required(true)
                .order(2)
                .build()
        )
        .configurationSchemaField(
            new IntegrationConfigurationSchemaFieldBuilder()
                .displayName("Root Folder Path")
                .fieldName(ROOT_PATH)
                .type(CustomEntityFieldType.STRING)
                .active(true)
                .description("Root folder to use for storing files. This folder must exist in Egnyte.")
                .required(true)
                .order(3)
                .build()
        )
        .configurationSchemaField(
            new IntegrationConfigurationSchemaFieldBuilder()
                .displayName("Root URL")
                .fieldName(ROOT_URL)
                .type(CustomEntityFieldType.STRING)
                .active(true)
                .description("Root URL for your Egnyte tenant.")
                .required(false)
                .order(4)
                .build()
        )
        .configurationSchemaField(
            new IntegrationConfigurationSchemaFieldBuilder()
                .displayName("QPS")
                .fieldName(QPS)
                .type(CustomEntityFieldType.INTEGER)
                .active(true)
                .description("Maximum number of queries-per-second allowed by the Egnyte API. Defaults to 1.")
                .required(false)
                .order(5)
                .build()
        )
        .configurationSchemaField(
            new IntegrationConfigurationSchemaFieldBuilder()
                .displayName("Use Existing Folders")
                .fieldName(USE_EXISTING)
                .type(CustomEntityFieldType.BOOLEAN)
                .active(true)
                .description("If 'true', when attempting to create new folders, Egnyte will use "
                    + "existing folders with the same name rather than deleting-and-recreating them.")
                .required(false)
                .order(6)
                .build()
        )
        .build();
  }

}
