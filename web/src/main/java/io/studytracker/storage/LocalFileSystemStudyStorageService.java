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

import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.model.Assay;
import io.studytracker.model.FileStorageLocation;
import io.studytracker.model.IntegrationInstance;
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import io.studytracker.repository.IntegrationInstanceRepository;
import io.studytracker.service.NamingService;
import io.studytracker.storage.exception.StudyStorageDuplicateException;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import io.studytracker.storage.exception.StudyStorageWriteException;
import io.studytracker.storage.integration.LocalFileSystemOptions;
import io.studytracker.storage.integration.LocalFileSystemOptionsFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class LocalFileSystemStudyStorageService implements StudyStorageService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(LocalFileSystemStudyStorageService.class);

  @Autowired
  private NamingService namingService;

  @Autowired
  private IntegrationInstanceRepository integrationInstanceRepository;

  private LocalFileSystemOptions getOptionsFromLocation(FileStorageLocation location) {
    IntegrationInstance instance = integrationInstanceRepository
        .findById(location.getIntegrationInstance().getId())
        .orElseThrow(() -> new RecordNotFoundException("Integration instance not found: "
            + location.getIntegrationInstance().getId()));
    return LocalFileSystemOptionsFactory.create(instance);
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
  public StorageFolder findFolder(FileStorageLocation location, Program program) throws StudyStorageNotFoundException {
    LOGGER.info("Fetching storage folder instance for program: " + program.getName());
    LocalFileSystemOptions options = getOptionsFromLocation(location);
    Path path = Paths.get(options.getRootPath()).resolve(namingService.getProgramStorageFolderName(program));
    LOGGER.info(path.toString());
    File file = path.toFile();
    if (!file.isDirectory() || !file.exists()) {
      throw new StudyStorageNotFoundException(program.getName());
    }
    StorageFolder folder = new StorageFolder();
    folder.setPath(path);
    folder.setName(file.getName());
    folder.setFiles(getFolderFiles(path));
    folder.setSubFolders(getSubfolders(path));
    return folder;
  }

  @Override
  public StorageFolder findFolder(FileStorageLocation location, Study study) throws StudyStorageNotFoundException {
    LOGGER.info("Fetching storage folder instance for study: " + study.getCode());
    StorageFolder programFolder = this.findFolder(location, study.getProgram());
    Path programPath = Paths.get(programFolder.getPath());
    Path studyFolder = programPath.resolve(namingService.getStudyStorageFolderName(study));
    LOGGER.info(studyFolder.toString());
    File file = studyFolder.toFile();
    if (!file.isDirectory() || !file.exists()) {
      throw new StudyStorageNotFoundException(study.getCode());
    }
    StorageFolder folder = new StorageFolder();
    folder.setPath(studyFolder);
    folder.setName(file.getName());
    folder.setFiles(getFolderFiles(studyFolder));
    folder.setSubFolders(getSubfolders(studyFolder));
    return folder;
  }

  @Override
  public StorageFolder findFolder(FileStorageLocation location, Assay assay) throws StudyStorageNotFoundException {
    LOGGER.info("Fetching storage folder instance for assay: " + assay.getCode());
    StorageFolder studyFolder = this.findFolder(location, assay.getStudy());
    Path studyPath = Paths.get(studyFolder.getPath());
    Path assayFolder = studyPath.resolve(namingService.getAssayStorageFolderName(assay));
    LOGGER.info(assayFolder.toString());
    File file = assayFolder.toFile();
    if (!file.isDirectory() || !file.exists()) {
      throw new StudyStorageNotFoundException(assay.getCode());
    }
    StorageFolder folder = new StorageFolder();
    folder.setPath(assayFolder);
    folder.setName(file.getName());
    folder.setFiles(getFolderFiles(assayFolder));
    folder.setSubFolders(getSubfolders(assayFolder));
    return folder;
  }

  @Override
  public StorageFolder createFolder(FileStorageLocation location, Program program) throws StudyStorageException {
    LOGGER.info("Creating storage folder instance for program: " + program.getName());
    LocalFileSystemOptions options = getOptionsFromLocation(location);
    String folderName = namingService.getProgramStorageFolderName(program);
    Path programPath = Paths.get(options.getRootPath()).resolve(folderName);
    File newFolder = programPath.toFile();
    if (newFolder.exists()) {
      if (options.isUseExisting()) {
        LOGGER.info("Using existing folder.");
      } else if (options.isOverwriteExisting()) {
        LOGGER.info("Overwriting existing folder");
        boolean success = newFolder.delete();
        if (!success) {
          throw new StudyStorageWriteException(
              "Failed to delete program folder: " + newFolder.getAbsolutePath());
        }
      } else {
        throw new StudyStorageDuplicateException(
            "Program folder already exists: " + newFolder.getAbsolutePath());
      }
    } else {
      boolean success = newFolder.mkdir();
      if (!success) {
        throw new StudyStorageWriteException(
            "Failed to create program folder: " + newFolder.getAbsolutePath());
      }
    }
    StorageFolder folder = new StorageFolder();
    folder.setName(newFolder.getName());
    folder.setPath(programPath);
    return folder;
  }

  @Override
  public StorageFolder createFolder(FileStorageLocation location, Study study) throws StudyStorageException {
    LOGGER.info("Creating storage folder instance for study: " + study.getCode());
    LocalFileSystemOptions options = getOptionsFromLocation(location);
    StorageFolder programFolder = this.findFolder(location, study.getProgram());
    Path programPath = Paths.get(programFolder.getPath());
    Path studyPath = programPath.resolve(namingService.getStudyStorageFolderName(study));
    File newFolder = studyPath.toFile();
    if (newFolder.exists()) {
      if (options.isUseExisting()) {
        LOGGER.info("Using existing folder.");
      } else if (options.isOverwriteExisting()) {
        LOGGER.info("Overwriting existing folder");
        boolean success = newFolder.delete();
        if (!success) {
          throw new StudyStorageWriteException(
              "Failed to delete study folder: " + newFolder.getAbsolutePath());
        }
      } else {
        throw new StudyStorageDuplicateException(
            "Study folder already exists: " + newFolder.getAbsolutePath());
      }
    } else {
      boolean success = newFolder.mkdir();
      if (!success) {
        throw new StudyStorageWriteException(
            "Failed to create study folder: " + newFolder.getAbsolutePath());
      }
    }
    StorageFolder folder = new StorageFolder();
    folder.setName(newFolder.getName());
    folder.setPath(studyPath);
    return folder;
  }

  @Override
  public StorageFolder createFolder(FileStorageLocation location, Assay assay) throws StudyStorageException {
    LOGGER.info("Creating storage folder instance for assay: " + assay.getCode());
    LocalFileSystemOptions options = getOptionsFromLocation(location);
    StorageFolder studyFolder = this.findFolder(location, assay.getStudy());
    Path studyPath = Paths.get(studyFolder.getPath());
    Path assayPath = studyPath.resolve(namingService.getAssayStorageFolderName(assay));
    File newFolder = assayPath.toFile();
    if (newFolder.exists()) {
      if (options.isUseExisting()) {
        LOGGER.info("Using existing folder.");
      } else if (options.isOverwriteExisting()) {
        LOGGER.info("Overwriting existing folder");
        boolean success = newFolder.delete();
        if (!success) {
          throw new StudyStorageWriteException(
              "Failed to delete assay folder: " + newFolder.getAbsolutePath());
        }
      } else {
        throw new StudyStorageDuplicateException(
            "Assay folder already exists: " + newFolder.getAbsolutePath());
      }
    } else {
      boolean success = newFolder.mkdir();
      if (!success) {
        throw new StudyStorageWriteException(
            "Failed to create assay folder: " + newFolder.getAbsolutePath());
      }
    }
    StorageFolder folder = new StorageFolder();
    folder.setName(newFolder.getName());
    folder.setPath(assayPath);
    return folder;
  }

  @Override
  public StorageFile saveFile(FileStorageLocation location, File file, Study study) throws StudyStorageException {
    LOGGER.info(
        String.format(
            "Saving file %s to storage folder instance for study %s",
            file.getName(), study.getCode()));
    StorageFolder studyFolder = this.findFolder(location, study);
    return this.saveFile(file, studyFolder);
  }

  @Override
  public StorageFile saveFile(FileStorageLocation location, File file, Assay assay) throws StudyStorageException {
    LOGGER.info(
        String.format(
            "Saving file %s to storage folder instance for assay %s",
            file.getName(), assay.getCode()));
    StorageFolder assayFolder = this.findFolder(location, assay);
    return this.saveFile(file, assayFolder);
  }

  private StorageFile saveFile(File file, StorageFolder folder) {
    Path path = Paths.get(folder.getPath());
    Path newFilePath = path.resolve(file.getName());
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

}
