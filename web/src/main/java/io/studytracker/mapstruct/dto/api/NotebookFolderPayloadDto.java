package io.studytracker.mapstruct.dto.api;

import lombok.Data;

@Data
public class NotebookFolderPayloadDto {
  private String url;
  private String name;
  private String path;
  private String referenceId;
}
