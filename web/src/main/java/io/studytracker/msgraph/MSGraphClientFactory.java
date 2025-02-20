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

package io.studytracker.msgraph;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.GraphServiceClient;
import io.studytracker.model.MSGraphIntegration;
import java.util.Arrays;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class MSGraphClientFactory {

  private static final String SCOPE = "https://graph.microsoft.com/.default";

  public static class MSGraphClientBuilder {
    private String clientId;
    private String clientSecret;
    private String tenantId;

    public MSGraphClientBuilder clientId(String clientId) {
      this.clientId = clientId;
      return this;
    }

    public MSGraphClientBuilder clientSecret(String clientSecret) {
      this.clientSecret = clientSecret;
      return this;
    }

    public MSGraphClientBuilder tenantId(String tenantId) {
      this.tenantId = tenantId;
      return this;
    }

    public GraphServiceClient<?> build() {
      Assert.isTrue(StringUtils.hasText(clientId), "Client ID is required");
      Assert.isTrue(StringUtils.hasText(clientSecret), "Client Secret is required");
      Assert.isTrue(StringUtils.hasText(tenantId), "Tenant ID is required");
      ClientSecretCredential credential = new ClientSecretCredentialBuilder()
          .clientId(clientId)
          .clientSecret(clientSecret)
          .tenantId(tenantId)
          .build();
      TokenCredentialAuthProvider provider = new TokenCredentialAuthProvider(
          Arrays.asList(SCOPE), credential);
      return GraphServiceClient.builder()
          .authenticationProvider(provider)
          .buildClient();
    }
  }

  public static GraphServiceClient<?> fromIntegrationInstance(MSGraphIntegration integration) {
    return new MSGraphClientBuilder()
        .clientId(integration.getClientId())
        .clientSecret(integration.getClientSecret())
        .tenantId(integration.getTenantId())
        .build();
  }

}
