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

package io.studytracker.egnyte;

import io.studytracker.egnyte.entity.EgnyteFile;
import io.studytracker.egnyte.entity.EgnyteFolder;
import io.studytracker.egnyte.entity.EgnyteObject;
import io.studytracker.egnyte.exception.EgnyteException;
import io.studytracker.egnyte.integration.EgnyteIntegrationOptions;
import io.studytracker.egnyte.integration.EgnyteIntegrationV1;
import io.studytracker.egnyte.rest.EgnyteRestApiClient;
import io.studytracker.exception.InsufficientPrivilegesException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.FileStorageLocation;
import io.studytracker.model.IntegrationInstance;
import io.studytracker.repository.IntegrationInstanceRepository;
import io.studytracker.storage.DataFileStorageService;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StoragePermissions;
import io.studytracker.storage.StorageUtils;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.io.File;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

public class EgnyteApiDataFileStorageService implements DataFileStorageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(EgnyteApiDataFileStorageService.class);

  @Autowired
  private EgnyteRestApiClient client;

  @Autowired
  private IntegrationInstanceRepository integrationInstanceRepository;

  private EgnyteIntegrationOptions getOptionsFromLocation(FileStorageLocation location) {
    Optional<IntegrationInstance> optional = integrationInstanceRepository
        .findById(location.getIntegrationInstance().getId());
    if (optional.isPresent()) {
      return new EgnyteIntegrationV1(optional.get());
    } else {
      throw new RecordNotFoundException("Integration instance not found: "
          + location.getIntegrationInstance().getId());
    }
  }

  @Override
  public StorageFolder findFolderByPath(FileStorageLocation location, String path)
      throws StudyStorageNotFoundException {
    LOGGER.debug("Looking up folder by path: {}", path);
    try {
      EgnyteObject egnyteObject = client.findObjectByPath(path, -1);
      if (egnyteObject.isFolder()) {
        EgnyteFolder folder = (EgnyteFolder) egnyteObject;
        return EgnyteUtils.convertEgnyteFolderWithContents(folder, location.getRootFolderPath());
      } else {
        throw new EgnyteException("Object is not a folder: " + path);
      }
    } catch (EgnyteException e) {
      e.printStackTrace();
      LOGGER.error("Failed to lookup folder by path: {}", path, e);
      throw new StudyStorageNotFoundException("Failed to lookup folder by path: " + path, e);
    }
  }

  @Override
  public StorageFile findFileByPath(FileStorageLocation location, String path)
      throws StudyStorageNotFoundException {
    LOGGER.debug("Finding file by path: {}", path);
    try {
      EgnyteObject egnyteObject = client.findObjectByPath(path, -1);
      if (!egnyteObject.isFolder()) {
        EgnyteFile file = (EgnyteFile) egnyteObject;
        return EgnyteUtils.convertEgnyteFile(file);
      } else {
        throw new EgnyteException("Object is not a file: " + path);
      }
    } catch (EgnyteException e) {
      e.printStackTrace();
      LOGGER.error("Error while finding file by path", e);
      throw new StudyStorageNotFoundException("Error while finding file by path", e);
    }
  }

  @Override
  public StorageFolder createFolder(FileStorageLocation location, String path, String name)
      throws StudyStorageException {
    LOGGER.info("Creating folder: {} in {}", name, path);
    try {
      if (!StoragePermissions.canWrite(location.getPermissions())) {
        throw new InsufficientPrivilegesException("Insufficient privileges to create folder");
      }
      EgnyteFolder folder = client.createFolder(StorageUtils.joinPath(path, name));
      return EgnyteUtils.convertEgnyteFolder(folder);
    } catch (EgnyteException e) {
      e.printStackTrace();
      LOGGER.error("Error while creating folder", e);
      throw new StudyStorageException("Error while creating folder", e);
    }
  }

  @Override
  public StorageFile saveFile(FileStorageLocation location, String path, File file)
      throws StudyStorageException {
    LOGGER.info("Uploading file: {} to {}", file.getName(), path);
    try {
      if (!StoragePermissions.canWrite(location.getPermissions())) {
        throw new InsufficientPrivilegesException("Insufficient privileges to write files.");
      }
      EgnyteFile egnyteFile = client.uploadFile(file, path);
      return EgnyteUtils.convertEgnyteFile(egnyteFile);
    } catch (EgnyteException e) {
      e.printStackTrace();
      LOGGER.error("Error while uploading file", e);
      throw new StudyStorageException("Error while uploading file", e);
    }
  }

  @Override
  public Resource fetchFile(FileStorageLocation location, String path)
      throws StudyStorageException {
    LOGGER.info("Downloading file: {}", path);
    throw new StudyStorageException("Not implemented");
  }
}
