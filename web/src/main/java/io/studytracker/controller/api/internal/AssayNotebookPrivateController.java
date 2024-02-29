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

import io.studytracker.controller.api.AbstractAssayController;
import io.studytracker.eln.NotebookFolder;
import io.studytracker.eln.NotebookFolderService;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.Assay;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/internal/assay/{assayId}/notebook")
@RestController
public class AssayNotebookPrivateController extends AbstractAssayController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssayNotebookPrivateController.class);

  @Autowired(required = false)
  private NotebookFolderService notebookFolderService;

  @GetMapping("")
  public NotebookFolder getNotebookFolder(@PathVariable("assayId") String assayId,
      @RequestParam(value = "contents", required = false) boolean includeContents)
      throws RecordNotFoundException {
    LOGGER.info("Fetching notebook folder for assay: " + assayId);
    Assay assay = getAssayFromIdentifier(assayId);

    Optional<NotebookFolder> notebookFolder =
        Optional.ofNullable(notebookFolderService)
            .flatMap(service -> service.findPrimaryAssayFolder(assay, includeContents));
    return notebookFolder.orElseThrow(
        () -> new RecordNotFoundException("Could not load assay folder"));
  }

  @PostMapping("/repair")
  public HttpEntity<?> repairNotebookFolder(@PathVariable("assayId") Long assayId) {

    // Check that the study exists
    Optional<Assay> optional = this.getAssayService().findById(assayId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException("Assay not found: " + assayId);
    }
    Assay assay = optional.get();

    // Repair the folder
    this.getAssayService().repairElnFolder(assay);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
