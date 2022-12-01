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
  private ServerProperties server;

  @Valid
  private AdminProperties admin;

  @Valid
  private DatabaseProperties db;

  @Valid
  private EventsProperties events;

  @Valid
  private AWSProperties aws;

  @Valid
  private EmailProperties email;

  @Valid
  private NotebookProperties notebook;


  @Getter
  @Setter
  public static class ServerProperties {
    private Integer port;
  }

}
