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

package io.studytracker.controller.api.v1;

import io.studytracker.controller.api.AbstractApiController;
import io.studytracker.service.FileSystemStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

//@RestController
@Deprecated
@RequestMapping("/api/v1/storage-folder")
public class StorageFolderPublicController extends AbstractApiController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StorageFolderPublicController.class);

  @Autowired
  private FileSystemStorageService fileStorageService;

  @GetMapping("")
  public HttpEntity<?> findAllStorageFolder(Pageable pageable) {
//    LOGGER.debug("Fethching all storage folders");
//    Page<FileStoreFolder> page = fileStoreFolderRepository.findAll(pageable);
//    return new PageImpl<>(fileStoreFolderMapper.toDtoList(page.getContent()), pageable, page.getTotalElements());
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @GetMapping("/{id}")
  public HttpEntity<?> findById(@PathVariable Long id) {
//    LOGGER.debug("Fetching storage folder with id {}", id);
//    FileStoreFolder folder = fileStoreFolderRepository.findById(id)
//        .orElseThrow(() -> new RecordNotFoundException("Cannot file folder with ID: " + id));
//    return fileStoreFolderMapper.toDto(folder);
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

  }

  @PostMapping("/{id}/upload")
  public HttpEntity<?> uploadFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
//    LOGGER.info("Uploading file: " + file.getOriginalFilename());
//    FileStoreFolder folder =
//        fileStoreFolderRepository
//            .findById(id)
//            .orElseThrow(() -> new RecordNotFoundException("Cannot file folder with ID: " + id));
//    FileStorageLocation location = storageLocationService.findById(folder.getFileStorageLocation().getId())
//        .orElseThrow(() -> new RecordNotFoundException("Cannot file storage location with ID: "
//            + folder.getFileStorageLocation().getId()));
//    DataFileStorageService storageService = storageLocationService.lookupDataFileStorageService(location);
//    Path path;
//    try {
//      path = fileStorageService.store(file);
//      LOGGER.info(path.toString());
//    } catch (FileStorageException e) {
//      e.printStackTrace();
//      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//    }
////    StorageFolder storageFolder = new StorageFolder();
////    storageFolder.setName(folder.getName());
////    storageFolder.setPath(folder.getPath());
////    storageFolder.setUrl(folder.getUrl());
//    try {
//      storageService.saveFile(location, folder.getPath(), path.toFile());
//      return new ResponseEntity<>(HttpStatus.OK);
//    } catch (StudyStorageException e) {
//      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//    }
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

}
