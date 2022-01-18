package com.decibeltx.studytracker.mapstruct.dto;

import com.decibeltx.studytracker.model.Status;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import lombok.Data;

@Data
public class AssayDetailsDto {

  private Long id;
  private Status status;
  private AssayTypeDetailsDto assayType;
  private String name;
  private String code;
  private String description;
  private UserSlimDto createdBy;
  private UserSlimDto lastModifiedBy;
  private UserSlimDto owner;
  private Date startDate;
  private Date endDate;
  private ELNFolderDto notebookFolder;
  private FileStoreFolderDto storageFolder;
  private boolean active = true;
  private Date createdAt;
  private Date updatedAt;
  private Set<UserSlimDto> users = new HashSet<>();
  private Map<String, Object> fields = new LinkedHashMap<>();
  private Map<String, String> attributes = new HashMap<>();
  private Set<AssayTaskDto> tasks = new HashSet<>();

}
