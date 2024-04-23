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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@ConfigurationProperties(prefix = "benchling")
@Validated
@Getter
@Setter
@ToString
@Deprecated
public class BenchlingProperties {

  @Deprecated
  private String tenantName;

  @Valid
  @Deprecated
  private final BenchlingApiProperties api = new BenchlingApiProperties();

  @Getter
  @Setter
  public static class BenchlingApiProperties {

    @JsonIgnore
    @Deprecated
    private String clientId;

    @JsonIgnore
    @Deprecated
    private String clientSecret;

    @JsonIgnore
    @Deprecated
    private String token;

    @JsonIgnore
    @Deprecated
    private String username;

    @JsonIgnore
    @Deprecated
    private String password;

    @Deprecated
    private String rootUrl;

    @Deprecated
    private String rootEntity;

    @Deprecated
    private String rootFolderUrl;

    @Override
    public String toString() {
      return "BenchlingApiProperties{" +
          "clientId='" + clientId + '\'' +
          ", clientSecret='*****'" +
          ", token='*****'" +
          ", username='" + username + '\'' +
          ", password='*****'" +
          ", rootUrl='" + rootUrl + '\'' +
          ", rootEntity='" + rootEntity + '\'' +
          ", rootFolderUrl='" + rootFolderUrl + '\'' +
          '}';
    }
  }

}
