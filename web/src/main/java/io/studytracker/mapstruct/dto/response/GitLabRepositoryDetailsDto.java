package io.studytracker.mapstruct.dto.response;

import lombok.Data;

@Data
public class GitLabRepositoryDetailsDto {

  private Long id;
  private GitLabGroupDetailsDto gitLabGroup;
  private GitRepositoryDetailsDto gitRepository;
  private Integer repositoryId;
  private String name;
  private String path;

}
