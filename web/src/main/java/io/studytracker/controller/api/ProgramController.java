/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.studytracker.controller.api;

import io.studytracker.controller.UserAuthenticationUtils;
import io.studytracker.eln.NotebookFolder;
import io.studytracker.eln.StudyNotebookService;
import io.studytracker.events.EventsService;
import io.studytracker.events.util.ProgramActivityUtils;
import io.studytracker.exception.InsufficientPrivilegesException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.response.ActivityDetailsDto;
import io.studytracker.mapstruct.dto.response.ProgramDetailsDto;
import io.studytracker.mapstruct.mapper.ActivityMapper;
import io.studytracker.mapstruct.mapper.ProgramMapper;
import io.studytracker.model.Activity;
import io.studytracker.model.Program;
import io.studytracker.model.User;
import io.studytracker.service.ActivityService;
import io.studytracker.service.ProgramService;
import io.studytracker.service.UserService;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StudyStorageService;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RestController
@RequestMapping("/api/program")
public class ProgramController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProgramController.class);

  @Autowired private ProgramService programService;

  @Autowired private UserService userService;

  @Autowired private ActivityService activityService;

  @Autowired private EventsService eventsService;

  @Autowired private ProgramMapper programMapper;

  @Autowired private ActivityMapper activityMapper;

  @Autowired private StudyStorageService storageService;

  @Autowired(required = false)
  private StudyNotebookService notebookService;

  @GetMapping("")
  public List<?> getAllPrograms(
      @RequestParam(required = false, name = "details") boolean showDetails) throws Exception {
    List<Program> programs = programService.findAll();
    if (showDetails) {
      return programMapper.toProgramDetailsList(programs);
    } else {
      return programMapper.toProgramSummaryList(programs);
    }
  }

  @GetMapping("/{id}")
  public ProgramDetailsDto getProgram(@PathVariable("id") Long programId) throws Exception {
    Optional<Program> optional = programService.findById(programId);
    if (optional.isPresent()) {
      return programMapper.toProgramDetails(optional.get());
    } else {
      throw new RecordNotFoundException("Could not find program: " + programId);
    }
  }

  @PostMapping("")
  public HttpEntity<ProgramDetailsDto> createProgram(@RequestBody @Valid ProgramDetailsDto dto) {

    LOGGER.info("Creating new program: " + dto.toString());

    // Get authenticated user
    String username =
        UserAuthenticationUtils.getUsernameFromAuthentication(
            SecurityContextHolder.getContext().getAuthentication());
    User user = userService.findByUsername(username).orElseThrow(RecordNotFoundException::new);
    if (!user.isAdmin()) {
      throw new InsufficientPrivilegesException(
          "You do not have permission to perform this action.");
    }

    Program program = programMapper.fromProgramDetails(dto);
    programService.create(program);

    // Publish events
    Activity activity = ProgramActivityUtils.fromNewProgram(program, user);
    activityService.create(activity);
    eventsService.dispatchEvent(activity);

    return new ResponseEntity<>(programMapper.toProgramDetails(program), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<ProgramDetailsDto> updateProgram(
      @PathVariable("id") Long programId, @RequestBody @Valid ProgramDetailsDto dto) {

    String username =
        UserAuthenticationUtils.getUsernameFromAuthentication(
            SecurityContextHolder.getContext().getAuthentication());
    User user = userService.findByUsername(username).orElseThrow(RecordNotFoundException::new);
    if (!user.isAdmin()) {
      throw new InsufficientPrivilegesException(
          "You do not have permission to perform this action.");
    }

    if (!programService.exists(programId)) {
      throw new RecordNotFoundException("Could not find program: " + programId);
    }

    Program program = programMapper.fromProgramDetails(dto);
    programService.update(program);

    // Publish events
    Activity activity = ProgramActivityUtils.fromUpdatedProgram(program, user);
    activityService.create(activity);
    eventsService.dispatchEvent(activity);

    return new ResponseEntity<>(programMapper.toProgramDetails(program), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> deleteProgram(@PathVariable("id") Long programId) {

    // Get authenticated user
    String username =
        UserAuthenticationUtils.getUsernameFromAuthentication(
            SecurityContextHolder.getContext().getAuthentication());
    User user = userService.findByUsername(username).orElseThrow(RecordNotFoundException::new);
    if (!user.isAdmin()) {
      throw new InsufficientPrivilegesException(
          "You do not have permission to perform this action.");
    }

    if (!programService.exists(programId)) {
      throw new RecordNotFoundException("Could not find program: " + programId);
    }

    //    program.setLastModifiedBy(user);
    programService.delete(programId);

    // Publish events
    Program program = programService.findById(programId).orElseThrow(RecordNotFoundException::new);
    Activity activity = ProgramActivityUtils.fromDeletedProgram(program, user);
    activityService.create(activity);
    eventsService.dispatchEvent(activity);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/{id}/status")
  public HttpEntity<?> updateProgramStatus(
      @PathVariable("id") Long programId, @RequestParam("active") boolean active) {

    String username =
        UserAuthenticationUtils.getUsernameFromAuthentication(
            SecurityContextHolder.getContext().getAuthentication());
    User user = userService.findByUsername(username).orElseThrow(RecordNotFoundException::new);
    if (!user.isAdmin()) {
      throw new InsufficientPrivilegesException(
          "You do not have permission to perform this action.");
    }

    Optional<Program> optional = programService.findById(programId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException("Program not found: " + programId);
    }
    Program program = optional.get();

    //    program.setLastModifiedBy(user);
    program.setActive(active);
    programService.update(program);

    // Publish events
    Activity activity = ProgramActivityUtils.fromUpdatedProgram(program, user);
    activityService.create(activity);
    eventsService.dispatchEvent(activity);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/{id}/activity")
  public HttpEntity<List<ActivityDetailsDto>> getProgramActivity(
      @PathVariable("id") Long programId) {
    Optional<Program> optional = programService.findById(programId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException("Program not found: " + programId);
    }
    Program program = optional.get();
    List<Activity> activities = activityService.findByProgram(program);
    return new ResponseEntity<>(activityMapper.toActivityDetailsList(activities), HttpStatus.OK);
  }

  /**
   * Retrieves the program's storage folder reference as a {@link StorageFolder} object.
   *
   * @param programId PKID of the program
   * @return
   */
  @GetMapping("/{id}/storage")
  public HttpEntity<StorageFolder> getProgramStorageFolder(@PathVariable("id") Long programId) {
    Optional<Program> optional = programService.findById(programId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException("Program not found: " + programId);
    }
    Program program = optional.get();
    try {
      return new ResponseEntity<>(storageService.getProgramFolder(program), HttpStatus.OK);
    } catch (StudyStorageNotFoundException e) {
      throw new RecordNotFoundException("Program folder not found:" + programId);
    }
  }

  /**
   * Repairs the reference to a program's storage folder by either fetching a new reference or
   * creating a new folder.
   *
   * @param programId
   * @return
   */
  @PatchMapping("/{id}/storage")
  public HttpEntity<?> repairProgramStorageFolder(@PathVariable("id") Long programId) {

    // Check user privileges
    String username =
        UserAuthenticationUtils.getUsernameFromAuthentication(
            SecurityContextHolder.getContext().getAuthentication());
    User user = userService.findByUsername(username).orElseThrow(RecordNotFoundException::new);
    if (!user.isAdmin()) {
      throw new InsufficientPrivilegesException(
          "You do not have permission to perform this action.");
    }

    // Check that the program exists
    Optional<Program> optional = programService.findById(programId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException("Program not found: " + programId);
    }
    Program program = optional.get();

    // Repair the storage folder
    programService.repairStorageFolder(program);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/{id}/notebook")
  public NotebookFolder getProgramElnFolder(@PathVariable("id") Long programId) {

    // Check that the program exists
    Optional<Program> optional = programService.findById(programId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException("Program not found: " + programId);
    }
    Program program = optional.get();

    // Check that the folder exists
    Optional<NotebookFolder> folderOptional =
        Optional.ofNullable(notebookService).flatMap(service -> service.findProgramFolder(program));
    if (!folderOptional.isPresent()) {
      throw new RecordNotFoundException("Cannot find notebook folder for program: " + programId);
    }

    return folderOptional.get();
  }

  @PatchMapping("/{id}/notebook")
  public HttpEntity<?> repairNotebookFolder(@PathVariable("id") Long programId) {

    // Check user privileges
    String username =
        UserAuthenticationUtils.getUsernameFromAuthentication(
            SecurityContextHolder.getContext().getAuthentication());
    User user = userService.findByUsername(username).orElseThrow(RecordNotFoundException::new);
    if (!user.isAdmin()) {
      throw new InsufficientPrivilegesException(
          "You do not have permission to perform this action.");
    }

    // Check that the program exists
    Optional<Program> optional = programService.findById(programId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException("Program not found: " + programId);
    }
    Program program = optional.get();

    // Repair the folder
    programService.repairElnFolder(program);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
