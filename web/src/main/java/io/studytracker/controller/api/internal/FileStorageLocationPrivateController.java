package io.studytracker.controller.api.internal;

import io.studytracker.controller.api.AbstractApiController;
import io.studytracker.events.util.StorageActivityUtils;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.form.FileStorageLocationFormDto;
import io.studytracker.mapstruct.dto.response.FileStorageLocationDetailsDto;
import io.studytracker.mapstruct.mapper.FileStorageLocationMapper;
import io.studytracker.model.Activity;
import io.studytracker.model.FileStorageLocation;
import io.studytracker.service.StorageLocationService;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/internal/storage-locations")
@RestController
public class FileStorageLocationPrivateController extends AbstractApiController {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageLocationPrivateController.class);

  @Autowired
  private StorageLocationService storageLocationService;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private FileStorageLocationMapper mapper;

  @GetMapping("")
  public List<FileStorageLocation> findAll() {
    LOGGER.debug("findAll()");
    return storageLocationService.findAll().stream()
        .filter(FileStorageLocation::isActive)
        .collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  public FileStorageLocationDetailsDto findById(@PathVariable("id") Long id) {
    LOGGER.debug("findById({})", id);
    FileStorageLocation fileStorageLocation = storageLocationService.findById(id)
        .orElseThrow(() -> new RecordNotFoundException("File storage location not found: " + id));
    return mapper.toDetails(fileStorageLocation);
  }

  @PostMapping("")
  public HttpEntity<FileStorageLocationDetailsDto> create(@Valid @RequestBody FileStorageLocationFormDto dto) {
    LOGGER.info("Creating new file storage location: {}", dto);
    FileStorageLocation location
        = storageLocationService.create(mapper.fromForm(dto));
    Activity activity = StorageActivityUtils
        .fromNewStorageLocation(location, this.getAuthenticatedUser());
    this.logActivity(activity);
    return new ResponseEntity<>(mapper.toDetails(location), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<FileStorageLocationDetailsDto> create(@PathVariable("id") Long id,
      @Valid @RequestBody FileStorageLocationFormDto dto) {
    LOGGER.info("Updating existing file storage location: {} {}", id, dto);
    FileStorageLocation location
        = storageLocationService.update(mapper.fromForm(dto));
    Activity activity = StorageActivityUtils
        .fromUpdatedStorageLocation(location, this.getAuthenticatedUser());
    this.logActivity(activity);
    return new ResponseEntity<>(mapper.toDetails(location), HttpStatus.OK);
  }

}
