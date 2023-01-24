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

package io.studytracker.controller.api.internal;

import io.studytracker.eln.NotebookEntryService;
import io.studytracker.eln.NotebookFolderService;
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

  @Autowired(required = false)
  private NotebookFolderService notebookFolderService;

  @Deprecated
  @GetMapping("/entrytemplate")
  public HttpEntity<List<NotebookTemplate>> findNotebookEntryTemplates() {
    if (notebookEntryService == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    List<NotebookTemplate> templates = notebookEntryService.findEntryTemplates();
    LOGGER.info(templates.toString());
    return new ResponseEntity<>(templates, HttpStatus.OK);
  }

  @Deprecated
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

  @GetMapping("/project-folders")
  public HttpEntity<?> findNotebookProjects() {
    if (notebookFolderService == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(notebookFolderService.listNotebookProjectFolders(), HttpStatus.OK);
  }



}
