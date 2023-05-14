package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.form.GitLabIntegrationFormDto;
import io.studytracker.mapstruct.dto.response.GitLabIntegrationDetailsDto;
import io.studytracker.model.GitLabIntegration;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GitLabIntegrationMapper {

  GitLabIntegration fromFormDto(GitLabIntegrationFormDto dto);

  GitLabIntegrationDetailsDto toDetailsDto(GitLabIntegration entity);
  List<GitLabIntegrationDetailsDto> toDetailsDto(List<GitLabIntegration> entities);
  Set<GitLabIntegrationDetailsDto> toDetailsDto(Set<GitLabIntegration> entities);

}
