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

package io.studytracker.service;

import io.studytracker.exception.InvalidConstraintException;
import io.studytracker.model.AssayType;
import io.studytracker.model.AssayTypeField;
import io.studytracker.model.AssayTypeTask;
import io.studytracker.model.AssayTypeTaskField;
import io.studytracker.model.CustomEntityField;
import io.studytracker.repository.AssayTypeRepository;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AssayTypeService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssayTypeService.class);

  @Autowired private AssayTypeRepository assayTypeRepository;

  public Optional<AssayType> findById(Long id) {
    return assayTypeRepository.findById(id);
  }

  public Optional<AssayType> findByName(String name) {
    return assayTypeRepository.findByName(name);
  }

  public Page<AssayType> findAll(Pageable pageable) {
    return assayTypeRepository.findAll(pageable);
  }

  public List<AssayType> findAll() {
    return assayTypeRepository.findAll();
  }

  private void validateFields(Collection<? extends CustomEntityField> fields) {
    Set<String> fieldNames = new HashSet<>();
    Set<String> displayNames = new HashSet<>();
    for (CustomEntityField field : fields) {

      // Check for required input
      if (!StringUtils.hasText(field.getFieldName())
          || !StringUtils.hasText(field.getDisplayName())
          || field.getType() == null
          || field.getFieldOrder() == null
      ) {
        throw new InvalidConstraintException(
            "Custom field is missing required attributes: "
                + field);
      }

      // Check that a field with the name doesn't exist
      if (fieldNames.contains(field.getFieldName())) {
        throw new InvalidConstraintException(
            "Entity already contains a field with name: "
                + field.getFieldName());
      }
      fieldNames.add(field.getFieldName());

      // Check that a field with the display name doesn't exist
      if (displayNames.contains(field.getDisplayName())) {
        throw new InvalidConstraintException(
            "Entity already contains a field with display name: "
                + field.getDisplayName());
      }
      displayNames.add(field.getDisplayName());
    }
  }

  @Transactional
  public AssayType create(AssayType assayType) {
    LOGGER.info("Creating new assay type: {}", assayType);
    try {
      validateFields(assayType.getFields());
    } catch (InvalidConstraintException e) {
      throw new InvalidConstraintException("Assay type field validation failed: " + assayType.getName());
    }
    for (AssayTypeField field : assayType.getFields()) {
      field.setAssayType(assayType);
    }
    for (AssayTypeTask task : assayType.getTasks()) {
      try {
        validateFields(task.getFields());
      } catch (InvalidConstraintException e) {
        throw new InvalidConstraintException("Assay type task field validation failed: " + task.getLabel());
      }
      for (AssayTypeTaskField tf: task.getFields()) {
        tf.setAssayTypeTask(task);
      }
      task.setAssayType(assayType);
    }
    assayTypeRepository.save(assayType);
    return assayTypeRepository.findById(assayType.getId())
        .orElseThrow(() -> new IllegalStateException("Assay type not found after creation: " + assayType.getName()));
  }

  @Transactional
  public AssayType update(AssayType assayType) {
    LOGGER.info("Updating assay type: {}", assayType);
    try {
      validateFields(assayType.getFields());
    } catch (InvalidConstraintException e) {
      throw new InvalidConstraintException("Assay type field validation failed: " + assayType.getName());
    }
    for (AssayTypeField field : assayType.getFields()) {
      field.setAssayType(assayType);
    }
    for (AssayTypeTask task : assayType.getTasks()) {
      try {
        validateFields(task.getFields());
      } catch (InvalidConstraintException e) {
        throw new InvalidConstraintException("Assay type task field validation failed: " + task.getLabel());
      }
      for (AssayTypeTaskField tf: task.getFields()) {
        tf.setAssayTypeTask(task);
      }
      task.setAssayType(assayType);
    }
    assayTypeRepository.save(assayType);
    return assayType;
  }

  @Transactional
  public void toggleActive(AssayType assayType) {
    LOGGER.info("Toggling active status for assay type: {}", assayType);
    assayType.setActive(!assayType.isActive());
    assayTypeRepository.save(assayType);
  }

  @Transactional
  public void delete(AssayType assayType) {
    LOGGER.info("Deleting assay type: {}", assayType);
    assayType.setActive(false);
    assayTypeRepository.save(assayType);
  }

  public long count() {
    return assayTypeRepository.count();
  }
}
