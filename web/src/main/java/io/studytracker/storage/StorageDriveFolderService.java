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

import io.studytracker.model.*;
import io.studytracker.model.StorageDrive.DriveType;
import io.studytracker.repository.StorageDriveFolderRepository;
import io.studytracker.repository.StorageDriveRepository;
import io.studytracker.storage.exception.StudyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * Service for {@link StorageDriveFolder} and {@link StorageDrive} entity records.
 *
 * @author Will Oemler
 * @since 0.9.0
 */
@Service
public class StorageDriveFolderService {

  private static final Logger LOGGER = LoggerFactory.getLogger(StorageDriveFolderService.class);

  @Autowired
  private StorageDriveFolderRepository folderRepository;

  @Autowired
  private StorageDriveRepository driveRepository;

  @Autowired
  private StudyStorageServiceLookup studyStorageServiceLookup;


  public List<StorageDriveFolder> findAll() {
    LOGGER.debug("Find all drive folders");
    return folderRepository.findAll();
  }

  public Page<StorageDriveFolder> findAll(Pageable pageable) {
    LOGGER.debug("Find all drive folders");
    return folderRepository.findAll(pageable);
  }

  public List<StorageDriveFolder> findStudyRootFolders() {
    LOGGER.debug("Find all study root drive folders");
    return folderRepository.findStudyRoot();
  }

  public List<StorageDriveFolder> findBrowserRootFolders() {
    LOGGER.debug("Find all browser root drive folders");
    return folderRepository.findBrowserRoot();
  }

  public Optional<StorageDriveFolder> findById(Long id) {
    LOGGER.debug("Find drive folder by id");
    return folderRepository.findById(id);
  }

  public List<StorageDriveFolder> findByProgram(Program program) {
    return folderRepository.findByProgramId(program.getId());
  }

  public Optional<StorageDriveFolder> findPrimaryProgramFolder(Program program) {
    return folderRepository.findPrimaryByProgramId(program.getId());
  }

  public List<StorageDriveFolder> findByStudy(Study study) {
    return folderRepository.findByStudyId(study.getId());
  }

  public Optional<StorageDriveFolder> findPrimaryStudyFolder(Study study) {
    return folderRepository.findPrimaryByStudyId(study.getId());
  }

  public List<StorageDriveFolder> findByAssay(Assay assay) {
    return folderRepository.findByAssayId(assay.getId());
  }

  public Optional<StorageDriveFolder> findPrimaryAssayFolder(Assay assay) {
    return folderRepository.findPrimaryByAssayId(assay.getId());
  }

  public StudyStorageService lookupStudyStorageService(StorageDriveFolder folder) {
    StorageDrive drive = this.findDriveByFolder(folder)
        .orElseThrow(() -> new IllegalArgumentException("No drive found for folder: " + folder.getId()));
    return studyStorageServiceLookup.lookup(drive.getDriveType())
        .orElseThrow(() -> new IllegalArgumentException("No storage service found for folder: "
        + folder.getId()));
  }

  public StudyStorageService lookupStudyStorageService(DriveType driveType) {
    return studyStorageServiceLookup.lookup(driveType)
        .orElseThrow(() -> new IllegalArgumentException("No storage service found for drive type: "
            + driveType));
  }

  /**
   * Registers a {@link StorageDriveFolder} and {@link StorageDriveFolderDetails} instance in the
   *  database for the requested folder. If the folder does not exist in the provided
   *  {@link StorageDrive}, it will be created.
   *
   * @param folder folder to register
   * @param drive drive containing the folder
   * @return registered folder record
   */
  @Transactional
  public StorageDriveFolder registerFolder(StorageDriveFolder folder, StorageDrive drive) {

    String path = StorageUtils.cleanInputPath(folder.getPath());
    String folderName = StorageUtils.getFolderNameFromPath(folder.getPath());
    if (!StringUtils.hasText(folder.getName())) {
      folder.setName(folderName);
    }
    LOGGER.info("Registering folder '{}' at path '{}' in drive '{}'", folderName, path, drive.getDisplayName());

    // Check that the requested folder is within the drive root path
    if (!path.startsWith(drive.getRootPath())) {
      throw new IllegalArgumentException("Folder path must be within drive root path");
    }

    StudyStorageService storageService = this.lookupStudyStorageService(drive.getDriveType());

    // Create or fetch the folder record
    StorageFolder storageFolder;
    try {
      if (storageService.folderExists(drive, path)) {
        LOGGER.info("Folder {} already exists in drive {}", path, drive.getDisplayName());
        storageFolder = storageService.findFolderByPath(drive, path);
        LOGGER.debug("Found existing folder '{}' at path '{}' in drive '{}'", storageFolder.getName(),
                storageFolder.getPath(), drive.getDisplayName());
      } else {
        LOGGER.info("Creating folder {} in drive {}", folder.getPath(), drive.getDisplayName());
        storageFolder = storageService.createFolder(drive, StorageUtils.getParentPathFromPath(path), folderName);
        LOGGER.debug("Created folder '{}' at path '{}' in drive '{}'", storageFolder.getName(),
                storageFolder.getPath(), drive.getDisplayName());
      }
    } catch (StudyStorageException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Unable to create folder: " + path, e);
    }

    return storageService.saveStorageFolderRecord(drive, storageFolder, folder);
  }

  @Transactional
  public StorageDriveFolder updateFolder(StorageDriveFolder folder) {
    LOGGER.debug("Updating folder: {}", folder);
    StorageDriveFolder f = folderRepository.getById(folder.getId());
        f.setName(folder.getName());
    f.setStudyRoot(folder.isStudyRoot());
    f.setBrowserRoot(folder.isBrowserRoot());
    f.setWriteEnabled(folder.isWriteEnabled());
    f.setDeleteEnabled(folder.isDeleteEnabled());
    return folderRepository.save(f);
  }

  @Transactional
  public void deleteFolder(StorageDriveFolder folder) {
    LOGGER.info("Deleting folder: {} {}", folder.getId(), folder.getPath());
    folderRepository.deleteById(folder.getId());
  }

  // Drives

  public List<StorageDrive> findAllDrives() {
    LOGGER.debug("Find all drives");
    return driveRepository.findAll();
  }

  public List<StorageDrive> findByDriveType(DriveType driveType) {
    LOGGER.debug("Find all drives by type: {}", driveType);
    return driveRepository.findByDriveType(driveType);
  }

  public Optional<StorageDrive> findDriveById(Long id) {
    LOGGER.debug("Find drive by id: {}", id);
    return driveRepository.findById(id);
  }

  public Optional<StorageDrive> findDriveByFolder(StorageDriveFolder folder) {
    LOGGER.debug("Find drive by folder: {}", folder.getId());
    return driveRepository.findByStorageDriveFolderId(folder.getId());
  }

}
