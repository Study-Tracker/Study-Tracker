package com.decibeltx.studytracker.mapstruct.dto;

import com.decibeltx.studytracker.model.Status;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudyFormDto {

  private Long id;
  private String code;
  private String externalCode;
  private @NotNull Status status;
  private @NotNull String name;
  private ProgramSummaryDto program;
  private @NotNull String description;
  private String notebookTemplateId;
  private CollaboratorDto collaborator;
  private boolean legacy = false;
  private boolean active = true;
  private ELNFolderDto notebookFolder;
  private FileStoreFolderDto storageFolder;
  private UserSummaryDto createdBy;
  private UserSummaryDto lastModifiedBy;
  private @NotNull Date startDate;
  private Date endDate;
  private Date createdAt;
  private Date updatedAt;
  private UserSummaryDto owner;
  private Set<UserSummaryDto> users = new HashSet<>();
  private Set<KeywordDto> keywords = new HashSet<>();
  private Map<String, String> attributes = new HashMap<>();
  private Set<ExternalLinkDto> externalLinks = new HashSet<>();
  private Set<StudyRelationshipDetailsDto> studyRelationships = new HashSet<>();
  private StudyConclusionsDto conclusions;
  private Set<CommentDto> comments = new HashSet<>();

}
