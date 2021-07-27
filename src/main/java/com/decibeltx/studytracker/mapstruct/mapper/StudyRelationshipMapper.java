package com.decibeltx.studytracker.mapstruct.mapper;

import com.decibeltx.studytracker.mapstruct.dto.StudyRelationshipDetailsDto;
import com.decibeltx.studytracker.mapstruct.dto.StudyRelationshipSlimDto;
import com.decibeltx.studytracker.model.StudyRelationship;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudyRelationshipMapper {

  StudyRelationship fromDetails(StudyRelationshipDetailsDto dto);
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

}
