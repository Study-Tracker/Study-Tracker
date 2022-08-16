package io.studytracker.mapstruct.dto.api;

import io.studytracker.model.TaskStatus;
import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssayTaskPayloadDto {
  private Long id;
  @NotNull private Long assayId;
  @NotNull private TaskStatus status;
  @NotNull private String label;
  private Integer order;
  private Long assignedTo;
  private Date dueDate;
}
