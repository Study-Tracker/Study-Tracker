package com.decibeltx.studytracker.web.controller.api;

import com.decibeltx.studytracker.core.events.util.EntryTemplateActivityUtils;
import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.Activity;
import com.decibeltx.studytracker.core.model.NotebookEntryTemplate;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.core.service.ActivityService;
import com.decibeltx.studytracker.core.service.EntryTemplateService;
import com.decibeltx.studytracker.core.service.EventsService;
import com.decibeltx.studytracker.core.service.UserService;
import com.decibeltx.studytracker.web.controller.UserAuthenticationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RestController
@RequestMapping("/api/entryTemplate")
public class EntryTemplateController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntryTemplateController.class);

    @Autowired
    private EntryTemplateService entryTemplateService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private EventsService eventsService;

    private User getAuthenticatedUser() throws RecordNotFoundException {
        String username = UserAuthenticationUtils
                .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
        return userService.findByUsername(username)
                .orElseThrow(RecordNotFoundException::new);
    }

    private NotebookEntryTemplate getTemplateById(String id) throws RecordNotFoundException {
        return entryTemplateService
                .findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Template not found: " + id));
    }

    @GetMapping("")
    public List<NotebookEntryTemplate> getEntryTemplates() {
        LOGGER.info("Getting all entry templates");

        return entryTemplateService.findAll();
    }

    @GetMapping("/active")
    public List<NotebookEntryTemplate> getActiveTemplates() {
        LOGGER.info("Getting all active entry templates");

        return entryTemplateService.findAllActive();
    }

    @PostMapping("")
    public HttpEntity<NotebookEntryTemplate> createTemplate(@RequestBody NotebookEntryTemplate notebookEntryTemplate)
            throws RecordNotFoundException {
        LOGGER.info("Creating new entry template : " + notebookEntryTemplate.toString());

        authenticateUserAndApplyTemplateOperation(Optional.empty(), notebookEntryTemplate,
                entryTemplateService::create, EntryTemplateActivityUtils::fromNewEntryTemplate);

        return new ResponseEntity<>(notebookEntryTemplate, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/status")
    public HttpEntity<NotebookEntryTemplate> updateTemplateStatus(@PathVariable("id") String id,
                                                                  @RequestParam("active") boolean active)
            throws RecordNotFoundException {
        LOGGER.info("Updating template with id: " + id);

        NotebookEntryTemplate notebookEntryTemplate = getTemplateById(id);
        authenticateUserAndApplyTemplateOperation(Optional.of(active), notebookEntryTemplate,
                entryTemplateService::update,EntryTemplateActivityUtils::fromUpdatedEntryTemplate);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("")
    public HttpEntity<NotebookEntryTemplate> updateEntryTemplate(@RequestBody NotebookEntryTemplate notebookEntryTemplate) {
        getTemplateById(notebookEntryTemplate.getId());
        authenticateUserAndApplyTemplateOperation(Optional.empty(), notebookEntryTemplate,
                entryTemplateService::update, EntryTemplateActivityUtils::fromUpdatedEntryTemplate);
        return new ResponseEntity<>(notebookEntryTemplate, HttpStatus.CREATED);
    }

    private void authenticateUserAndApplyTemplateOperation(Optional<Boolean> status, NotebookEntryTemplate notebookEntryTemplate,
                                                           Consumer<NotebookEntryTemplate> templateConsumer,
                                                           BiFunction<NotebookEntryTemplate, User, Activity> activityGenerator) {
        User user = getAuthenticatedUser();
        notebookEntryTemplate.setLastModifiedBy(user);
        status.ifPresent(notebookEntryTemplate::setActive);
        templateConsumer.accept(notebookEntryTemplate);
        publishEvents(notebookEntryTemplate, user, activityGenerator);
    }

    private void publishEvents(NotebookEntryTemplate notebookEntryTemplate, User user,
                               BiFunction<NotebookEntryTemplate, User, Activity> generateActivity) {
        Activity activity = generateActivity.apply(notebookEntryTemplate, user);
        activityService.create(activity);
        eventsService.dispatchEvent(activity);
    }
}
