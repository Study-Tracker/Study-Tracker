package io.studytracker.config.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "application")
@Validated
@Getter
@Setter
public class ApplicationProperties {

  @NotEmpty
  private String hostName;

  @NotEmpty
  @Length(min = 16, max = 512)
  @JsonIgnore
  private String secret;

}
