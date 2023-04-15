/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.studytracker.controller.api.internal;

import io.studytracker.mapstruct.dto.response.StorageDriveDetailsDto;
import io.studytracker.mapstruct.mapper.StorageDriveMapper;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDrive.DriveType;
import io.studytracker.storage.StorageDriveFolderService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/storage-drives")
public class StorageDrivePrivateController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StorageDrivePrivateController.class);

  @Autowired
  private StorageDriveMapper mapper;

  @Autowired
  private StorageDriveFolderService storageDriveFolderService;

  @GetMapping("")
  public List<StorageDriveDetailsDto> findDrives(
      @RequestParam(name = "driveType", required = false) DriveType driveType) {
    LOGGER.debug("Fetching all storage drives for organization");
    List<StorageDrive> drives;
    if (driveType != null) {
      drives = storageDriveFolderService.findAllDrives()
          .stream()
          .filter(d -> d.getDriveType().equals(driveType))
          .collect(Collectors.toList());
    } else {
      drives = storageDriveFolderService.findAllDrives();
    }
    return mapper.toDetailsDto(drives);
  }

  @GetMapping("/{id}")
  public HttpEntity<StorageDriveDetailsDto> findDriveById(@PathVariable("id") Long id) {
    LOGGER.debug("Fetching storage drive with id: {}", id);
    Optional<StorageDrive> optional = storageDriveFolderService.findDriveById(id);
    if (optional.isPresent()) {
      return new ResponseEntity<>(mapper.toDetailsDto(optional.get()), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

}
