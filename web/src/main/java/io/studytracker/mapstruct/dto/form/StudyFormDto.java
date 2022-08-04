package io.studytracker.mapstruct.dto.form;

import io.studytracker.mapstruct.dto.response.CollaboratorDto;
import io.studytracker.mapstruct.dto.response.CommentDto;
import io.studytracker.mapstruct.dto.response.ExternalLinkDto;
import io.studytracker.mapstruct.dto.response.FileStoreFolderDetailsDto;
import io.studytracker.mapstruct.dto.response.KeywordDetailsDto;
import io.studytracker.mapstruct.dto.response.NotebookFolderDetailsDto;
import io.studytracker.mapstruct.dto.response.ProgramSummaryDto;
import io.studytracker.mapstruct.dto.response.StudyConclusionsDto;
import io.studytracker.mapstruct.dto.response.StudyRelationshipDetailsDto;
import io.studytracker.mapstruct.dto.response.UserSummaryDto;
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
public class StudyFormDto {

  private Long id;
  private String code;
  private String externalCode;
  private @NotNull Status status;
  private @NotBlank String name;
  private ProgramSummaryDto program;
  private @NotBlank String description;
  private String notebookTemplateId;
  private CollaboratorDto collaborator;
  private boolean legacy = false;
  private boolean active = true;
  private boolean external = false;
  private NotebookFolderDetailsDto notebookFolder;
  private FileStoreFolderDetailsDto storageFolder;
  private UserSummaryDto createdBy;
  private UserSummaryDto lastModifiedBy;
  private @NotNull Date startDate;
  private Date endDate;
  private Date createdAt;
  private Date updatedAt;
  private UserSummaryDto owner;
  private Set<UserSummaryDto> users = new HashSet<>();
  private Set<KeywordDetailsDto> keywords = new HashSet<>();
  private Map<String, String> attributes = new HashMap<>();
  private Set<ExternalLinkDto> externalLinks = new HashSet<>();
  private Set<StudyRelationshipDetailsDto> studyRelationships = new HashSet<>();
  private StudyConclusionsDto conclusions;
  private Set<CommentDto> comments = new HashSet<>();
}
