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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/study/{studyId}/notebook")
public class StudyNotebookPrivateController extends AbstractStudyController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyNotebookPrivateController.class);

  @Autowired(required = false)
  private NotebookFolderService notebookFolderService;

  @GetMapping("")
  public NotebookFolder getStudyNotebookFolder(@PathVariable("studyId") String studyId,
      @RequestParam(value = "contents", required = false) boolean includeContents)
      throws RecordNotFoundException {
    LOGGER.info("Fetching notebook folder for study: " + studyId);
    Study study = getStudyFromIdentifier(studyId);

    Optional<NotebookFolder> notebookFolder =
        Optional.ofNullable(notebookFolderService)
            .flatMap(service -> service.findStudyFolder(study, includeContents));
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
