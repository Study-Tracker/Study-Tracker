package com.decibeltx.studytracker.service;

import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.NotebookEntryTemplate;
import com.decibeltx.studytracker.repository.NotebookEntryTemplateRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotebookEntryTemplateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotebookEntryTemplateService.class);

    @Autowired
    private NotebookEntryTemplateRepository notebookEntryTemplateRepository;

    public Optional<NotebookEntryTemplate> findById(Long id) {
        return notebookEntryTemplateRepository.findById(id);
    }

    public List<NotebookEntryTemplate> findAll() {
        return notebookEntryTemplateRepository.findAll();
    }

    public List<NotebookEntryTemplate> findAllActive() {
        return findAll().stream().filter(NotebookEntryTemplate::isActive).collect(Collectors.toList());
    }

    @Transactional
    public NotebookEntryTemplate create(NotebookEntryTemplate notebookEntryTemplate) {
        LOGGER.info("Creating new entry template with name: " + notebookEntryTemplate.getName());
        notebookEntryTemplateRepository.save(notebookEntryTemplate);
        return notebookEntryTemplate;
    }

    @Transactional
    public NotebookEntryTemplate update(NotebookEntryTemplate notebookEntryTemplate) {
        LOGGER.info("Updating entry template with name: " + notebookEntryTemplate.getName());
        NotebookEntryTemplate t = notebookEntryTemplateRepository
            .getById(notebookEntryTemplate.getId());
        t.setActive(notebookEntryTemplate.isActive());
        t.setName(notebookEntryTemplate.getName());
        t.setTemplateId(notebookEntryTemplate.getTemplateId());
        t.setCategory(notebookEntryTemplate.getCategory());
        t.setDefault(notebookEntryTemplate.isDefault());
        notebookEntryTemplateRepository.save(t);
        return notebookEntryTemplateRepository.findById(notebookEntryTemplate.getId())
            .orElseThrow(RecordNotFoundException::new);
    }

    @Transactional
    public void updateActive(NotebookEntryTemplate notebookEntryTemplate, boolean isActive) {
        LOGGER.info("Updating status of notebook entry template: " + notebookEntryTemplate.getName());
        NotebookEntryTemplate t = notebookEntryTemplateRepository
            .getById(notebookEntryTemplate.getId());
        t.setActive(isActive);
        notebookEntryTemplateRepository.save(t);
    }

    @Transactional
    public void updateDefault(NotebookEntryTemplate template) {
        LOGGER.info("Setting new default notebook entry template: " + template.getName());

        // Get the old default
        Optional<NotebookEntryTemplate> optional = notebookEntryTemplateRepository
            .findDefaultByCategory(template.getCategory());
        if (optional.isPresent()) {
            NotebookEntryTemplate old = optional.get();
            old.setDefault(false);
        }

        NotebookEntryTemplate t = notebookEntryTemplateRepository.getById(template.getId());
        t.setDefault(true);
        notebookEntryTemplateRepository.save(t);
    }

    @Transactional
    public void delete(NotebookEntryTemplate template) {
        notebookEntryTemplateRepository.deleteById(template.getId());
    }

    public boolean exists(Long id) {
        return notebookEntryTemplateRepository.existsById(id);
    }

}
