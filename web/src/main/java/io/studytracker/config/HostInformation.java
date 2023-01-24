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

package io.studytracker.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ToString
public class HostInformation {

  private static final Logger LOGGER = LoggerFactory.getLogger(HostInformation.class);

  @Value("${application.host-name:localhost}")
  private String hostName;

  @Value("${server.port}")
  private Integer port;

  @Value("${application.name}")
  private String applicationName;

  @Value("${application.java-version}")
  private String javaVersion;

  @Value("${application.version}")
  private String applicationVersion;

  @Value("${application.build-time}")
  private String buildTime;

  private String getProtocol() {
    return port.equals(443) || port.equals(8443) ? "https" : "http";
  }

  @JsonProperty("url")
  public String getApplicationUrl() {
    return String.format("%s://%s:%d", getProtocol(), hostName, port);
  }

  @PostConstruct
  public void init() {
    LOGGER.info("Host Information: " + this.toString());
  }

}
