package io.studytracker.mapstruct.dto.response;

import io.studytracker.model.RelationshipType;
import lombok.Data;

@Data
public class StudyRelationshipPostDto {

  private Long sourceStudyId;
  private RelationshipType type;
  private Long targetStudyId;
}
