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

package io.studytracker.egnyte;

import io.studytracker.config.properties.StorageProperties;
import io.studytracker.egnyte.entity.EgnyteFile;
import io.studytracker.egnyte.entity.EgnyteFolder;
import io.studytracker.egnyte.entity.EgnyteObject;
import io.studytracker.egnyte.exception.DuplicateFolderException;
import io.studytracker.egnyte.exception.EgnyteException;
import io.studytracker.egnyte.exception.ObjectNotFoundException;
import io.studytracker.exception.InsufficientPrivilegesException;
import io.studytracker.exception.InvalidRequestException;
import io.studytracker.model.Assay;
import io.studytracker.model.EgnyteDrive;
import io.studytracker.model.EgnyteDriveFolder;
import io.studytracker.model.EgnyteIntegration;
import io.studytracker.model.Program;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.model.Study;
import io.studytracker.repository.EgnyteDriveFolderRepository;
import io.studytracker.repository.EgnyteDriveRepository;
import io.studytracker.repository.EgnyteIntegrationRepository;
import io.studytracker.storage.StorageDriveFolderService;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StorageUtils;
import io.studytracker.storage.StudyStorageService;
import io.studytracker.storage.exception.StudyStorageDuplicateException;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

public class EgnyteStudyStorageService implements StudyStorageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(EgnyteStudyStorageService.class);

  @Autowired
  private EgnyteClientOperations egnyteClient;

  @Autowired
  private EgnyteDriveRepository egnyteDriveRepository;

  @Autowired
  private EgnyteIntegrationRepository egnyteIntegrationRepository;

  @Autowired
  private StorageDriveFolderService storageDriveFolderService;

  @Autowired
  private StorageProperties storageProperties;

  @Autowired
  private EgnyteDriveFolderRepository egnyteDriveFolderRepository;

  public String getProgramFolderPath(Program program, String rootPath) {
    LOGGER.debug("getProgramFolderPath({})", program.getName());
    Optional<StorageDriveFolder> optional = storageDriveFolderService.findPrimaryProgramFolder(program);
    String path;
    if (optional.isPresent()) {
      StorageDriveFolder storageDriveFolder = optional.get();
      path = storageDriveFolder.getPath();
      if (!path.endsWith("/")) {
        path = path + "/";
      }
    } else {
      path = rootPath + EgnyteFolderNameGenerator.getProgramStorageFolderName(program) + "/";
    }
    LOGGER.debug("Program folder path: " + path);
    return path;
  }

  public String getStudyFolderPath(Study study, String rootPath) {
    LOGGER.debug("getStudyFolderPath({})", study.getName());
    Optional<StorageDriveFolder> optional = storageDriveFolderService.findPrimaryStudyFolder(study);
    String path;
    if (optional.isPresent()) {
      StorageDriveFolder storageDriveFolder = optional.get();
      path = storageDriveFolder.getPath();
      if (!path.endsWith("/")) {
        path = path + "/";
      }
    } else {
      path =
          this.getProgramFolderPath(study.getProgram(), rootPath)
              + EgnyteFolderNameGenerator.getStudyStorageFolderName(study)
              + "/";
    }
    LOGGER.debug("Study folder path: " + path);
    return path;
  }

  public String getAssayFolderPath(Assay assay, String rootPath) {
    LOGGER.debug("getAssayFolderPath({})", assay.getName());
    Optional<StorageDriveFolder> optional = storageDriveFolderService.findPrimaryAssayFolder(assay);
    String path;
    if (optional.isPresent()) {
      StorageDriveFolder storageDriveFolder = optional.get();
      path = storageDriveFolder.getPath();
      if (!path.endsWith("/")) {
        path = path + "/";
      }
    } else {
      Study study = assay.getStudy();
      String studyPath = this.getStudyFolderPath(study, rootPath);
      path = studyPath + EgnyteFolderNameGenerator.getAssayStorageFolderName(assay) + "/";
    }
    LOGGER.debug("Assay folder path: " + path);
    return path;
  }

  private StorageFolder convertFolder(EgnyteFolder egnyteFolder, String rootUrl) {
    StorageFolder storageFolder = EgnyteUtils.convertEgnyteFolder(egnyteFolder, rootUrl);
    for (EgnyteFile file : egnyteFolder.getFiles()) {
      storageFolder.getFiles().add(EgnyteUtils.convertEgnyteFile(file, rootUrl));
    }
    for (EgnyteFolder subFolder : egnyteFolder.getSubFolders()) {
      storageFolder.getSubFolders().add(convertFolder(subFolder, rootUrl));
    }
    EgnyteFolder parentFolder = null;
    if (egnyteFolder.getParentId() != null) {
      File parentFile = new File(egnyteFolder.getPath());
      parentFolder = new EgnyteFolder();
      parentFolder.setFolderId(egnyteFolder.getParentId());
      parentFolder.setName(parentFile.getName());
      parentFolder.setPath(parentFile.getPath());
    }
    if (parentFolder != null) {
      storageFolder.setParentFolder(EgnyteUtils.convertEgnyteFolder(parentFolder, rootUrl));
    }
    return storageFolder;
  }

  @Override
  public StorageFolder findFolderByPath(StorageDriveFolder parentFolder, String path)
      throws StudyStorageNotFoundException {
    return this.findFolderByPath(parentFolder.getStorageDrive(), path);
  }

  @Override
  public StorageFolder findFolderByPath(StorageDrive drive, String path)
      throws StudyStorageNotFoundException {
    LOGGER.debug("Looking up folder by path: {}", path);
    EgnyteIntegration integration = egnyteIntegrationRepository.findByStorageDriveId(drive.getId());
    try {
      EgnyteObject egnyteObject = egnyteClient.findObjectByPath(
          new URL(integration.getRootUrl()), path, integration.getApiToken());
      if (egnyteObject.isFolder()) {
        EgnyteFolder folder = (EgnyteFolder) egnyteObject;
        return EgnyteUtils.convertEgnyteFolderWithContents(folder, integration.getRootUrl(),
            drive.getRootPath());
      } else {
        throw new EgnyteException("Object is not a folder: " + path);
      }
    } catch (EgnyteException | MalformedURLException e) {
      e.printStackTrace();
      LOGGER.error("Failed to lookup folder by path: {}", path, e);
      throw new StudyStorageNotFoundException("Failed to lookup folder by path: " + path, e);
    }
  }

  @Override
  public StorageFile findFileByPath(StorageDriveFolder parentFolder, String path)
      throws StudyStorageNotFoundException {
    return this.findFileByPath(parentFolder.getStorageDrive(), path);
  }

  @Override
  public StorageFile findFileByPath(StorageDrive drive, String path)
      throws StudyStorageNotFoundException {
    LOGGER.debug("Finding file by path: {}", path);
    EgnyteIntegration integration = egnyteIntegrationRepository.findByStorageDriveId(drive.getId());
    try {
      EgnyteObject egnyteObject = egnyteClient.findObjectByPath(
          new URL(integration.getRootUrl()), path, integration.getApiToken());
      if (!egnyteObject.isFolder()) {
        EgnyteFile file = (EgnyteFile) egnyteObject;
        return EgnyteUtils.convertEgnyteFile(file, integration.getRootUrl());
      } else {
        throw new EgnyteException("Object is not a file: " + path);
      }
    } catch (EgnyteException | MalformedURLException e) {
      e.printStackTrace();
      LOGGER.error("Error while finding file by path", e);
      throw new StudyStorageNotFoundException("Error while finding file by path", e);
    }
  }

  @Override
  public StorageFolder createFolder(StorageDriveFolder parentFolder, String path, String name)
      throws StudyStorageException {
    LOGGER.info("Creating folder: {} in {}", name, path);
    EgnyteIntegration integration = egnyteIntegrationRepository
        .findByStorageDriveId(parentFolder.getStorageDrive().getId());
    try {
      if (!parentFolder.isWriteEnabled()) {
        throw new InsufficientPrivilegesException("Insufficient privileges to create folder");
      }
      if (!EgnyteUtils.directoryIsSubfolderOf(parentFolder.getPath(), path)) {
        throw new InvalidRequestException("Requested path is not a subfolder of parent folder");
      }
      EgnyteFolder folder = egnyteClient.createFolder(new URL(integration.getRootUrl()),
          StorageUtils.joinPath(path, name), integration.getApiToken());
      return EgnyteUtils.convertEgnyteFolder(folder, integration.getRootUrl());
    } catch (Exception e) {
      e.printStackTrace();
      LOGGER.error("Error while creating folder", e);
      throw new StudyStorageException("Error while creating folder", e);
    }
  }

  @Override
  public StorageDriveFolder createProgramFolder(StorageDriveFolder parentFolder, Program program)
      throws StudyStorageException {

    LOGGER.info(String.format("Creating folder for program %s", program.getName()));

    // Check that parent folder is study root
    if (!parentFolder.isStudyRoot()) {
      throw new InvalidRequestException("Parent folder is not a study root folder.");
    }

    EgnyteIntegration integration = egnyteIntegrationRepository
        .findByStorageDriveId(parentFolder.getStorageDrive().getId());
    String path = getProgramFolderPath(program, parentFolder.getPath());
    StorageFolder storageFolder;

    // Try to create the folder
    try {
      EgnyteFolder egnyteFolder = egnyteClient.createFolder(new URL(integration.getRootUrl()),
          path, integration.getApiToken());
      storageFolder = this.convertFolder(egnyteFolder, integration.getRootUrl());
    }

    // If the folder already exists, check if it can be used
    catch (DuplicateFolderException e) {
      LOGGER.warn("Duplicate folder found: " + path);
      if (storageProperties.getUseExisting()) {
        LOGGER.warn("Existing folder will be used.");
        storageFolder = this.findFolderByPath(parentFolder, path);
      } else {
        throw new StudyStorageDuplicateException(e);
      }
    }

    // Throw remaining exceptions
    catch (EgnyteException | MalformedURLException e) {
      throw new StudyStorageException(e);
    }

    // Save the records in the database
    return saveStorageFolderRecord(parentFolder, storageFolder);

  }

  @Override
  public StorageDriveFolder createStudyFolder(StorageDriveFolder parentFolder, Study study)
      throws StudyStorageException {

    EgnyteIntegration integration = egnyteIntegrationRepository
        .findByStorageDriveId(parentFolder.getStorageDrive().getId());
    Program program = study.getProgram();
    String path = getStudyFolderPath(study, parentFolder.getPath());
    StorageFolder storageFolder;
    LOGGER.info(
        String.format(
            "Creating folder for study %s in program folder %s with path: %s",
            study.getCode(), program.getName(), path));

    // Trey to create the folder
    try {
      EgnyteFolder egnyteFolder = egnyteClient.createFolder(new URL(integration.getRootUrl()), path,
          integration.getApiToken());
      storageFolder = this.convertFolder(egnyteFolder, integration.getRootUrl());
    }

    // If the folder already exists, check if it can be used
    catch (DuplicateFolderException e) {
      if (storageProperties.getUseExisting()) {
        LOGGER.warn("Existing folder will be used.");
        storageFolder = this.findFolderByPath(parentFolder, path);
      } else {
        throw new StudyStorageDuplicateException(e);
      }
    }

    // Throw any remaining exceptions
    catch (EgnyteException | MalformedURLException e) {
      throw new StudyStorageException(e);
    }

    // Save the records in the database
    return saveStorageFolderRecord(parentFolder, storageFolder);

  }

  @Override
  public StorageDriveFolder createAssayFolder(StorageDriveFolder parentFolder, Assay assay)
      throws StudyStorageException {

    Study study = assay.getStudy();
    LOGGER.info(
        String.format(
            "Creating folder for assay %s in study folder %s",
            assay.getCode(), study.getName() + " (" + study.getCode() + ")"));
    EgnyteIntegration integration = egnyteIntegrationRepository
        .findByStorageDriveId(parentFolder.getStorageDrive().getId());
    String path = getAssayFolderPath(assay, parentFolder.getPath());
    StorageFolder storageFolder;

    // Try to create the folder
    try {
      EgnyteFolder egnyteFolder = egnyteClient.createFolder(new URL(integration.getRootUrl()), path,
          integration.getApiToken());
      storageFolder = this.convertFolder(egnyteFolder, integration.getRootUrl());
    }

    // If the folder already exists, check if it can be used
    catch (DuplicateFolderException e) {
      if (storageProperties.getUseExisting()) {
        LOGGER.warn("Existing folder will be used.");
        storageFolder = this.findFolderByPath(parentFolder, path);
      } else {
        throw new StudyStorageDuplicateException(e);
      }
    }

    // throw any remaining exceptions
    catch (EgnyteException | MalformedURLException e) {
      throw new StudyStorageException(e);
    }

    return saveStorageFolderRecord(parentFolder, storageFolder);

  }

  @Override
  public StorageFile saveFile(StorageDriveFolder folder, String path, File file)
      throws StudyStorageException {
    LOGGER.info("Uploading file: {} to {}", file.getName(), path);
    EgnyteIntegration integration = egnyteIntegrationRepository
        .findByStorageDriveId(folder.getStorageDrive().getId());
    try {
      if (!folder.isWriteEnabled()) {
        throw new InsufficientPrivilegesException("Insufficient privileges to write files.");
      }
      EgnyteFile egnyteFile = egnyteClient.uploadFile(new URL(integration.getRootUrl()), file, path,
          integration.getApiToken());
      return EgnyteUtils.convertEgnyteFile(egnyteFile, integration.getRootUrl());
    } catch (EgnyteException | MalformedURLException e) {
      e.printStackTrace();
      LOGGER.error("Error while uploading file", e);
      throw new StudyStorageException("Error while uploading file", e);
    }
  }

  @Override
  public Resource fetchFile(StorageDriveFolder folder, String path) throws StudyStorageException {
    LOGGER.info("Downloading file: {}", path);
    throw new StudyStorageException("Not implemented");
  }

  @Override
  public boolean fileExists(StorageDriveFolder folder, String path) {
    LOGGER.debug("Checking if file exists: {}", path);
    EgnyteIntegration integration = egnyteIntegrationRepository
        .findByStorageDriveId(folder.getStorageDrive().getId());
    try {
      EgnyteObject egnyteObject = egnyteClient.findObjectByPath(new URL(integration.getRootUrl()),
          path, integration.getApiToken());
      return !egnyteObject.isFolder();
    } catch (EgnyteException | MalformedURLException e) {
      if (!(e.getCause() instanceof ObjectNotFoundException)) {
        e.printStackTrace();
        LOGGER.error("Error while checking if file exists", e);
      }
      return false;
    }
  }

  @Override
  public boolean folderExists(StorageDriveFolder folder, String path) {
    LOGGER.debug("Checking if folder exists: {}", path);
    EgnyteIntegration integration = egnyteIntegrationRepository
        .findByStorageDriveId(folder.getStorageDrive().getId());
    try {
      EgnyteObject egnyteObject = egnyteClient.findObjectByPath(new URL(integration.getRootUrl()),
          path, integration.getApiToken());
      return egnyteObject.isFolder();
    } catch (EgnyteException | MalformedURLException e) {
      if (!(e.getCause() instanceof ObjectNotFoundException)) {
        e.printStackTrace();
        LOGGER.error("Error while checking if folder exists", e);
      }
      return false;
    }
  }

  /**
   * Persists {@link StorageDriveFolder} and {@link EgnyteDriveFolder} records, given a parent
   *  folder and a {@link StorageFolder} object;
   *
   * @param parentFolder parent folder
   * @param storageFolder storage folder
   * @return persisted storage drive folder
   */
  private StorageDriveFolder saveStorageFolderRecord(StorageDriveFolder parentFolder, StorageFolder storageFolder) {

    StorageDrive drive = parentFolder.getStorageDrive();
    Optional<EgnyteDrive> optional = egnyteDriveRepository.findByStorageDriveId(drive.getId());
    if (optional.isEmpty()) {
      throw new InvalidRequestException("Egnyte drive not found.");
    }
    EgnyteDrive egnyteDrive = optional.get();

    StorageDriveFolder storageDriveFolder = new StorageDriveFolder();
    storageDriveFolder.setStorageDrive(drive);
    storageDriveFolder.setName(storageFolder.getName());
    storageDriveFolder.setPath(storageFolder.getPath());
    storageDriveFolder.setBrowserRoot(false);
    storageDriveFolder.setDeleteEnabled(false);
    storageDriveFolder.setStudyRoot(false);
    storageDriveFolder.setWriteEnabled(true);

    EgnyteDriveFolder egnyteDriveFolder = new EgnyteDriveFolder();
    egnyteDriveFolder.setEgnyteDrive(egnyteDrive);
    egnyteDriveFolder.setStorageDriveFolder(storageDriveFolder);
    egnyteDriveFolder.setFolderId(storageFolder.getFolderId());
    egnyteDriveFolder.setWebUrl(storageFolder.getUrl());

    egnyteDriveFolderRepository.save(egnyteDriveFolder);
    return egnyteDriveFolder.getStorageDriveFolder();
  }

}
