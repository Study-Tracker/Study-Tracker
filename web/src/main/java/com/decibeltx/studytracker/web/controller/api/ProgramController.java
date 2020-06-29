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

package com.decibeltx.studytracker.web.controller.api;

import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.Activity;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.core.service.ActivityService;
import com.decibeltx.studytracker.core.service.ProgramService;
import com.decibeltx.studytracker.core.service.UserService;
import com.decibeltx.studytracker.web.controller.UserAuthenticationUtils;
import java.util.List;
import java.util.Optional;
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

  @GetMapping("")
  public List<Program> getAllPrograms() throws Exception {
    return programService.findAll();
  }

  @GetMapping("/{id}")
  public Program getProgram(@PathVariable("id") String programId) throws Exception {
    Optional<Program> optional = programService.findById(programId);
    if (optional.isPresent()) {
      return optional.get();
    } else {
      throw new RecordNotFoundException();
    }
  }

  @PostMapping("")
  public HttpEntity<Program> createProgram(@RequestBody Program program) {

    LOGGER.info("Creating new program: " + program.toString());

    // Get authenticated user
    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = userService.findByAccountName(username)
        .orElseThrow(RecordNotFoundException::new);
    program.setCreatedBy(user);

    programService.create(program);
    return new ResponseEntity<>(program, HttpStatus.CREATED);

  }

  @PutMapping("/{id}")
  public HttpEntity<Program> updateProgram(@PathVariable("id") String programId,
      @RequestBody Program program) {

    Optional<Program> optional = programService.findById(programId);
    if (!optional.isPresent()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Get authenticated user
    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = userService.findByAccountName(username)
        .orElseThrow(RecordNotFoundException::new);
    program.setLastModifiedBy(user);
    programService.update(program);
    return new ResponseEntity<>(program, HttpStatus.CREATED);

  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> deleteProgram(@PathVariable("id") String programId) {
    Optional<Program> optional = programService.findById(programId);
    if (!optional.isPresent()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    Program program = optional.get();

    // Get authenticated user
    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = userService.findByAccountName(username)
        .orElseThrow(RecordNotFoundException::new);
    program.setLastModifiedBy(user);
    programService.delete(program);
    return new ResponseEntity<>(program, HttpStatus.OK);
  }

  @PostMapping("/{id}/status")
  public HttpEntity<?> updateProgramStatus(@PathVariable("id") String programId,
      @RequestParam("active") boolean active) {
    Optional<Program> optional = programService.findById(programId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException("Program not found: " + programId);
    }
    Program program = optional.get();
    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = userService.findByAccountName(username)
        .orElseThrow(RecordNotFoundException::new);
    program.setLastModifiedBy(user);
    program.setActive(active);
    programService.update(program);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/{id}/activity")
  public HttpEntity<List<Activity>> getProgramActivity(@PathVariable("id") String programId) {
    Optional<Program> optional = programService.findById(programId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException("Program not found: " + programId);
    }
    Program program = optional.get();
    List<Activity> activities = activityService.findByProgram(program);
    return new ResponseEntity<>(activities, HttpStatus.OK);
  }

}
