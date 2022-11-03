package io.studytracker.egnyte;

import io.studytracker.integration.IntegrationConfigurationSchemaFieldBuilder;
import io.studytracker.integration.SupportedIntegrationBuilder;
import io.studytracker.model.CustomEntityFieldType;
import io.studytracker.model.SupportedIntegration;
import java.util.ArrayList;
import java.util.List;

public class EgnyteIntegrationDefinition {

  public static final String INTEGRATION_NAME = "Egnyte";
  public static final String TENANT_NAME = "tenant-name";
  public static final String ROOT_URL = "root-url";
  public static final String API_TOKEN = "api-token";
  public static final String ROOT_PATH = "root-path";

  public static List<SupportedIntegration> integrationDefinitions() {

    List<SupportedIntegration> list = new ArrayList<>();

    // V1
    SupportedIntegration integration = new SupportedIntegrationBuilder()
        .name(INTEGRATION_NAME)
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
      .build();

    list.add(integration);

    return list;

  }

}
