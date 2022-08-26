package io.studytracker.mapstruct.dto.api;

import io.studytracker.model.Status;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import lombok.Data;

@Data
public class AssayDto {

  private Long id;
  private Status status;
  private Long assayTypeId;
  private Long studyId;
  private String name;
  private String code;
  private String description;
  private Long createdBy;
  private Long lastModifiedBy;
  private Long owner;
  private Date startDate;
  private Date endDate;
  private Long notebookFolderId;
  private Long storageFolderId;
  private boolean active = true;
  private Date createdAt;
  private Date updatedAt;
  private Set<Long> users = new HashSet<>();
  private Map<String, Object> fields = new LinkedHashMap<>();
  private Map<String, String> attributes = new HashMap<>();
  private Set<Long> tasks = new HashSet<>();
}
