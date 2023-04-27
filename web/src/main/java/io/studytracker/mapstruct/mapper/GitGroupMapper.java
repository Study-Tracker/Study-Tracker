package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.response.GitGroupDetailsDto;
import io.studytracker.model.GitGroup;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GitGroupMapper {

  GitGroupDetailsDto toDetailsDto(GitGroup gitGroup);
  List<GitGroupDetailsDto> toDetailsDto(List<GitGroup> gitGroups);
  Set<GitGroupDetailsDto> toDetailsDto(Set<GitGroup> gitGroups);

}
