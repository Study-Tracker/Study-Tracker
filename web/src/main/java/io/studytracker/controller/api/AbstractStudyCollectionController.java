package io.studytracker.controller.api;

import io.studytracker.events.util.StudyCollectionActivityUtils;
import io.studytracker.exception.InsufficientPrivilegesException;
import io.studytracker.exception.InvalidConstraintException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.mapper.StudyCollectionMapper;
import io.studytracker.model.Activity;
import io.studytracker.model.Study;
import io.studytracker.model.StudyCollection;
import io.studytracker.model.User;
import io.studytracker.service.StudyCollectionService;
import io.studytracker.service.StudyService;
import io.studytracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractStudyCollectionController extends AbstractApiController {

  private StudyCollectionService studyCollectionService;
  private StudyCollectionMapper studyCollectionMapper;
  private StudyService studyService;
  private UserService userService;

  /**
   * Creates a new study collection.
   *
   * @param collection Collection to be created
   * @return new study collection
   */
  protected StudyCollection createNewStudyCollection(StudyCollection collection) {

    // Make sure a collection owned by the same user does not exist already
    User user = this.getAuthenticatedUser();
    if (studyCollectionService.collectionWithNameExists(collection, user)) {
      throw new InvalidConstraintException("A study collection with this name already exists.");
    }

    // Create the collection
    studyCollectionService.create(collection);

    // Publish the event
    Activity activity =
        StudyCollectionActivityUtils.fromNewStudyCollection(collection, user);
    this.logActivity(activity);

    return collection;
  }

  /**
   * Updates an existing collection.
   *
   * @param collection collection to update
   * @return updated collection
   */
  protected StudyCollection updateExistingStudyCollection(StudyCollection collection) {

    // Make sure a collection owned by the same user does not exist already
    User user = this.getAuthenticatedUser();
    if (this.getStudyCollectionService().collectionWithNameExists(collection, user)) {
      throw new InvalidConstraintException("A study collection with this name already exists.");
    }

    this.getStudyCollectionService().update(collection);

    StudyCollection output =
        this.getStudyCollectionService()
            .findById(collection.getId())
            .orElseThrow(() -> new RecordNotFoundException(
                "Study collection not found: " + collection.getId()));

    // Publish the event
    Activity activity =
        StudyCollectionActivityUtils.fromUpdatedStudyCollection(output, user);
    this.logActivity(activity);

    return output;

  }

  /**
   * Deletes an existing collection.
   *
   * @param id ID of the collection to delete
   */
  protected void deleteStudyCollection(Long id) {

    // Make sure the collection exists
    StudyCollection collection =
        this.getStudyCollectionService()
            .findById(id)
            .orElseThrow(() -> new RecordNotFoundException("Study collection not found: " + id));

    // Make sure the collection belongs to the user
    User user = this.getAuthenticatedUser();
    if (!user.getId().equals(collection.getCreatedBy().getId())) {
      throw new InsufficientPrivilegesException("Only the study collection owner may delete it.");
    }

    // Delete the collection
    this.getStudyCollectionService().delete(collection);

    // Publish the event
    Activity activity =
        StudyCollectionActivityUtils.fromDeletedStudyCollection(collection, user);
    this.logActivity(activity);

  }

  protected void addStudyToCollection(Long collectionId, Long studyId) {

    // Get the collection
    StudyCollection collection =
        this.getStudyCollectionService()
            .findById(collectionId)
            .orElseThrow(
                () -> new RecordNotFoundException("Study collection not found: " + collectionId));

    // Get the study
    Study study =
        this.getStudyService()
            .findById(studyId)
            .orElseThrow(() -> new RecordNotFoundException("Study does not exist: " + studyId));

    // Add the study to the collection
    collection.addStudy(study);
    this.getStudyCollectionService().update(collection);

    // Publish the event
    Activity activity =
        StudyCollectionActivityUtils.fromStudyAddedToCollection(
            study, collection, this.getAuthenticatedUser());
    this.logActivity(activity);

  }

  protected void removeStudyFromCollection(Long collectionId, Long studyId) {

    // Get the collection
    StudyCollection collection =
        this.getStudyCollectionService()
            .findById(collectionId)
            .orElseThrow(
                () -> new RecordNotFoundException("Study collection not found: " + collectionId));

    // Get the study
    Study study =
        this.getStudyService()
            .findById(studyId)
            .orElseThrow(() -> new RecordNotFoundException("Study does not exist: " + studyId));

    // Remove the study from the collection
    collection.removeStudy(study);
    this.getStudyCollectionService().update(collection);

    // Publish the event
    Activity activity =
        StudyCollectionActivityUtils.fromStudyRemovedFromCollection(
            study, collection, this.getAuthenticatedUser());
    this.logActivity(activity);

  }

  public StudyCollectionService getStudyCollectionService() {
    return studyCollectionService;
  }

  @Autowired
  public void setStudyCollectionService(
      StudyCollectionService studyCollectionService) {
    this.studyCollectionService = studyCollectionService;
  }

  public StudyCollectionMapper getStudyCollectionMapper() {
    return studyCollectionMapper;
  }

  @Autowired
  public void setStudyCollectionMapper(
      StudyCollectionMapper studyCollectionMapper) {
    this.studyCollectionMapper = studyCollectionMapper;
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
}
