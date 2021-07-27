package com.decibeltx.studytracker.repository;

import com.decibeltx.studytracker.model.AssayType;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssayTypeRepository extends JpaRepository<AssayType, Long> {

  @Override
  @EntityGraph("assay-type-details")
  Optional<AssayType> findById(Long id);

  @EntityGraph("assay-type-details")
  Optional<AssayType> findByName(String name);

}
