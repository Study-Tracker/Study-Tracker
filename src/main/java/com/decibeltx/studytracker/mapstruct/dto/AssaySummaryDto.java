package com.decibeltx.studytracker.mapstruct.dto;

import com.decibeltx.studytracker.model.Status;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssaySummaryDto {

  private Long id;
  private Status status;
  private AssayTypeSlimDto assayType;
  @NotNull private String name;
  private String code;
  @NotNull private String description;
  private Date createdAt;
  private Date updateAt;
  private UserSlimDto owner;
  private ELNFolderDto notebookFolder;
  private FileStoreFolderDto storageFolder;
  private boolean active = true;
  private Set<UserSlimDto> users = new HashSet<>();

}
