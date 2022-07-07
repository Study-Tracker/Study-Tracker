package io.studytracker.mapstruct.dto.response;

import io.studytracker.model.Status;
import lombok.Data;

@Data
public class StudySlimDto {
  private Long id;
  private String code;
  private String externalCode;
  private Status status;
  private String name;
  private boolean active = true;
}
