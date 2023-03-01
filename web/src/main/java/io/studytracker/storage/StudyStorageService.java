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

package io.studytracker.storage;

import io.studytracker.model.Assay;
import io.studytracker.model.Program;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.model.Study;
import io.studytracker.storage.exception.StudyStorageDuplicateException;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.io.File;

/**
 * Base interface for a service that reads and writes study files to a connected file system.
 *
 * @author Will Oemler
 * @since < 0.6.0
 */
public interface StudyStorageService {

  /**
   * Returns reference to a {@link Program} instance's storage folder. Throws a {@link
   * StudyStorageNotFoundException} if the folder does not exist;
   *
   * @param parentFolder the parent folder
   * @param program the program
   * @return the storage folder
   */
  StorageDriveFolder findFolder(StorageDriveFolder parentFolder, Program program) throws StudyStorageNotFoundException;

  /**
   * Returns reference to a {@link Study} instance's storage folder. Throws a {@link
   * StudyStorageNotFoundException} if the folder does not exist;
   *
   * @param parentFolder the parent folder
   * @param study the study
   * @return the storage folder
   */
  StorageDriveFolder findFolder(StorageDriveFolder parentFolder, Study study) throws StudyStorageNotFoundException;

  /**
   * Returns reference to a {@link Assay} instance's storage folder. Throws a {@link
   * StudyStorageNotFoundException} if the folder does not exist;
   *
   * @param parentFolder the parent folder
   * @param assay the assay
   * @return the storage folder
   */
  StorageDriveFolder findFolder(StorageDriveFolder parentFolder, Assay assay) throws StudyStorageNotFoundException;

  /**
   * Creates a {@link StorageDriveFolder} and accompanying
   * {@link io.studytracker.model.StorageDriveFolderDetails} object for the target
   * {@link Program}. Throws a {@link StudyStorageDuplicateException} if the folder already exists
   * and a {@link io.studytracker.storage.exception.StudyStorageWriteException} if the folder cannot
   * be created.
   *
   * @param parentFolder the parent folder
   * @param program the program
   * @return the created storage folder
   */
  StorageDriveFolder createFolder(StorageDriveFolder parentFolder, Program program) throws StudyStorageException;

  /**
   * Creates a {@link StorageDriveFolder} and accompanying
   * {@link io.studytracker.model.StorageDriveFolderDetails} object for the target {@link Study}.
   * Throws a {@link StudyStorageDuplicateException} if the folder already exists and a {@link
   * io.studytracker.storage.exception.StudyStorageWriteException} if the folder cannot be created.
   *
   * @param parentFolder the parent folder
   * @param study the study
   * @return the created storage folder
   */
  StorageDriveFolder createFolder(StorageDriveFolder parentFolder, Study study) throws StudyStorageException;

  /**
   * Creates a {@link StorageDriveFolder} and accompanying
   * {@link io.studytracker.model.StorageDriveFolderDetails} object for the target {@link Assay}.
   * Throws a {@link StudyStorageDuplicateException} if the folder already exists and a {@link
   * io.studytracker.storage.exception.StudyStorageWriteException} if the folder cannot be created.
   *
   * @param parentFolder the parent folder
   * @param assay the assay
   * @return the created storage folder
   */
  StorageDriveFolder createFolder(StorageDriveFolder parentFolder, Assay assay) throws StudyStorageException;

  /**
   * Uploads the target file to the appropriate directory for the target {@link Study}. Throws a
   * {@link StudyStorageDuplicateException} if the file already exists, a {@link
   * io.studytracker.storage.exception.StudyStorageWriteException} if the file cannot be written,
   * and a {@link StudyStorageNotFoundException} if the target folder cannot be found.
   *
   * @param parentFolder the parent folder
   * @param file the file to upload
   * @param study the study to associate the file with
   * @return the uploaded file reference
   */
  @Deprecated
  StorageFile saveFile(StorageDriveFolder parentFolder, File file, Study study) throws StudyStorageException;

  /**
   * Uploads the target file to the appropriate directory for the target {@link Assay}. Throws a *
   * {@link StudyStorageDuplicateException} if the file already exists, a * {@link
   * io.studytracker.storage.exception.StudyStorageWriteException} if the file cannot be written,
   * and a * {@link StudyStorageNotFoundException} if the target folder cannot be found.
   *
   * @param parentFolder the parent folder
   * @param file the file to upload
   * @param assay the assay to associate the file with
   * @return the uploaded file reference
   */
  @Deprecated
  StorageFile saveFile(StorageDriveFolder parentFolder, File file, Assay assay) throws StudyStorageException;

}
