package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.api.StudyConclusionsDto;
import io.studytracker.mapstruct.dto.api.StudyConclusionsPayloadDto;
import io.studytracker.mapstruct.dto.form.StudyConclusionsFormDto;
import io.studytracker.mapstruct.dto.response.StudyConclusionsDetailsDto;
import io.studytracker.model.StudyConclusions;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudyConclusionsMapper {
  StudyConclusions fromDetailsDto(StudyConclusionsDetailsDto dto);
  StudyConclusions fromFormDto(StudyConclusionsFormDto dto);

  StudyConclusionsDetailsDto toDetailsDto(StudyConclusions conclusions);

  @Mapping(target = "createdBy", source = "createdBy.id")
  @Mapping(target = "lastModifiedBy", source = "lastModifiedBy.id")
  @Mapping(target = "studyId", source = "study.id")
  StudyConclusionsDto toDto(StudyConclusions conclusions);

  List<StudyConclusionsDto> toDtoList(List<StudyConclusions> conclusions);
  StudyConclusions fromPayload(StudyConclusionsPayloadDto dto);
}
