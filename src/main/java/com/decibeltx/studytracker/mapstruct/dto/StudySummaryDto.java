package com.decibeltx.studytracker.mapstruct.dto;

import com.decibeltx.studytracker.model.Status;
import java.util.Date;
import lombok.Data;

@Data
public class StudySummaryDto {

  private Long id;
  private String code;
  private String externalCode;
  private Status status;
  private String name;
  private ProgramSummaryDto program;
  private String description;
  private CollaboratorDto collaborator;
  private boolean legacy = false;
  private boolean active = true;
  private ELNFolderDto notebookFolder;
  private FileStoreFolderDto storageFolder;
//  private UserSummaryDto createdBy;
//  private UserSummaryDto lastModifiedBy;
  private Date startDate;
  private Date endDate;
  private Date createdAt;
  private Date updatedAt;
  private UserSummaryDto owner;
//  private Set<UserSummaryDto> users = new HashSet<>();
//  private Set<KeywordDto> keywords = new HashSet<>();

}
