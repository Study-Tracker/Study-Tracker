package com.decibeltx.studytracker.mapstruct.mapper;

import com.decibeltx.studytracker.mapstruct.dto.StudyConclusionsDto;
import com.decibeltx.studytracker.model.StudyConclusions;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudyConclusionsMapper {
  StudyConclusions fromDto(StudyConclusionsDto dto);
  StudyConclusionsDto toDto(StudyConclusions conclusions);
}
