package io.studytracker.mapstruct.dto.response;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class ProgramDetailsDto {

  private Long id;
  private String code;
  private String name;
  private String description;
  private UserSummaryDto createdBy;
  private UserSummaryDto lastModifiedBy;
  private Date createdAt;
  private Date updatedAt;
  private boolean active;
  private NotebookFolderDetailsDto notebookFolder;
  private FileStoreFolderDetailsDto storageFolder;
  private Map<String, String> attributes = new HashMap<>();
}
