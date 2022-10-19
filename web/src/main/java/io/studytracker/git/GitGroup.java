package io.studytracker.git;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitGroup {

  private String groupId;
  private String parentGroupId;
  private String name;
  private String path;
  private String description;
  private Date createdAt;
  private Date updatedAt;
  private String webUrl;

}
