package io.studytracker.mapstruct.dto.response;

import io.studytracker.model.TaskStatus;
import java.util.Date;
import lombok.Data;

@Data
public class AssayTaskDetailsDto {

  private Long id;
  private TaskStatus status;
  private String label;
  private Integer order;
  private Date createdAt;
  private Date updatedAt;
  private UserSlimDto createdBy;
  private UserSlimDto lastModifiedBy;
  private UserSlimDto assignedTo;
  private Date dueDate;
}
