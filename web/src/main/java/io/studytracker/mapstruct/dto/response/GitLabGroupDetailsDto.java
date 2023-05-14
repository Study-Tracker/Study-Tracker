package io.studytracker.mapstruct.dto.response;

import lombok.Data;

@Data
public class GitLabGroupDetailsDto {

  private Long id;
  private GitLabIntegrationDetailsDto gitLabIntegration;
  private GitGroupDetailsDto gitGroup;
  private Integer groupId;
  private String name;
  private String path;

}
