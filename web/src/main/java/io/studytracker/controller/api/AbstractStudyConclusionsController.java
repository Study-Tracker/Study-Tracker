package io.studytracker.controller.api;

import io.studytracker.events.util.StudyActivityUtils;
import io.studytracker.mapstruct.mapper.StudyConclusionsMapper;
import io.studytracker.model.Activity;
import io.studytracker.model.Study;
import io.studytracker.model.StudyConclusions;
import io.studytracker.service.StudyConclusionsService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractStudyConclusionsController extends AbstractStudyController{

  private StudyConclusionsService studyConclusionsService;

  private StudyConclusionsMapper conclusionsMapper;

  /**
   * Creates a new Study Conclusions record and associates it with the given study.
   *
   * @param study The study to associate the conclusions with.
   * @param conclusions The conclusions to create.
   * @return The created Study Conclusions record.
   */
  protected StudyConclusions createNewConclusions(Study study, StudyConclusions conclusions) {
    StudyConclusions created = this.getStudyConclusionsService().addStudyConclusions(study, conclusions);
    Activity activity =
        StudyActivityUtils.fromNewConclusions(study, this.getAuthenticatedUser(), created);
    this.logActivity(activity);
    return created;
  }

  /**
   * Updates the given Study Conclusions record.
   *
   * @param study The study to associate the conclusions with.
   * @param conclusions The conclusions to update.
   * @return The updated Study Conclusions record.
   */
  protected StudyConclusions updateExistingConclusions(Study study, StudyConclusions conclusions) {
    StudyConclusions updated = this.getStudyConclusionsService().updateStudyConclusions(study, conclusions);
    Activity activity =
        StudyActivityUtils.fromUpdatedConclusions(study, this.getAuthenticatedUser(), updated);
    this.logActivity(activity);
    return updated;
  }

  /**
   * Removes conclusions from the provided study.
   *
   * @param study
   */
  protected void deleteStudyConclusions(Study study) {
    this.getStudyConclusionsService().deleteStudyConclusions(study);
    Activity activity = StudyActivityUtils.fromDeletedConclusions(study, this.getAuthenticatedUser());
    this.logActivity(activity);
  }

  public StudyConclusionsService getStudyConclusionsService() {
    return studyConclusionsService;
  }

  @Autowired
  public void setStudyConclusionsService(
      StudyConclusionsService studyConclusionsService) {
    this.studyConclusionsService = studyConclusionsService;
  }

  public StudyConclusionsMapper getConclusionsMapper() {
    return conclusionsMapper;
  }

  @Autowired
  public void setConclusionsMapper(
      StudyConclusionsMapper conclusionsMapper) {
    this.conclusionsMapper = conclusionsMapper;
  }
}
