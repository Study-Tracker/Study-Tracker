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

package io.studytracker.controller.api.internal.autocomplete;

import io.studytracker.eln.NotebookEntryService;
import io.studytracker.eln.NotebookTemplate;
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

import java.util.List;

@RestController
@RequestMapping("/api/internal/autocomplete/notebook-entry-template")
public class NotebookTemplateAutocompleteController {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(NotebookTemplateAutocompleteController.class);

  @Autowired
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
