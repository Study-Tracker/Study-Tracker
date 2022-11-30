package io.studytracker.config.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "db")
@Validated
@Getter
@Setter
public class DatabaseProperties {

  @NotEmpty
  private String username;

  @NotEmpty
  @JsonIgnore
  private String password;

  @NotEmpty
  private String host;

  @NotEmpty
  private String name;

  @NotNull
  private Integer port;

}
