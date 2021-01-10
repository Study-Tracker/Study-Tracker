package com.decibeltx.studytracker.core.repository;

import com.decibeltx.studytracker.core.model.AssayType;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AssayTypeRepository extends MongoRepository<AssayType, String> {

  Optional<AssayType> findByName(String name);

}
