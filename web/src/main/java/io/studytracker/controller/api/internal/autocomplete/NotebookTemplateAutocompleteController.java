package io.studytracker.controller.api.internal.autocomplete;

import io.studytracker.eln.NotebookEntryService;
import io.studytracker.eln.NotebookTemplate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/autocomplete/notebook-entry-template")
public class NotebookTemplateAutocompleteController {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(NotebookTemplateAutocompleteController.class);

  @Autowired(required = false)
  private NotebookEntryService notebookEntryService;

  @GetMapping("")
  public HttpEntity<List<NotebookTemplate>> findNotebookTemplates(@RequestParam("q") String keyword) {
    LOGGER.info("findNotebookTemplates: {}", keyword);
    if (notebookEntryService != null) {
      List<NotebookTemplate> templates = notebookEntryService.searchNotebookTemplates(keyword);
      return new ResponseEntity<>(templates, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
  }

}
