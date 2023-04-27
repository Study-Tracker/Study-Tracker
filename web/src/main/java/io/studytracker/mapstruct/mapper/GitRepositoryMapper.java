package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.response.GitRepositoryDetailsDto;
import io.studytracker.model.GitRepository;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GitRepositoryMapper {

  GitRepositoryDetailsDto toDetailsDto(GitRepository gitRepository);
  List<GitRepositoryDetailsDto> toDetailsDto(List<GitRepository> gitRepositories);
  Set<GitRepositoryDetailsDto> toDetailsDto(Set<GitRepository> gitRepositories);

}
