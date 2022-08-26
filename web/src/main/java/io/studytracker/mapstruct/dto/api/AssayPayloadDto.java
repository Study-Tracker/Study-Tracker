package io.studytracker.mapstruct.dto.api;

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
public class AssayPayloadDto {

  private Long id;
  private Status status;
  @NotNull private Long assayTypeId;
  @NotNull private Long studyId;
  @NotBlank private String name;
  private String code;
  @NotBlank private String description;
  private Long owner;
  @NotNull private Date startDate;
  private Date endDate;
  private NotebookFolderPayloadDto notebookFolder;
  private FileStoreFolderPayloadDto storageFolder;
  private boolean active = true;
  private String notebookTemplateId;
  private Set<Long> users = new HashSet<>();
  private Map<String, Object> fields = new LinkedHashMap<>();
  private Map<String, String> attributes = new HashMap<>();
}
