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
    collaborator = this.getCollaboratorService().update(collaborator);
    this.logActivity(CollaboratorActivityUtils
        .fromUpdatedCollaborator(collaborator, this.getAuthenticatedUser()));
    return collaborator;
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
