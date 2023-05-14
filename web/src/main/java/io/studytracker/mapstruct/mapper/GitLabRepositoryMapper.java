package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.response.GitLabRepositoryDetailsDto;
import io.studytracker.model.GitLabRepository;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GitLabRepositoryMapper {

  GitLabRepositoryDetailsDto toDetailsDto(GitLabRepository gitLabRepository);
  List<GitLabRepositoryDetailsDto> toDetailsDto(List<GitLabRepository> gitLabRepositories);
  Set<GitLabRepositoryDetailsDto> toDetailsDto(Set<GitLabRepository> gitLabRepositories);

}
