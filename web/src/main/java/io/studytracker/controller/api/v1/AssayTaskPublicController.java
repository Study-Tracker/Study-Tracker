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

package io.studytracker.controller.api.v1;

import io.studytracker.controller.api.AbstractAssayController;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.AssayTaskDto;
import io.studytracker.mapstruct.dto.api.AssayTaskPayloadDto;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayTask;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/assay-task")
public class AssayTaskPublicController extends AbstractAssayController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssayTaskPublicController.class);

  @GetMapping("")
  public Page<AssayTaskDto> findAll(
      @RequestParam(required = false) Long assayId,
      Pageable pageable
  ) {
    LOGGER.debug("Find all assay tasks");
    Page<AssayTask> page;
    if (assayId != null) {
      page = this.getAssayTaskService().findAssayTasks(assayId, pageable);
    } else {
      page = this.getAssayTaskService().findAll(pageable);
    }
    return new PageImpl<>(this.getAssayTaskMapper().toDtoList(page.getContent()), pageable, page.getTotalElements());
  }

  @GetMapping("/{id}")
  public HttpEntity<AssayTaskDto> findById(@PathVariable Long id) {
    LOGGER.debug("Find assay task by id: {}", id);
    AssayTask task = this.getAssayTaskService().findById(id)
        .orElseThrow(() -> new RecordNotFoundException("No task found with id " + id));
    return new ResponseEntity<>(this.getAssayTaskMapper().toDto(task), HttpStatus.OK);
  }

  @PostMapping("")
  public HttpEntity<AssayTaskDto> create(@Valid @RequestBody AssayTaskPayloadDto dto) {
    LOGGER.info("Create assay task: {}", dto);
    Assay assay = this.getAssayService().findById(dto.getAssayId())
        .orElseThrow(() -> new RecordNotFoundException("No assay found with id " + dto.getAssayId()));
    AssayTask assayTask = this.addNewAssayTask(this.getAssayTaskMapper().fromPayload(dto), assay);
    return new ResponseEntity<>(this.getAssayTaskMapper().toDto(assayTask), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<AssayTaskDto> update(
      @PathVariable Long id, @Valid @RequestBody AssayTaskPayloadDto dto) {
    LOGGER.info("Update assay task: {}", dto);
    Assay assay = this.getAssayService().findById(dto.getAssayId())
        .orElseThrow(() -> new RecordNotFoundException("No assay found with id " + dto.getAssayId()));
    AssayTask assayTask = this.updateExistingAssayTask(this.getAssayTaskMapper().fromPayload(dto), assay);
    return new ResponseEntity<>(this.getAssayTaskMapper().toDto(assayTask), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> delete(@PathVariable Long id) {
    LOGGER.info("Delete assay task: {}", id);
    AssayTask assayTask = this.getAssayTaskService().findById(id)
        .orElseThrow(() -> new RecordNotFoundException("No task found with id " + id));
    Assay assay = assayTask.getAssay();
    this.deleteAssayTask(assayTask, assay);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
