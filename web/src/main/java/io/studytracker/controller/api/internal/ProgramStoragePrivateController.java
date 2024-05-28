/*
 * Copyright 2019-2023 the original author or authors.
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

package io.studytracker.controller.api.internal;

import io.studytracker.controller.api.AbstractProgramController;
import io.studytracker.exception.InsufficientPrivilegesException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.form.StorageDriveFolderFormDto;
import io.studytracker.mapstruct.dto.response.ProgramStorageDriveFolderSummaryDto;
import io.studytracker.mapstruct.mapper.StorageDriveFolderMapper;
import io.studytracker.model.Program;
import io.studytracker.model.ProgramStorageFolder;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.model.User;
import io.studytracker.repository.ProgramStorageFolderRepository;
import io.studytracker.service.FileSystemStorageService;
import io.studytracker.storage.StorageDriveFolderService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/internal/program/{programId}/storage")
@RestController
public class ProgramStoragePrivateController extends AbstractProgramController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProgramStoragePrivateController.class);

  @Autowired private FileSystemStorageService fileStorageService;

  @Autowired private ProgramStorageFolderRepository programStorageFolderRepository;

  @Autowired private StorageDriveFolderMapper storageDriveFolderMapper;
  
  @Autowired private StorageDriveFolderService storageDriveFolderService;

  @GetMapping("")
  public List<ProgramStorageDriveFolderSummaryDto> getStudyStorageFolders(@PathVariable("programId") Long programId) {
    LOGGER.info("Fetching storage folder for program: " + programId);
    List<ProgramStorageFolder> programStorageFolders = programStorageFolderRepository
        .findByProgramId(programId);
    return storageDriveFolderMapper.toProgramFolderSummaryDto(programStorageFolders);
  }

  @PatchMapping("")
  public HttpEntity<?> addFolderToProgram(@PathVariable("programId") Long programId,
          @RequestBody StorageDriveFolderFormDto dto) {
    LOGGER.info("Adding storage folder {} to program {}", dto.getPath(), programId);

    // Check user privileges
    User user = this.getAuthenticatedUser();
    if (!user.isAdmin()) {
      throw new InsufficientPrivilegesException(
          "You do not have permission to perform this action.");
    }

    Program program = this.getProgramService().findById(programId)
        .orElseThrow(() -> new RecordNotFoundException("Program not found: " + programId));
    StorageDrive drive = storageDriveFolderService.findDriveById(dto.getStorageDriveId())
            .orElseThrow(() -> new IllegalArgumentException("Storage drive not found: " + dto.getStorageDriveId()));
    StorageDriveFolder folder = storageDriveFolderService
            .registerFolder(storageDriveFolderMapper.fromFormDto(dto), drive);
    this.getProgramService().addStorageFolder(program, folder);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/repair")
  public HttpEntity<?> repairStorageFolder(@PathVariable("programId") Long programId) {
    LOGGER.info("Repairing storage folder for program: " + programId);

    // Check user privileges
    User user = this.getAuthenticatedUser();
    if (!user.isAdmin()) {
      throw new InsufficientPrivilegesException(
          "You do not have permission to perform this action.");
    }

    Program program = this.getProgramService().findById(programId)
        .orElseThrow(() -> new RecordNotFoundException("Program not found: " + programId));
    getProgramService().repairStorageFolder(program);
    return new ResponseEntity<>(HttpStatus.OK);
  }
  
  @GetMapping("/{folderId}")
  public ProgramStorageDriveFolderSummaryDto getProgramFolderById(
      @PathVariable("programId") Long programId,
      @PathVariable("folderId") Long folderId) {
    LOGGER.info("Fetching storage folder {} for program {}", folderId, programId);
    Program program = this.getProgramService().findById(programId)
        .orElseThrow(() -> new RecordNotFoundException("Program not found: " + programId));
    ProgramStorageFolder folder = programStorageFolderRepository.findByProgramId(program.getId()).stream()
            .filter(f -> f.getStorageDriveFolder().getId().equals(folderId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Storage folder not found: " + folderId));
    return storageDriveFolderMapper.toProgramFolderSummaryDto(folder);
  }
  
  @PatchMapping("/{folderId}")
  public HttpEntity<?> setDefaultProgramFolder(@PathVariable("programId") Long programId,
          @PathVariable("folderId") Long folderId) {
    LOGGER.info("Setting storage folder {} as default for program {}", folderId, programId);

    // Check user privileges
    User user = this.getAuthenticatedUser();
    if (!user.isAdmin()) {
      throw new InsufficientPrivilegesException(
          "You do not have permission to perform this action.");
    }

    Program program = this.getProgramService().findById(programId)
        .orElseThrow(() -> new RecordNotFoundException("Program not found: " + programId));
    List<ProgramStorageFolder> folders = programStorageFolderRepository
        .findByProgramId(program.getId());
    for (ProgramStorageFolder folder: folders) {
      folder.setPrimary(folder.getId().equals(folderId));
      programStorageFolderRepository.save(folder);
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }
  
  @DeleteMapping("/{folderId}")
  public HttpEntity<?> removeProgramFolder(@PathVariable("programId") Long programId,
          @PathVariable("folderId") Long folderId) {
    LOGGER.info("Removing storage folder {} from program {}", folderId, programId);

    // Check user privileges
    User user = this.getAuthenticatedUser();
    if (!user.isAdmin()) {
      throw new InsufficientPrivilegesException(
          "You do not have permission to perform this action.");
    }

    Program program = this.getProgramService().findById(programId)
        .orElseThrow(() -> new RecordNotFoundException("Program not found: " + programId));
    ProgramStorageFolder folder = programStorageFolderRepository.findByProgramId(program.getId()).stream()
            .filter(f -> f.getId().equals(folderId))
            .findFirst()
            .orElseThrow(() -> new RecordNotFoundException("Storage folder not found: " + folderId));
    this.getProgramService().removeStorageFolder(program, folder);
    return new ResponseEntity<>(HttpStatus.OK);
  }
  
}
