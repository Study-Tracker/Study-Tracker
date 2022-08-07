package io.studytracker.controller.api.internal;

import io.studytracker.controller.api.AbstractStudyController;
import io.studytracker.eln.NotebookFolder;
import io.studytracker.eln.NotebookFolderService;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.Study;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/study/{studyId}/notebook")
public class StudyNotebookController extends AbstractStudyController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyNotebookController.class);

  @Autowired(required = false)
  private NotebookFolderService notebookFolderService;

  @GetMapping("")
  public NotebookFolder getStudyNotebookFolder(@PathVariable("studyId") String studyId)
      throws RecordNotFoundException {
    LOGGER.info("Fetching notebook folder for study: " + studyId);
    Study study = getStudyFromIdentifier(studyId);

    Optional<NotebookFolder> notebookFolder =
        Optional.ofNullable(notebookFolderService)
            .flatMap(service -> service.findStudyFolder(study));
    return notebookFolder.orElseThrow(
        () -> new RecordNotFoundException("Could not load notebook folder"));
  }

  @PostMapping("/{id}/notebook")
  public HttpEntity<?> repairNotebookFolder(@PathVariable("id") Long studyId) {

    // Check that the study exists
    Optional<Study> optional = this.getStudyService().findById(studyId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException("Study not found: " + studyId);
    }
    Study study = optional.get();

    // Repair the folder
    this.getStudyService().repairElnFolder(study);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
