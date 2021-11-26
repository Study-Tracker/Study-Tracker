package com.decibeltx.studytracker.config;

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

  @Autowired
  private Environment env;

  @Bean
  public AwsCredentialsProvider credentialsProvider() {
    Assert.isTrue(env.containsProperty("aws.secret-access-key"),
        "Property 'aws.secret-access-key' must be set.");
    AwsCredentials credentials = AwsBasicCredentials.create(
        env.getRequiredProperty("aws.access-key-id"),
        env.getRequiredProperty("aws.secret-access-key")
    );
    return StaticCredentialsProvider.create(credentials);
  }

}
