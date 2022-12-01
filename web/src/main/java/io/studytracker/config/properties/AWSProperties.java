package io.studytracker.config.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "aws")
@Validated
@Getter
@Setter
public class AWSProperties {

  private String region;

  @JsonIgnore
  private String accessKeyId;

  @JsonIgnore
  private String secretAccessKey;

  @Valid
  private EventBridgeProperties eventbridge;


  @Getter
  @Setter
  public static class EventBridgeProperties {

    private String busName;

  }

}
