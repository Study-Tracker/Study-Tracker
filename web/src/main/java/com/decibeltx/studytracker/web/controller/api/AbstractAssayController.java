package com.decibeltx.studytracker.web.controller.api;

import com.decibeltx.studytracker.core.events.util.AssayActivityUtils;
import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.Activity;
import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.Status;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.core.service.ActivityService;
import com.decibeltx.studytracker.core.service.AssayService;
import com.decibeltx.studytracker.core.service.AssayTypeService;
import com.decibeltx.studytracker.core.service.EventsService;
import com.decibeltx.studytracker.core.service.StudyService;
import com.decibeltx.studytracker.core.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public abstract class AbstractAssayController {

  private AssayService assayService;

  private StudyService studyService;

  private AssayTypeService assayTypeService;

  private UserService userService;

  private EventsService eventsService;

  private ActivityService activityService;

  /**
   * Looks up an {@link Assay} given an ID or code.
   *
   * @param id
   * @return
   */
  protected Assay getAssayFromIdentifier(String id) {
    Assay assay;
    Optional<Assay> optional = assayService.findById(id);
    if (optional.isPresent()) {
      assay = optional.get();
    } else {
      optional = assayService.findByCode(id);
      if (optional.isPresent()) {
        assay = optional.get();
      } else {
        throw new RecordNotFoundException("Cannot identify assay: " + id);
      }
    }
    return assay;
  }

  /**
   * Looks up an {@link Assay} given an ID or code.
   *
   * @param id
   * @return
   */
  protected Study getStudyFromIdentifier(String id) {
    Study study;
    Optional<Study> optional = studyService.findById(id);
    if (optional.isPresent()) {
      study = optional.get();
    } else {
      optional = studyService.findByCode(id);
      if (optional.isPresent()) {
        study = optional.get();
      } else {
        throw new RecordNotFoundException("Cannot identify study: " + id);
      }
    }
    return study;
  }

  /**
   * Writes a new {@link Assay} record to the database, given an input object and user.
   *
   * @param assay
   * @param study
   * @param user
   * @return
   */
  protected Assay createAssay(Assay assay, Study study, User user) {

    assay.setCreatedBy(user);
    assay.setStudy(study);

    // Assay team
    List<User> team = new ArrayList<>();
    for (User u : assay.getUsers()) {
      team.add(userService.findByUsername(u.getUsername())
          .orElseThrow(RecordNotFoundException::new));
    }
    assay.setUsers(team);

    // Owner
    assay.setOwner(userService.findByUsername(assay.getOwner().getUsername())
        .orElseThrow(RecordNotFoundException::new));

    assayService.create(assay);
    Assert.notNull(assay.getId(), "Assay not persisted.");

    study.getAssays().add(assay);
    studyService.update(study);

    Activity activity = AssayActivityUtils.fromNewAssay(assay, user);
    activityService.create(activity);
    eventsService.dispatchEvent(activity);

    return assay;

  }

  /**
   * Updates an existing {@link Assay} record, given an input object and {@link User}.
   *
   * @param assay
   * @param user
   * @return
   */
  protected Assay updateAssay(Assay assay, User user) {

    assay.setLastModifiedBy(user);

    // Assay team
    List<User> team = new ArrayList<>();
    for (User u : assay.getUsers()) {
      team.add(userService.findByUsername(u.getUsername())
          .orElseThrow(RecordNotFoundException::new));
    }
    assay.setUsers(team);

    // Owner
    assay.setOwner(userService.findByUsername(assay.getOwner().getUsername())
        .orElseThrow(RecordNotFoundException::new));

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
    assay.setLastModifiedBy(user);
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
  protected void updateAssayStatus(String assayId, Status status, User user) {

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
}
