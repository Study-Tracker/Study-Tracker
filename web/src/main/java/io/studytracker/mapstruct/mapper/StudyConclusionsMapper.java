package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.form.StudyConclusionsFormDto;
import io.studytracker.mapstruct.dto.response.StudyConclusionsDto;
import io.studytracker.model.StudyConclusions;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudyConclusionsMapper {
  StudyConclusions fromDto(StudyConclusionsDto dto);
  StudyConclusions fromFormDto(StudyConclusionsFormDto dto);

  StudyConclusionsDto toDto(StudyConclusions conclusions);
}
