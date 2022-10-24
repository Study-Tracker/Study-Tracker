package io.studytracker.git;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitUser {

  private String userId;
  private String username;
  private String name;
  private String email;

}
