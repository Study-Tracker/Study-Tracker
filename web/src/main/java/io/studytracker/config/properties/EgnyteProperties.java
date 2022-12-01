package io.studytracker.config.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "egnyte")
@Validated
@Getter
@Setter
public class EgnyteProperties {

  private String tenantName;

  private String rootUrl;

  @JsonIgnore
  private String apiToken;

  private String rootPath;

  private Integer qps;

}
