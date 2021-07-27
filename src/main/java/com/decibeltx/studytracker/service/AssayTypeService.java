package com.decibeltx.studytracker.service;

import com.decibeltx.studytracker.exception.InvalidConstraintException;
import com.decibeltx.studytracker.model.AssayType;
import com.decibeltx.studytracker.model.AssayTypeField;
import com.decibeltx.studytracker.model.AssayTypeTask;
import com.decibeltx.studytracker.repository.AssayTypeRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AssayTypeService {

  @Autowired
  private AssayTypeRepository assayTypeRepository;

  public Optional<AssayType> findById(Long id) {
    return assayTypeRepository.findById(id);
  }

  public Optional<AssayType> findByName(String name) {
    return assayTypeRepository.findByName(name);
  }

  public List<AssayType> findAll() {
    return assayTypeRepository.findAll();
  }

  private void validateFields(AssayType assayType) {
    Set<String> fieldNames = new HashSet<>();
    Set<String> displayNames = new HashSet<>();
    for (AssayTypeField field : assayType.getFields()) {

      // Check for required input
      if (StringUtils.isEmpty(field.getFieldName()) || StringUtils.isEmpty(field.getDisplayName())
          || field.getType() == null) {
        throw new InvalidConstraintException("Assay type " + assayType.getName()
            + " field is missing required attributes: " + assayType.toString());
      }

      // Check that a field with the name doesn't exist
      if (fieldNames.contains(field.getFieldName())) {
        throw new InvalidConstraintException("Assay type " + assayType.getName()
            + " already contains a field with name: " + field.getFieldName());
      }
      fieldNames.add(field.getFieldName());

      // Check that a field with the display name doesn't exist
      if (displayNames.contains(field.getDisplayName())) {
        throw new InvalidConstraintException("Assay type " + assayType.getName()
            + " already contains a field with display name: " + field.getDisplayName());
      }
      displayNames.add(field.getDisplayName());

    }
  }

  @Transactional
  public AssayType create(AssayType assayType) {
    validateFields(assayType);
    for (AssayTypeField field: assayType.getFields()) {
      field.setAssayType(assayType);
    }
    for (AssayTypeTask task: assayType.getTasks()) {
      task.setAssayType(assayType);
    }
    assayTypeRepository.save(assayType);
    return assayType;
  }

  @Transactional
  public AssayType update(AssayType assayType) {

    validateFields(assayType);
    for (AssayTypeField field: assayType.getFields()) {
      field.setAssayType(assayType);
    }
    for (AssayTypeTask task: assayType.getTasks()) {
      task.setAssayType(assayType);
    }
    assayTypeRepository.save(assayType);
    return assayType;
  }

  @Transactional
  public void toggleActive(AssayType assayType) {
    assayType.setActive(!assayType.isActive());
    assayTypeRepository.save(assayType);
  }

  @Transactional
  public void delete(AssayType assayType) {
    assayType.setActive(false);
    assayTypeRepository.save(assayType);
  }

  public long count() {
    return assayTypeRepository.count();
  }

}
