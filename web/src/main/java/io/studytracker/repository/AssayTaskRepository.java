package io.studytracker.repository;

import io.studytracker.model.AssayTask;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AssayTaskRepository extends JpaRepository<AssayTask, Long> {

  @Query("select v from AssayTask v where v.assay.id = ?1")
  List<AssayTask> findByAssayId(Long assayId);

  @Query("select v from AssayTask v where v.assay.id = ?1")
  Page<AssayTask> findByAssayId(Long assayId, Pageable pageable);
}
