package com.decibeltx.studytracker.controller.api;

import com.decibeltx.studytracker.eln.NotebookFolder;
import com.decibeltx.studytracker.eln.StudyNotebookService;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.Study;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/study/{studyId}/notebook")
public class StudyNotebookController extends AbstractStudyController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyNotebookController.class);

  @Autowired(required = false)
  private StudyNotebookService studyNotebookService;

  @GetMapping("")
  public NotebookFolder getStudyNotebookFolder(@PathVariable("studyId") String studyId)
          throws RecordNotFoundException {
    LOGGER.info("Fetching notebook folder for study: " + studyId);
    Study study = getStudyFromIdentifier(studyId);

    Optional<NotebookFolder> notebookFolder = Optional.ofNullable(studyNotebookService)
            .flatMap(service -> service.findStudyFolder(study));
    return notebookFolder
            .orElseThrow(() -> new RecordNotFoundException("Could not load notebook folder"));
  }
}