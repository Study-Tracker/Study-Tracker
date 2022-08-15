package io.studytracker.mapstruct.dto.api;

import io.studytracker.model.RelationshipType;
import lombok.Data;

@Data
public class StudyRelationshipDto {
  private Long id;
  private RelationshipType type;
  private Long sourceStudyId;
  private Long targetStudyId;
}
