package com.decibeltx.studytracker.repository;

import com.decibeltx.studytracker.model.AssayType;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AssayTypeRepository extends MongoRepository<AssayType, String> {

  Optional<AssayType> findByName(String name);

}
