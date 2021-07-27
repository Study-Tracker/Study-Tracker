package com.decibeltx.studytracker.repository;

import com.decibeltx.studytracker.model.AssayTypeField;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AssayTypeFieldRepository extends JpaRepository<AssayTypeField, Long> {

  @Query("select f from AssayTypeField f where f.assayType.id = ?1")
  List<AssayTypeField> findByAssayTypeId(Long assayTypeId);

}
