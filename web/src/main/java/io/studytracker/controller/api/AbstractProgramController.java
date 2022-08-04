package io.studytracker.controller.api;

import io.studytracker.events.util.ProgramActivityUtils;
import io.studytracker.exception.InsufficientPrivilegesException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.mapper.ProgramMapper;
import io.studytracker.model.Activity;
import io.studytracker.model.Program;
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

    // Make sure the user has the necessary privileges to create a new program
    User user = this.getAuthenticatedUser();
    if (!user.isAdmin()) {
      throw new InsufficientPrivilegesException(
          "You do not have permission to perform this action.");
    }

    // Create the new program and publish the event
    this.getProgramService().create(program);
    Activity activity = ProgramActivityUtils.fromNewProgram(program, user);
    this.logActivity(activity);

    return program;
  }

  /**
   * Updates an existing program.
   *
   * @param program the program to update
   * @return the updated program
   */
  public Program updateExistingProgram(Program program) {

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

    program = this.getProgramService().update(program);
    this.logActivity(ProgramActivityUtils.fromUpdatedProgram(program, user));

    return program;
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
