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
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.model.StorageDriveFolderDetails;
import io.studytracker.model.Study;
import io.studytracker.storage.exception.StudyStorageDuplicateException;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.io.File;
import org.springframework.core.io.Resource;

/**
 * Base interface for a service that reads and writes study files to a connected file system.
 *
 * @author Will Oemler
 * @since < 0.6.0
 */
public interface StudyStorageService {

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
  StorageDriveFolder createProgramFolder(StorageDriveFolder parentFolder, Program program) throws StudyStorageException;

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
  StorageDriveFolder createStudyFolder(StorageDriveFolder parentFolder, Study study) throws StudyStorageException;

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
  StorageDriveFolder createAssayFolder(StorageDriveFolder parentFolder, Assay assay) throws StudyStorageException;

  /**
   * Creates a new folder at the given path.
   *
   * @param parentFolder the storage location
   * @param path the path to the folder to create the new folder within
   * @param name the name of the new folder
   * @return the new folder
   * @throws StudyStorageException if the folder cannot be created
   */
  StorageFolder createFolder(StorageDriveFolder parentFolder, String path, String name) throws StudyStorageException;

  /**
   * Creates a new folder at the given path.
   *
   * @param drive the storage drive
   * @param path the path to the folder to create the new folder within
   * @param name the name of the new folder
   * @return the new folder
   * @throws StudyStorageException if the folder cannot be created
   */
  StorageFolder createFolder(StorageDrive drive, String path, String name) throws StudyStorageException;

  /**
   * Looks up a folder and its contents by path, given a parent folder.
   *
   * @param parentFolder the parent folder
   * @param path the path to the folder
   * @return the folder
   * @throws StudyStorageNotFoundException if the folder is not found
   */
  StorageFolder findFolderByPath(StorageDriveFolder parentFolder, String path) throws StudyStorageNotFoundException;

  /**
   * Looks up a folder and its contents by path, given a parent drive.
   *
   * @param drive the parent storage drive
   * @param path the path to the folder
   * @return the folder
   * @throws StudyStorageNotFoundException if the folder is not found
   */
  StorageFolder findFolderByPath(StorageDrive drive, String path) throws StudyStorageNotFoundException;

  /**
   * Finds a file by its path in the file system, given a parent folder.
   *
   * @param parentFolder the parent folder
   * @param path the path to the file
   * @return the file
   * @throws StudyStorageNotFoundException if the file is not found
   */
  StorageFile findFileByPath(StorageDriveFolder parentFolder, String path) throws StudyStorageNotFoundException;

  /**
   * Finds a file by its path in the file system, given a parent folder.
   *
   * @param drive the parent folder drive
   * @param path the path to the file
   * @return the file
   * @throws StudyStorageNotFoundException if the file is not found
   */
  StorageFile findFileByPath(StorageDrive drive, String path) throws StudyStorageNotFoundException;

  /**
   * Uploads the given file to the provided path.
   *
   * @param folder the storage folder
   * @param path the path to the folder to upload the file to
   * @param file the file to upload
   * @return the uploaded file object
   * @throws StudyStorageException if the file cannot be uploaded
   */
  StorageFile saveFile(StorageDriveFolder folder, String path, File file) throws StudyStorageException;

  /**
   * Downloads the file at the provided path.
   *
   * @param folder the storage folder
   * @param path the path to the object to download
   * @return the downloaded file byte stream
   * @throws StudyStorageException if the file cannot be downloaded
   */
  Resource fetchFile(StorageDriveFolder folder, String path) throws StudyStorageException;

  /**
   * Returns true if the file exists at the provided path.
   *
   * @param folder the parent storage folder
   * @param path the path to the object to check
   * @return true if the file exists
   */
  boolean fileExists(StorageDriveFolder folder, String path);

  /**
   * Returns true if the file exists at the provided path.
   *
   * @param drive the parent storage drive
   * @param path the path to the object to check
   * @return true if the file exists
   */
  boolean fileExists(StorageDrive drive, String path);

  /**
   * Returns true if the folder exists at the provided path.
   *
   * @param folder the storage folder
   * @param path the path to the object to check
   * @return true if the folder exists
   */
  boolean folderExists(StorageDriveFolder folder, String path);

  /**
   * Returns true if the folder exists at the provided path.
   *
   * @param drive the storage drive
   * @param path the path to the object to check
   * @return true if the folder exists
   */
  boolean folderExists(StorageDrive drive, String path);

  /**
   * Persists a {@link StorageDriveFolder} and accompanying {@link StorageDriveFolderDetails}
   * details record for the provided {@link StorageFolder} in the database. The optional
   * {@link StorageDriveFolder} parameter can be used to specify additional options for the folder.
   *
   * @param drive
   * @param storageFolder
   * @param folderOptions
   * @return
   */
  StorageDriveFolder saveStorageFolderRecord(StorageDrive drive, StorageFolder storageFolder, StorageDriveFolder folderOptions);

  /**
   * Persists a {@link StorageDriveFolder} and accompanying {@link StorageDriveFolderDetails}
   * details record for the provided {@link StorageFolder} in the database.
   *
   * @param drive
   * @param storageFolder
   * @return
   */
  StorageDriveFolder saveStorageFolderRecord(StorageDrive drive, StorageFolder storageFolder);

}
