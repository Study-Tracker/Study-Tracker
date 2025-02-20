/*
 * Copyright 2019-2025 the original author or authors.
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

package io.studytracker.aws;

import io.studytracker.model.AwsIntegration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

/***
 * Factory methods for creating AWS clients.
 */
public class AWSClientFactory {

  private static AwsCredentialsProvider createCredentialsProvider(AwsIntegration integration) {
    if (StringUtils.hasText(integration.getAccessKeyId())
        && StringUtils.hasText(integration.getSecretAccessKey())) {
      AwsCredentials credentials =
          AwsBasicCredentials.create(integration.getAccessKeyId(), integration.getSecretAccessKey());
      return StaticCredentialsProvider.create(credentials);
    } else {
      return null;
    }
  }

  public static S3Client createS3Client(AwsIntegration integration) throws IllegalArgumentException {
    S3ClientBuilder builder = S3Client.builder().region(Region.of(integration.getRegion()));
    AwsCredentialsProvider credentialsProvider = createCredentialsProvider(integration);
    if (credentialsProvider != null) {
      return builder.credentialsProvider(credentialsProvider).build();
    } else if (integration.isUseIam()) {
      return builder.build();
    } else {
      throw new IllegalArgumentException("Invalid AWS integration configuration: "
          + integration.getId());
    }
  }

}
