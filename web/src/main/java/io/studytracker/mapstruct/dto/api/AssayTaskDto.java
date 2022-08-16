package io.studytracker.mapstruct.dto.api;

import io.studytracker.model.TaskStatus;
import java.util.Date;
import lombok.Data;

@Data
public class AssayTaskDto {

  private Long id;
  private Long assayId;
  private TaskStatus status;
  private String label;
  private Integer order;
  private Date createdAt;
  private Date updatedAt;
  private Long createdBy;
  private Long lastModifiedBy;
  private Long assignedTo;
  private Date dueDate;
}
