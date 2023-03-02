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
import io.studytracker.model.Assay;
import io.studytracker.model.FileStorageLocation;
import io.studytracker.model.Program;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.model.Study;
import io.studytracker.repository.LocalDriveRepository;
import io.studytracker.service.NamingService;
import io.studytracker.storage.exception.StudyStorageDuplicateException;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import io.studytracker.storage.exception.StudyStorageWriteException;
import io.studytracker.storage.integration.LocalFileSystemOptions;
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

public class LocalFileSystemStorageService implements StudyStorageService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(LocalFileSystemStorageService.class);

  @Autowired
  private NamingService namingService;

  @Autowired
  private LocalDriveRepository localDriveRepository;

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
  public StorageDriveFolder createProgramFolder(StorageDriveFolder parentFolder, Program program)
      throws StudyStorageException {
    LOGGER.info("Creating storage folder instance for program: " + program.getName());
    LocalFileSystemOptions options = getOptionsFromLocation(location);
    String folderName = namingService.getProgramStorageFolderName(program);
    return createFolder(location, options.getRootPath(), folderName);
  }

  @Override
  public StorageFolder createFolder(FileStorageLocation location, Study study) throws StudyStorageException {
    LOGGER.info("Creating storage folder instance for study: " + study.getCode());
    StorageFolder programFolder = this.findFolder(location, study.getProgram());
    return createFolder(location, programFolder.getPath(), namingService.getStudyStorageFolderName(study));
  }

  @Override
  public StorageFolder createFolder(FileStorageLocation location, Assay assay) throws StudyStorageException {
    LOGGER.info("Creating storage folder instance for assay: " + assay.getCode());
    StorageFolder studyFolder = this.findFolder(location, assay.getStudy());
    return createFolder(location, studyFolder.getPath(), namingService.getAssayStorageFolderName(assay));
  }

  @Override
  public StorageFile saveFile(FileStorageLocation location, File file, Study study) throws StudyStorageException {
    LOGGER.info(
        String.format(
            "Saving file %s to storage folder instance for study %s",
            file.getName(), study.getCode()));
//    validatePath(location, file.getPath());
    StorageFolder studyFolder = this.findFolder(location, study);
    return this.saveFileToFolder(file, studyFolder);
  }

  @Override
  public StorageFile saveFile(FileStorageLocation location, File file, Assay assay) throws StudyStorageException {
    LOGGER.info(
        String.format(
            "Saving file %s to storage folder instance for assay %s",
            file.getName(), assay.getCode()));
//    validatePath(location, file.getPath());
    StorageFolder assayFolder = this.findFolder(location, assay);
    return this.saveFileToFolder(file, assayFolder);
  }

  @Override
  public StorageFolder findFolderByPath(FileStorageLocation location, String path)
      throws StudyStorageNotFoundException {
    try {
      validatePath(location, path);
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
  public StorageFile findFileByPath(FileStorageLocation location, String path)
      throws StudyStorageNotFoundException {
    try {
      validatePath(location, path);
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
  public StorageFolder createFolder(FileStorageLocation location, String path, String name)
      throws StudyStorageException {

    LOGGER.info("Creating storage folder {} at path {} for location {}",
        name, path, location.getName());
    validatePath(location, path);
    LocalFileSystemOptions options = getOptionsFromLocation(location);
    Path newFolderPath = Paths.get(path).normalize().resolve(name);
    File newFolder = newFolderPath.toFile();

    // Folder exists
    if (newFolder.exists()) {

      // Use the existing folder
      if (options.isUseExisting()) {
        LOGGER.warn("Folder exists and will be used.");
      }

      // Delete and recreate the folder
      else if (options.isOverwriteExisting()) {
        LOGGER.warn("Folder exists and will be overwritten.");
        boolean success = newFolder.delete();
        if (!success) {
          throw new StudyStorageWriteException(
              "Failed to delete folder: " + newFolder.getAbsolutePath());
        }
        success = newFolder.mkdir();
        if (!success) {
          throw new StudyStorageWriteException(
              "Failed to create folder: " + newFolder.getAbsolutePath());
        }
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
  public StorageFile saveFile(FileStorageLocation location, String path, File file)
      throws StudyStorageException {
    LOGGER.info("Saving file {} to storage location {} at path {}", file.getName(), location.getName(), path);
    validatePath(location, path);
    return saveFileToPath(file, Paths.get(path).normalize());
  }

  @Override
  public Resource fetchFile(FileStorageLocation location, String path)
      throws StudyStorageException {
    try {
      return new ByteArrayResource(Files.readAllBytes(Paths.get(path).normalize()));
    } catch (IOException e) {
      throw new StudyStorageException("Failed to read file from path: " + path, e);
    }
  }

  @Override
  public boolean fileExists(FileStorageLocation location, String path) {
    try {
      validatePath(location, path);
    } catch (StudyStorageException e) {
      return false;
    }
    Path filePath = Paths.get(path).normalize();
    File file = filePath.toFile();
    return file.exists() && file.isFile();
  }

  @Override
  public boolean folderExists(FileStorageLocation location, String path) {
    try {
      validatePath(location, path);
    } catch (StudyStorageException e) {
      return false;
    }
    Path filePath = Paths.get(path).normalize();
    File file = filePath.toFile();
    return file.exists() && !file.isFile();
  }

  private StorageFile saveFileToFolder(File file, StorageFolder folder) {
    return saveFileToPath(file, Paths.get(folder.getPath()));
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

  private void validatePath(FileStorageLocation location, String path) throws StudyStorageException {
    Path cleanPath = Paths.get(path).normalize();
    Path locationPath = Paths.get(location.getRootFolderPath());
    if (!cleanPath.startsWith(locationPath)) {
      throw new StudyStorageException("Path is not within the storage location: " + path);
    }
  }

}
