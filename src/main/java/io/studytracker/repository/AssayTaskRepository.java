package io.studytracker.repository;

import io.studytracker.model.AssayTask;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AssayTaskRepository extends JpaRepository<AssayTask, Long> {

  @Query("select v from AssayTask v where v.assay.id = ?1")
  List<AssayTask> findByAssayId(Long assayId);

}
