package io.studytracker.mapstruct.dto.api;

import io.studytracker.model.Status;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Data;

@Data
public class StudyDto {

  private Long id;
  private String code;
  private String externalCode;
  private Status status;
  private String name;
  private Long programId;
  private String description;
  private Long collaboratorId;
  private boolean legacy = false;
  private boolean active = true;
  private Long notebookFolderId;
  private Long storageFolderId;
  private Long createdBy;
  private Long lastModifiedBy;
  private Date startDate;
  private Date endDate;
  private Date createdAt;
  private Date updatedAt;
  private Long owner;
  private Set<Long> users = new HashSet<>();
  private Set<Long> keywords = new HashSet<>();
  private Map<String, String> attributes = new HashMap<>();
  private Set<Long> externalLinks = new HashSet<>();
  private Set<Long> studyRelationships = new HashSet<>();
  private Long conclusionsId;
  private Set<Long> comments = new HashSet<>();
}
