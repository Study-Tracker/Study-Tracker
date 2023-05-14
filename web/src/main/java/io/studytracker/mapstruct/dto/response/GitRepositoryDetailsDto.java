package io.studytracker.mapstruct.dto.response;

import java.util.Date;
import lombok.Data;

@Data
public class GitRepositoryDetailsDto {

  private Long id;
  private GitGroupDetailsDto gitGroup;
  private String displayName;
  private String description;
  private String webUrl;
  private String sshUrl;
  private String httpUrl;
  private Date createdAt;
  private Date updatedAt;

}
