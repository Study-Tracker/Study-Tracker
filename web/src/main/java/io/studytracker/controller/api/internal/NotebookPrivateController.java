package io.studytracker.controller.api.internal;

import io.studytracker.eln.NotebookEntryService;
import io.studytracker.eln.NotebookTemplate;
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
@RequestMapping("/api/internal/eln")
public class NotebookPrivateController {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotebookPrivateController.class);

  @Autowired(required = false)
  private NotebookEntryService notebookEntryService;

  @GetMapping("/entrytemplate")
  public HttpEntity<List<NotebookTemplate>> findNotebookEntryTemplates() {
    if (notebookEntryService == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    List<NotebookTemplate> templates = notebookEntryService.findEntryTemplates();
    LOGGER.info(templates.toString());
    return new ResponseEntity<>(templates, HttpStatus.OK);
  }

  @GetMapping("/entrytemplate/{id}")
  public HttpEntity<NotebookTemplate> findNotebookEntryTemplateById(@PathVariable String id) {
    Optional<NotebookTemplate> optional = notebookEntryService.findEntryTemplateById(id);
    if (optional.isPresent()) {
      return new ResponseEntity<>(optional.get(), HttpStatus.OK);
    } else {
      LOGGER.warn("Could not find notebook entry template with id: " + id);
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }
}
