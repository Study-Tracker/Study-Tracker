package com.decibeltx.studytracker.mapstruct.dto;

import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudyConclusionsDto {
  private Long id;
  @NotNull private String content;
  private UserSlimDto createdBy;
  private UserSlimDto lastModifiedBy;
  private Date createdAt;
  private Date updatedAt;
}
