package io.studytracker.config.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "admin")
@Validated
@Getter
@Setter
public class AdminProperties {

  @NotEmpty
  @Email
  private String email;

  @JsonIgnore
  private String password;

}
