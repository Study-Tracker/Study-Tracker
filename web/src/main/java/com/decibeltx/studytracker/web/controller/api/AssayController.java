/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.decibeltx.studytracker.web.controller.api;

import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.exception.StudyTrackerException;
import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.Status;
import com.decibeltx.studytracker.core.service.AssayService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assay")
public class AssayController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssayController.class);

  @Autowired
  private AssayService assayService;

  @GetMapping("")
  public List<Assay> getAllAssays() {
    return assayService.findAll();
  }

  @GetMapping("/{id}")
  public Assay getAssay(@PathVariable("id") String assayId) throws RecordNotFoundException {
    Assay assay;
    Optional<Assay> optional = assayService.findById(assayId);
    if (optional.isPresent()) {
      assay = optional.get();
    } else {
      optional = assayService.findByCode(assayId);
      if (optional.isPresent()) {
        assay = optional.get();
      } else {
        throw new RecordNotFoundException();
      }
    }
    return assay;
  }

  @PostMapping("")
  public Assay createAssay(@RequestBody Assay assay) {
    LOGGER.info("Creating assay");
    LOGGER.info(assay.toString());
    assayService.create(assay);
    return assay;
  }

  @PutMapping("/{id}")
  public void updateAssay(@PathVariable("id") String id, @RequestBody Assay assay) {
    LOGGER.info("Updating assay");
    LOGGER.info(assay.toString());
    assayService.update(assay);
  }

  @DeleteMapping("/{id}")
  public void deleteAssay(@PathVariable("id") String id) {
    LOGGER.info("Deleting assay: " + id);
    Assay assay = assayService.findById(id).orElseThrow(RecordNotFoundException::new);
    assayService.delete(assay);
  }

  @PostMapping("/{id}/status")
  public void updateAssayStatus(@PathVariable("id") String id,
      @RequestBody Map<String, Object> params) throws StudyTrackerException {
    if (!params.containsKey("status")) {
      throw new StudyTrackerException("No status label provided.");
    }
    Assay assay = assayService.findById(id).orElseThrow(RecordNotFoundException::new);
    String label = (String) params.get("status");
    Status status = Status.valueOf(label);
    LOGGER.info(String.format("Setting status of assay %s to %s", id, label));
    assayService.updateStatus(assay, status);
  }

}
