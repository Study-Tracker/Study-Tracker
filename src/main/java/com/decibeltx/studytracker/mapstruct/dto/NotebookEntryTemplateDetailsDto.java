package com.decibeltx.studytracker.mapstruct.dto;

import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotebookEntryTemplateDetailsDto {

  private Long id;
  private @NotNull(message = "Template name must not be empty") String name;
  private @NotNull(message = "Template id must not be empty") String templateId;
  private UserSlimDto createdBy;
  private UserSlimDto lastModifiedBy;
  private Date createdAt;
  private Date updatedAt;
  private boolean active = true;

}
