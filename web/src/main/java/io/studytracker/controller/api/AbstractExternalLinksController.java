/*
 * Copyright 2019-2023 the original author or authors.
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
