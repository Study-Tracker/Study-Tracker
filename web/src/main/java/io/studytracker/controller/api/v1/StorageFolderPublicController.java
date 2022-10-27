/*
 * Copyright 2022 the original author or authors.
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

package io.studytracker.controller.api.v1;

import io.studytracker.controller.api.AbstractApiController;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.FileStoreFolderDto;
import io.studytracker.mapstruct.mapper.FileStoreFolderMapper;
import io.studytracker.model.FileStoreFolder;
import io.studytracker.repository.FileStoreFolderRepository;
import io.studytracker.service.FileSystemStorageService;
import io.studytracker.storage.StudyStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/storage-folder")
public class StorageFolderPublicController extends AbstractApiController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StorageFolderPublicController.class);

  @Autowired
  private FileSystemStorageService fileStorageService;

  @Autowired
  private StudyStorageService studyStorageService;

  @Autowired
  private FileStoreFolderRepository fileStoreFolderRepository;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private FileStoreFolderMapper  fileStoreFolderMapper;

  @GetMapping("")
  public Page<FileStoreFolderDto> findAllStorageFolder(Pageable pageable) {
    LOGGER.debug("Fethching all storage folders");
    Page<FileStoreFolder> page = fileStoreFolderRepository.findAll(pageable);
    return new PageImpl<>(fileStoreFolderMapper.toDto(page.getContent()), pageable, page.getTotalElements());
  }

  @GetMapping("/{id}")
  public FileStoreFolderDto findById(@PathVariable Long id) {
    LOGGER.debug("Fetching storage folder with id {}", id);
    FileStoreFolder folder = fileStoreFolderRepository.findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Cannot file folder with ID: " + id));
    return fileStoreFolderMapper.toDto(folder);
  }

//  @PostMapping("/{id}/upload")
//  public HttpEntity<?> uploadFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
//    LOGGER.info("Uploading file: " + file.getOriginalFilename());
//    FileStoreFolder folder =
//        fileStoreFolderRepository
//            .findById(id)
//            .orElseThrow(() -> new RecordNotFoundException("Cannot file folder with ID: " + id));
//    Path path;
//    try {
//      path = fileStorageService.store(file);
//      LOGGER.info(path.toString());
//    } catch (FileStorageException e) {
//      e.printStackTrace();
//      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//    StorageFolder storageFolder = new StorageFolder();
//    storageFolder.setName(folder.getName());
//    storageFolder.setPath(folder.getPath());
//    storageFolder.setUrl(folder.getUrl());
//    try {
//      studyStorageService.saveFile(path.toFile(), storageFolder);
//      return new ResponseEntity<>(HttpStatus.OK);
//    } catch (StudyStorageException e) {
//      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//  }

}
