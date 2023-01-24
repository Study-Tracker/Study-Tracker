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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "egnyte")
@Validated
@Getter
@Setter
public class EgnyteProperties {

  private String tenantName;

  private String rootUrl;

  @JsonIgnore
  private String apiToken;

  private String rootPath;

  private Integer qps;

  @Override
  public String toString() {
    return "EgnyteProperties{" +
        "tenantName='" + tenantName + '\'' +
        ", rootUrl='" + rootUrl + '\'' +
        ", apiToken='*****'" +
        ", rootPath='" + rootPath + '\'' +
        ", qps=" + qps +
        '}';
  }
}
