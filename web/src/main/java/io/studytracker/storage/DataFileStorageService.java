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

import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.io.File;

public interface DataFileStorageService {

  /**
   * Looks up a folder and its contents by path.
   *
   * @param path the path to the folder
   * @return the folder
   * @throws StudyStorageNotFoundException if the folder is not found
   */
  StorageFolder findFolderByPath(String path) throws StudyStorageNotFoundException;

  /**
   * Looks up a folder by its file system identifier.
   *
   * @param id the file system identifier
   * @return the folder
   * @throws StudyStorageNotFoundException if the folder is not found
   */
  StorageFolder findFolderById(String id) throws StudyStorageNotFoundException;

  /**
   * Looks up a file by its file system identifier.
   *
   * @param id the file system identifier
   * @return the file
   * @throws StudyStorageNotFoundException if the file is not found
   */
  StorageFile findFileById(String id) throws StudyStorageNotFoundException;

  /**
   * Finds a file by its path in the file system.
   *
   * @param path the path to the file
   * @return the file
   * @throws StudyStorageNotFoundException if the file is not found
   */
  StorageFile findFileByPath(String path) throws StudyStorageNotFoundException;

  /**
   * Creates a new folder at the given path.
   *
   * @param path the path to the folder to create the new folder within
   * @param name the name of the new folder
   * @return the new folder
   * @throws StudyStorageException if the folder cannot be created
   */
  StorageFolder createFolder(String path, String name) throws StudyStorageException;

  /**
   * Uploads the given file to the provided path.
   *
   * @param path the path to the folder to upload the file to
   * @param file the file to upload
   * @return the uploaded file object
   * @throws StudyStorageException if the file cannot be uploaded
   */
  StorageFile uploadFile(String path, File file) throws StudyStorageException;

}
