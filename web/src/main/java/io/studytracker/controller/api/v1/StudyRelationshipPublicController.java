package io.studytracker.controller.api.v1;

import io.studytracker.controller.api.AbstractStudyRelationshipController;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.StudyRelationshipDto;
import io.studytracker.mapstruct.dto.api.StudyRelationshipPayloadDto;
import io.studytracker.model.Study;
import io.studytracker.model.StudyRelationship;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/study-relationship")
public class StudyRelationshipPublicController extends AbstractStudyRelationshipController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyRelationshipPublicController.class);

  @GetMapping("")
  public Page<StudyRelationshipDto> findAll(
      @RequestParam(required = false) Long studyId, Pageable pageable) {
    LOGGER.debug("Finding all study relationships for study {}", studyId);
    Page<StudyRelationship> relationships = this.getStudyRelationshipService().findAll(pageable);
    List<StudyRelationshipDto> dtos = this.getStudyRelationshipMapper()
        .toDtoList(relationships.getContent());
    return new PageImpl<>(dtos, pageable, relationships.getTotalElements());
  }

  @GetMapping("/{id}")
  public StudyRelationshipDto findById(Long id) {
    LOGGER.debug("Finding study relationship with id {}", id);
    return this.getStudyRelationshipService().findById(id)
        .map(this.getStudyRelationshipMapper()::toDto)
        .orElseThrow(() -> new RecordNotFoundException("Study relationship not found: " + id));
  }

  @PostMapping("")
  public HttpEntity<StudyRelationshipDto> create(@Valid @RequestBody StudyRelationshipPayloadDto dto) {
    LOGGER.info("Creating study relationship {}", dto);

    // Get the studies
    Study sourceStudy = this.getStudyService().findById(dto.getSourceStudyId())
        .orElseThrow(() -> new RecordNotFoundException("Source study not found: " + dto.getSourceStudyId()));
    Study targetStudy = this.getStudyService().findById(dto.getTargetStudyId())
        .orElseThrow(() -> new RecordNotFoundException("Target study not found: " + dto.getTargetStudyId()));

    // Create the relationship
    StudyRelationship relationship = this.createNewStudyRelationship(sourceStudy, targetStudy, dto.getType());

    return new ResponseEntity<>(this.getStudyRelationshipMapper().toDto(relationship), HttpStatus.CREATED);

  }

//  @PutMapping("/{id}")
//  public HttpEntity<?> update(@PathVariable Long id, @Valid @RequestBody Object dto) {
//    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
//  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> delete(@PathVariable Long id) {

    // Make sure the relationship exists
    StudyRelationship relationship = this.getStudyRelationshipService().findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Study relationship not found: " + id));;

    // Get the studies
    Study sourceStudy = this.getStudyService().findById(relationship.getSourceStudy().getId())
        .orElseThrow(() -> new RecordNotFoundException("Source study not found: " + relationship.getSourceStudy().getId()));
    Study targetStudy = this.getStudyService().findById(relationship.getTargetStudy().getId())
        .orElseThrow(() -> new RecordNotFoundException("Target study not found: " + relationship.getTargetStudy().getId()));

    this.deleteStudyRelationship(sourceStudy, targetStudy);

    return new ResponseEntity<>(HttpStatus.OK);
  }

}
