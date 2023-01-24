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

import io.studytracker.config.properties.AWSProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

@Configuration
@ConditionalOnProperty(name = "aws.access-key-id", havingValue = "")
public class AmazonWebServicesConfiguration {

  @Autowired private AWSProperties properties;

  @Bean
  public AwsCredentialsProvider credentialsProvider() {
    if (properties.getAccessKeyId() != null && properties.getSecretAccessKey() != null) {
      Assert.isTrue(StringUtils.hasText(properties.getAccessKeyId())
          && StringUtils.hasText(properties.getSecretAccessKey()),
          "AWS access key ID and secret access key must be set");
      AwsCredentials credentials =
          AwsBasicCredentials.create(properties.getAccessKeyId(), properties.getSecretAccessKey());
      return StaticCredentialsProvider.create(credentials);
    } else {
      return null;
    }
  }
}
