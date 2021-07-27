package com.decibeltx.studytracker.mapstruct.dto;

import com.decibeltx.studytracker.model.Status;
import lombok.Data;

@Data
public class AssayParentDto {

  private Long id;
  private Status status;
  private AssayTypeSlimDto assayType;
  private String name;
  private String code;
  private String description;
  private UserSlimDto owner;
  private ELNFolderDto notebookFolder;
  private FileStoreFolderDto storageFolder;
  private boolean active = true;
  private StudySummaryDto study;

}
