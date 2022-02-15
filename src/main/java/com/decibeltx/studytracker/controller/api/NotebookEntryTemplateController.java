package com.decibeltx.studytracker.controller.api;

import com.decibeltx.studytracker.controller.UserAuthenticationUtils;
import com.decibeltx.studytracker.events.EventsService;
import com.decibeltx.studytracker.events.util.EntryTemplateActivityUtils;
import com.decibeltx.studytracker.exception.InsufficientPrivilegesException;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.mapstruct.dto.NotebookEntryTemplateDetailsDto;
import com.decibeltx.studytracker.mapstruct.dto.NotebookEntryTemplateFormDto;
import com.decibeltx.studytracker.mapstruct.dto.NotebookEntryTemplateSlimDto;
import com.decibeltx.studytracker.mapstruct.mapper.NotebookEntryTemplateMapper;
import com.decibeltx.studytracker.model.Activity;
import com.decibeltx.studytracker.model.NotebookEntryTemplate;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.service.ActivityService;
import com.decibeltx.studytracker.service.NotebookEntryTemplateService;
import com.decibeltx.studytracker.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RestController
@RequestMapping("/api/notebookentrytemplate")
public class NotebookEntryTemplateController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotebookEntryTemplateController.class);

    @Autowired
    private NotebookEntryTemplateService entryTemplateService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private EventsService eventsService;

    @Autowired
    private NotebookEntryTemplateMapper mapper;

    private User getAuthenticatedUser() throws RecordNotFoundException {
        String username = UserAuthenticationUtils
                .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
        return userService.findByUsername(username)
                .orElseThrow(RecordNotFoundException::new);
    }

    private NotebookEntryTemplate getTemplateById(Long id) throws RecordNotFoundException {
        return entryTemplateService
                .findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Template not found: " + id));
    }

    @GetMapping("")
    public List<NotebookEntryTemplateSlimDto> findEntryTemplates(
        @RequestParam(value = "category", required = false) NotebookEntryTemplate.Category category,
        @RequestParam(value = "active", required = false) Boolean isActive
    ) {
        LOGGER.info("Getting all entry templates");
        List<NotebookEntryTemplate> templates = entryTemplateService.findAll();
        if (category != null) {
            templates = templates.stream()
                .filter(t -> t.getCategory().equals(category))
                .collect(Collectors.toList());
        }
        if (isActive != null) {
            templates = templates.stream()
                .filter(t -> t.isActive() == isActive)
                .collect(Collectors.toList());
        }
        return mapper.toSlimList(templates);
    }

    @GetMapping("/active")
    public List<NotebookEntryTemplateSlimDto> getActiveTemplates() {
        LOGGER.info("Getting all active entry templates");
        return mapper.toSlimList(entryTemplateService.findAllActive());
    }

    @PostMapping("")
    public HttpEntity<NotebookEntryTemplateDetailsDto> createTemplate(
        @RequestBody @Valid NotebookEntryTemplateFormDto dto) {

        LOGGER.info("Creating new entry template : " + dto.toString());
        User user = getAuthenticatedUser();
        if (!user.isAdmin()){
            throw new InsufficientPrivilegesException("User does not have sufficient privileges "
                + "to perform this action: " + user.getUsername());
        }

        NotebookEntryTemplate notebookEntryTemplate = mapper.fromForm(dto);
        entryTemplateService.create(notebookEntryTemplate);

        Activity activity = EntryTemplateActivityUtils.fromNewEntryTemplate(notebookEntryTemplate, user);
        activityService.create(activity);
        eventsService.dispatchEvent(activity);

        return new ResponseEntity<>(mapper.toDetails(notebookEntryTemplate), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/status")
    public HttpEntity<?> updateTemplateStatus(@PathVariable("id") Long id,
        @RequestParam("active") boolean active) throws RecordNotFoundException {

        LOGGER.info("Updating template with id: " + id);

        User user = getAuthenticatedUser();
        NotebookEntryTemplate notebookEntryTemplate = getTemplateById(id);
        entryTemplateService.updateActive(notebookEntryTemplate, active);

        Activity activity = EntryTemplateActivityUtils.fromUpdatedEntryTemplate(notebookEntryTemplate, user);
        activityService.create(activity);
        eventsService.dispatchEvent(activity);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{id}/default")
    public HttpEntity<?> updateTemplateDefault(@PathVariable("id") Long id)
        throws RecordNotFoundException {

        LOGGER.info("Updating template with id: " + id);

        User user = getAuthenticatedUser();
        NotebookEntryTemplate notebookEntryTemplate = getTemplateById(id);
        entryTemplateService.updateDefault(notebookEntryTemplate);

        Activity activity = EntryTemplateActivityUtils.fromUpdatedEntryTemplate(notebookEntryTemplate, user);
        activityService.create(activity);
        eventsService.dispatchEvent(activity);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public HttpEntity<NotebookEntryTemplateDetailsDto> updateEntryTemplate(
        @PathVariable("id") Long id,
        @RequestBody @Valid NotebookEntryTemplateFormDto dto) {

        User user = getAuthenticatedUser();
        if (!user.isAdmin()){
            throw new InsufficientPrivilegesException("User does not have sufficient privileges "
                + "to perform this action: " + user.getUsername());
        }

        if (!entryTemplateService.exists(id)) {
            throw new RecordNotFoundException("Cannot find notebook entry template with ID: " + id);
        }

        NotebookEntryTemplate notebookEntryTemplate = mapper.fromForm(dto);
        getTemplateById(notebookEntryTemplate.getId());
        entryTemplateService.update(notebookEntryTemplate);

        Activity activity = EntryTemplateActivityUtils.fromUpdatedEntryTemplate(notebookEntryTemplate, user);
        activityService.create(activity);
        eventsService.dispatchEvent(activity);

        return new ResponseEntity<>(mapper.toDetails(notebookEntryTemplate), HttpStatus.CREATED);
    }

}
