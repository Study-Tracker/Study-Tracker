package com.decibeltx.studytracker.aws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;

@Configuration
@ConditionalOnProperty(name = "events.mode", havingValue = "eventbridge")
public class EventBridgeConfiguration {

  @Autowired
  private Environment env;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private ObjectMapper awsObjectMapper;

  @Bean
  public EventBridgeClient eventBridgeClient() {
    Region region = Region.of(env.getRequiredProperty("aws.region"));
    return EventBridgeClient.builder()
        .region(region)
        .credentialsProvider(credentialsProvider())
        .build();
  }

  @Bean
  public AwsCredentialsProvider credentialsProvider() {
    AwsCredentials credentials = AwsBasicCredentials.create(
        env.getRequiredProperty("aws.access-key-id"),
        env.getRequiredProperty("aws.secret-access-key")
    );
    return StaticCredentialsProvider.create(credentials);
  }

  @Bean
  public EventBridgeService eventBridgeService() {
    return new EventBridgeService(
        eventBridgeClient(),
        awsObjectMapper,
        env.getRequiredProperty("aws.eventbridge.bus-name")
    );
  }

}
