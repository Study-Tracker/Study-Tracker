package io.studytracker.config.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "benchling")
@Validated
@Getter
@Setter
public class BenchlingProperties {

  private String tenantName;

  @Valid
  private BenchlingApiProperties api;

  @Getter
  @Setter
  public static class BenchlingApiProperties {

    @JsonIgnore
    private String clientId;

    @JsonIgnore
    private String clientSecret;

    @JsonIgnore
    @Deprecated
    private String token;

    @JsonIgnore
    @Deprecated
    private String username;

    @JsonIgnore
    @Deprecated
    private String password;

    @Deprecated
    private String rootUrl;

    @Deprecated
    private String rootEntity;

    @Deprecated
    private String rootFolderUrl;

  }

}
