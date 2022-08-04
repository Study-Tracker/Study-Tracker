package io.studytracker.mapstruct.dto.api;

import lombok.Data;

@Data
public class FileStoreFolderDto {

  private Long id;
  private String url;
  private String name;
  private String path;
  private String referenceId;
}
