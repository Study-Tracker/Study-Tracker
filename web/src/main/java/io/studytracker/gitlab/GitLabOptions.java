package io.studytracker.gitlab;

import java.net.URL;
import lombok.Data;

@Data
public class GitLabOptions {

  private URL rootUrl;
  private String clientId;
  private String clientSecret;
  private String username;
  private String password;

}
