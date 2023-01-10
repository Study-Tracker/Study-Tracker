/*
 * Copyright 2022 the original author or authors.
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
import io.studytracker.mapstruct.dto.form.AssayTaskFormDto;
import io.studytracker.mapstruct.dto.response.AssayTaskDetailsDto;
import io.studytracker.model.Activity;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayTask;
import io.studytracker.model.User;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/internal/assay/{assayId}/tasks", "/api/internal/study/{studyId}/assays/{assayId}/tasks"})
public class AssayTasksPrivateController extends AbstractAssayController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssayTasksPrivateController.class);

  @GetMapping("")
  public List<AssayTaskDetailsDto> fetchTasks(@PathVariable("assayId") String assayId) {
    Assay assay = this.getAssayFromIdentifier(assayId);
    return this.getAssayTaskMapper().toDetailsDtoList(this.getAssayTaskService().findAssayTasks(assay));
  }

  @PostMapping("")
  public HttpEntity<AssayTaskDetailsDto> addTask(
      @PathVariable("assayId") String assayId,
      @RequestBody AssayTaskFormDto dto
  ) {
    LOGGER.info("Adding assay task to assay {}: {}", assayId, dto);
    Assay assay = this.getAssayFromIdentifier(assayId);
    AssayTask task = this.addNewAssayTask(this.getAssayTaskMapper().fromFormDto(dto), assay);
    return new ResponseEntity<>(this.getAssayTaskMapper().toDetailsDto(task), HttpStatus.CREATED);
  }

  @PutMapping("")
  public HttpEntity<AssayTaskDetailsDto> updateTask(
      @PathVariable("assayId") String assayId,
      @RequestBody AssayTaskFormDto dto
  ) {
    LOGGER.info("Updating assay task: {}", dto);
    Assay assay = this.getAssayFromIdentifier(assayId);
    AssayTask task = this.updateExistingAssayTask(this.getAssayTaskMapper().fromFormDto(dto), assay);
    return new ResponseEntity<>(this.getAssayTaskMapper().toDetailsDto(task), HttpStatus.OK);
  }

  @PatchMapping("")
  public HttpEntity<?> updateTaskStatus(
      @PathVariable("assayId") String assayId,
      @RequestBody AssayTaskFormDto dto
  ) {
    LOGGER.info("Updating assay task status: {}", dto);
    Assay assay = this.getAssayFromIdentifier(assayId);
    this.updateAssayTaskStatus(this.getAssayTaskMapper().fromFormDto(dto), assay);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("")
  public HttpEntity<?> removeTask(
      @PathVariable("assayId") String assayId,
      @RequestBody AssayTaskFormDto dto
  ) {

    User user = this.getAuthenticatedUser();
    Assay assay = this.getAssayFromIdentifier(assayId);
    assay.setLastModifiedBy(user);
    AssayTask task = this.getAssayTaskMapper().fromFormDto(dto);
    this.getAssayTaskService().deleteAssayTask(task, assay);

    Activity activity = AssayActivityUtils.fromTaskDeleted(assay, user, task);
    this.getActivityService().create(activity);
    this.getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(HttpStatus.OK);
  }
}
