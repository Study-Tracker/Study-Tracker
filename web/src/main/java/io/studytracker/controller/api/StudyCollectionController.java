package io.studytracker.controller.api;

import io.studytracker.events.EventsService;
import io.studytracker.events.util.StudyCollectionActivityUtils;
import io.studytracker.exception.InsufficientPrivilegesException;
import io.studytracker.exception.InvalidConstraintException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.response.StudyCollectionDetailsDto;
import io.studytracker.mapstruct.dto.response.StudyCollectionSummaryDto;
import io.studytracker.mapstruct.mapper.StudyCollectionMapper;
import io.studytracker.model.Activity;
import io.studytracker.model.Study;
import io.studytracker.model.StudyCollection;
import io.studytracker.model.User;
import io.studytracker.service.ActivityService;
import io.studytracker.service.StudyCollectionService;
import io.studytracker.service.StudyService;
import io.studytracker.service.UserService;
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
public class StudyCollectionController extends AbstractAPIController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyCollectionController.class);

  @Autowired private StudyCollectionService studyCollectionService;

  @Autowired private UserService userService;

  @Autowired private StudyService studyService;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private StudyCollectionMapper mapper;

  @Autowired private ActivityService activityService;

  @Autowired private EventsService eventsService;

  @GetMapping("")
  public List<StudyCollectionSummaryDto> getStudyCollections(
      @RequestParam(name = "userId", required = false) Long userId,
      @RequestParam(name = "studyId", required = false) Long studyId
  ) {

    List<StudyCollection> collections;
    User authenticatedUser = this.getAuthenticatedUser();
    if (userId != null) {
      User user =
          userService
              .findById(userId)
              .orElseThrow(() -> new InvalidConstraintException("User does not exist: " + userId));
      collections = studyCollectionService.findByUser(user);
    } else if (studyId != null) {
      Study study =
          studyService
              .findById(studyId)
              .orElseThrow(
                  () -> new InvalidConstraintException("Study does not exist: " + studyId));
      collections = studyCollectionService.findByStudy(study);
    } else {
      collections = studyCollectionService.findAll();
    }

    // Filter out private collections
    collections =
        collections.stream()
            .filter(
                c ->
                    c.isShared()
                        || c.getCreatedBy().getId().equals(authenticatedUser.getId())
                        || authenticatedUser.isAdmin())
            .collect(Collectors.toList());

    return mapper.toSummaryDtoList(collections);
  }

  @GetMapping("/{id}")
  public StudyCollectionDetailsDto findById(@PathVariable("id") Long id) {

    Optional<StudyCollection> optional = studyCollectionService.findById(id);
    User user = this.getAuthenticatedUser();

    if (optional.isPresent()) {
      StudyCollection collection = optional.get();
      if (collection.isShared()
          || collection.getCreatedBy().getId().equals(user.getId())
          || user.isAdmin()) {
        return mapper.toDetailsDto(collection);
      }
    }

    throw new RecordNotFoundException("Could not find study collection: " + id);
  }

  @PostMapping("")
  public HttpEntity<StudyCollectionSummaryDto> createCollection(
      @RequestBody @Valid StudyCollectionSummaryDto payload
  ) {

    LOGGER.info("Creating new study collections: " + payload.toString());
    User user = this.getAuthenticatedUser();
    StudyCollection collection = mapper.fromSummaryDto(payload);

    // Make sure a collection owned by the same user does not exist already
    if (studyCollectionService.collectionWithNameExists(collection, user)) {
      throw new InvalidConstraintException("A study collection with this name already exists.");
    }

    studyCollectionService.create(collection);

    // Publish the event
    Activity activity =
        StudyCollectionActivityUtils.fromNewStudyCollection(collection, user);
    activityService.create(activity);
    eventsService.dispatchEvent(activity);

    return new ResponseEntity<>(mapper.toSummaryDto(collection), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<StudyCollectionSummaryDto> updateCollection(
      @PathVariable("id") Long id,
      @RequestBody @Valid StudyCollectionSummaryDto dto
  ) {

    LOGGER.info("Attempting to update existing study collections: " + dto.toString());

    studyCollectionService
        .findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Study collection not found: " + id));
    StudyCollection collection = mapper.fromSummaryDto(dto);
    User user = this.getAuthenticatedUser();

    // If collections is not public, only owner can edit
    if (!dto.isShared() && !user.getId().equals(collection.getCreatedBy().getId())) {
      throw new InsufficientPrivilegesException(
          "You do not have permission to modify this study collection.");
    }

    // Make sure a collection owned by the same user does not exist already
    if (studyCollectionService.collectionWithNameExists(collection, user)) {
      throw new InvalidConstraintException("A study collection with this name already exists.");
    }

    studyCollectionService.update(collection);

    StudyCollection output =
        studyCollectionService
            .findById(id)
            .orElseThrow(() -> new RecordNotFoundException("Study collection not found: " + id));

    // Publish the event
    Activity activity =
        StudyCollectionActivityUtils.fromUpdatedStudyCollection(collection, user);
    activityService.create(activity);
    eventsService.dispatchEvent(activity);

    return new ResponseEntity<>(mapper.toSummaryDto(output), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> deleteCollection(@PathVariable("id") Long id) {

    LOGGER.info("Attempting to delete study collection: " + id);

    StudyCollection collection =
        studyCollectionService
            .findById(id)
            .orElseThrow(() -> new RecordNotFoundException("Study collection not found: " + id));
    User user = this.getAuthenticatedUser();
    if (!user.getId().equals(collection.getCreatedBy().getId())) {
      throw new InsufficientPrivilegesException("Only the study collection owner may delete it.");
    }

    studyCollectionService.delete(collection);

    // Publish the event
    Activity activity =
        StudyCollectionActivityUtils.fromDeletedStudyCollection(collection, user);
    activityService.create(activity);
    eventsService.dispatchEvent(activity);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/{collectionId}/{studyId}")
  public HttpEntity<?> addStudyToCollection(
      @PathVariable("collectionId") Long collectionId,
      @PathVariable("studyId") Long studyId
  ) {

    StudyCollection collection =
        studyCollectionService
            .findById(collectionId)
            .orElseThrow(
                () -> new RecordNotFoundException("Study collection not found: " + collectionId));
    Study study =
        studyService
            .findById(studyId)
            .orElseThrow(() -> new RecordNotFoundException("Study does not exist: " + studyId));

    collection.addStudy(study);
    studyCollectionService.update(collection);

    // Publish the event
    Activity activity =
        StudyCollectionActivityUtils.fromStudyAddedToCollection(
            study, collection, this.getAuthenticatedUser());
    activityService.create(activity);
    eventsService.dispatchEvent(activity);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/{collectionId}/{studyId}")
  public HttpEntity<?> removeStudyFromCollection(
      @PathVariable("collectionId") Long collectionId,
      @PathVariable("studyId") Long studyId
  ) {

    StudyCollection collection =
        studyCollectionService
            .findById(collectionId)
            .orElseThrow(
                () -> new RecordNotFoundException("Study collection not found: " + collectionId));
    Study study =
        studyService
            .findById(studyId)
            .orElseThrow(() -> new RecordNotFoundException("Study does not exist: " + studyId));

    collection.removeStudy(study);
    studyCollectionService.update(collection);

    // Publish the event
    Activity activity =
        StudyCollectionActivityUtils.fromStudyRemovedFromCollection(
            study, collection, this.getAuthenticatedUser());
    activityService.create(activity);
    eventsService.dispatchEvent(activity);

    return new ResponseEntity<>(HttpStatus.OK);
  }
}
