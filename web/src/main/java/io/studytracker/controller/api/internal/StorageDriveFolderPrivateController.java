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

import io.studytracker.mapstruct.dto.form.StorageDriveFolderFormDto;
import io.studytracker.mapstruct.dto.response.StorageDriveFolderDetailsDto;
import io.studytracker.mapstruct.mapper.StorageDriveFolderMapper;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.storage.StorageDriveFolderService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/storage-drive-folders")
public class StorageDriveFolderPrivateController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StorageDriveFolderPrivateController.class);

  @Autowired
  private StorageDriveFolderMapper mapper;

  @Autowired
  private StorageDriveFolderService storageDriveFolderService;

  @GetMapping("")
  public List<StorageDriveFolderDetailsDto> findFolders(
      @RequestParam(name = "studyRoot",  required = false) boolean studyRoot,
      @RequestParam(name = "browserRoot",  required = false) boolean browserRoot,
      @RequestParam(name = "root", required = false) boolean root
  ) {
    LOGGER.debug("Fetching all storage drive folders for organization");
    List<StorageDriveFolder> folders;
    if (studyRoot) {
      folders = storageDriveFolderService.findStudyRootFolders();
    } else if (browserRoot) {
      folders = storageDriveFolderService.findBrowserRootFolders();
    } else if (root) {
      folders = storageDriveFolderService.findAll().stream()
          .filter(f -> f.isBrowserRoot() || f.isStudyRoot())
          .collect(Collectors.toList());
    } else {
      folders = storageDriveFolderService.findAll();
    }
    return mapper.toDetailsDto(folders);
  }

  @GetMapping("/{id}")
  public HttpEntity<StorageDriveFolderDetailsDto> findFolderById(@PathVariable("id") Long id) {
    LOGGER.debug("Fetching storage drive folder with id: {}", id);
    Optional<StorageDriveFolder> optional = storageDriveFolderService.findById(id);
    if (optional.isPresent()) {
      return new ResponseEntity<>(mapper.toDetailsDto(optional.get()), HttpStatus.OK);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping("")
  public HttpEntity<StorageDriveFolderDetailsDto> createFolder(@Valid @RequestBody StorageDriveFolderFormDto dto) {
    LOGGER.debug("Creating new storage drive folder: {}", dto);
    StorageDriveFolder folder = mapper.fromFormDto(dto);
    StorageDrive drive = storageDriveFolderService.findDriveById(dto.getStorageDriveId())
        .orElseThrow(() -> new IllegalArgumentException("Storage drive not found"));
    StorageDriveFolder saved = storageDriveFolderService.registerFolder(folder, drive);
    return new ResponseEntity<>(mapper.toDetailsDto(saved), HttpStatus.CREATED);
  }

}
