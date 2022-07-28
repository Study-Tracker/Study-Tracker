package io.studytracker.mapstruct.dto.form;

import io.studytracker.mapstruct.dto.response.ELNFolderDto;
import io.studytracker.mapstruct.dto.response.FileStoreFolderDto;
import io.studytracker.mapstruct.dto.response.UserSummaryDto;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProgramFormDto {

  private Long id;
  private @NotNull(message = "Program code must not be empty") String code;
  private @NotNull(message = "Program name must not be empty") String name;
  private String description;
  private UserSummaryDto createdBy;
  private UserSummaryDto lastModifiedBy;
  private Date createdAt;
  private Date updatedAt;
  private boolean active;
  private ELNFolderDto notebookFolder;
  private FileStoreFolderDto storageFolder;
  private Map<String, String> attributes = new HashMap<>();
}
