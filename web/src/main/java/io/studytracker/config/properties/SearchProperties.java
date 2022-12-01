package io.studytracker.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "search")
@Validated
@Getter
@Setter
public class SearchProperties {

  @ConfigurationModeConstraint(options = {"elasticsearch"})
  private String mode;

}
