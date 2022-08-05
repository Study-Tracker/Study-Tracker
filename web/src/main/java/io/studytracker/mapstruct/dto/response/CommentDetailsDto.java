package io.studytracker.mapstruct.dto.response;

import java.util.Date;
import lombok.Data;

@Data
public class CommentDetailsDto {
  private Long id;
  private String text;
  private UserSlimDto createdBy;
  private Date createdAt;
  private Date updatedAt;
}
