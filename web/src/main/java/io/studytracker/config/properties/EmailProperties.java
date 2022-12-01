package io.studytracker.config.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "email")
@Validated
@Getter
@Setter
public class EmailProperties {

  private String host;

  private String username;

  @JsonIgnore
  private String password;

  private Boolean smtpAuth;

  private Boolean smtpStartTls;

  @Email
  private String outgoingEmailAddress;

  private String protocol;

}
