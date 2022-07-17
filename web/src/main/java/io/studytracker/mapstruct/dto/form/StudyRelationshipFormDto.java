package io.studytracker.mapstruct.dto.form;

import io.studytracker.model.RelationshipType;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudyRelationshipFormDto {

  private Long id;
  private @NotNull Long targetStudyId;
  private @NotNull RelationshipType type;

}
