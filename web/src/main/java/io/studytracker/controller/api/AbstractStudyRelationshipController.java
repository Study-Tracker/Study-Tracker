package io.studytracker.controller.api;

import io.studytracker.events.util.StudyActivityUtils;
import io.studytracker.mapstruct.mapper.StudyRelationshipMapper;
import io.studytracker.model.Activity;
import io.studytracker.model.RelationshipType;
import io.studytracker.model.Study;
import io.studytracker.model.StudyRelationship;
import io.studytracker.model.User;
import io.studytracker.service.StudyRelationshipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractStudyRelationshipController extends AbstractStudyController{

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStudyRelationshipController.class);

  private StudyRelationshipService studyRelationshipService;

  private StudyRelationshipMapper studyRelationshipMapper;

  /**
   * Create a new study relationship.
   * @param sourceStudy The study that is the source of the relationship.
   * @param targetStudy The study that is the target of the relationship.
   * @param relationshipType The type of the relationship.
   * @return The created study relationship.
   */
  protected StudyRelationship createNewStudyRelationship(
      Study sourceStudy,
      Study targetStudy,
      RelationshipType relationshipType
  ) {

    // Create the relationship
    StudyRelationship relationship = studyRelationshipService.addStudyRelationship(sourceStudy, targetStudy, relationshipType);
    LOGGER.debug("New study relationship source: {}  target: {}  type: {}",
        relationship.getSourceStudy().getId(), relationship.getTargetStudy().getId(), relationship.getType().toString());
    User user = this.getAuthenticatedUser();

    // Mark studies as updated
    this.getStudyService().markAsUpdated(sourceStudy, user);
    this.getStudyService().markAsUpdated(targetStudy, user);

    // Create activity
    Activity activity = StudyActivityUtils.fromNewStudyRelationship(sourceStudy, targetStudy, user, relationship);
    this.logActivity(activity);
    StudyRelationship inverseRelationship =
        new StudyRelationship(RelationshipType.getInverse(relationshipType), targetStudy, sourceStudy);
    activity = StudyActivityUtils.fromNewStudyRelationship(targetStudy, sourceStudy, user, inverseRelationship);
    this.logActivity(activity);

    return relationship;
  }

  protected void deleteStudyRelationship(Study sourceStudy, Study targetStudy) {

    User user = this.getAuthenticatedUser();

    // Delete the relationships
    studyRelationshipService.removeStudyRelationship(sourceStudy, targetStudy);

    // Mark studies as updated
    this.getStudyService().markAsUpdated(sourceStudy, user);
    this.getStudyService().markAsUpdated(targetStudy, user);

    // Publish the activity
    Activity sourceActivity = StudyActivityUtils.fromDeletedStudyRelationship(sourceStudy, user);
    this.logActivity(sourceActivity);
    Activity targetActivity = StudyActivityUtils.fromDeletedStudyRelationship(targetStudy, user);
    this.logActivity(targetActivity);

  }

  public StudyRelationshipService getStudyRelationshipService() {
    return studyRelationshipService;
  }

  @Autowired
  public void setStudyRelationshipService(
      StudyRelationshipService studyRelationshipService) {
    this.studyRelationshipService = studyRelationshipService;
  }

  public StudyRelationshipMapper getStudyRelationshipMapper() {
    return studyRelationshipMapper;
  }

  @Autowired
  public void setStudyRelationshipMapper(
      StudyRelationshipMapper studyRelationshipMapper) {
    this.studyRelationshipMapper = studyRelationshipMapper;
  }
}
