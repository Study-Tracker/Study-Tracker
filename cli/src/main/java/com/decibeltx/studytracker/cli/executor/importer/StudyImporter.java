package com.decibeltx.studytracker.cli.executor.importer;

import com.decibeltx.studytracker.core.events.util.StudyActivityUtils;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.core.service.ActivityService;
import com.decibeltx.studytracker.core.service.StudyService;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudyImporter extends RecordImporter<Study> {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyImporter.class);

  private User createdBy;

  @Autowired
  private StudyService studyService;

  @Autowired
  private ActivityService activityService;

//  @Autowired
//  private ProgramService programService;
//
//  @Autowired
//  private UserService userService;
//
//  @Autowired
//  private CollaboratorService collaboratorService;
//
//  @Autowired
//  private KeywordService keywordService;

  public StudyImporter() {
    super(Study.class);
  }

  public void importRecords(Collection<Study> records, User createdBy) throws Exception {
    this.createdBy = createdBy;
    this.importRecords(records);
  }

  @Override
  void importRecord(Study study) throws Exception {
    if (study.getCode() != null && studyService.findByCode(study.getCode()).isPresent()) {
      LOGGER.warn(String.format("A study with code '%s' already exists. Skipping this record.",
          study.getCode()));
    } else {
      study.setCreatedBy(createdBy);
      this.validate(study);
      studyService.create(study);
      activityService.create(StudyActivityUtils.fromNewStudy(study, createdBy));
    }
  }

}
