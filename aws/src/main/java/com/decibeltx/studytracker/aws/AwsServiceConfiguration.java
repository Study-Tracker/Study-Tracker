package com.decibeltx.studytracker.aws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsServiceConfiguration {

  @Bean
  public ObjectMapper awsObjectMapper() {
    return new ObjectMapper();
  }

}
