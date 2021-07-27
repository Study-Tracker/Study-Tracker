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

    public NotebookEntryTemplate create(NotebookEntryTemplate notebookEntryTemplate) {
        LOGGER.info("Creating new entry template with name: " + notebookEntryTemplate.getName());
        notebookEntryTemplateRepository.save(notebookEntryTemplate);
        return notebookEntryTemplate;
    }

    public NotebookEntryTemplate update(NotebookEntryTemplate notebookEntryTemplate) {
        LOGGER.info("Updating entry template with name: " + notebookEntryTemplate.getName());
        NotebookEntryTemplate t = notebookEntryTemplateRepository
            .getOne(notebookEntryTemplate.getId());
        t.setActive(notebookEntryTemplate.isActive());
        t.setName(notebookEntryTemplate.getName());
        t.setTemplateId(notebookEntryTemplate.getTemplateId());
        notebookEntryTemplateRepository.save(t);
        return notebookEntryTemplateRepository.findById(notebookEntryTemplate.getId())
            .orElseThrow(RecordNotFoundException::new);
    }

    public void delete(NotebookEntryTemplate template) {
        notebookEntryTemplateRepository.deleteById(template.getId());
    }
}
