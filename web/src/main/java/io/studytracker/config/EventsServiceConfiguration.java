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

import io.studytracker.aws.EventBridgeService;
import io.studytracker.events.EventsService;
import io.studytracker.events.LocalEventsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.EventBridgeClientBuilder;

@Configuration
public class EventsServiceConfiguration {

  @Configuration
  @ConditionalOnProperty(name = "events.mode", havingValue = "local", matchIfMissing = true)
  public static class LocalEventsConfiguration {

    @Bean
    public EventsService localEventsService() {
      return new LocalEventsService();
    }
  }

  @Configuration
  @ConditionalOnProperty(name = "events.mode", havingValue = "eventbridge")
  public static class EventBridgeConfiguration {

    @Autowired private Environment env;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired(required = false)
    private AwsCredentialsProvider credentialsProvider;

    @Bean
    public EventBridgeClient eventBridgeClient() {
      Region region = Region.of(env.getRequiredProperty("aws.region"));
      EventBridgeClientBuilder builder = EventBridgeClient.builder().region(region);
      if (credentialsProvider != null) {
        builder.credentialsProvider(credentialsProvider);
      }
      return builder.build();
    }

    @Bean
    public EventBridgeService eventBridgeService() {
      return new EventBridgeService(
          eventBridgeClient(), env.getRequiredProperty("aws.eventbridge.bus-name"));
    }
  }
}
