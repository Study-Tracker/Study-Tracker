package io.studytracker.mapstruct.dto.api;

import java.util.Date;
import lombok.Data;

@Data
public class CommentDto {
  private Long id;
  private String text;
  private Long studyId;
  private Long createdBy;
  private Date createdAt;
  private Date updatedAt;
}
