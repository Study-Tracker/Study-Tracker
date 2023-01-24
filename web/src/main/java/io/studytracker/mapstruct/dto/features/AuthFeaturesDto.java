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

package io.studytracker.mapstruct.dto.features;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthFeaturesDto {

  private SingleSignOnOptionsDto sso = new SingleSignOnOptionsDto();

  @JsonProperty("logoutUrl")
  public String getLogoutUrl() {
    if (sso.isEnabled()) {
      return "/saml/logout";
    } else {
      return "/logout";
    }
  }

  @Data
  public static class SingleSignOnOptionsDto {

    private String mode;
    private String ssoUrl;

    @JsonProperty("isEnabled")
    public boolean isEnabled() {
      return mode.equals("okta-saml");
    }

  }

}


