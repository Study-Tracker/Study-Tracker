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
import io.studytracker.exception.InvalidRequestException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.mapstruct.dto.form.AssayFormDto;
import io.studytracker.mapstruct.dto.response.AssayDetailsDto;
import io.studytracker.mapstruct.dto.response.AssaySummaryDto;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayOptions;
import io.studytracker.model.AssayType;
import io.studytracker.model.Status;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/internal/study/{studyId}/assays")
@RestController
public class StudyAssayPrivateController extends AbstractAssayController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyAssayPrivateController.class);

  @GetMapping("")
  public List<AssaySummaryDto> findStudyAssays(@PathVariable("studyId") String studyId) {
    Study study = getStudyFromIdentifier(studyId);
    return this.getAssayMapper().toAssaySummaryList(this.getAssayService().findByStudyId(study.getId()));
  }

  @GetMapping("/{assayId}")
  public AssayDetailsDto findById(@PathVariable("assayId") String assayId)
      throws RecordNotFoundException {
    return this.getAssayMapper().toAssayDetails(getAssayFromIdentifier(assayId));
  }

  @PostMapping("")
  public HttpEntity<AssayDetailsDto> create(
      @PathVariable("studyId") String studyId,
      @RequestBody @Valid AssayFormDto dto
  ) {

    LOGGER.info("Creating assay");
    LOGGER.info(dto.toString());

    Study study = this.getStudyFromIdentifier(studyId);
    Assay assay = this.getAssayMapper().fromAssayForm(dto);
    AssayType assayType = this.getAssayTypeService().findById(dto.getAssayType().getId())
        .orElseThrow(() -> new InvalidRequestException("Assay type not found: " + dto.getAssayType().getId()));
    assay.setAssayType(assayType);
    AssayOptions options = this.getAssayMapper().optionsFromAssayForm(dto);
    User user = this.getAuthenticatedUser();
    Assay created = this.createAssay(assay, study, user, options);
    return new ResponseEntity<>(this.getAssayMapper().toAssayDetails(created), HttpStatus.CREATED);
  }

  @PutMapping("/{assayId}")
  public HttpEntity<AssayDetailsDto> update(
      @PathVariable("assayId") String assayId,
      @RequestBody @Valid AssayFormDto dto
  ) {
    LOGGER.info("Updating assay");
    LOGGER.info(dto.toString());
    Assay assay = this.getAssayMapper().fromAssayForm(dto);
    this.updateAssay(assay, this.getAuthenticatedUser());
    return new ResponseEntity<>(this.getAssayMapper().toAssayDetails(assay), HttpStatus.OK);
  }

  @DeleteMapping("/{assayId}")
  public HttpEntity<?> delete(@PathVariable("assayId") String id) {
    LOGGER.info("Deleting assay: " + id);
    this.deleteAssay(id, this.getAuthenticatedUser());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/{id}/status")
  public HttpEntity<?> updateStatus(
      @PathVariable("id") String id,
      @RequestBody Map<String, Object> params
  ) throws StudyTrackerException {

    if (!params.containsKey("status")) {
      throw new StudyTrackerException("No status label provided.");
    }

    Assay assay = this.getAssayFromIdentifier(id);
    String label = (String) params.get("status");
    Status status = Status.valueOf(label);
    LOGGER.info(String.format("Setting status of assay %s to %s", id, label));

    this.updateAssayStatus(assay.getId(), status, this.getAuthenticatedUser());

    return new ResponseEntity<>(HttpStatus.OK);
  }
}
