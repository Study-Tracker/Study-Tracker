package com.decibeltx.studytracker.repository;

import com.decibeltx.studytracker.model.AssayTypeTask;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AssayTypeTaskRepository extends JpaRepository<AssayTypeTask, Long> {

  @Query("select t from AssayTypeTask t where t.assayType.id = ?1")
  List<AssayTypeTask> findByAssayTypeId(Long assayTypeId);

}
