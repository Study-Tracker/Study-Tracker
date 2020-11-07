package com.decibeltx.studytracker.core.config;

import com.decibeltx.studytracker.core.events.LocalEventsService;
import com.decibeltx.studytracker.core.service.EventsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "events.mode", havingValue = "local")
public class LocalEventsConfiguration {

  @Bean
  public EventsService localEventsService() {
    return new LocalEventsService();
  }

}
