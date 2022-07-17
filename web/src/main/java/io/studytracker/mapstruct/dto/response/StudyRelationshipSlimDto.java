package io.studytracker.mapstruct.dto.response;

import io.studytracker.model.RelationshipType;
import lombok.Data;

@Data
public class StudyRelationshipSlimDto {

  private Long id;
  private RelationshipType type;
  private Long sourceStudyId;
  private Long targetStudyId;
}
