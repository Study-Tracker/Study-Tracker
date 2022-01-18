package com.decibeltx.studytracker.controller.api;

import com.decibeltx.studytracker.eln.NotebookTemplate;
import com.decibeltx.studytracker.eln.StudyNotebookService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/eln")
public class NotebookController {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotebookController.class);

  @Autowired(required = false)
  private StudyNotebookService notebookService;

  @GetMapping("/entrytemplate")
  public HttpEntity<List<NotebookTemplate>> findNotebookEntryTemplates() {
    if (notebookService == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    List<NotebookTemplate> templates = notebookService.findEntryTemplates();
    LOGGER.info(templates.toString());
    return new ResponseEntity<>(templates, HttpStatus.OK);
  }

  @GetMapping("/entrytemplate/{id}")
  public HttpEntity<NotebookTemplate> findNotebookEntryTemplateById(@PathVariable String id) {
    Optional<NotebookTemplate> optional = notebookService.findEntryTemplateById(id);
    if (optional.isPresent()) {
      return new ResponseEntity<>(optional.get(), HttpStatus.OK);
    } else {
      LOGGER.warn("Could not find notebook entry template with id: " + id);
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

}
