package io.studytracker.git;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitRepository {

  private String repositoryId;
  private String groupId;
  private String ownerId;
  private String name;
  private String description;
  private String path;
  private Date createdAt;
  private Date updatedAt;
  private String defaultBranch;
  private String sshUrl;
  private String httpUrl;
  private String webUrl;

}
