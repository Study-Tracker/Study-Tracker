package io.studytracker.repository;

import io.studytracker.model.StudyCollection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  Page<StudyCollection> findByCreatedById(Long id, Pageable pageable);

  @EntityGraph("study-collection-summary")
  List<StudyCollection> findByStudiesId(Long id);

  Page<StudyCollection> findByStudiesId(Long id, Pageable pageable);
}
