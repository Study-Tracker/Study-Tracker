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

package io.studytracker.controller.api;

import io.studytracker.events.util.ProgramActivityUtils;
import io.studytracker.exception.InsufficientPrivilegesException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.mapper.ProgramMapper;
import io.studytracker.model.Activity;
import io.studytracker.model.Program;
import io.studytracker.model.ProgramOptions;
import io.studytracker.model.User;
import io.studytracker.service.ProgramService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractProgramController extends AbstractApiController {

  private ProgramService programService;
  private ProgramMapper programMapper;

  /**
   * Creates a new program.
   *
   * @param program the program to create
   * @return the created program
   */
  public Program createNewProgram(Program program) {

    ProgramOptions options = program.getOptions();

    // Make sure the user has the necessary privileges to create a new program
    User user = this.getAuthenticatedUser();
    if (!user.isAdmin()) {
      throw new InsufficientPrivilegesException(
          "You do not have permission to perform this action.");
    }

    // Create the new program and publish the event
    Program created = this.getProgramService().create(program);
    Activity activity = ProgramActivityUtils.fromNewProgram(created, user);
    this.logActivity(activity);

    return created;
  }

  /**
   * Updates an existing program.
   *
   * @param program the program to update
   * @return the updated program
   */
  public Program updateExistingProgram(Program program) {

    ProgramOptions options = program.getOptions();

    // Make sure the user has the necessary privileges to update a program
    User user = this.getAuthenticatedUser();
    if (!user.isAdmin()) {
      throw new InsufficientPrivilegesException(
          "You do not have permission to perform this action.");
    }

    // Make sure the program already exists
    if (!this.getProgramService().exists(program.getId())) {
      throw new RecordNotFoundException("Could not find program: " + program.getId());
    }

    Program updated = this.getProgramService().update(program, options);
    this.logActivity(ProgramActivityUtils.fromUpdatedProgram(updated, user));

    return updated;
  }

  /**
   * Deletes an existing program. In most cases, this simply deactivates it without removing the
   *   actual database records.
   *
   * @param programId the id of the program to delete
   */
  public void deleteExistingProgram(Long programId) {
    // Make sure the user has the necessary privileges to delete a program
    User user = this.getAuthenticatedUser();
    if (!user.isAdmin()) {
      throw new InsufficientPrivilegesException(
          "You do not have permission to perform this action.");
    }

    // Make sure the program already exists
    Optional<Program> optional = programService.findById(programId);
    if (optional.isEmpty()) {
      throw new RecordNotFoundException("Could not find program: " + programId);
    }
    Program program = optional.get();

    // Delete the program
    this.getProgramService().delete(programId);
    this.logActivity(ProgramActivityUtils.fromDeletedProgram(program, user));
  }

  public ProgramService getProgramService() {
    return programService;
  }

  @Autowired
  public void setProgramService(ProgramService programService) {
    this.programService = programService;
  }

  public ProgramMapper getProgramMapper() {
    return programMapper;
  }

  @Autowired
  public void setProgramMapper(ProgramMapper programMapper) {
    this.programMapper = programMapper;
  }

}
