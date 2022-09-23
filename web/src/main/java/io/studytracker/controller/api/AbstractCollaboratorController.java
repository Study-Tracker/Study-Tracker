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

import io.studytracker.events.util.CollaboratorActivityUtils;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.mapper.CollaboratorMapper;
import io.studytracker.model.Collaborator;
import io.studytracker.service.CollaboratorService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractCollaboratorController extends AbstractApiController {

  private CollaboratorService collaboratorService;

  private CollaboratorMapper collaboratorMapper;

  /**
   * Registers a new external collaborator.
   *
   * @param collaborator collaborator to register
   * @return created collaborator record
   */
  protected Collaborator createNewCollaborator(Collaborator collaborator) {
    this.getCollaboratorService().create(collaborator);
    this.logActivity(CollaboratorActivityUtils
        .fromNewCollaborator(collaborator, this.getAuthenticatedUser()));
    return collaborator;
  }

  /**
   * Updates an existing collaborator record.
   * @param collaborator collaborator to update
   * @param id collaborator ID
   * @return updated record
   */
  protected Collaborator updateExistingCollaborator(Collaborator collaborator, Long id) {
    if (!this.getCollaboratorService().exists(id)) {
      throw new RecordNotFoundException("Collaborator does not exist: " + id);
    }
    Collaborator updated = this.getCollaboratorService().update(collaborator);
    this.logActivity(CollaboratorActivityUtils
        .fromUpdatedCollaborator(updated, this.getAuthenticatedUser()));
    return updated;
  }

  /**
   * Removes the provided collaborator record, identified by its primary key ID.
   *
   * @param id collaborator ID
   */
  protected void deleteCollaborator(Long id) {
    Collaborator collaborator = this.getCollaboratorService().findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Cannot find collaborator: " + id));
    this.getCollaboratorService().delete(id);
    this.logActivity(CollaboratorActivityUtils
        .fromDeletedCollaborator(collaborator, this.getAuthenticatedUser()));
  }

  public CollaboratorService getCollaboratorService() {
    return collaboratorService;
  }

  @Autowired
  public void setCollaboratorService(CollaboratorService collaboratorService) {
    this.collaboratorService = collaboratorService;
  }

  public CollaboratorMapper getCollaboratorMapper() {
    return collaboratorMapper;
  }

  @Autowired
  public void setCollaboratorMapper(CollaboratorMapper collaboratorMapper) {
    this.collaboratorMapper = collaboratorMapper;
  }
}
