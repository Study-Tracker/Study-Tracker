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

package com.decibeltx.studytracker.controller.api;

import com.decibeltx.studytracker.controller.UserAuthenticationUtils;
import com.decibeltx.studytracker.events.EventsService;
import com.decibeltx.studytracker.events.util.ProgramActivityUtils;
import com.decibeltx.studytracker.exception.InsufficientPrivilegesException;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.mapstruct.dto.ActivityDetailsDto;
import com.decibeltx.studytracker.mapstruct.dto.ProgramDetailsDto;
import com.decibeltx.studytracker.mapstruct.dto.ProgramSummaryDto;
import com.decibeltx.studytracker.mapstruct.mapper.ActivityMapper;
import com.decibeltx.studytracker.mapstruct.mapper.ProgramMapper;
import com.decibeltx.studytracker.model.Activity;
import com.decibeltx.studytracker.model.Program;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.service.ActivityService;
import com.decibeltx.studytracker.service.ProgramService;
import com.decibeltx.studytracker.service.UserService;
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

  @Autowired
  private ProgramService programService;

  @Autowired
  private UserService userService;

  @Autowired
  private ActivityService activityService;

  @Autowired
  private EventsService eventsService;

  @Autowired
  private ProgramMapper programMapper;

  @Autowired
  private ActivityMapper activityMapper;

  @GetMapping("")
  public List<ProgramSummaryDto> getAllPrograms() throws Exception {
    return programMapper.toProgramSummaryList(programService.findAll());
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
    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = userService.findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);
    if (!user.isAdmin()) {
      throw new InsufficientPrivilegesException("You do not have permission to perform this action.");
    }

    Program program = programMapper.fromProgramDetails(dto);
    programService.create(program);

    // Publish events
    Activity activity = ProgramActivityUtils
        .fromNewProgram(program, user);
    activityService.create(activity);
    eventsService.dispatchEvent(activity);

    return new ResponseEntity<>(programMapper.toProgramDetails(program), HttpStatus.CREATED);

  }

  @PutMapping("/{id}")
  public HttpEntity<ProgramDetailsDto> updateProgram(@PathVariable("id") Long programId,
      @RequestBody @Valid ProgramDetailsDto dto) {

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = userService.findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);
    if (!user.isAdmin()) {
      throw new InsufficientPrivilegesException("You do not have permission to perform this action.");
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
    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = userService.findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);
    if (!user.isAdmin()) {
      throw new InsufficientPrivilegesException("You do not have permission to perform this action.");
    }

    if (!programService.exists(programId)) {
      throw new RecordNotFoundException("Could not find program: " + programId);
    }

//    program.setLastModifiedBy(user);
    programService.delete(programId);

    // Publish events
    Program program = programService.findById(programId)
        .orElseThrow(RecordNotFoundException::new);
    Activity activity = ProgramActivityUtils
        .fromDeletedProgram(program, user);
    activityService.create(activity);
    eventsService.dispatchEvent(activity);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/{id}/status")
  public HttpEntity<?> updateProgramStatus(@PathVariable("id") Long programId,
      @RequestParam("active") boolean active) {

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = userService.findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);
    if (!user.isAdmin()) {
      throw new InsufficientPrivilegesException("You do not have permission to perform this action.");
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
    Activity activity = ProgramActivityUtils
        .fromUpdatedProgram(program, user);
    activityService.create(activity);
    eventsService.dispatchEvent(activity);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/{id}/activity")
  public HttpEntity<List<ActivityDetailsDto>> getProgramActivity(@PathVariable("id") Long programId) {
    Optional<Program> optional = programService.findById(programId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException("Program not found: " + programId);
    }
    Program program = optional.get();
    List<Activity> activities = activityService.findByProgram(program);
    return new ResponseEntity<>(activityMapper.toActivityDetailsList(activities), HttpStatus.OK);
  }

}
