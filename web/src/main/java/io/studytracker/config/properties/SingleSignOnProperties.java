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

package io.studytracker.config.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "sso")
@Validated
@Getter
@Setter
@ToString
public class SingleSignOnProperties {

  @ConfigurationModeConstraint(options = {"okta-saml", "entra-saml"})
  private String mode;

  @Valid
  private final OktaSsoProperties okta = new OktaSsoProperties();

  @Valid
  private final EntraSsoProperties entra = new EntraSsoProperties();

  @Valid
  private final SamlSsoProperties saml = new SamlSsoProperties();


  @Getter
  @Setter
  @ToString
  public static class OktaSsoProperties {

    private String url;

  }

  @Getter
  @Setter
  @ToString
  public static class EntraSsoProperties {

    private String url;

  }

  @Getter
  @Setter
  @ToString
  public static class SamlSsoProperties {

    private String audience;
    private String idp;
    private String metadataUrl;
    private String metadataBaseUrl;
    private Integer maxAuthenticationAge;
    @Valid private SamlKeystoreProperties keystore;

  }

  @Getter
  @Setter
  public static class SamlKeystoreProperties {

    private String location;

    private String alias;

    @JsonIgnore
    private String password;

    @Override
    public String toString() {
      return "SamlKeystoreProperties{" +
          "location='" + location + '\'' +
          ", alias='" + alias + '\'' +
          ", password='*****'" +
          '}';
    }
  }

}
