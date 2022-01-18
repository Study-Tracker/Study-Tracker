package com.decibeltx.studytracker.controller.api;

import com.decibeltx.studytracker.eln.NotebookTemplate;
import com.decibeltx.studytracker.eln.StudyNotebookService;
import com.decibeltx.studytracker.events.EventsService;
import com.decibeltx.studytracker.events.util.AssayActivityUtils;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.Activity;
import com.decibeltx.studytracker.model.Assay;
import com.decibeltx.studytracker.model.Status;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.service.ActivityService;
import com.decibeltx.studytracker.service.AssayService;
import com.decibeltx.studytracker.service.AssayTypeService;
import com.decibeltx.studytracker.service.StudyService;
import com.decibeltx.studytracker.service.UserService;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public abstract class AbstractAssayController {

  private AssayService assayService;

  private StudyService studyService;

  private AssayTypeService assayTypeService;

  private UserService userService;

  private EventsService eventsService;

  private ActivityService activityService;

  private StudyNotebookService studyNotebookService;

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
      team.add(userService.findById(u.getId())
          .orElseThrow(() -> new RecordNotFoundException("Cannot find user: " + user.getId())));
    }
    assay.setUsers(team);

    // Owner
    assay.setOwner(userService.findById(assay.getOwner().getId())
        .orElseThrow(() -> new RecordNotFoundException("Cannot find user: " + user.getId())));

    // Create the record
    assayService.create(assay, template);
    Assert.notNull(assay.getId(), "Assay not persisted.");

    // Update the study
    studyService.markAsUpdated(study, user);

    // Add activity record and dispatch event
    Activity activity = AssayActivityUtils.fromNewAssay(assay, user);
    activityService.create(activity);
    eventsService.dispatchEvent(activity);

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
      team.add(userService.findById(u.getId())
          .orElseThrow(() -> new RecordNotFoundException("Cannot find user: " + user.getId())));
    }
    assay.setUsers(team);

    // Owner
    assay.setOwner(userService.findById(assay.getOwner().getId())
        .orElseThrow(() -> new RecordNotFoundException("Cannot find user: " + user.getId())));

    assayService.update(assay);

    Activity activity = AssayActivityUtils.fromUpdatedAssay(assay, user);
    activityService.create(activity);
    eventsService.dispatchEvent(activity);

    return assay;

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
    activityService.create(activity);
    eventsService.dispatchEvent(activity);

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
    activityService.create(activity);
    eventsService.dispatchEvent(activity);

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
  public void setAssayTypeService(
      AssayTypeService assayTypeService) {
    this.assayTypeService = assayTypeService;
  }

  public UserService getUserService() {
    return userService;
  }

  @Autowired
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  public EventsService getEventsService() {
    return eventsService;
  }

  @Autowired
  public void setEventsService(EventsService eventsService) {
    this.eventsService = eventsService;
  }

  public ActivityService getActivityService() {
    return activityService;
  }

  @Autowired
  public void setActivityService(ActivityService activityService) {
    this.activityService = activityService;
  }

  public StudyNotebookService getStudyNotebookService() {
    return studyNotebookService;
  }

  @Autowired(required = false)
  public void setStudyNotebookService(
      StudyNotebookService studyNotebookService) {
    this.studyNotebookService = studyNotebookService;
  }
}
