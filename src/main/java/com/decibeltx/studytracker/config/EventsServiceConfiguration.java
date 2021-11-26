package com.decibeltx.studytracker.config;

import com.decibeltx.studytracker.aws.EventBridgeService;
import com.decibeltx.studytracker.events.EventsService;
import com.decibeltx.studytracker.events.LocalEventsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;

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

    @Autowired
    private Environment env;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private AwsCredentialsProvider credentialsProvider;

    @Bean
    public EventBridgeClient eventBridgeClient() {
      Region region = Region.of(env.getRequiredProperty("aws.region"));
      return EventBridgeClient.builder()
          .region(region)
          .credentialsProvider(credentialsProvider)
          .build();
    }

    @Bean
    public EventBridgeService eventBridgeService() {
      return new EventBridgeService(
          eventBridgeClient(),
          env.getRequiredProperty("aws.eventbridge.bus-name")
      );
    }

  }

}
