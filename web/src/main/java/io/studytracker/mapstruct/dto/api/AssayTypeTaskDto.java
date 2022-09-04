package io.studytracker.mapstruct.dto.api;

import io.studytracker.model.TaskStatus;
import java.util.Date;
import lombok.Data;

@Data
public class AssayTypeTaskDto {
  private Long id;
  private TaskStatus status;
  private String label;
  private Integer order;
  private Date createdAt;
  private Date updatedAt;
}
