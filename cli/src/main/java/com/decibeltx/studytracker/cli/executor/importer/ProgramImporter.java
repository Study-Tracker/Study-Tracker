package com.decibeltx.studytracker.cli.executor.importer;

import com.decibeltx.studytracker.core.events.util.ProgramActivityUtils;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.core.service.ActivityService;
import com.decibeltx.studytracker.core.service.ProgramService;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProgramImporter extends RecordImporter<Program> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProgramImporter.class);

  private User createdBy;

  @Autowired
  private ProgramService programService;

  @Autowired
  private ActivityService activityService;

  public ProgramImporter() {
    super(Program.class);
  }

  public void importRecords(Collection<Program> records, User createdBy) throws Exception {
    this.createdBy = createdBy;
    this.importRecords(records);
  }

  @Override
  void importRecord(Program program) throws Exception {
    if (programService.findByName(program.getName()).isPresent()) {
      LOGGER.warn(String.format("A program with this name %s already exists. Skipping record.",
          program.getName()));
    } else {
      program.setCreatedBy(createdBy);
      program.setLastModifiedBy(createdBy);
      this.validate(program);
      programService.create(program);
      activityService.create(ProgramActivityUtils.fromNewProgram(program, createdBy));
    }
  }
}
