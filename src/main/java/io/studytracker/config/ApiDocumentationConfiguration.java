package io.studytracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiDocumentationConfiguration {

  @Bean
  public OpenAPI publicApi() {
    return new OpenAPI()
        .info(new Info().title("Study Tracker API").description("Study Tracker public REST API"));
  }
}
