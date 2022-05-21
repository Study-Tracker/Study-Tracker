package io.studytracker.mapstruct.dto;

import io.studytracker.model.RelationshipType;
import lombok.Data;

@Data
public class StudyRelationshipPostDto {

  private Long sourceStudyId;
  private RelationshipType type;
  private Long targetStudyId;

}
