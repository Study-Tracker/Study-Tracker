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

@ConfigurationProperties(prefix = "opensearch")
@Validated
@Getter
@Setter
public class OpensearchProperties {

  private String host;

  private Integer port;

  private Boolean useSsl;

  private String username;
  
  private Integer connectTimeout = 60000; // 60 seconds
  
  private Integer socketTimeout = 50000; // 50 seconds

  @JsonIgnore
  private String password;

  @Override
  public String toString() {
    return "OpensearchProperties{" +
        "host='" + host + '\'' +
        ", port=" + port +
        ", useSsl=" + useSsl +
        ", username='" + username + '\'' +
        ", connectTimeout=" + connectTimeout +
        ", socketTimeout=" + socketTimeout +
        ", password='*****'" +
        '}';
  }
}
