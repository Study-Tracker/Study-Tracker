package com.decibeltx.studytracker.mapstruct.dto;

import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentDto {
  private Long id;
  @NotNull private String text;
  private UserSlimDto createdBy;
  private Date createdAt;
  private Date updatedAt;
}
