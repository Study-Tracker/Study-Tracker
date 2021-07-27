package com.decibeltx.studytracker.repository;

import com.decibeltx.studytracker.model.StudyConclusions;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudyConclusionsRepository extends JpaRepository<StudyConclusions, Long> {

  @Query("select c from StudyConclusions c where c.study.id = ?1")
  Optional<StudyConclusions> findByStudyId(Long id);

}
