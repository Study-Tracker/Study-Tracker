package io.studytracker.config.properties;

import javax.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "")
@Validated
@Getter
@Setter
public class StudyTrackerProperties {

  @Valid
  private ApplicationProperties application;

  @Valid
  private AdminProperties admin;

  @Valid
  private DatabaseProperties db;

  @Valid
  private EventsProperties events;

  @Valid
  private AWSProperties aws;

}
