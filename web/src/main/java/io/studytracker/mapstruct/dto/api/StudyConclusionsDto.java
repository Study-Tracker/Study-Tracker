package io.studytracker.mapstruct.dto.api;

import java.util.Date;
import lombok.Data;

@Data
public class StudyConclusionsDto {
  private Long id;
  private Long studyId;
  private String content;
  private Long createdBy;
  private Long lastModifiedBy;
  private Date createdAt;
  private Date updatedAt;
}
