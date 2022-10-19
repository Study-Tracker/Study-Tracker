package io.studytracker.gitlab;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.net.URL;
import lombok.Data;

@Data
public class GitLabOptions {

  private URL rootUrl;

  private String clientId;
  @JsonIgnore
  private String clientSecret;

  private String username;

  @JsonIgnore
  private String password;

  @JsonIgnore
  private String accessToken;

  private Integer rootGroupId;

}
