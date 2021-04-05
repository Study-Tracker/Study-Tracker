/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.decibeltx.studytracker.egnyte;

import com.decibeltx.studytracker.egnyte.entity.EgnyteFile;
import com.decibeltx.studytracker.egnyte.entity.EgnyteFolder;
import com.decibeltx.studytracker.egnyte.entity.EgnyteObject;
import com.decibeltx.studytracker.egnyte.exception.EgnyteException;
import java.io.File;

public interface EgnyteClientOperations {

  /**
   * Creates a new directory or directories with the specified path. If the full path contains
   * multiple new subdirectories, they will all be created.
   *
   * @param path full path of folders to be created.
   * @return
   * @throws EgnyteException
   */
  EgnyteFolder createFolder(String path) throws EgnyteException;

  /**
   * Returns the {@link EgnyteObject} the resides at the target path. Throws an exception if nothing
   * exists at the given location. The response object will either be a {@link EgnyteFolder} or
   * {@link EgnyteFile} object.
   *
   * @param path full path of file or folder
   * @return
   * @throws EgnyteException
   */
  EgnyteObject findObjectByPath(String path) throws EgnyteException;

  /**
   * Returns the {@link EgnyteObject} the resides at the target path. Throws an exception if nothing
   * exists at the given location. The response object will either be a {@link EgnyteFolder} or
   * {@link EgnyteFile} object.
   *
   * @param path  full path of file or folder
   * @param depth maximum folder depth to traverse
   * @return
   * @throws EgnyteException
   */
  EgnyteObject findObjectByPath(String path, int depth) throws EgnyteException;

  /**
   * Fetches information about the folder with the provided {@code folder_id}.
   *
   * @param folderId
   * @return
   * @throws EgnyteException
   */
  EgnyteFolder findFolderById(String folderId) throws EgnyteException;

  /**
   * Fetches information about the file with the provided {@code group_id}.
   *
   * @param fileId
   * @return
   * @throws EgnyteException
   */
  EgnyteFile findFileById(String fileId) throws EgnyteException;

  /**
   * Uploads the supplied file to the target directory. If the directory does not already exist, it
   * will be created.
   *
   * @param file
   * @param path
   * @throws EgnyteException
   */
  EgnyteFile uploadFile(File file, String path) throws EgnyteException;

  void deleteObjectByPath(String path) throws EgnyteException;

}
