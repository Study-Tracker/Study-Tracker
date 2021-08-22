package com.decibeltx.studytracker.controller.api;

import com.decibeltx.studytracker.controller.UserAuthenticationUtils;
import com.decibeltx.studytracker.events.EventsService;
import com.decibeltx.studytracker.events.util.StudyCollectionActivityUtils;
import com.decibeltx.studytracker.exception.InsufficientPrivilegesException;
import com.decibeltx.studytracker.exception.InvalidConstraintException;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.mapstruct.dto.StudyCollectionDetailsDto;
import com.decibeltx.studytracker.mapstruct.dto.StudyCollectionSummaryDto;
import com.decibeltx.studytracker.mapstruct.mapper.StudyCollectionMapper;
import com.decibeltx.studytracker.model.Activity;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.StudyCollection;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.service.ActivityService;
import com.decibeltx.studytracker.service.StudyCollectionService;
import com.decibeltx.studytracker.service.StudyService;
import com.decibeltx.studytracker.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/studycollection")
public class StudyCollectionController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyCollectionController.class);

  @Autowired
  private StudyCollectionService studyCollectionService;

  @Autowired
  private UserService userService;

  @Autowired
  private StudyService studyService;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private StudyCollectionMapper mapper;

  @Autowired
  private ActivityService activityService;

  @Autowired
  private EventsService eventsService;

  @GetMapping("")
  public List<StudyCollectionSummaryDto> getStudyCollections(
      @RequestParam(name = "userId", required = false) Long userId,
      @RequestParam(name = "studyId", required = false) Long studyId
  ) {

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User currentUser = userService.findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    List<StudyCollection> collections;

    if (userId != null) {
      User user = userService.findById(userId)
          .orElseThrow(() -> new InvalidConstraintException("User does not exist: " + userId));
      collections = studyCollectionService.findByUser(user);
    } else if (studyId != null) {
      Study study = studyService.findById(studyId)
          .orElseThrow(() -> new InvalidConstraintException("Study does not exist: " + studyId));
      collections = studyCollectionService.findByStudy(study);
    } else {
      collections = studyCollectionService.findAll();
    }

    // Filter out private collections
    collections = collections.stream()
        .filter(c -> c.isShared()
            || c.getCreatedBy().getId().equals(currentUser.getId())
            || currentUser.isAdmin())
        .collect(Collectors.toList());

    return mapper.toSummaryDtoList(collections);
  }

  @GetMapping("/{id}")
  public StudyCollectionDetailsDto findById(@PathVariable("id") Long id) {

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User currentUser = userService.findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    Optional<StudyCollection> optional = studyCollectionService.findById(id);

    if (optional.isPresent()) {
      StudyCollection collection = optional.get();
      if (collection.isShared()
          || collection.getCreatedBy().getId().equals(currentUser.getId())
          || currentUser.isAdmin()) {
        return mapper.toDetailsDto(collection);
      }
    }

    throw new RecordNotFoundException("Could not find study collection: " + id);

  }

  @PostMapping("")
  public HttpEntity<StudyCollectionSummaryDto> createCollection(@RequestBody @Valid StudyCollectionSummaryDto payload) {

    LOGGER.info("Creating new study collections: " + payload.toString());

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User currentUser = userService.findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    StudyCollection collection = mapper.fromSummaryDto(payload);

    // Make sure a collection owned by the same user does not exist already
    if (studyCollectionService.collectionWithNameExists(collection, currentUser)) {
      throw new InvalidConstraintException("A study collection with this name already exists.");
    }

    studyCollectionService.create(collection);

    // Publish the event
    Activity activity = StudyCollectionActivityUtils.fromNewStudyCollection(collection, currentUser);
    activityService.create(activity);
    eventsService.dispatchEvent(activity);

    return new ResponseEntity<>(mapper.toSummaryDto(collection), HttpStatus.CREATED);

  }

  @PutMapping("/{id}")
  public HttpEntity<StudyCollectionSummaryDto> updateCollection(@PathVariable("id") Long id,
      @RequestBody @Valid StudyCollectionSummaryDto dto) {

    LOGGER.info("Attempting to update existing study collections: " + dto.toString());

    studyCollectionService.findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Study collection not found: " + id));
    StudyCollection collection = mapper.fromSummaryDto(dto);

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User currentUser = userService.findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    // If collections is not public, only owner can edit
    if (!dto.isShared() && !currentUser.getId().equals(collection.getCreatedBy().getId())) {
      throw new InsufficientPrivilegesException("You do not have permission to modify this study collection.");
    }

    // Make sure a collection owned by the same user does not exist already
    if (studyCollectionService.collectionWithNameExists(collection, currentUser)) {
      throw new InvalidConstraintException("A study collection with this name already exists.");
    }

    studyCollectionService.update(collection);

    StudyCollection output = studyCollectionService.findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Study collection not found: " + id));

    // Publish the event
    Activity activity = StudyCollectionActivityUtils.fromUpdatedStudyCollection(collection, currentUser);
    activityService.create(activity);
    eventsService.dispatchEvent(activity);

    return new ResponseEntity<>(mapper.toSummaryDto(output), HttpStatus.OK);

  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> deleteCollection(@PathVariable("id") Long id) {

    LOGGER.info("Attempting to delete study collection: " + id);

    StudyCollection collection = studyCollectionService.findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Study collection not found: " + id));

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User currentUser = userService.findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    if (!currentUser.getId().equals(collection.getCreatedBy().getId())) {
      throw new InsufficientPrivilegesException("Only the study collection owner may delete it.");
    }

    studyCollectionService.delete(collection);

    // Publish the event
    Activity activity = StudyCollectionActivityUtils.fromDeletedStudyCollection(collection, currentUser);
    activityService.create(activity);
    eventsService.dispatchEvent(activity);

    return new ResponseEntity<>(HttpStatus.OK);

  }

  @PostMapping("/{collectionId}/{studyId}")
  public HttpEntity<?> addStudyToCollection(@PathVariable("collectionId") Long collectionId,
      @PathVariable("studyId") Long studyId) {

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User currentUser = userService.findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    StudyCollection collection = studyCollectionService.findById(collectionId)
        .orElseThrow(() -> new RecordNotFoundException("Study collection not found: " + collectionId));
    Study study = studyService.findById(studyId)
        .orElseThrow(() -> new RecordNotFoundException("Study does not exist: " + studyId));

    collection.addStudy(study);
    studyCollectionService.update(collection);

    // Publish the event
    Activity activity = StudyCollectionActivityUtils.fromStudyAddedToCollection(study, collection, currentUser);
    activityService.create(activity);
    eventsService.dispatchEvent(activity);

    return new ResponseEntity<>(HttpStatus.OK);

  }

  @DeleteMapping("/{collectionId}/{studyId}")
  public HttpEntity<?> removeStudyFromCollection(@PathVariable("collectionId") Long collectionId,
      @PathVariable("studyId") Long studyId) {

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User currentUser = userService.findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    StudyCollection collection = studyCollectionService.findById(collectionId)
        .orElseThrow(() -> new RecordNotFoundException("Study collection not found: " + collectionId));
    Study study = studyService.findById(studyId)
        .orElseThrow(() -> new RecordNotFoundException("Study does not exist: " + studyId));

    collection.removeStudy(study);
    studyCollectionService.update(collection);

    // Publish the event
    Activity activity = StudyCollectionActivityUtils.fromStudyRemovedFromCollection(study, collection, currentUser);
    activityService.create(activity);
    eventsService.dispatchEvent(activity);

    return new ResponseEntity<>(HttpStatus.OK);

  }

}
