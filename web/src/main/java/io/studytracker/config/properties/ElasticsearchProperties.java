package io.studytracker.config.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "elasticsearch")
@Validated
@Getter
@Setter
public class ElasticsearchProperties {

  private String host;

  private Integer port;

  private Boolean useSsl;

  private String username;

  @JsonIgnore
  private String password;

}
