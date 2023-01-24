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
import javax.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "aws")
@Validated
@Getter
@Setter
public class AWSProperties {

  private String region;

  @JsonIgnore
  private String accessKeyId;

  @JsonIgnore
  private String secretAccessKey;

  @Valid
  private final EventBridgeProperties eventbridge = new EventBridgeProperties();

  @Valid
  private final S3Properties s3 = new S3Properties();

  @Override
  public String toString() {
    return "AWSProperties{" +
        "region='" + region + '\'' +
        ", accessKeyId='" + accessKeyId.substring(0, 5) + "*****'" +
        ", secretAccessKey='*****'" +
        ", eventbridge=" + eventbridge +
        ", s3=" + s3 +
        '}';
  }

  @Getter
  @Setter
  @ToString
  public static class EventBridgeProperties {

    private String busName;

  }

  @Getter
  @Setter
  @ToString
  public static class S3Properties {

    private String defaultStudyLocation;

    private String buckets;

  }

}
