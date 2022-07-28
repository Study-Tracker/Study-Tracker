package io.studytracker.mapstruct.dto.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class ProgramDto {

  private Long id;
  private String code;
  private String name;
  private String description;
  private Long createdBy;
  private Long lastModifiedBy;
  private Date createdAt;
  private Date updatedAt;
  private boolean active;
  private Long notebookFolderId;
  private Long storageFolderId;
  private Map<String, String> attributes = new HashMap<>();
}
