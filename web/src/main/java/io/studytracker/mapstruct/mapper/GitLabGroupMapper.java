package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.response.GitLabGroupDetailsDto;
import io.studytracker.model.GitLabGroup;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GitLabGroupMapper {

  GitLabGroupDetailsDto toDetailsDto(GitLabGroup gitLabGroup);
  List<GitLabGroupDetailsDto> toDetailsDto(List<GitLabGroup> gitLabGroups);
  Set<GitLabGroupDetailsDto> toDetailsDto(Set<GitLabGroup> gitLabGroups);

}
