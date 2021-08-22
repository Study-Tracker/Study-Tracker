package com.decibeltx.studytracker.repository;

import com.decibeltx.studytracker.model.StudyCollection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyCollectionRepository extends JpaRepository<StudyCollection, Long> {

  @Override
  @EntityGraph("study-collection-summary")
  List<StudyCollection> findAll();

  @Override
  @EntityGraph("study-collection-details")
  Optional<StudyCollection> findById(Long id);

  @EntityGraph("study-collection-summary")
  List<StudyCollection> findByCreatedById(Long id);

  @EntityGraph("study-collection-summary")
  List<StudyCollection> findByStudiesId(Long id);

}
