/*
 * Copyright 2022 the original author or authors.
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

@Configuration
@ConditionalOnProperty(name = "aws.access-key-id", havingValue = "")
public class AmazonWebServicesConfiguration {

  @Autowired private Environment env;

  @Bean
  public AwsCredentialsProvider credentialsProvider() {
    if (env.containsProperty("aws.secret-access-key")
        && env.containsProperty("aws.access-key-id")) {
      Assert.isTrue(
          env.containsProperty("aws.secret-access-key"),
          "Property 'aws.secret-access-key' must be set.");
      AwsCredentials credentials =
          AwsBasicCredentials.create(
              env.getRequiredProperty("aws.access-key-id"),
              env.getRequiredProperty("aws.secret-access-key"));
      return StaticCredentialsProvider.create(credentials);
    } else {
      return null;
    }
  }
}
