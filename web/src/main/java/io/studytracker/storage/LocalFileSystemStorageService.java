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
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.model.LocalDriveFolderDetails;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.repository.StorageDriveFolderRepository;
import io.studytracker.repository.StorageDriveRepository;
import io.studytracker.storage.exception.StudyStorageDuplicateException;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import io.studytracker.storage.exception.StudyStorageWriteException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

public class LocalFileSystemStorageService implements StudyStorageService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(LocalFileSystemStorageService.class);

  @Autowired
  private StorageDriveRepository driveRepository;

  @Autowired
  private StorageDriveFolderRepository folderRepository;

  @Autowired
  private StorageProperties storageProperties;

  private File getObjectByPath(String rootPath, String objectPath)
      throws StudyStorageNotFoundException {
    try {
      validatePath(rootPath, objectPath);
    } catch (StudyStorageException e) {
      throw new StudyStorageNotFoundException("Invalid object path", e);
    }
    Path path = Paths.get(objectPath).normalize();
    File file = path.toFile();
    if (!file.exists()) {
      throw new StudyStorageNotFoundException("Cannot find object at path: " + objectPath);
    }
    return file;
  }

  private File getFolderByPath(String rootPath, String folderPath) throws StudyStorageNotFoundException {
    File folder = getObjectByPath(rootPath, folderPath);
    if (!folder.isDirectory()) {
      throw new StudyStorageNotFoundException("Object at path is not a folder: " + folderPath);
    }
    return folder;
  }

  private File getFileByPath(String rootPath, String filePath) throws StudyStorageNotFoundException {
    File file = getObjectByPath(rootPath, filePath);
    if (file.isDirectory()) {
      throw new StudyStorageNotFoundException("Object at path is not a folder: " + filePath);
    }
    return file;
  }

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
  public StorageFolder findFolderByPath(StorageDrive drive, String path)
      throws StudyStorageNotFoundException {
    File file = this.getFolderByPath(drive.getRootPath(), path);
    StorageFolder folder = new StorageFolder();
    folder.setPath(file.getAbsolutePath());
    folder.setName(file.getName());
    folder.setFiles(getFolderFiles(file.getAbsoluteFile().toPath()));
    folder.setSubFolders(getSubfolders(file.getAbsoluteFile().toPath()));
    return folder;
  }

  @Override
  public StorageFolder findFolderByPath(StorageDriveFolder parentFolder, String path)
      throws StudyStorageNotFoundException {
    return findFolderByPath(parentFolder.getStorageDrive(), path);
  }
  
  @Override
  public StorageFolder renameFolder(StorageDrive drive, String path, String newName) throws StudyStorageException {
    LOGGER.info("Renaming folder at path {} to {}", path, newName);
    File file = this.getFolderByPath(drive.getRootPath(), path);
    File newFolder = new File(file.getParentFile(), StorageUtils.cleanInputObjectName(newName));

    try {
      file.renameTo(newFolder);
    } catch (Exception e) {
      throw new StudyStorageWriteException("Failed to rename folder at path: " + path, e);
    }
    
    StorageFolder folder = new StorageFolder();
    folder.setPath(newFolder.getAbsolutePath());
    folder.setName(newFolder.getName());
    return folder;
    
  }

  @Override
  public StorageFolder moveFolder(StorageDrive storageDrive, String path, String newParentPath)
      throws StudyStorageException {
    File existingFolder = this.getFolderByPath(storageDrive.getRootPath(), path);
    String folderName = existingFolder.getName();
    File targetFolder = Paths.get(StorageUtils.joinPath(newParentPath, folderName))
        .normalize()
        .toFile();
    try {
      FileUtils.moveDirectoryToDirectory(existingFolder, targetFolder, true);
    } catch (IOException e) {
      throw new StudyStorageException("Failed to move folder: " + path, e);
    }
    return this.findFolderByPath(storageDrive, targetFolder.getPath());
  }

  @Override
  public StorageFile findFileByPath(StorageDrive drive, String path)
      throws StudyStorageNotFoundException {
    File file = this.getFileByPath(drive.getRootPath(), path);
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
    Path newFolderPath = Paths.get(path).normalize().resolve(StorageUtils.cleanInputObjectName(name));
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
  public StorageFolder createFolder(StorageDriveFolder parentFolder, String name)
      throws StudyStorageException {
    LOGGER.info("Creating storage folder {} at path {} for location {}",
        name, parentFolder.getPath(), parentFolder.getName());
    return this.createFolder(parentFolder.getPath(), name);
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
