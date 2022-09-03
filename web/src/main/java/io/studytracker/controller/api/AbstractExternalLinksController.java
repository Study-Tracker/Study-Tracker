package io.studytracker.controller.api;

import io.studytracker.events.util.StudyActivityUtils;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.mapper.ExternalLinkMapper;
import io.studytracker.model.Activity;
import io.studytracker.model.ExternalLink;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import io.studytracker.service.StudyExternalLinkService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractExternalLinksController extends AbstractStudyController {

  private StudyExternalLinkService studyExternalLinkService;

  private ExternalLinkMapper externalLinkMapper;

  /**
   * Creates a new external link for the given study.
   *
   * @param study the study to create the external link for
   * @param externalLink the external link to create
   * @return the created external link
   */
  protected ExternalLink createNewExternalLink(Study study, ExternalLink externalLink) {
    ExternalLink created = this.getStudyExternalLinkService().addStudyExternalLink(study, externalLink);
    Activity activity =
        StudyActivityUtils.fromNewExternalLink(study, this.getAuthenticatedUser(), created);
    this.logActivity(activity);
    return created;
  }

  /**
   * Updates the given external link.
   *
   * @param study the study to update the external link for
   * @param externalLink the external link to update
   * @return the updated external link
   */
  protected ExternalLink updateExistingExternalLink(Study study, ExternalLink externalLink) {
    ExternalLink updated = this.getStudyExternalLinkService().updateStudyExternalLink(study, externalLink);
    User user = this.getAuthenticatedUser();
    this.getStudyService().markAsUpdated(study, user);
    Activity activity =
        StudyActivityUtils.fromUpdatedExternalLink(study, user, updated);
    this.logActivity(activity);
    return updated;
  }

  /**
   * Deletes an external link from the given study.
   *
   * @param linkId the ID of the external link to delete
   */
  protected void deleteExternalLink(Long linkId) {
    ExternalLink externalLink = this.getStudyExternalLinkService().findById(linkId).
        orElseThrow(() -> new RecordNotFoundException("Cannot find external link with ID: " + linkId));
    Study study = this.getStudyService().findById(externalLink.getStudy().getId()).
        orElseThrow(() -> new RecordNotFoundException("Cannot find study with ID: " + externalLink.getStudy().getId()));
    User user = this.getAuthenticatedUser();
    study.setLastModifiedBy(user);
    this.getStudyExternalLinkService().deleteStudyExternalLink(study, linkId);
    Activity activity = StudyActivityUtils.fromDeletedExternalLink(study, user);
    this.logActivity(activity);
  }

  public StudyExternalLinkService getStudyExternalLinkService() {
    return studyExternalLinkService;
  }

  @Autowired
  public void setStudyExternalLinkService(
      StudyExternalLinkService studyExternalLinkService) {
    this.studyExternalLinkService = studyExternalLinkService;
  }

  public ExternalLinkMapper getExternalLinkMapper() {
    return externalLinkMapper;
  }

  @Autowired
  public void setExternalLinkMapper(ExternalLinkMapper externalLinkMapper) {
    this.externalLinkMapper = externalLinkMapper;
  }

}
