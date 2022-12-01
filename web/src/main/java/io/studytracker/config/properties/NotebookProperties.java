package io.studytracker.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "notebook")
@Validated
@Getter
@Setter
public class NotebookProperties {

  @ConfigurationModeConstraint(options = {"none", "benchling"})
  private String mode;

}
