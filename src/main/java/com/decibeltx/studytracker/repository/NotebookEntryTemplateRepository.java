package com.decibeltx.studytracker.repository;

import com.decibeltx.studytracker.model.NotebookEntryTemplate;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotebookEntryTemplateRepository extends JpaRepository<NotebookEntryTemplate, Long> {

  @Override
  @EntityGraph("entry-template-details")
  Optional<NotebookEntryTemplate> findById(Long id);

}
