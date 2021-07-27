package com.decibeltx.studytracker.mapstruct.dto;

import com.decibeltx.studytracker.model.RelationshipType;
import lombok.Data;

@Data
public class StudyRelationshipPostDto {

  private Long sourceStudyId;
  private RelationshipType type;
  private Long targetStudyId;

}
