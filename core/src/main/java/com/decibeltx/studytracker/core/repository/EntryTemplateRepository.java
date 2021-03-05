package com.decibeltx.studytracker.core.repository;

import com.decibeltx.studytracker.core.model.NotebookEntryTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EntryTemplateRepository extends MongoRepository<NotebookEntryTemplate, String>  {

}
