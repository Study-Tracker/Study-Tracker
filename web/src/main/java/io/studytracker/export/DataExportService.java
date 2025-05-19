/*
 * Copyright 2019-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.studytracker.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.*;
import io.studytracker.repository.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DataExportService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataExportService.class);

  private static final SimpleDateFormat SDF =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

  @Value("${storage.temp-dir}")
  private String tempDir;

  @Autowired private ObjectMapper objectMapper;
  @Autowired private StudyRepository studyRepository;
  @Autowired private ProgramRepository programRepository;
  @Autowired private AssayRepository assayRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private CollaboratorRepository collaboratorRepository;
  @Autowired private CommentRepository commentRepository;
  @Autowired private KeywordRepository keywordRepository;
  @Autowired private StudyCollectionRepository studyCollectionRepository;
  @Autowired private AssayTypeRepository assayTypeRepository;
  @Autowired private ActivityRepository activityRepository;
  @Autowired private ExternalLinkRepository externalLinkRepository;
  @Autowired private StudyConclusionsRepository studyConclusionsRepository;
  @Autowired private StudyRelationshipRepository studyRelationshipRepository;
  @Autowired private AssayTaskRepository assayTaskRepository;
  @Autowired private StorageDriveRepository storageDriveRepository;
  @Autowired private GitRepositoryRepository gitRepositoryRepository;
  @Autowired private ProgramNotebookFolderRepository programNotebookFolderRepository;
  @Autowired private ProgramStorageFolderRepository programStorageFolderRepository;

  @Autowired
  private StorageDriveFolderRepository storageDriveFolderRepository;

  @Autowired
  private GitGroupRepository gitGroupRepository;

  @Autowired
  private GitLabGroupRepository gitLabGroupRepository;

  @Autowired
  private GitLabRepositoryRepository gitLabRepositoryRepository;

  @Autowired
  private AssayTypeFieldRepository assayTypeFieldRepository;

  @Autowired
  private AssayTypeTaskRepository assayTypeTaskRepository;

  @Autowired
  private AssayTypeTaskFieldRepository assayTypeTaskFieldRepository;

  @Autowired
  private StudyNotebookFolderRepository studyNotebookFolderRepository;

  @Autowired
  private StudyStorageFolderRepository studyStorageFolderRepository;

  @Autowired
  private AssayNotebookFolderRepository assayNotebookFolderRepository;

  @Autowired
  private AssayStorageFolderRepository assayStorageFolderRepository;

  @Autowired
  private AssayTaskFieldRepository assayTaskFieldRepository;

  public Path exportAllDataToCsv() throws IOException {

    // Create the temporary export directory
    Path tempPath = Paths.get(tempDir, "export", "export-" + UUID.randomUUID());
    Path tempDir = Files.createDirectories(tempPath);
    LOGGER.info("Exporting data to temporary directory: {}", tempDir.toString());

    // Export each entity type to a separate CSV file
    exportUsersToCsv(tempDir);
    exportProgramsToCsv(tempDir);
    exportStorageDrivesToCsv(tempDir);
    exportStorageDriveFoldersToCsv(tempDir);
    exportGitGroupsToCsv(tempDir);
    exportGitRepositoriesToCsv(tempDir);
    exportAssayTypesToCsv(tempDir);
    exportStudiesToCsv(tempDir);
    exportAssaysToCsv(tempDir);
    exportCollaboratorsToCsv(tempDir);
    exportKeywordsToCsv(tempDir);
    exportStudyCollectionsToCsv(tempDir);
    exportActivitiesToCsv(tempDir);

    return tempDir;
  }

  private void exportUsersToCsv(Path directory) throws IOException {
    LOGGER.info("Exporting users...");
    List<User> users = userRepository.findAll();
    Path filePath = directory.resolve("users.csv");

    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      // Write header
      String[] header = {
          "ID",
          "Username",
          "DisplayName",
          "Email",
          "Type",
          "Department",
          "Title",
          "Active",
          "Admin",
          "Locked",
          "Expired",
          "CredentialExpired",
          "CreatedAt",
          "UpdatedAt",
          "Attributes",
          "Configuration"
      };
      writer.writeNext(header);

      // Write data rows
      for (User user : users) {
        String[] row = {
            String.valueOf(user.getId()),
            user.getUsername(),
            user.getDisplayName(),
            user.getEmail(),
            user.getType().toString(),
            user.getDepartment(),
            user.getTitle(),
            String.valueOf(user.isActive()),
            String.valueOf(user.isAdmin()),
            String.valueOf(user.isLocked()),
            String.valueOf(user.isExpired()),
            String.valueOf(user.isCredentialsExpired()),
            SDF.format(user.getCreatedAt()),
            user.getUpdatedAt() != null ? SDF.format(user.getUpdatedAt()) : "",
            objectMapper.writeValueAsString(user.getAttributes()),
            objectMapper.writeValueAsString(user.getConfiguration())
        };
        writer.writeNext(row);
      }
    }
  }

  private void exportStorageDrivesToCsv(Path directory) throws IOException {
    LOGGER.info("Exporting storage drives...");
    List<StorageDrive> drives = storageDriveRepository.findAll();
    Path filePath = directory.resolve("storage_drives.csv");

    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      // Write header
      String[] header = {
          "ID",
          "DisplayName",
          "DriveType",
          "RootPath",
          "Active",
          "CreatedAt",
          "UpdatedAt",
          "Details"
      };
      writer.writeNext(header);

      // Write data rows
      for (StorageDrive drive : drives) {
        String[] row = {
            String.valueOf(drive.getId()),
            drive.getDisplayName(),
            drive.getDriveType().toString(),
            drive.getRootPath(),
            String.valueOf(drive.isActive()),
            SDF.format(drive.getCreatedAt()),
            drive.getUpdatedAt() != null ? SDF.format(drive.getUpdatedAt()) : "",
            objectMapper.writeValueAsString(drive.getDetails())
        };
        writer.writeNext(row);
      }
    }
  }

  private void exportStorageDriveFoldersToCsv(Path directory) throws IOException {
    LOGGER.info("Exporting storage drive folders...");
    List<StorageDriveFolder> folders = storageDriveFolderRepository.findAll();
    Path filePath = directory.resolve("storage_drive_folders.csv");

    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      // Write header
      String[] header = {
          "ID",
          "StorageDriveId",
          "Name",
          "Path",
          "BrowserRoot",
          "StudyRoot",
          "WriteEnabled",
          "DeleteEnabled",
          "CreatedAt",
          "UpdatedAt",
          "Details"
      };
      writer.writeNext(header);

      // Write data rows
      for (StorageDriveFolder folder : folders) {
        String[] row = {
            String.valueOf(folder.getId()),
            String.valueOf(folder.getStorageDrive().getId()),
            folder.getName(),
            folder.getPath(),
            String.valueOf(folder.isBrowserRoot()),
            String.valueOf(folder.isStudyRoot()),
            String.valueOf(folder.isWriteEnabled()),
            String.valueOf(folder.isDeleteEnabled()),
            SDF.format(folder.getCreatedAt()),
            folder.getUpdatedAt() != null ? SDF.format(folder.getUpdatedAt()) : "",
            objectMapper.writeValueAsString(folder.getDetails())
        };
        writer.writeNext(row);
      }
    }
  }

  private void exportProgramsToCsv(Path directory) throws IOException {
    LOGGER.info("Exporting programs...");
    List<Program> programs = programRepository.findAll();

    // program details
    Path filePath = directory.resolve("programs.csv");
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "ID",
          "Name",
          "Code",
          "Description",
          "CreatedBy",
          "LastModifiedBy",
          "CreatedAt",
          "UpdatedAt",
          "Active",
          "Attributes"
      };
      writer.writeNext(header);

      for (Program program : programs) {
        String[] row = {
            String.valueOf(program.getId()),
            program.getName(),
            program.getCode(),
            program.getDescription(),
            String.valueOf(program.getCreatedBy().getId()),
            program.getLastModifiedBy() != null ? String.valueOf(program.getLastModifiedBy().getId()) : "",
            SDF.format(program.getCreatedAt()),
            program.getUpdatedAt() != null ? SDF.format(program.getUpdatedAt()) : "",
            String.valueOf(program.isActive()),
            objectMapper.writeValueAsString(program.getAttributes())
        };
        writer.writeNext(row);
      }
    }

    // program notebook folders
    filePath = directory.resolve("programs_notebook_folders.csv");
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "ID",
          "ProgramId",
          "ElnFolderId",
          "IsPrimary"
      };
      writer.writeNext(header);

      for (ProgramNotebookFolder folder: programNotebookFolderRepository.findAll()){
        String[] row = {
            String.valueOf(folder.getId()),
            String.valueOf(folder.getProgram().getId()),
            String.valueOf(folder.getElnFolder().getId()),
            String.valueOf(folder.isPrimary())
        };
        writer.writeNext(row);
      }
    }

    // program storage folders
    filePath = directory.resolve("programs_storage_folders.csv");
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "ID",
          "ProgramId",
          "StorageDriveFolderId",
          "IsPrimary"
      };
      writer.writeNext(header);

      for (ProgramStorageFolder folder: programStorageFolderRepository.findAll()){
        String[] row = {
            String.valueOf(folder.getId()),
            String.valueOf(folder.getProgram().getId()),
            String.valueOf(folder.getStorageDriveFolder().getId()),
            String.valueOf(folder.isPrimary())
        };
        writer.writeNext(row);
      }
    }

    // program git groups
    filePath = directory.resolve("programs_git_groups.csv");
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "GitGroupId",
          "ProgramId",
      };
      writer.writeNext(header);

      for (Program program : programs) {
        for (GitGroup group: gitGroupRepository.findByProgramId(program.getId())){
          String[] row = {
              String.valueOf(group.getId()),
              String.valueOf(program.getId())
          };
          writer.writeNext(row);
        }
      }
    }

  }

  private void exportGitGroupsToCsv(Path directory) throws IOException {
    LOGGER.info("Exporting git groups...");
    Path filePath = directory.resolve("git_groups.csv");

    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      // Write header
      String[] header = {
          "ID",
          "ParentGroupId",
          "DisplayName",
          "WebUrl",
          "Active",
          "GitServiceType",
          "CreatedAt",
          "UpdatedAt"
      };
      writer.writeNext(header);

      // Write data rows
      for (GitGroup group : gitGroupRepository.findAll()) {
        String[] row = {
            String.valueOf(group.getId()),
            group.getParentGroup() != null ? String.valueOf(group.getParentGroup().getId()) : "",
            group.getDisplayName(),
            group.getWebUrl(),
            String.valueOf(group.isActive()),
            group.getGitServiceType().toString(),
            SDF.format(group.getCreatedAt()),
            group.getUpdatedAt() != null ? SDF.format(group.getUpdatedAt()) : ""
        };
        writer.writeNext(row);
      }
    }

    filePath = directory.resolve("gitlab_groups.csv");
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      // Write header
      String[] header = {
          "ID",
          "GitLabIntegrationId",
          "GitGroupId",
          "GroupId",
          "Name",
          "Path"
      };
      writer.writeNext(header);

      // Write data rows
      for (GitLabGroup group : gitLabGroupRepository.findAll()) {
        String[] row = {
            String.valueOf(group.getId()),
            group.getGitLabIntegration() != null ? String.valueOf(group.getGitLabIntegration().getId()) : "",
            group.getGitGroup() != null ? String.valueOf(group.getGitGroup().getId()) : "",
            String.valueOf(group.getGroupId()),
            group.getName(),
            group.getPath()
        };
        writer.writeNext(row);
      }
    }


  }

  private void exportGitRepositoriesToCsv(Path directory) throws IOException {
    LOGGER.info("Exporting git repositories...");
    Path filePath = directory.resolve("git_repositories.csv");

    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      // Write header
      String[] header = {
          "ID",
          "GitGroupId",
          "DisplayName",
          "Description",
          "WebURL",
          "SshURL",
          "HttpURL",
          "CreatedAt",
          "UpdatedAt"
      };
      writer.writeNext(header);

      // Write data rows
      for (GitRepository repository : gitRepositoryRepository.findAll()) {
        String[] row = {
            String.valueOf(repository.getId()),
            repository.getGitGroup() != null ? String.valueOf(repository.getGitGroup().getId()) : "",
            repository.getDisplayName(),
            repository.getDescription(),
            repository.getWebUrl(),
            repository.getSshUrl(),
            repository.getHttpUrl(),
            SDF.format(repository.getCreatedAt()),
            repository.getUpdatedAt() != null ? SDF.format(repository.getUpdatedAt()) : ""
        };
        writer.writeNext(row);
      }
    }

    filePath = directory.resolve("gitlab_repositories.csv");
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      // Write header
      String[] header = {
          "ID",
          "GitLabGroupId",
          "RepositoryId",
          "Name",
          "Path",
      };
      writer.writeNext(header);

      // Write data rows
      for (GitLabRepository repository : gitLabRepositoryRepository.findAll()) {
        String[] row = {
            String.valueOf(repository.getId()),
            String.valueOf(repository.getGitLabGroup().getId()),
            String.valueOf(repository.getRepositoryId()),
            repository.getName(),
            repository.getPath()
        };
        writer.writeNext(row);
      }
    }

  }

  private void exportAssayTypesToCsv(Path directory) throws IOException {
    LOGGER.info("Exporting assay types...");

    List<AssayType> assayTypes = assayTypeRepository.findAll();
    Path filePath = directory.resolve("assay_types.csv");
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "ID",
          "Name",
          "Description",
          "Active",
          "Attributes"
      };
      writer.writeNext(header);

      // Write data rows
      for (AssayType assayType : assayTypes) {
        String[] row = {
            String.valueOf(assayType.getId()),
            assayType.getName(),
            assayType.getDescription(),
            String.valueOf(assayType.isActive()),
            objectMapper.writeValueAsString(assayType.getAttributes())
        };
        writer.writeNext(row);
      }
    }

    // Fields
    filePath = directory.resolve("assay_type_fields.csv");
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "ID",
          "AssayTypeId",
          "DisplayName",
          "FieldName",
          "Type",
          "Required",
          "Description",
          "Active",
          "FieldOrder",
          "DropdownOptions",
          "DefaultValue"
      };
      writer.writeNext(header);

      // Write data rows
      for (AssayTypeField field: assayTypeFieldRepository.findAll()) {
        String[] row = {
            String.valueOf(field.getId()),
            String.valueOf(field.getAssayType().getId()),
            field.getDisplayName(),
            field.getFieldName(),
            field.getType().toString(),
            String.valueOf(field.isRequired()),
            field.getDescription(),
            String.valueOf(field.isActive()),
            String.valueOf(field.getFieldOrder()),
            field.getDropdownOptions(),
            field.getDefaultValue()
        };
        writer.writeNext(row);
      }
    }

    // Tasks
    filePath = directory.resolve("assay_type_tasks.csv");
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "ID",
          "AssayTypeId",
          "Status",
          "Label",
          "Order",
          "CreatedAt",
          "UpdatedAt"
      };
      writer.writeNext(header);

      // Write data rows
      for (AssayTypeTask task: assayTypeTaskRepository.findAll()) {
        String[] row = {
            String.valueOf(task.getId()),
            String.valueOf(task.getAssayType().getId()),
            task.getStatus().toString(),
            task.getLabel(),
            String.valueOf(task.getOrder()),
            SDF.format(task.getCreatedAt()),
            task.getUpdatedAt() != null ? SDF.format(task.getUpdatedAt()) : ""

        };
        writer.writeNext(row);
      }
    }

    // Task Fields
    filePath = directory.resolve("assay_type_task_fields.csv");
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "ID",
          "AssayTypeTaskId",
          "DisplayName",
          "FieldName",
          "Type",
          "Required",
          "Description",
          "Active",
          "FieldOrder",
          "DropdownOptions",
          "DefaultValue"
      };
      writer.writeNext(header);

      // Write data rows
      for (AssayTypeTaskField field: assayTypeTaskFieldRepository.findAll()) {
        String[] row = {
            String.valueOf(field.getId()),
            String.valueOf(field.getAssayTypeTask().getId()),
            field.getDisplayName(),
            field.getFieldName(),
            field.getType().toString(),
            String.valueOf(field.isRequired()),
            field.getDescription(),
            String.valueOf(field.isActive()),
            String.valueOf(field.getFieldOrder()),
            field.getDropdownOptions(),
            field.getDefaultValue()
        };
        writer.writeNext(row);
      }
    }

  }

  private void exportStudiesToCsv(Path directory) throws IOException {
    List<Study> studies = studyRepository.findAll();
    Path filePath = directory.resolve("studies.csv");
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "ID",
          "ProgramId",
          "Code",
          "ExternalCode",
          "Status",
          "Name",
          "Description",
          "CollaboratorId",
          "Legacy",
          "Active",
          "CreatedBy",
          "LastModifiedBy",
          "StartDate",
          "EndDate",
          "CreatedAt",
          "UpdatedAt",
          "Owner",
          "Users",
          "Keywords",
          "Attributes"
      };
      writer.writeNext(header);

      // Write data rows
      for (Study study : studies) {
        String[] row = {
            String.valueOf(study.getId()),
            String.valueOf(study.getProgram().getId()),
            study.getCode(),
            study.getExternalCode(),
            study.getStatus().toString(),
            study.getName(),
            study.getDescription(),
            study.getCollaborator() != null ? String.valueOf(study.getCollaborator().getId()) : "",
            String.valueOf(study.isLegacy()),
            String.valueOf(study.isActive()),
            study.getCreatedBy() != null ? String.valueOf(study.getCreatedBy().getId()) : "",
            study.getLastModifiedBy() != null ? String.valueOf(study.getLastModifiedBy().getId()) : "",
            SDF.format(study.getStartDate()),
            study.getEndDate() != null ? SDF.format(study.getEndDate()) : "",
            SDF.format(study.getCreatedAt()),
            study.getUpdatedAt() != null ? SDF.format(study.getUpdatedAt()) : "",
            String.valueOf(study.getOwner().getId()),
            study.getUsers().stream()
                .map(u -> String.valueOf(u.getId()))
                .reduce((a, b) -> a + "," + b)
                .orElse(""),
            study.getKeywords().stream()
                .map(k -> String.valueOf(k.getId()))
                .reduce((a, b) -> a + "," + b)
                .orElse(""),
            objectMapper.writeValueAsString(study.getAttributes())
        };
        writer.writeNext(row);
      }
    }

    // ELN folder
    filePath = directory.resolve("study_notebook_folders.csv");
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "ID",
          "StudyId",
          "ElnFolderId",
          "IsPrimary"
      };
      writer.writeNext(header);
      for (StudyNotebookFolder folder: studyNotebookFolderRepository.findAll()) {
        String[] row = {
            String.valueOf(folder.getId()),
            String.valueOf(folder.getStudy().getId()),
            String.valueOf(folder.getElnFolder().getId()),
            String.valueOf(folder.isPrimary())
        };
        writer.writeNext(row);
      }
    }

    // Storage folder
    filePath = directory.resolve("study_storage_folders.csv");
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "ID",
          "StudyId",
          "StorageDriveFolderId",
          "IsPrimary"
      };
      writer.writeNext(header);
      for (StudyStorageFolder folder: studyStorageFolderRepository.findAll()) {
        String[] row = {
            String.valueOf(folder.getId()),
            String.valueOf(folder.getStudy().getId()),
            String.valueOf(folder.getStorageDriveFolder().getId()),
            String.valueOf(folder.isPrimary())
        };
        writer.writeNext(row);
      }
    }

    // External links
    filePath = directory.resolve("study_external_links.csv");
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "ID",
          "StudyId",
          "Label",
          "Url"
      };
      writer.writeNext(header);
      for (ExternalLink link: externalLinkRepository.findAll()) {
        String[] row = {
            String.valueOf(link.getId()),
            String.valueOf(link.getStudy().getId()),
            String.valueOf(link.getLabel()),
            String.valueOf(link.getUrl())
        };
        writer.writeNext(row);
      }
    }

    // relationships
    filePath = directory.resolve("study_relationships.csv");
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "ID",
          "Type",
          "SourceStudyId",
          "TargetStudyId"
      };
      writer.writeNext(header);
      for (StudyRelationship relationship: studyRelationshipRepository.findAll()) {
        String[] row = {
            String.valueOf(relationship.getId()),
            relationship.getType().toString(),
            String.valueOf(relationship.getSourceStudy().getId()),
            String.valueOf(relationship.getTargetStudy().getId())
        };
        writer.writeNext(row);
      }
    }

    // Conclusions
    filePath = directory.resolve("study_conclusions.csv");
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "ID",
          "StudyId",
          "Content",
          "CreatedBy",
          "LastModifiedBy",
          "CreatedAt",
          "UpdatedAt"
      };
      writer.writeNext(header);
      for (StudyConclusions conclusions: studyConclusionsRepository.findAll()) {
        String[] row = {
            String.valueOf(conclusions.getId()),
            String.valueOf(conclusions.getStudy().getId()),
            conclusions.getContent(),
            String.valueOf(conclusions.getCreatedBy().getId()),
            conclusions.getLastModifiedBy() != null ? String.valueOf(conclusions.getLastModifiedBy().getId()) : "",
            SDF.format(conclusions.getCreatedAt()),
            conclusions.getUpdatedAt() != null ? SDF.format(conclusions.getUpdatedAt()) : ""
        };
        writer.writeNext(row);
      }
    }

    // Comments
    filePath = directory.resolve("study_comments.csv");
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "ID",
          "StudyId",
          "Text",
          "CreatedBy",
          "CreatedAt",
          "UpdatedAt"
      };
      writer.writeNext(header);
      for (Comment comment: commentRepository.findAll()) {
        String[] row = {
            String.valueOf(comment.getId()),
            String.valueOf(comment.getStudy().getId()),
            comment.getText(),
            String.valueOf(comment.getCreatedBy().getId()),
            SDF.format(comment.getCreatedAt()),
            comment.getUpdatedAt() != null ? SDF.format(comment.getUpdatedAt()) : ""
        };
        writer.writeNext(row);
      }
    }

    // Git repositories
    filePath = directory.resolve("study_git_repositories.csv");
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "GitRepositoryId",
          "StudyId",
      };
      writer.writeNext(header);
      for (Study study: studies) {
        for (GitRepository repository : gitRepositoryRepository.findByStudyId(study.getId())) {
          String[] row = {
              String.valueOf(repository.getId()),
              String.valueOf(study.getId())
          };
          writer.writeNext(row);
        }
      }
    }

  }

  private void exportAssaysToCsv(Path directory) throws IOException {
    LOGGER.info("Exporting assays...");
    List<Assay> assays = assayRepository.findAll();
    Path filePath = directory.resolve("assays.csv");

    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "ID",
          "AssayTypeId",
          "StudyId",
          "Code",
          "Name",
          "Description",
          "Status",
          "CreatedBy",
          "LastModifiedBy",
          "Owner",
          "CreatedAt",
          "UpdatedAt",
          "StartDate",
          "EndDate",
          "Active",
          "Users",
          "Fields",
          "Attributes"
      };
      writer.writeNext(header);

      // Write data rows
      for (Assay assay : assays) {
        Set<User> users = userRepository.findByAssayId(assay.getId());
        String[] row = {
            String.valueOf(assay.getId()),
            String.valueOf(assay.getAssayType().getId()),
            String.valueOf(assay.getStudy().getId()),
            assay.getCode(),
            assay.getName(),
            assay.getDescription(),
            assay.getStatus().toString(),
            assay.getCreatedBy().toString(),
            assay.getLastModifiedBy() != null ? assay.getLastModifiedBy().getDisplayName() : "",
            assay.getOwner().getId().toString(),
            SDF.format(assay.getCreatedAt()),
            assay.getUpdatedAt() != null ? SDF.format(assay.getUpdatedAt()) : "",
            assay.getStartDate() != null ? SDF.format(assay.getStartDate()) : "",
            assay.getEndDate() != null ? SDF.format(assay.getEndDate()) : "",
            String.valueOf(assay.isActive()),
            users.stream()
                .map(u -> String.valueOf(u.getId()))
                .reduce((a, b) -> a + "," + b)
                .orElse(""),
            objectMapper.writeValueAsString(assay.getFields()),
            objectMapper.writeValueAsString(assay.getAttributes())
        };
        writer.writeNext(row);
      }
    }

    // ELN folder
    filePath = directory.resolve("assay_notebook_folders.csv");
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "ID",
          "AssayId",
          "ElnFolderId",
          "IsPrimary"
      };
      writer.writeNext(header);
      for (AssayNotebookFolder folder: assayNotebookFolderRepository.findAll()) {
        String[] row = {
            String.valueOf(folder.getId()),
            String.valueOf(folder.getAssay().getId()),
            String.valueOf(folder.getElnFolder().getId()),
            String.valueOf(folder.isPrimary())
        };
        writer.writeNext(row);
      }
    }

    // Storage folder
    filePath = directory.resolve("assay_storage_folders.csv");
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "ID",
          "AssayId",
          "StorageDriveFolderId",
          "IsPrimary"
      };
      writer.writeNext(header);
      for (AssayStorageFolder folder: assayStorageFolderRepository.findAll()) {
        String[] row = {
            String.valueOf(folder.getId()),
            String.valueOf(folder.getAssay().getId()),
            String.valueOf(folder.getStorageDriveFolder().getId()),
            String.valueOf(folder.isPrimary())
        };
        writer.writeNext(row);
      }
    }

    // Git repositories
    filePath = directory.resolve("assay_git_repositories.csv");
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "GitRepositoryId",
          "AssayId",
      };
      writer.writeNext(header);
      for (Assay assay: assays) {
        for (GitRepository repository : gitRepositoryRepository.findByAssayId(assay.getId())) {
          String[] row = {
              String.valueOf(repository.getId()),
              String.valueOf(assay.getId())
          };
          writer.writeNext(row);
        }
      }
    }

    // Tasks
    filePath = directory.resolve("assay_tasks.csv");
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "ID",
          "AssayId",
          "Status",
          "Label",
          "Order",
          "CreatedAt",
          "UpdatedAt",
          "CreatedBy",
          "LastModifiedBy",
          "AssignedTo",
          "DueDate",
          "Data"
      };
      writer.writeNext(header);

      // Write data rows
      for (AssayTask task: assayTaskRepository.findAll()) {
        String[] row = {
            String.valueOf(task.getId()),
            String.valueOf(task.getAssay().getId()),
            task.getStatus().toString(),
            task.getLabel(),
            String.valueOf(task.getOrder()),
            SDF.format(task.getCreatedAt()),
            task.getUpdatedAt() != null ? SDF.format(task.getUpdatedAt()) : "",
            task.getCreatedBy() != null ? String.valueOf(task.getCreatedBy().getId()) : "",
            task.getLastModifiedBy() != null ? String.valueOf(task.getLastModifiedBy().getId()) : "",
            task.getAssignedTo() != null ? String.valueOf(task.getAssignedTo().getId()) : "",
            task.getDueDate() != null ? SDF.format(task.getDueDate()) : "",
            objectMapper.writeValueAsString(task.getData())
        };
        writer.writeNext(row);
      }
    }

    // Task Fields
    filePath = directory.resolve("assay_task_fields.csv");
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "ID",
          "AssayTaskId",
          "DisplayName",
          "FieldName",
          "Type",
          "Required",
          "Description",
          "Active",
          "FieldOrder",
          "DropdownOptions",
          "DefaultValue"
      };
      writer.writeNext(header);

      // Write data rows
      for (AssayTaskField field: assayTaskFieldRepository.findAll()) {
        String[] row = {
            String.valueOf(field.getId()),
            String.valueOf(field.getAssayTask().getId()),
            field.getDisplayName(),
            field.getFieldName(),
            field.getType().toString(),
            String.valueOf(field.isRequired()),
            field.getDescription(),
            String.valueOf(field.isActive()),
            String.valueOf(field.getFieldOrder()),
            field.getDropdownOptions(),
            field.getDefaultValue()
        };
        writer.writeNext(row);
      }
    }

  }

  private void exportCollaboratorsToCsv(Path directory) throws IOException {
    LOGGER.info("Exporting collaborators...");
    List<Collaborator> collaborators = collaboratorRepository.findAll();
    Path filePath = directory.resolve("collaborators.csv");

    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "ID",
          "Label",
          "Code",
          "OrganizationName",
          "OrganizationLocation",
          "ContactPersonName",
          "ContactEmail",
          "Active"
      };
      writer.writeNext(header);

      for (Collaborator collaborator : collaborators) {
        String[] row = {
            String.valueOf(collaborator.getId()),
            collaborator.getLabel(),
            collaborator.getCode(),
            collaborator.getOrganizationName(),
            collaborator.getOrganizationLocation() != null ? collaborator.getOrganizationLocation() : "",
            collaborator.getContactPersonName() != null ? collaborator.getContactPersonName() : "",
            collaborator.getContactEmail() != null ? collaborator.getContactEmail() : "",
            String.valueOf(collaborator.isActive())
        };
        writer.writeNext(row);
      }
    }
  }

  private void exportKeywordsToCsv(Path directory) throws IOException {
    LOGGER.info("Exporting keywords...");
    List<Keyword> keywords = keywordRepository.findAll();
    Path filePath = directory.resolve("keywords.csv");

    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "ID",
          "Keyword",
          "Category"
      };
      writer.writeNext(header);

      for (Keyword keyword : keywords) {
        String[] row = {
            String.valueOf(keyword.getId()),
            keyword.getKeyword(),
            keyword.getCategory()
        };
        writer.writeNext(row);
      }
    }
  }

  private void exportStudyCollectionsToCsv(Path directory) throws IOException {
    LOGGER.info("Exporting study collections...");
    List<StudyCollection> collections = studyCollectionRepository.findAll();
    Path filePath = directory.resolve("study_collections.csv");

    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "ID",
          "Name",
          "Description",
          "Shared",
          "CreatedBy",
          "LastModifiedBy",
          "CreatedAt",
          "UpdatedAt",
          "Studies"
      };
      writer.writeNext(header);

      // Write data rows
      for (StudyCollection c : collections) {
        StudyCollection collection = studyCollectionRepository.findById(c.getId())
            .orElseThrow(RecordNotFoundException::new);
        String[] row = {
            String.valueOf(collection.getId()),
            collection.getName(),
            collection.getDescription(),
            String.valueOf(collection.isShared()),
            String.valueOf(collection.getCreatedBy().getId()),
            collection.getLastModifiedBy() != null ? String.valueOf(collection.getLastModifiedBy().getId()) : "",
            SDF.format(collection.getCreatedAt()),
            collection.getUpdatedAt() != null ? SDF.format(collection.getUpdatedAt()) : "",
            collection.getStudies().stream()
                .map(s -> String.valueOf(s.getId()))
                .reduce((a, b) -> a + "," + b)
                .orElse("")
        };
        writer.writeNext(row);
      }
    }
  }

  private void exportActivitiesToCsv(Path directory) throws IOException {
    LOGGER.info("Exporting activity...");
    List<Activity> activities = activityRepository.findAll();
    Path filePath = directory.resolve("activity.csv");

    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      String[] header = {
          "ID",
          "ProgramId",
          "StudyId",
          "AssayId",
          "EventType",
          "Data",
          "User",
          "Date"
      };
      writer.writeNext(header);

      // Write data rows
      for (Activity activity : activities) {
        String[] row = {
            String.valueOf(activity.getId()),
            activity.getProgram() != null ? String.valueOf(activity.getProgram().getId()) : "",
            activity.getStudy() != null ? String.valueOf(activity.getStudy().getId()) : "",
            activity.getAssay() != null ? String.valueOf(activity.getAssay().getId()) : "",
            activity.getEventType().toString(),
            objectMapper.writeValueAsString(activity.getData()),
            activity.getUser() != null ? String.valueOf(activity.getUser().getId()) : "",
            SDF.format(activity.getDate())
        };
        writer.writeNext(row);
      }
    }
  }

  private void exportExternalLinksToCsv(Path directory) throws IOException {
    List<ExternalLink> links = externalLinkRepository.findAll();
    Path filePath = directory.resolve("external_links.csv");

    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      // Write header
      String[] header = {"ID", "Label", "URL", "Study"};
      writer.writeNext(header);

      // Write data rows
      for (ExternalLink link : links) {
        String[] row = {
            String.valueOf(link.getId()),
            link.getLabel(),
            link.getUrl().toString(),
            link.getStudy().getCode()
        };
        writer.writeNext(row);
      }
    }
  }

  private void exportStudyRelationshipsToCsv(Path directory) throws IOException {
    List<StudyRelationship> relationships = studyRelationshipRepository.findAll();
    Path filePath = directory.resolve("study_relationships.csv");

    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      // Write header
      String[] header = {"ID", "SourceStudy", "TargetStudy", "Type"};
      writer.writeNext(header);

      // Write data rows
      for (StudyRelationship relationship : relationships) {
        String[] row = {
            String.valueOf(relationship.getId()),
            relationship.getSourceStudy().getCode(),
            relationship.getTargetStudy().getCode(),
            relationship.getType().toString()
        };
        writer.writeNext(row);
      }
    }
  }

  private void exportAssayTasksToCsv(Path directory) throws IOException {
    List<AssayTask> tasks = assayTaskRepository.findAll();
    Path filePath = directory.resolve("assay_tasks.csv");

    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      // Write header
      String[] header = {"ID", "Label", "Order", "Status", "AssayCode"};
      writer.writeNext(header);

      // Write data rows
      for (AssayTask task : tasks) {
        String[] row = {
            String.valueOf(task.getId()),
            task.getLabel(),
            String.valueOf(task.getOrder()),
            task.getStatus().toString(),
            task.getAssay().getCode()
        };
        writer.writeNext(row);
      }
    }
  }

  private void exportStudyConclusionsToCsv(Path directory) throws IOException {
    List<StudyConclusions> conclusions = studyConclusionsRepository.findAll();
    Path filePath = directory.resolve("study_conclusions.csv");

    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()))) {
      // Write header
      String[] header = {"ID", "StudyId", "Content", "Created", "Updated"};
      writer.writeNext(header);

      // Write data rows
      for (StudyConclusions conclusion : conclusions) {
        String[] row = {
            String.valueOf(conclusion.getId()),
            String.valueOf(conclusion.getStudy().getId()),
            conclusion.getContent(),
            conclusion.getCreatedAt().toString(),
            conclusion.getUpdatedAt() != null ? conclusion.getUpdatedAt().toString() : ""
        };
        writer.writeNext(row);
      }
    }
  }

}
