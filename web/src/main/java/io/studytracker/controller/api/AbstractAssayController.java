/*
 * Copyright 2022 the original author or authors.
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

import io.studytracker.eln.NotebookEntryService;
import io.studytracker.eln.NotebookTemplate;
import io.studytracker.events.util.AssayActivityUtils;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.mapper.AssayMapper;
import io.studytracker.mapstruct.mapper.AssayTaskMapper;
import io.studytracker.mapstruct.mapper.AssayTypeMapper;
import io.studytracker.model.Activity;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayTask;
import io.studytracker.model.Status;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import io.studytracker.service.AssayService;
import io.studytracker.service.AssayTaskService;
import io.studytracker.service.AssayTypeService;
import io.studytracker.service.StudyService;
import io.studytracker.service.UserService;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public abstract class AbstractAssayController extends AbstractApiController {

  private AssayService assayService;

  private StudyService studyService;

  private AssayTypeService assayTypeService;

  private AssayTaskService assayTaskService;

  private UserService userService;

  private AssayMapper assayMapper;

  private AssayTypeMapper assayTypeMapper;

  private AssayTaskMapper assayTaskMapper;

  private NotebookEntryService notebookEntryService;

  private boolean isLong(String value) {
    try {
      Long.parseLong(value);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /**
   * Looks up an {@link Assay} given an ID or code.
   *
   * @param id
   * @return
   */
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
      throw new RecordNotFoundException();
    }
  }

  /**
   * Looks up an {@link Assay} given an ID or code.
   *
   * @param id
   * @return
   */
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
      throw new RecordNotFoundException();
    }
  }

  /**
   * Writes a new {@link Assay} record to the database, given an input object and user.
   *
   * @param assay
   * @param study
   * @param user
   * @return
   */
  protected Assay createAssay(Assay assay, Study study, User user, NotebookTemplate template) {

    assay.setStudy(study);

    // Assay team
    Set<User> team = new HashSet<>();
    for (User u : assay.getUsers()) {
      team.add(
          userService
              .findById(u.getId())
              .orElseThrow(() -> new RecordNotFoundException("Cannot find user: " + user.getId())));
    }
    assay.setUsers(team);

    // Owner
    assay.setOwner(
        userService
            .findById(assay.getOwner().getId())
            .orElseThrow(() -> new RecordNotFoundException("Cannot find user: " + user.getId())));

    // Create the record
    assayService.create(assay, template);
    Assert.notNull(assay.getId(), "Assay not persisted.");

    // Update the study
    studyService.markAsUpdated(study, user);

    // Add activity record and dispatch event
    Activity activity = AssayActivityUtils.fromNewAssay(assay, user);
    this.logActivity(activity);

    return assay;
  }

  protected Assay createAssay(Assay assay, Study study, User user) {
    return this.createAssay(assay, study, user, null);
  }

  /**
   * Updates an existing {@link Assay} record, given an input object and {@link User}.
   *
   * @param assay
   * @param user
   * @return
   */
  protected Assay updateAssay(Assay assay, User user) {

    // Assay team
    Set<User> team = new HashSet<>();
    for (User u : assay.getUsers()) {
      team.add(
          userService
              .findById(u.getId())
              .orElseThrow(() -> new RecordNotFoundException("Cannot find user: " + user.getId())));
    }
    assay.setUsers(team);

    // Owner
    assay.setOwner(
        userService
            .findById(assay.getOwner().getId())
            .orElseThrow(() -> new RecordNotFoundException("Cannot find user: " + user.getId())));

    Assay updated = assayService.update(assay);

    Activity activity = AssayActivityUtils.fromUpdatedAssay(updated, user);
    this.logActivity(activity);

    return updated;
  }

  /**
   * Deletes an {@link Assay} record (or inactivates it).
   *
   * @param id
   * @param user
   */
  protected void deleteAssay(String id, User user) {

    Assay assay = this.getAssayFromIdentifier(id);
    assayService.delete(assay);

    Activity activity = AssayActivityUtils.fromDeletedAssay(assay, user);
    this.logActivity(activity);
  }

  /**
   * Updates the {@link Status} of the given {@link Assay}.
   *
   * @param assayId
   * @param status
   * @param user
   */
  protected void updateAssayStatus(Long assayId, Status status, User user) {

    Assay assay = assayService.findById(assayId).orElseThrow(RecordNotFoundException::new);
    assay.setLastModifiedBy(user);

    Status oldStatus = assay.getStatus();
    assayService.updateStatus(assay, status);

    Activity activity = AssayActivityUtils.fromChangedAssayStatus(assay, user, oldStatus, status);
    this.logActivity(activity);
  }

  /**
   * Create a new assay task associated with the given assay
   *
   * @param assayTask
   * @param assay
   * @return
   */
  protected AssayTask addNewAssayTask(AssayTask assayTask, Assay assay) {
    AssayTask created = this.getAssayTaskService().addAssayTask(assayTask, assay);
    Activity activity = AssayActivityUtils.fromTaskAdded(assay, this.getAuthenticatedUser(), created);
    this.logActivity(activity);
    return created;
  }

  /**
   * Updates an existing assay task.
   *
   * @param assayTask
   * @param assay
   * @return
   */
  protected AssayTask updateExistingAssayTask(AssayTask assayTask, Assay assay) {
    User user = this.getAuthenticatedUser();
    assay.setLastModifiedBy(user);
    AssayTask updated = this.getAssayTaskService().updateAssayTask(assayTask, assay);
    Activity activity = AssayActivityUtils.fromAssayTaskUpdate(assay, user, updated);
    this.logActivity(activity);
    return updated;
  }

  protected void deleteAssayTask(AssayTask assayTask, Assay assay) {
    User user = this.getAuthenticatedUser();
    assay.setLastModifiedBy(user);
    this.getAssayTaskService().deleteAssayTask(assayTask, assay);
    Activity activity = AssayActivityUtils.fromTaskDeleted(assay, user, assayTask);
    this.logActivity(activity);
  }

  /* Getters and Setters */

  public StudyService getStudyService() {
    return studyService;
  }

  @Autowired
  public void setStudyService(StudyService studyService) {
    this.studyService = studyService;
  }

  public AssayService getAssayService() {
    return assayService;
  }

  @Autowired
  public void setAssayService(AssayService assayService) {
    this.assayService = assayService;
  }

  public AssayTypeService getAssayTypeService() {
    return assayTypeService;
  }

  @Autowired
  public void setAssayTypeService(AssayTypeService assayTypeService) {
    this.assayTypeService = assayTypeService;
  }

  public UserService getUserService() {
    return userService;
  }

  @Autowired
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  public AssayTaskService getAssayTaskService() {
    return assayTaskService;
  }

  @Autowired
  public void setAssayTaskService(AssayTaskService assayTaskService) {
    this.assayTaskService = assayTaskService;
  }

  public AssayMapper getAssayMapper() {
    return assayMapper;
  }

  @Autowired
  public void setAssayMapper(AssayMapper assayMapper) {
    this.assayMapper = assayMapper;
  }

  public AssayTypeMapper getAssayTypeMapper() {
    return assayTypeMapper;
  }

  @Autowired
  public void setAssayTypeMapper(AssayTypeMapper assayTypeMapper) {
    this.assayTypeMapper = assayTypeMapper;
  }

  public AssayTaskMapper getAssayTaskMapper() {
    return assayTaskMapper;
  }

  @Autowired
  public void setAssayTaskMapper(AssayTaskMapper assayTaskMapper) {
    this.assayTaskMapper = assayTaskMapper;
  }

  public NotebookEntryService getNotebookEntryService() {
    return notebookEntryService;
  }

  @Autowired(required = false)
  public void setNotebookEntryService(NotebookEntryService notebookEntryService) {
    this.notebookEntryService = notebookEntryService;
  }
}
