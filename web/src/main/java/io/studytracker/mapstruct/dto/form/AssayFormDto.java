package io.studytracker.mapstruct.dto.form;

import io.studytracker.mapstruct.dto.response.AssayTaskDetailsDto;
import io.studytracker.mapstruct.dto.response.AssayTypeDetailsDto;
import io.studytracker.mapstruct.dto.response.FileStoreFolderDetailsDto;
import io.studytracker.mapstruct.dto.response.NotebookFolderDetailsDto;
import io.studytracker.mapstruct.dto.response.UserSlimDto;
import io.studytracker.model.Status;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssayFormDto {

  private Long id;
  private Status status;
  private AssayTypeDetailsDto assayType;
  @NotBlank private String name;
  private String code;
  @NotBlank private String description;
  private UserSlimDto createdBy;
  private UserSlimDto lastModifiedBy;
  private UserSlimDto owner;
  @NotNull private Date startDate;
  private Date endDate;
  private NotebookFolderDetailsDto notebookFolder;
  private FileStoreFolderDetailsDto storageFolder;
  private boolean active = true;
  private String notebookTemplateId;
  private Date createdAt;
  private Date updatedAt;
  private Set<UserSlimDto> users = new HashSet<>();
  private Map<String, Object> fields = new LinkedHashMap<>();
  private Map<String, String> attributes = new HashMap<>();
  private Set<AssayTaskDetailsDto> tasks = new HashSet<>();
}
