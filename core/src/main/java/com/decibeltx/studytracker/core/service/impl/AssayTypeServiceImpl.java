package com.decibeltx.studytracker.core.service.impl;

import com.decibeltx.studytracker.core.exception.InvalidConstraintException;
import com.decibeltx.studytracker.core.model.AssayType;
import com.decibeltx.studytracker.core.model.AssayTypeField;
import com.decibeltx.studytracker.core.repository.AssayTypeRepository;
import com.decibeltx.studytracker.core.service.AssayTypeService;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AssayTypeServiceImpl implements AssayTypeService {

  @Autowired
  private AssayTypeRepository assayTypeRepository;

  @Override
  public Optional<AssayType> findById(String id) {
    return assayTypeRepository.findById(id);
  }

  @Override
  public Optional<AssayType> findByName(String name) {
    return assayTypeRepository.findByName(name);
  }

  @Override
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

  @Override
  public void create(AssayType assayType) {
    validateFields(assayType);
    assayTypeRepository.insert(assayType);
  }

  @Override
  public void update(AssayType assayType) {
    validateFields(assayType);
    assayTypeRepository.save(assayType);
  }

  @Override
  public void delete(AssayType assayType) {
    assayType.setActive(false);
    assayTypeRepository.save(assayType);
  }

  @Override
  public long count() {
    return assayTypeRepository.count();
  }
}
