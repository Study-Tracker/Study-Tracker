package io.studytracker.mapstruct.dto.response;

import io.studytracker.model.Status;
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
  private CollaboratorDetailsDto collaborator;
  private boolean legacy = false;
  private boolean active = true;
  private NotebookFolderDetailsDto notebookFolder;
  private FileStoreFolderDetailsDto storageFolder;
  private Date startDate;
  private Date endDate;
  private Date createdAt;
  private Date updatedAt;
  private UserSummaryDto owner;

}
