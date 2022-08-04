package io.studytracker.mapstruct.dto.response;

import lombok.Data;

@Data
public class FileStoreFolderDetailsDto {

  private Long id;
  private String url;
  private String name;
  private String path;
  private String referenceId;
}
