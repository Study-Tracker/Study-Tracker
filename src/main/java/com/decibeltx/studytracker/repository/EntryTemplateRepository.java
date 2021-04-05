package com.decibeltx.studytracker.repository;

import com.decibeltx.studytracker.model.NotebookEntryTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EntryTemplateRepository extends MongoRepository<NotebookEntryTemplate, String>  {

}
