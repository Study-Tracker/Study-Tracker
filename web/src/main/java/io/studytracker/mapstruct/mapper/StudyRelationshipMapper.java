package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.api.StudyRelationshipDto;
import io.studytracker.mapstruct.dto.api.StudyRelationshipPayloadDto;
import io.studytracker.mapstruct.dto.form.StudyRelationshipFormDto;
import io.studytracker.mapstruct.dto.response.StudyRelationshipDetailsDto;
import io.studytracker.mapstruct.dto.response.StudyRelationshipSlimDto;
import io.studytracker.model.StudyRelationship;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudyRelationshipMapper {

  StudyRelationship fromDetails(StudyRelationshipDetailsDto dto);

  StudyRelationship fromFormDto(StudyRelationshipFormDto dto);

  List<StudyRelationship> fromDetailsList(List<StudyRelationshipDetailsDto> dtos);

  Set<StudyRelationship> fromDetailsSet(Set<StudyRelationshipDetailsDto> dtos);

  StudyRelationshipDetailsDto toDetails(StudyRelationship relationship);

  List<StudyRelationshipDetailsDto> toDetailsList(List<StudyRelationship> relationships);

  Set<StudyRelationship> toDetailsSet(Set<StudyRelationship> relationships);

  @Mapping(source = "sourceStudy.id", target = "sourceStudyId")
  @Mapping(source = "targetStudy.id", target = "targetStudyId")
  StudyRelationshipSlimDto toSlim(StudyRelationship relationship);

  List<StudyRelationshipSlimDto> toSlimList(List<StudyRelationship> relationships);

  Set<StudyRelationshipSlimDto> toSlimSet(Set<StudyRelationship> relationships);

  @Mapping(source = "sourceStudy.id", target = "sourceStudyId")
  @Mapping(source = "targetStudy.id", target = "targetStudyId")
  StudyRelationshipDto toDto(StudyRelationship relationship);
  List<StudyRelationshipDto> toDtoList(List<StudyRelationship> relationships);
  StudyRelationship fromPayload(StudyRelationshipPayloadDto dto);
}
