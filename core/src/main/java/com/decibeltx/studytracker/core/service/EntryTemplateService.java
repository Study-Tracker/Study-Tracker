package com.decibeltx.studytracker.core.service;

import com.decibeltx.studytracker.core.model.NotebookEntryTemplate;

import java.util.List;
import java.util.Optional;

public interface EntryTemplateService {

    Optional<NotebookEntryTemplate> findById(String id);

    List<NotebookEntryTemplate> findAll();

    List<NotebookEntryTemplate> findAllActive();

    void create(NotebookEntryTemplate notebookEntryTemplate);

    void update(NotebookEntryTemplate notebookEntryTemplate);
}
