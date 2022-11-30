package io.studytracker.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "aws.eventbridge")
@Validated
@Getter
@Setter
public class EventBridgeProperties {

  private String busName;

}
