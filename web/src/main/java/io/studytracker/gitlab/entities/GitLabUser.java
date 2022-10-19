package io.studytracker.gitlab.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabUser {

  private Integer id;
  private String username;
  private String name;
  private String state;
  private String avatarUrl;
  private String webUrl;
  private Date createdAt;
  private Date confirmedAt;
  private Date lastActivityOn;
  private Date currentSignInAt;
  private String organization;
  private String jobTitle;
  private boolean bot;
  private String email;
  private String commitEmail;
  private boolean external;
  private boolean isAdmin;
  private Integer namespaceId;
  private String bio;
  private String location;
  private boolean isAuditor;
  private boolean usingLicenseSeat;
  private String note;

}
