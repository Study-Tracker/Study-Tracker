package io.studytracker.mapstruct.dto;

import io.studytracker.model.TaskStatus;
import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssayTypeTaskDto {

  private Long id;
  @NotNull private TaskStatus status;
  @NotNull private String label;
  private Integer order;
  private Date createdAt;
  private Date updatedAt;
}
