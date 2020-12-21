package com.decibeltx.studytracker.core.service;

import com.decibeltx.studytracker.core.model.AssayType;
import java.util.List;
import java.util.Optional;

public interface AssayTypeService {

  Optional<AssayType> findById(String id);

  Optional<AssayType> findByName(String name);

  List<AssayType> findAll();

  void create(AssayType assayType);

  void update(AssayType assayType);

  void delete(AssayType assayType);

  long count();

}
