package io.studytracker.mapstruct.dto.response;

import io.studytracker.model.NotebookEntryTemplate;
import lombok.Data;

@Data
public class NotebookEntryTemplateSlimDto {

  private Long id;
  private String name;
  private String templateId;
  private boolean active;
  private NotebookEntryTemplate.Category category;
  private boolean isDefault;
}
