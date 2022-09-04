package io.studytracker.controller.api.v1;

import io.studytracker.controller.api.AbstractAssayTypeController;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.AssayTypeDto;
import io.studytracker.mapstruct.dto.api.AssayTypePayloadDto;
import io.studytracker.model.AssayType;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/assay-type")
public class AssayTypePublicController extends AbstractAssayTypeController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssayTypePublicController.class);

  @GetMapping("")
  public Page<AssayTypeDto> findAll(Pageable pageable) {
    LOGGER.debug("Find all assay types");
    Page<AssayType> page = this.getAssayTypeService().findAll(pageable);
    return new PageImpl<>(this.getAssayTypeMapper().toDtoList(page.getContent()), pageable, page.getTotalElements());
  }

  @GetMapping("/{id}")
  public AssayTypeDto findById(@PathVariable Long id) {
    AssayType assayType = this.getAssayTypeService().findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Cannot find assay type with ID: " + id));
    return this.getAssayTypeMapper().toDto(assayType);
  }

  @PostMapping("")
  public HttpEntity<AssayTypeDto> create(@Valid @RequestBody AssayTypePayloadDto dto) {
    LOGGER.info("Creating new assay type: {}", dto);
    AssayType assayType = this.createNewAssayType(this.getAssayTypeMapper().fromPayloadDto(dto));
    return new ResponseEntity<>(this.getAssayTypeMapper().toDto(assayType), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<AssayTypeDto> update(
      @PathVariable Long id, @Valid @RequestBody AssayTypePayloadDto dto) {
    LOGGER.info("Updating assay type with id {}: {}", id, dto);
    AssayType assayType = this.updateExistingAssayType(this.getAssayTypeMapper().fromPayloadDto(dto));
    return new ResponseEntity<>(this.getAssayTypeMapper().toDto(assayType), HttpStatus.OK);
  }

  @PatchMapping("/{id}")
  public HttpEntity<?> patch(
      @PathVariable("id") Long id, @RequestParam(required = false) Boolean active) {
    AssayType assayType =
        this.getAssayTypeService()
            .findById(id)
            .orElseThrow(() -> new RecordNotFoundException("Cannot find assay type: " + id));
    if (active != null) {
      assayType.setActive(active);
      this.updateExistingAssayType(assayType);
      return new ResponseEntity<>(HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> delete(@PathVariable Long id) {
    LOGGER.info("Deleting assay type: " + id);
    AssayType assayType = this.getAssayTypeService()
        .findById(id).orElseThrow(RecordNotFoundException::new);
    this.deleteAssayType(assayType);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
