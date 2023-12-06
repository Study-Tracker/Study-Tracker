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

import io.studytracker.config.properties.StorageProperties;
import io.studytracker.exception.InvalidRequestException;
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.model.*;
import io.studytracker.repository.StorageDriveFolderRepository;
import io.studytracker.repository.StorageDriveRepository;
import io.studytracker.service.NamingService;
import io.studytracker.storage.exception.StudyStorageDuplicateException;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import io.studytracker.storage.exception.StudyStorageWriteException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class LocalFileSystemStorageService implements StudyStorageService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(LocalFileSystemStorageService.class);

  @Autowired
  private NamingService namingService;

  @Autowired
  private StorageDriveRepository driveRepository;

  @Autowired
  private StorageDriveFolderRepository folderRepository;

  @Autowired
  private StorageProperties storageProperties;

  /**
   * Returns {@link StorageFile} instances for every file in the target folder, at the top level.
   *
   * @param path
   * @return
   */
  private List<StorageFile> getFolderFiles(Path path) {
    try {
      return Files.walk(path, 1)
          .filter(Files::isRegularFile)
          .map(
              f -> {
                StorageFile file = new StorageFile();
                file.setPath(f);
                file.setName(f.getFileName().toString());
                file.setDownloadable(true);
                return file;
              })
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new StudyTrackerException(e);
    }
  }

  /**
   * Returns {@link StorageFolder} instances for every subfolder in the target folder.
   *
   * @param path
   * @return
   */
  private List<StorageFolder> getSubfolders(Path path) {
    try {
      return Files.walk(path, 1)
          .filter(Files::isDirectory)
          .filter(p -> !p.toString().equals(path.toString()))
          .map(
              d -> {
                StorageFolder folder = new StorageFolder();
                folder.setName(d.toFile().getName());
                folder.setPath(d);
                return folder;
              })
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new StudyTrackerException(e);
    }
  }

  @Override
  @Transactional
  public StorageDriveFolder createProgramFolder(StorageDriveFolder parentFolder, Program program)
      throws StudyStorageException {
    LOGGER.info("Creating storage folder instance for program: " + program.getName());

    // Check that parent folder is study root
    if (!parentFolder.isStudyRoot()) {
      throw new InvalidRequestException("Parent folder is not a study root folder.");
    }

    // Create the folder
    String folderName = NamingService.getProgramStorageFolderName(program);
    StorageFolder storageFolder = createFolder(parentFolder, parentFolder.getPath(), folderName);
    StorageDriveFolder options = new StorageDriveFolder();
    options.setWriteEnabled(true);
    return saveStorageFolderRecord(parentFolder.getStorageDrive(), storageFolder, options);

  }

  @Override
  @Transactional
  public StorageDriveFolder createStudyFolder(StorageDriveFolder parentFolder, Study study)
      throws StudyStorageException {
    LOGGER.info("Creating storage folder instance for study: " + study.getCode());
    StorageFolder storageFolder = createFolder(parentFolder, parentFolder.getPath(),
        NamingService.getStudyStorageFolderName(study));
    StorageDriveFolder options = new StorageDriveFolder();
    options.setWriteEnabled(true);
    return saveStorageFolderRecord(parentFolder.getStorageDrive(), storageFolder, options);
  }

  @Override
  @Transactional
  public StorageDriveFolder createAssayFolder(StorageDriveFolder parentFolder, Assay assay)
      throws StudyStorageException {
    LOGGER.info("Creating storage folder instance for assay: " + assay.getCode());
    StorageFolder storageFolder = createFolder(parentFolder, parentFolder.getPath(),
        NamingService.getAssayStorageFolderName(assay));
    StorageDriveFolder options = new StorageDriveFolder();
    options.setWriteEnabled(true);
    return saveStorageFolderRecord(parentFolder.getStorageDrive(), storageFolder, options);
  }

  @Override
  public StorageFolder findFolderByPath(StorageDrive drive, String path)
      throws StudyStorageNotFoundException {
    try {
      validatePath(drive.getRootPath(), path);
    } catch (StudyStorageException e) {
      e.printStackTrace();
      throw new StudyStorageNotFoundException(e);
    }
    Path folderPath = Paths.get(path).normalize();
    File file = folderPath.toFile();
    if (!file.isDirectory() || !file.exists()) {
      throw new StudyStorageNotFoundException("Cannot find folder at path: " + path);
    }
    StorageFolder folder = new StorageFolder();
    folder.setPath(folderPath.toString());
    folder.setName(file.getName());
    folder.setFiles(getFolderFiles(folderPath));
    folder.setSubFolders(getSubfolders(folderPath));
    return folder;
  }

  @Override
  public StorageFolder findFolderByPath(StorageDriveFolder parentFolder, String path)
      throws StudyStorageNotFoundException {
    return findFolderByPath(parentFolder.getStorageDrive(), path);
  }

  @Override
  public StorageFile findFileByPath(StorageDrive drive, String path)
      throws StudyStorageNotFoundException {
    try {
      validatePath(drive.getRootPath(), path);
    } catch (StudyStorageException e) {
      e.printStackTrace();
      throw new StudyStorageNotFoundException(e);
    }
    Path filePath = Paths.get(path).normalize();
    File file = filePath.toFile();
    if (!file.isFile() || !file.exists()) {
      throw new StudyStorageNotFoundException("Cannot find file at path: " + path);
    }
    StorageFile storageFile = new StorageFile();
    storageFile.setName(file.getName());
    storageFile.setPath(file.getPath());
    return storageFile;
  }

  @Override
  public StorageFile findFileByPath(StorageDriveFolder parentFolder, String path)
      throws StudyStorageNotFoundException {
    return findFileByPath(parentFolder.getStorageDrive(), path);
  }

  private StorageFolder createFolder(String path, String name) throws StudyStorageException {
    Path newFolderPath = Paths.get(path).normalize().resolve(name);
    File newFolder = newFolderPath.toFile();

    // Folder exists
    if (newFolder.exists()) {

      // Use the existing folder
      if (storageProperties.getUseExisting()) {
        LOGGER.warn("Folder exists and will be used.");
      } else {
        throw new StudyStorageDuplicateException(
            "Folder already exists: " + newFolder.getAbsolutePath());
      }
    }

    // Create a new folder
    else {
      boolean success = newFolder.mkdir();
      if (!success) {
        throw new StudyStorageWriteException(
            "Failed to create folder: " + newFolder.getAbsolutePath());
      }
    }

    // Return the storage folder object
    StorageFolder folder = new StorageFolder();
    folder.setName(newFolder.getName());
    folder.setPath(newFolderPath);
    return folder;
  }

  @Override
  public StorageFolder createFolder(StorageDrive drive, String path, String name)
      throws StudyStorageException {
    LOGGER.info("Creating storage folder {} at path {} for drive {}",
        name, path, drive.getDisplayName());
    validatePath(drive.getRootPath(), path);
    return this.createFolder(path, name);
  }

  @Override
  public StorageFolder createFolder(StorageDriveFolder parentFolder, String path, String name)
      throws StudyStorageException {
    LOGGER.info("Creating storage folder {} at path {} for location {}",
        name, path, parentFolder.getName());
    validatePath(parentFolder.getPath(), path);
    return this.createFolder(path, name);
  }

  @Override
  public StorageFile saveFile(StorageDriveFolder folder, String path, File file)
      throws StudyStorageException {
    LOGGER.info("Saving file {} to storage location {} at path {}", file.getName(), folder.getName(), path);
    validatePath(folder.getPath(), path);
    return saveFileToPath(file, Paths.get(path).normalize());
  }

  @Override
  public Resource fetchFile(StorageDriveFolder parentFolder, String path)
      throws StudyStorageException {
    validatePath(parentFolder.getPath(), path);
    try {
      return new ByteArrayResource(Files.readAllBytes(Paths.get(path).normalize()));
    } catch (IOException e) {
      throw new StudyStorageException("Failed to read file from path: " + path, e);
    }
  }

  private boolean fileExists(String path) {
    Path filePath = Paths.get(path).normalize();
    File file = filePath.toFile();
    return file.exists() && file.isFile();
  }

  @Override
  public boolean fileExists(StorageDrive drive, String path) {
    try {
      validatePath(drive.getRootPath(), path);
    } catch (StudyStorageException e) {
      return false;
    }
    return this.fileExists(path);
  }

  @Override
  public boolean fileExists(StorageDriveFolder parentFolder, String path) {
    try {
      validatePath(parentFolder.getPath(), path);
    } catch (StudyStorageException e) {
      return false;
    }
    return this.fileExists(path);
  }

  private boolean folderExists(String path) {
    Path filePath = Paths.get(path).normalize();
    File file = filePath.toFile();
    return file.exists() && !file.isFile();
  }

  @Override
  public boolean folderExists(StorageDriveFolder parentFolder, String path) {
    try {
      validatePath(parentFolder.getPath(), path);
    } catch (StudyStorageException e) {
      return false;
    }
    return this.folderExists(path);
  }

  @Override
  public boolean folderExists(StorageDrive drive, String path) {
    try {
      validatePath(drive.getRootPath(), path);
    } catch (StudyStorageException e) {
      return false;
    }
    return this.folderExists(path);
  }

  private StorageFile saveFileToPath(File file, Path path) {
    String fileName = FilenameUtils.getName(file.getName());
    Path cleanPath = path.normalize();
    Path newFilePath = cleanPath.resolve(fileName);
    File newFile = newFilePath.toFile();
    try {
      FileUtils.copyFile(file, newFile);
    } catch (Exception e) {
      throw new StudyTrackerException(e);
    }
    StorageFile studyFile = new StorageFile();
    studyFile.setPath(newFilePath);
    studyFile.setName(newFile.getName());
    return studyFile;
  }

  private void validatePath(String parentPath, String path) throws StudyStorageException {
    Path cleanPath = Paths.get(path).normalize();
    Path locationPath = Paths.get(parentPath);
    if (!cleanPath.startsWith(locationPath)) {
      throw new StudyStorageException("Path is not within the storage location: " + path);
    }
  }

  @Override
  @Transactional
  public StorageDriveFolder saveStorageFolderRecord(StorageDrive drive, StorageFolder storageFolder,
      StorageDriveFolder options) {

    String folderName = StringUtils.hasText(options.getName()) ? options.getName()
        : storageFolder.getName();

    StorageDriveFolder storageDriveFolder = new StorageDriveFolder();
    storageDriveFolder.setStorageDrive(drive);
    storageDriveFolder.setName(folderName);
    storageDriveFolder.setPath(storageFolder.getPath());
    storageDriveFolder.setBrowserRoot(options.isBrowserRoot());
    storageDriveFolder.setDeleteEnabled(options.isDeleteEnabled());
    storageDriveFolder.setStudyRoot(options.isStudyRoot());
    storageDriveFolder.setWriteEnabled(options.isWriteEnabled());
    storageDriveFolder.setDetails(new LocalDriveFolderDetails());

    return folderRepository.save(storageDriveFolder);
  }

  @Transactional
  @Override
  public StorageDriveFolder saveStorageFolderRecord(StorageDrive drive, StorageFolder storageFolder) {
    return this.saveStorageFolderRecord(drive, storageFolder, new StorageDriveFolder());
  }

}
