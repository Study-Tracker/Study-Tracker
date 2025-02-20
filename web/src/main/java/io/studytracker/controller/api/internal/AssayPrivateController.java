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
import io.studytracker.events.util.AssayActivityUtils;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.mapstruct.dto.form.AssayFormDto;
import io.studytracker.mapstruct.dto.response.ActivitySummaryDto;
import io.studytracker.mapstruct.dto.response.AssayDetailsDto;
import io.studytracker.mapstruct.dto.response.AssayParentDto;
import io.studytracker.mapstruct.mapper.ActivityMapper;
import io.studytracker.model.Activity;
import io.studytracker.model.Assay;
import io.studytracker.model.Status;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/api/internal/assay")
public class AssayPrivateController extends AbstractAssayController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssayPrivateController.class);

  @Autowired private ActivityMapper activityMapper;

  @GetMapping("")
  public List<AssayParentDto> findAll() {
    return this.getAssayMapper().toAssayParentList(getAssayService().findAll());
  }

  @GetMapping("/{id}")
  public AssayDetailsDto findById(@PathVariable("id") String assayId)
      throws RecordNotFoundException {
    return this.getAssayMapper().toAssayDetails(getAssayFromIdentifier(assayId));
  }

  @PutMapping("/{id}")
  public HttpEntity<AssayDetailsDto> update(
      @PathVariable("id") Long id,
      @RequestBody @Valid AssayFormDto dto
  ) {
    LOGGER.info("Updating assay with id: " + id);
    LOGGER.info(dto.toString());
    User user = this.getAuthenticatedUser();
    Assay assay = this.getAssayMapper().fromAssayForm(dto);
    Assay updated = updateAssay(assay, user);
    return new ResponseEntity<>(this.getAssayMapper().toAssayDetails(updated), HttpStatus.CREATED);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> delete(@PathVariable("id") String id) {
    LOGGER.info("Deleting assay: " + id);
    this.deleteAssay(id, this.getAuthenticatedUser());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/{id}/restore")
  public HttpEntity<?> restore(@PathVariable("id") String id) {
    LOGGER.info("Restoring assay: " + id);
    this.restoreAssay(id, this.getAuthenticatedUser());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/{id}/status")
  public HttpEntity<?> updateStatus(
      @PathVariable("id") String id,
      @RequestBody Map<String, Object> params
  ) throws StudyTrackerException {
    if (!params.containsKey("status")) throw new StudyTrackerException("No status label provided.");
    Assay assay = this.getAssayFromIdentifier(id);
    String label = (String) params.get("status");
    Status status = Status.valueOf(label);
    LOGGER.info(String.format("Setting status of assay %s to %s", id, label));
    this.updateAssayStatus(assay.getId(), status, this.getAuthenticatedUser());
    return new ResponseEntity<>(HttpStatus.OK);
  }
  
  @PutMapping("/{id}/study")
  public AssayDetailsDto updateAssayStudy(
      @PathVariable("id") String id,
      @RequestBody Map<String, Object> params
  ) throws StudyTrackerException {
    if (!params.containsKey("studyId")) throw new StudyTrackerException("No studyId provided.");
    String studyId = params.get("studyId").toString();
    LOGGER.info(String.format("Setting study of assay %s to %s", id, studyId));
    Assay assay = this.getAssayFromIdentifier(id);
    Study oldStudy = assay.getStudy();
    Study study = this.getStudyFromIdentifier(studyId);
    this.getAssayService().moveAssayToStudy(assay, study);
    Assay updated = this.getAssayFromIdentifier(id);
    Activity activity = AssayActivityUtils.fromMovedAssay(assay, oldStudy, study, this.getAuthenticatedUser());
    this.logActivity(activity);
    return this.getAssayMapper().toAssayDetails(updated);
  }

  @GetMapping("/{assayId}/activity")
  public List<ActivitySummaryDto> getAssayActivity(@PathVariable("assayId") String assayId) {
    Assay assay = this.getAssayFromIdentifier(assayId);
    return activityMapper.toActivitySummaryList(getActivityService().findByAssay(assay));
  }
}
