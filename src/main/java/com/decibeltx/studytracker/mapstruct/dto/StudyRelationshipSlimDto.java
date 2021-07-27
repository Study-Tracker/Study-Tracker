package com.decibeltx.studytracker.mapstruct.dto;

import com.decibeltx.studytracker.model.RelationshipType;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudyRelationshipSlimDto {

  private Long id;
  private @NotNull  RelationshipType type;
  private Long sourceStudyId;
  private @NotNull Long targetStudyId;

}
