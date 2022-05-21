package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.StudyConclusionsDto;
import io.studytracker.model.StudyConclusions;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudyConclusionsMapper {
  StudyConclusions fromDto(StudyConclusionsDto dto);
  StudyConclusionsDto toDto(StudyConclusions conclusions);
}
