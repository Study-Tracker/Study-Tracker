package com.decibeltx.studytracker.service.impl;

import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.NotebookEntryTemplate;
import com.decibeltx.studytracker.repository.EntryTemplateRepository;
import com.decibeltx.studytracker.service.EntryTemplateService;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntryTemplateServiceImpl implements EntryTemplateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntryTemplateServiceImpl.class);

    @Autowired
    private EntryTemplateRepository entryTemplateRepository;

    @Override
    public Optional<NotebookEntryTemplate> findById(String id) {
        return entryTemplateRepository.findById(id);
    }

    @Override
    public List<NotebookEntryTemplate> findAll() {
        return entryTemplateRepository.findAll();
    }

    @Override
    public List<NotebookEntryTemplate> findAllActive() {
        return findAll().stream().filter(NotebookEntryTemplate::isActive).collect(Collectors.toList());
    }

    @Override
    public void create(NotebookEntryTemplate notebookEntryTemplate) {
        LOGGER.info("Creating new entry template with name: " + notebookEntryTemplate.getName());

        Date now = new Date();
        notebookEntryTemplate.setCreatedAt(now);
        notebookEntryTemplate.setUpdatedAt(now);
        entryTemplateRepository.insert(notebookEntryTemplate);
    }

    @Override
    public void update(NotebookEntryTemplate notebookEntryTemplate) {
        LOGGER.info("Updating entry template with name: " + notebookEntryTemplate.getName());

        assert notebookEntryTemplate.getId() != null;
        entryTemplateRepository.findById(notebookEntryTemplate.getId()).orElseThrow(RecordNotFoundException::new);
        entryTemplateRepository.save(notebookEntryTemplate);
    }
}
