package io.studytracker.mapstruct.dto.response;

import io.studytracker.model.GitServiceType;
import java.util.Date;
import lombok.Data;

@Data
public class GitGroupDetailsDto {

  private Long id;
  private OrganizationDetailsDto organization;
  private GitGroupDetailsDto parentGroup;
  private String displayName;
  private String webUrl;
  private boolean active;
  private GitServiceType gitServiceType;
  private Date createdAt;
  private Date updatedAt;

}
