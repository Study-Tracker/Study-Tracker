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
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "db")
@Validated
@Getter
@Setter
public class DatabaseProperties {

  @NotEmpty
  private String username;

  @NotEmpty
  @JsonIgnore
  private String password;

  @NotEmpty
  private String host;

  @NotEmpty
  private String name;

  @NotNull
  private Integer port;

  @Override
  public String toString() {
    return "DatabaseProperties{" +
        "username='" + username + '\'' +
        ", password='*****'" +
        ", host='" + host + '\'' +
        ", name='" + name + '\'' +
        ", port=" + port +
        '}';
  }
}
