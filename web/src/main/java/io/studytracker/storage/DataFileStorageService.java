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

package io.studytracker.storage;

import io.studytracker.model.FileStorageLocation;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.io.File;
import org.springframework.core.io.Resource;

/**
 * Storage service for storing files using local file system or integrated storage service.
 *
 * @author Will Oemler
 * @since 0.7.1
 */
public interface DataFileStorageService {

  /**
   * Looks up a folder and its contents by path.
   *
   * @param location the storage location
   * @param path the path to the folder
   * @return the folder
   * @throws StudyStorageNotFoundException if the folder is not found
   */
  StorageFolder findFolderByPath(FileStorageLocation location, String path) throws StudyStorageNotFoundException;


  /**
   * Finds a file by its path in the file system.
   *
   * @param location the storage location
   * @param path the path to the file
   * @return the file
   * @throws StudyStorageNotFoundException if the file is not found
   */
  StorageFile findFileByPath(FileStorageLocation location, String path) throws StudyStorageNotFoundException;

  /**
   * Creates a new folder at the given path.
   *
   * @param location the storage location
   * @param path the path to the folder to create the new folder within
   * @param name the name of the new folder
   * @return the new folder
   * @throws StudyStorageException if the folder cannot be created
   */
  StorageFolder createFolder(FileStorageLocation location, String path, String name) throws StudyStorageException;

  /**
   * Uploads the given file to the provided path.
   *
   * @param location the storage location
   * @param path the path to the folder to upload the file to
   * @param file the file to upload
   * @return the uploaded file object
   * @throws StudyStorageException if the file cannot be uploaded
   */
  StorageFile saveFile(FileStorageLocation location, String path, File file) throws StudyStorageException;

  /**
   * Downloads the file at the provided path.
   *
   * @param location the storage location
   * @param path the path to the object to download
   * @return the downloaded file byte stream
   * @throws StudyStorageException if the file cannot be downloaded
   */
  Resource fetchFile(FileStorageLocation location, String path) throws StudyStorageException;

}
