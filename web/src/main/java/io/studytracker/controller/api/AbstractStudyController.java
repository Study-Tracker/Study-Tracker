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

import io.studytracker.eln.NotebookTemplate;
import io.studytracker.eln.StudyNotebookService;
import io.studytracker.events.util.StudyActivityUtils;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.mapper.ActivityMapper;
import io.studytracker.mapstruct.mapper.AssayMapper;
import io.studytracker.mapstruct.mapper.StudyMapper;
import io.studytracker.model.Activity;
import io.studytracker.model.Assay;
import io.studytracker.model.Status;
import io.studytracker.model.Study;
import io.studytracker.service.AssayService;
import io.studytracker.service.CollaboratorService;
import io.studytracker.service.KeywordService;
import io.studytracker.service.ProgramService;
import io.studytracker.service.StudyService;
import io.studytracker.service.UserService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public abstract class AbstractStudyController extends AbstractApiController {

  private StudyService studyService;

  private UserService userService;

  private ProgramService programService;

  private AssayService assayService;

  private StudyNotebookService notebookService;

  private StudyMapper studyMapper;

  private AssayMapper assayMapper;

  private ActivityMapper activityMapper;

  private CollaboratorService collaboratorService;

  private KeywordService keywordService;

  private boolean isLong(String value) {
    try {
      Long.parseLong(value);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  protected Study getStudyFromIdentifier(String id) {
    Optional<Study> optional;
    if (isLong(id)) {
      optional = studyService.findById(Long.parseLong(id));
    } else {
      optional = studyService.findByCode(id);
    }
    if (optional.isPresent()) {
      return optional.get();
    } else {
      throw new RecordNotFoundException("Cannot find study: " + id);
    }
  }

  protected Assay getAssayFromIdentifier(String id) {
    Optional<Assay> optional;
    if (isLong(id)) {
      optional = assayService.findById(Long.parseLong(id));
    } else {
      optional = assayService.findByCode(id);
    }
    if (optional.isPresent()) {
      return optional.get();
    } else {
      throw new RecordNotFoundException("Cannot find assay: " + id);
    }
  }

  /**
   * Creates a new study with notebook and storage folders, where appropriate.
   * @param study the study to create
   * @param notebookTemplateId the notebook template to use, or null to use the default
   * @return
   */
  protected Study createNewStudy(Study study, String notebookTemplateId) {

    // If a notebook template was requested, find it
    if (notebookService != null && StringUtils.hasText(notebookTemplateId)) {
      Optional<NotebookTemplate> templateOptional =
          notebookService.findEntryTemplateById(notebookTemplateId);
      if (templateOptional.isPresent()) {
        getStudyService().create(study, templateOptional.get());
      } else {
        throw new RecordNotFoundException(
            "Could not find notebook entry template: " + notebookTemplateId);
      }
    } else {
      studyService.create(study);
    }

    Assert.notNull(study.getId(), "Study not persisted.");

    // Publish events
    Activity activity = StudyActivityUtils.fromNewStudy(study, this.getAuthenticatedUser());
    this.logActivity(activity);

    return study;
  }

  /**
   * Updates an existing study.
   *
   * @param study the study to update
   * @return the updated study
   */
  protected Study updateExistingStudy(Study study) {
    studyService.update(study);
    Study updated = studyService.findById(study.getId())
        .orElseThrow(() -> new RecordNotFoundException("Study not found: " + study.getId()));
    Activity activity = StudyActivityUtils.fromUpdatedStudy(updated, this.getAuthenticatedUser());
    this.logActivity(activity);
    return updated;
  }

  /**
   * Innactivates an existing study.
   *
   * @param study the study to inactivate
   */
  protected void deleteExistingStudy(Study study) {
    studyService.delete(study);
    Activity activity = StudyActivityUtils.fromDeletedStudy(study, this.getAuthenticatedUser());
    this.logActivity(activity);
  }

  protected void updateExistingStudyStatus(Study study, Status status) {
    Status oldStatus = study.getStatus();
    studyService.updateStatus(study, status);
    Activity activity = StudyActivityUtils.fromStudyStatusChange(study, this.getAuthenticatedUser(), oldStatus, status);
    this.logActivity(activity);
  }

  protected void updateExistingStudyStatus(Study study, String statusString) {
    Status status = Status.valueOf(statusString);
    this.updateExistingStudyStatus(study, status);
  }

  public StudyService getStudyService() {
    return studyService;
  }

  @Autowired
  public void setStudyService(StudyService studyService) {
    this.studyService = studyService;
  }

  public UserService getUserService() {
    return userService;
  }

  @Autowired
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  public ProgramService getProgramService() {
    return programService;
  }

  @Autowired
  public void setProgramService(ProgramService programService) {
    this.programService = programService;
  }

  public AssayService getAssayService() {
    return assayService;
  }

  @Autowired
  public void setAssayService(AssayService assayService) {
    this.assayService = assayService;
  }

  public StudyMapper getStudyMapper() {
    return studyMapper;
  }

  @Autowired
  public void setStudyMapper(StudyMapper studyMapper) {
    this.studyMapper = studyMapper;
  }

  public AssayMapper getAssayMapper() {
    return assayMapper;
  }

  @Autowired
  public void setAssayMapper(AssayMapper assayMapper) {
    this.assayMapper = assayMapper;
  }

  public ActivityMapper getActivityMapper() {
    return activityMapper;
  }

  @Autowired
  public void setActivityMapper(ActivityMapper activityMapper) {
    this.activityMapper = activityMapper;
  }

  public StudyNotebookService getNotebookService() {
    return notebookService;
  }

  @Autowired(required = false)
  public void setNotebookService(StudyNotebookService notebookService) {
    this.notebookService = notebookService;
  }

  public CollaboratorService getCollaboratorService() {
    return collaboratorService;
  }

  @Autowired
  public void setCollaboratorService(CollaboratorService collaboratorService) {
    this.collaboratorService = collaboratorService;
  }

  public KeywordService getKeywordService() {
    return keywordService;
  }

  @Autowired
  public void setKeywordService(KeywordService keywordService) {
    this.keywordService = keywordService;
  }
}
