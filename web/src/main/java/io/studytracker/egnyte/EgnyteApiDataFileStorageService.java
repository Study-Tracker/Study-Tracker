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
import io.studytracker.egnyte.rest.EgnyteRestApiClient;
import io.studytracker.storage.DataFileStorageService;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EgnyteApiDataFileStorageService implements DataFileStorageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(EgnyteApiDataFileStorageService.class);

  private final EgnyteRestApiClient client;
  private final EgnyteOptions egnyteOptions;

  public EgnyteApiDataFileStorageService(EgnyteRestApiClient client, EgnyteOptions egnyteOptions) {
    this.client = client;
    this.egnyteOptions = egnyteOptions;
  }

  @Override
  public StorageFolder findFolderByPath(String path) throws StudyStorageNotFoundException {
    LOGGER.debug("Looking up folder by path: {}", path);
    try {
      EgnyteObject egnyteObject = client.findObjectByPath(path, -1);
      if (egnyteObject.isFolder()) {
        EgnyteFolder folder = (EgnyteFolder) egnyteObject;
        return EgnyteUtils.convertEgnyteFolderWithContents(folder, egnyteOptions.getRootPath());
      } else {
        throw new EgnyteException("Object is not a folder: " + path);
      }
    } catch (EgnyteException e) {
      LOGGER.error("Failed to lookup folder by path: {}", path, e);
      throw new StudyStorageNotFoundException("Failed to lookup folder by path: " + path, e);
    }
  }

  @Override
  public StorageFolder findFolderById(String id) throws StudyStorageNotFoundException {
    LOGGER.debug("Finding folder by id: {}", id);
    try {
      EgnyteFolder folder = client.findFolderById(id);
      return EgnyteUtils.convertEgnyteFolderWithContents(folder, egnyteOptions.getRootPath());
    } catch (EgnyteException e) {
      LOGGER.error("Error while finding folder by id", e);
      throw new StudyStorageNotFoundException("Error while finding folder by id", e);
    }
  }

  @Override
  public StorageFile findFileById(String id) throws StudyStorageNotFoundException {
    LOGGER.debug("Finding file by id: {}", id);
    try {
      EgnyteFile file = client.findFileById(id);
      return EgnyteUtils.convertEgnyteFile(file);
    } catch (EgnyteException e) {
      LOGGER.error("Error while finding file by id", e);
      throw new StudyStorageNotFoundException("Error while finding file by id", e);
    }
  }

  @Override
  public StorageFile findFileByPath(String path) throws StudyStorageNotFoundException {
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
      LOGGER.error("Error while finding file by path", e);
      throw new StudyStorageNotFoundException("Error while finding file by path", e);
    }
  }

  @Override
  public StorageFolder createFolder(String path, String name) throws StudyStorageException {
    LOGGER.info("Creating folder: {} in {}", name, path);
    try {
      EgnyteFolder folder = client.createFolder(EgnyteUtils.joinPath(path, name));
      return EgnyteUtils.convertEgnyteFolder(folder);
    } catch (EgnyteException e) {
      LOGGER.error("Error while creating folder", e);
      throw new StudyStorageException("Error while creating folder", e);
    }
  }

  @Override
  public StorageFile uploadFile(String path, File file) throws StudyStorageException {
    LOGGER.info("Uploading file: {} to {}", file.getName(), path);
    try {
      EgnyteFile egnyteFile = client.uploadFile(file, path);
      return EgnyteUtils.convertEgnyteFile(egnyteFile);
    } catch (EgnyteException e) {
      LOGGER.error("Error while uploading file", e);
      throw new StudyStorageException("Error while uploading file", e);
    }
  }
}
