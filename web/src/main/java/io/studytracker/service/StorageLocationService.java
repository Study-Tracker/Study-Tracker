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

import io.studytracker.model.FileStorageLocation;
import io.studytracker.repository.FileStorageLocationRepository;
import io.studytracker.storage.StorageLocationType;
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

  public List<FileStorageLocation> findAll() {
    return fileStorageLocationRepository.findAll();
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
    f.setRootFolderPath(location.getRootFolderPath());
    f.setPermissions(location.getPermissions());
    f.setDefaultDataLocation(location.isDefaultDataLocation());
    f.setDefaultStudyLocation(location.isDefaultStudyLocation());
    f.setReferenceId(location.getReferenceId());
    f.setUrl(location.getUrl());
    f.setDisplayName(location.getDisplayName());
    f.setActive(location.isActive());
    return fileStorageLocationRepository.save(f);
  }

  @Transactional
  public void remove(Long id) {
    FileStorageLocation f = fileStorageLocationRepository.getById(id);
    f.setActive(false);
    fileStorageLocationRepository.save(f);
  }

}
