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

package io.studytracker.egnyte;

import io.studytracker.egnyte.rest.EgnyteRestApiClient;
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.model.EgnyteIntegration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class EgnyteClientFactory {

  public static class EgnyteRestApiClientBuilder {
    private String tenantName;
    private String rootUrl;
    private String apiKey;

    public EgnyteRestApiClientBuilder tenantName(String tenantName) {
      this.tenantName = tenantName;
      return this;
    }

    public EgnyteRestApiClientBuilder rootUrl(String rootUrl) {
      this.rootUrl = rootUrl;
      return this;
    }

    public EgnyteRestApiClientBuilder apiKey(String apiKey) {
      this.apiKey = apiKey;
      return this;
    }

    public EgnyteRestApiClient build() {
      Assert.notNull(apiKey, "Egnyte API key is not set.");
      Assert.isTrue(StringUtils.hasText(tenantName) || StringUtils.hasText(rootUrl),
          "Egnyte root URL or tenant name is not set. Eg. egnyte.root-url=https://tenant.egnyte.com");
      EgnyteIntegration integration = new EgnyteIntegration();
      integration.setTenantName(tenantName);
      integration.setRootUrl(rootUrl);
      integration.setApiToken(apiKey);
      return createRestApiClient(integration);
    }

  }


  public static EgnyteRestApiClient createRestApiClient(EgnyteIntegration integration) {
    String url;
    if (StringUtils.hasText(integration.getRootUrl())) {
      url = integration.getRootUrl();
    } else if (StringUtils.hasText(integration.getTenantName())) {
      url = "https://" + integration.getTenantName() + ".egnyte.com";
    } else {
      throw new StudyTrackerException(
          "Egnyte root URL or tenant name is not set. Eg. egnyte.root-url=https://tenant.egnyte.com");
    }
    return new EgnyteRestApiClient(url, integration.getApiToken());
  }

}
