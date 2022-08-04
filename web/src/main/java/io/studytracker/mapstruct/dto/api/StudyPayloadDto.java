package io.studytracker.mapstruct.dto.api;

import io.studytracker.model.Status;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudyPayloadDto {
  private Long id;
  private String code;
  private String externalCode;
  private @NotNull Status status;
  private @NotBlank String name;
  private Long programId;
  private @NotBlank String description;
  private String notebookTemplateId;
  private Long collaboratorId;
  private boolean legacy = false;
  private boolean active = true;
  private boolean external = false;
  private NotebookFolderPayloadDto notebookFolder;
  private FileStoreFolderPayloadDto storageFolder;
  private @NotNull Date startDate;
  private Date endDate;
  private Long owner;
  private Set<Long> users = new HashSet<>();
  private Set<Long> keywords = new HashSet<>();
  private Map<String, String> attributes = new HashMap<>();
}
