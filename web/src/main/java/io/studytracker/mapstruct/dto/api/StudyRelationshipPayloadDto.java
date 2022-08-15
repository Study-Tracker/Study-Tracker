package io.studytracker.mapstruct.dto.api;

import io.studytracker.model.RelationshipType;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudyRelationshipPayloadDto {
  private Long id;
  private @NotNull RelationshipType type;
  private @NotNull Long sourceStudyId;
  private @NotNull Long targetStudyId;
}
