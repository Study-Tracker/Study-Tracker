package io.studytracker.mapstruct.dto.form;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.studytracker.model.NotebookEntryTemplate;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotebookEntryTemplateFormDto {

  private Long id;
  private @NotNull(message = "Template name must not be empty") String name;
  private @NotNull(message = "Template id must not be empty") String templateId;
  //  private UserSlimDto createdBy;
  //  private UserSlimDto lastModifiedBy;
  //  private Date createdAt;
  //  private Date updatedAt;
  private boolean active = true;
  private NotebookEntryTemplate.Category category;
  private boolean isDefault = false;
}
