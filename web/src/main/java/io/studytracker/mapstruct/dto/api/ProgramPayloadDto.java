package io.studytracker.mapstruct.dto.api;

import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProgramPayloadDto {

  private Long id;
  private @NotNull(message = "Program code must not be empty") String code;
  private @NotNull(message = "Program name must not be empty") String name;
  private String description;
  private boolean active;
  private Long notebookFolderId;
  private Long storageFolderId;
  private Map<String, String> attributes = new HashMap<>();
}
