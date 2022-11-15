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

package io.studytracker.service;

import io.studytracker.exception.FileStorageException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.FileStorageLocation;
import io.studytracker.model.FileStoreFolder;
import io.studytracker.repository.FileStorageLocationRepository;
import io.studytracker.storage.DataFileStorageService;
import io.studytracker.storage.DataFileStorageServiceLookup;
import io.studytracker.storage.StorageLocationType;
import io.studytracker.storage.StudyFileStorageServiceLookup;
import io.studytracker.storage.StudyStorageService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StorageLocationService {

  public static final Logger LOGGER = LoggerFactory.getLogger(StorageLocationService.class);

  @Autowired
  private FileStorageLocationRepository fileStorageLocationRepository;

  @Autowired
  private DataFileStorageServiceLookup dataFileStorageServiceLookup;

  @Autowired
  private StudyFileStorageServiceLookup studyFileStorageServiceLookup;

  public List<FileStorageLocation> findAll() {
    return fileStorageLocationRepository.findAll();
  }

  public FileStorageLocation findDefaultStudyLocation() throws FileStorageException {
    Optional<FileStorageLocation> optional = this.findDefaultStudyLocationByType(StorageLocationType.EGNYTE_API);
    if (optional.isPresent()) return optional.get();
    optional = this.findDefaultStudyLocationByType(StorageLocationType.LOCAL_FILE_SYSTEM);
    if (optional.isPresent()) {
      return optional.get();
    } else {
      throw new FileStorageException("Cannot find appropriate default study storage location.");
    }
  }

  public Optional<FileStorageLocation> findDefaultStudyLocationByType(StorageLocationType type)
      throws FileStorageException {
    List<FileStorageLocation> locations = fileStorageLocationRepository.findByDefaultStudyLocationByType(type);
    if (locations.isEmpty()) {
      return Optional.empty();
    } else if (locations.size() > 1) {
      throw new FileStorageException("Multiple default study locations have been set. Change the "
          + "default study location to one location per type.");
    } else {
      return Optional.of(locations.get(0));
    }
  }

  public FileStorageLocation findDefaultDataLocation() throws FileStorageException {
    List<FileStorageLocation> locations = fileStorageLocationRepository.findByDefaultDataLocation();
    if (locations.isEmpty()) {
      throw new FileStorageException("No default data location has been set.");
    } else if (locations.size() > 1) {
      throw new FileStorageException("Multiple default data locations have been set. Change the "
          + "default data location to one location.");
    } else {
      return locations.get(0);
    }
  }

  public FileStorageLocation findByFileStoreFolder(FileStoreFolder folder) {
    return fileStorageLocationRepository.findByFileStoreFolderId(folder.getId());
  }

  public List<FileStorageLocation> findByType(StorageLocationType type) {
    return fileStorageLocationRepository.findByType(type);
  }

  public Optional<FileStorageLocation> findById(Long id) {
    return fileStorageLocationRepository.findById(id);
  }

  @Transactional
  public FileStorageLocation create(FileStorageLocation location) {
    return fileStorageLocationRepository.save(location);
  }

  @Transactional
  public FileStorageLocation update(FileStorageLocation location) {
    FileStorageLocation f = fileStorageLocationRepository.getById(location.getId());
    f.setName(location.getName());
    f.setDisplayName(location.getDisplayName());
    f.setRootFolderPath(location.getRootFolderPath());
    f.setPermissions(location.getPermissions());
    f.setDefaultDataLocation(location.isDefaultDataLocation());
    f.setDefaultStudyLocation(location.isDefaultStudyLocation());
    f.setReferenceId(location.getReferenceId());
    f.setUrl(location.getUrl());
    f.setActive(location.isActive());
    return fileStorageLocationRepository.save(f);
  }

  @Transactional
  public void remove(Long id) {
    FileStorageLocation f = fileStorageLocationRepository.getById(id);
    f.setActive(false);
    fileStorageLocationRepository.save(f);
  }

  public StudyStorageService lookupStudyStorageService(FileStorageLocation location) {
    return studyFileStorageServiceLookup.lookup(location.getType())
        .orElseThrow(() -> new IllegalArgumentException("No storage service found for type: "
            + location.getType()));
  }

  public StudyStorageService lookupStudyStorageService(FileStoreFolder folder) {
    FileStorageLocation location = fileStorageLocationRepository
        .findById(folder.getFileStorageLocation().getId())
        .orElseThrow(() -> new RecordNotFoundException("File storage location not found: "
            + folder.getFileStorageLocation().getId()));
    return lookupStudyStorageService(location);
  }

  public DataFileStorageService lookupDataFileStorageService(FileStorageLocation location) {
    return dataFileStorageServiceLookup.lookup(location.getType())
        .orElseThrow(() -> new IllegalArgumentException("No storage service found for type: "
            + location.getType()));
  }

  public DataFileStorageService lookupDataStorageService(FileStoreFolder folder) {
    FileStorageLocation location = fileStorageLocationRepository
        .findById(folder.getFileStorageLocation().getId())
        .orElseThrow(() -> new RecordNotFoundException("File storage location not found: "
            + folder.getFileStorageLocation().getId()));
    return lookupDataFileStorageService(location);
  }

}
