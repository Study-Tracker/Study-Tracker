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

package io.studytracker.msgraph;

import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.models.DriveItemCollectionResponse;
import com.microsoft.graph.models.Folder;
import com.microsoft.graph.models.ItemReference;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import io.studytracker.config.properties.StorageProperties;
import io.studytracker.model.MSGraphIntegration;
import io.studytracker.model.OneDriveDriveDetails;
import io.studytracker.model.OneDriveFolderDetails;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.repository.MSGraphIntegrationRepository;
import io.studytracker.repository.StorageDriveFolderRepository;
import io.studytracker.repository.StorageDriveRepository;
import io.studytracker.storage.StorageDriveFolderService;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StudyStorageService;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OneDriveStorageService implements StudyStorageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(OneDriveStorageService.class);

  @Autowired
  private StorageDriveRepository driveRepository;

  @Autowired
  private StorageDriveFolderRepository folderRepository;

  @Autowired
  private MSGraphIntegrationRepository integrationRepository;

  @Autowired
  private StorageDriveFolderService storageDriveFolderService;

  @Autowired
  private StorageProperties storageProperties;

  private DriveItem fetchDriveItemByPath(GraphServiceClient client, String driveId, String path)
      throws StudyStorageNotFoundException {
    DriveItem driveItem = null;
    try {
      driveItem = client.drives()
          .byDriveId(driveId)
          .items()
          .byDriveItemId("root:" + path)
          .get();
    } catch (Exception e) {
      LOGGER.warn("Drive item not found: " + path);
    }
    if (driveItem == null || driveItem.getId() == null) {
      throw new StudyStorageNotFoundException(
          "No drive item found for path: " + path + " in drive: " + driveId);
    }
    return driveItem;
  }

  private DriveItem fetchFolderItemByPath(GraphServiceClient client, String driveId, String path)
      throws StudyStorageNotFoundException {
    DriveItem driveItem = this.fetchDriveItemByPath(client, driveId, path);
    if (driveItem.getFolder() == null) {
      throw new StudyStorageNotFoundException(
          "No drive folder found for path: " + path + " in drive: " + driveId);
    }
    return driveItem;
  }

  private DriveItem fetchFileItemByPath(GraphServiceClient client, String driveId, String path)
      throws StudyStorageNotFoundException {
    DriveItem driveItem = this.fetchDriveItemByPath(client, driveId, path);
    if (driveItem.getFile() == null) {
      throw new StudyStorageNotFoundException(
          "No drive file found for path: " + path + " in drive: " + driveId);
    }
    return driveItem;
  }

  @Override
  public StorageFolder createFolder(StorageDriveFolder parentFolder, String name)
      throws StudyStorageException {
    StorageDrive drive = storageDriveFolderService.findDriveById(parentFolder.getStorageDrive().getId())
        .orElseThrow(() -> new StudyStorageNotFoundException(
            "No drive found for folder with id: " + parentFolder.getId()));
    return this.createFolder(drive, parentFolder.getPath(), name);
  }

  @Override
  public StorageFolder createFolder(StorageDrive drive, String path, String name)
      throws StudyStorageException {

    // Get the client
    OneDriveDriveDetails oneDriveDriveDetails = (OneDriveDriveDetails) drive.getDetails();
    GraphServiceClient client = this.getClientFromDrive(drive);

    // Get the parent folder
    DriveItem folderItem = this.fetchFolderItemByPath(client, oneDriveDriveDetails.getDriveId(), path);

    // Check to see if the folder already exists
    String cleanName = OneDriveUtils.cleanInputObjectName(name);
    DriveItem existing = null;
    try {
      existing = this.fetchFolderItemByPath(client, oneDriveDriveDetails.getDriveId(),
          OneDriveUtils.joinPaths(path, cleanName));
    } catch (Exception e) {
      LOGGER.debug("Folder does not already exist: " + cleanName);
    }
    if (existing != null) {
      if (storageProperties.getUseExisting()) {
        LOGGER.debug("Using existing folder: " + cleanName);
        return OneDriveUtils.convertDriveItemFolderWithChildren(existing, new ArrayList<>());
      } else {
        throw new StudyStorageException("Folder already exists: " + cleanName);
      }
    }

    // Create the folder
    DriveItem newFolder = new DriveItem();
    newFolder.setName(cleanName);
    newFolder.setFolder(new Folder());
    Map<String, Object> metadata = new HashMap<>();
    if (storageProperties.getUseExisting()) {
      metadata.put("@microsoft.graph.conflictBehavior", "replace");
    } else {
      metadata.put("@microsoft.graph.conflictBehavior", "fail");
    }
    newFolder.setAdditionalData(metadata);
    DriveItem created = client
        .drives()
        .byDriveId(oneDriveDriveDetails.getDriveId())
        .items()
        .byDriveItemId(folderItem.getId())
        .children()
        .post(newFolder);
    return OneDriveUtils.convertDriveItemFolderWithChildren(created, new ArrayList<>());

  }

  @Override
  public StorageFolder findFolderByPath(StorageDriveFolder parentFolder, String path)
      throws StudyStorageNotFoundException {
    StorageDrive drive = storageDriveFolderService.findDriveById(parentFolder.getStorageDrive().getId())
        .orElseThrow(() -> new StudyStorageNotFoundException(
            "No drive found for folder with id: " + parentFolder.getId()));
    return this.findFolderByPath(drive, path);
  }

  @Override
  public StorageFolder findFolderByPath(StorageDrive drive, String path)
      throws StudyStorageNotFoundException {

    LOGGER.debug("Finding folder by path: {} in drive with id: {}", path, drive.getId());

    // Get the client
    OneDriveDriveDetails oneDriveDriveDetails = (OneDriveDriveDetails) drive.getDetails();
    GraphServiceClient client = this.getClientFromDrive(drive);

    // Find the folder
    DriveItem folderItem = this.fetchFolderItemByPath(client, oneDriveDriveDetails.getDriveId(), path);

    // Get the folder contents
    DriveItemCollectionResponse page = client.drives().byDriveId(oneDriveDriveDetails.getDriveId())
        .items()
        .byDriveItemId(folderItem.getId())
        .children()
        .get();
    List<DriveItem> children = page.getValue();

    StorageFolder folder = OneDriveUtils.convertDriveItemFolderWithChildren(folderItem, children);
    LOGGER.debug("Found folder: {}", folder);
    return folder;
  }
  
  @Override
  public StorageFolder renameFolder(StorageDrive storageDrive, String path, String newName) throws StudyStorageException {
    
    // Get the client
    GraphServiceClient client = this.getClientFromDrive(storageDrive);
    OneDriveDriveDetails oneDriveDriveDetails = (OneDriveDriveDetails) storageDrive.getDetails();
    
    // Find the folder
    DriveItem folderItem = fetchFolderItemByPath(client, oneDriveDriveDetails.getDriveId(), path);
    folderItem.setName(OneDriveUtils.cleanInputObjectName(newName));
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("@microsoft.graph.conflictBehavior", "fail");
    folderItem.setAdditionalData(metadata);
    
    try {
      DriveItem updated = client.drives()
          .byDriveId(oneDriveDriveDetails.getDriveId())
          .items()
          .byDriveItemId(folderItem.getId())
          .patch(folderItem);
      StorageFolder folder = OneDriveUtils.convertDriveItemFolder(updated);
      LOGGER.debug("Renamed folder: {}", folder);
      return folder;
    } catch (Exception e) {
      throw new StudyStorageException("Error while renaming folder in OneDrive", e);
    }
    
  }

  @Override
  public StorageFolder moveFolder(StorageDrive storageDrive, String path, String newPath)
      throws StudyStorageException {
    LOGGER.info("Moving OneDrive folder {} to {}", path, newPath);
    // Get the client
    GraphServiceClient client = this.getClientFromDrive(storageDrive);
    OneDriveDriveDetails oneDriveDriveDetails = (OneDriveDriveDetails) storageDrive.getDetails();

    // Find the folder to move
    DriveItem folderItem = fetchFolderItemByPath(client, oneDriveDriveDetails.getDriveId(), path);
    DriveItem parentFolder = fetchFolderItemByPath(client, oneDriveDriveDetails.getDriveId(), newPath);
    ItemReference parentReference = new ItemReference();
    parentReference.setId(parentFolder.getId());
    folderItem.setParentReference(parentReference);

    // Update the record
    try {
      DriveItem updated = client.drives()
          .byDriveId(oneDriveDriveDetails.getDriveId())
          .items()
          .byDriveItemId(folderItem.getId())
          .patch(folderItem);
      StorageFolder folder = OneDriveUtils.convertDriveItemFolder(updated);
      LOGGER.debug("Moved folder: {}", folder);
      return folder;
    } catch (Exception e) {
      throw new StudyStorageException("Error while moving folder in OneDrive", e);
    }

  }

  @Override
  public StorageFile findFileByPath(StorageDriveFolder parentFolder, String path)
      throws StudyStorageNotFoundException {
    StorageDrive drive = storageDriveFolderService.findDriveById(parentFolder.getStorageDrive().getId())
        .orElseThrow(() -> new StudyStorageNotFoundException(
            "No drive found for folder with id: " + parentFolder.getId()));
    return this.findFileByPath(drive, path);
  }

  @Override
  public StorageFile findFileByPath(StorageDrive drive, String path)
      throws StudyStorageNotFoundException {
    LOGGER.debug("Finding file by path: {} in drive with id: {}", path, drive.getId());
    OneDriveDriveDetails oneDriveDriveDetails = (OneDriveDriveDetails) drive.getDetails();
    GraphServiceClient client = this.getClientFromDrive(drive);
    DriveItem fileItem = this.fetchFileItemByPath(client, oneDriveDriveDetails.getDriveId(), path);
    StorageFile file = OneDriveUtils.convertDriveItemFile(fileItem);
    LOGGER.debug("Found file: {}", file);
    return file;
  }

  @Override
  public StorageFile saveFile(StorageDriveFolder folder, String path, File file)
      throws StudyStorageException {
    LOGGER.info("Saving file {} to OneDrive folder {} at path {}",
        file.getName(), folder.getName(), path);
    StorageDrive storageDrive = driveRepository.findById(folder.getStorageDrive().getId())
        .orElseThrow(() -> new StudyStorageNotFoundException(
            "No storage drive found for folder with id: " + folder.getId()));
    OneDriveDriveDetails oneDriveDriveDetails = (OneDriveDriveDetails) storageDrive.getDetails();
    GraphServiceClient client = this.getClientFromDrive(storageDrive);
    DriveItem folderItem = this.fetchFolderItemByPath(client, oneDriveDriveDetails.getDriveId(), path);
    try {
      DriveItem uploadedFileItem = client
          .drives()
          .byDriveId(oneDriveDriveDetails.getDriveId())
          .items()
          .byDriveItemId(folderItem.getId())
          .children()
          .byDriveItemId1(file.getName())
          .content()
          .put(new FileSystemResource(file).getInputStream());
      return OneDriveUtils.convertDriveItemFile(uploadedFileItem);
    } catch (IOException e) {
      throw new StudyStorageException("Error while uploading file to OneDrive", e);
    }
  }

  @Override
  public Resource fetchFile(StorageDriveFolder folder, String path) throws StudyStorageException {
    LOGGER.info("Fetching file from OneDrive folder {} at path {}",
        folder.getName(), path);
    StorageDrive storageDrive = driveRepository.findById(folder.getStorageDrive().getId())
        .orElseThrow(() -> new StudyStorageNotFoundException(
            "No OneDrive drive found for folder with id: " + folder.getId()));
    OneDriveDriveDetails oneDriveDriveDetails = (OneDriveDriveDetails) storageDrive.getDetails();
    GraphServiceClient client = this.getClientFromDrive(storageDrive);
    DriveItem fileItem = this.fetchFileItemByPath(client, oneDriveDriveDetails.getDriveId(), path);
    try {
      InputStream inputStream = client
          .drives()
          .byDriveId(oneDriveDriveDetails.getDriveId())
          .items()
          .byDriveItemId(fileItem.getId())
          .content()
          .get();
      return new ByteArrayResource(inputStream.readAllBytes());
    } catch (IOException e) {
      e.printStackTrace();
      throw new StudyStorageException("Error while fetching file from OneDrive", e);
    }
  }

  @Override
  public boolean fileExists(StorageDriveFolder folder, String path) {
    try {
      StorageDrive drive = storageDriveFolderService.findDriveById(folder.getStorageDrive().getId())
          .orElseThrow(() -> new StudyStorageNotFoundException(
              "No drive found for folder with id: " + folder.getId()));
      return this.fileExists(drive, path);
    } catch (StudyStorageNotFoundException e) {
      LOGGER.error("Error while checking if file exists", e);
    }
    return false;
  }

  @Override
  public boolean fileExists(StorageDrive drive, String path) {
    try {
      StorageFile file = this.findFileByPath(drive, path);
      return file != null;
    } catch (StudyStorageNotFoundException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean folderExists(StorageDriveFolder folder, String path) {
    try {
      StorageDrive drive = storageDriveFolderService.findDriveById(folder.getStorageDrive().getId())
          .orElseThrow(() -> new StudyStorageNotFoundException(
              "No drive found for folder with id: " + folder.getId()));
      return this.folderExists(drive, path);
    } catch (StudyStorageNotFoundException e) {
      LOGGER.error("Error while checking if file exists", e);
    }
    return false;
  }

  @Override
  public boolean folderExists(StorageDrive drive, String path) {
    try {
      StorageFolder folder = this.findFolderByPath(drive, path);
      return folder != null;
    } catch (StudyStorageNotFoundException e) {
      return false;
    }
  }

  @Override
  public StorageDriveFolder saveStorageFolderRecord(StorageDrive drive, StorageFolder storageFolder,
      StorageDriveFolder folderOptions) {

    String folderName = StringUtils.hasText(folderOptions.getName())
        ? folderOptions.getName() : storageFolder.getName();

    LOGGER.info("Saving folder record for OneDrive folder {} at path {}",
        folderName, storageFolder.getPath());

    StorageDriveFolder newFolder = new StorageDriveFolder();
    newFolder.setStorageDrive(drive);
    newFolder.setPath(storageFolder.getPath());
    newFolder.setName(folderName);
    newFolder.setStudyRoot(folderOptions.isStudyRoot());
    newFolder.setBrowserRoot(folderOptions.isBrowserRoot());
    newFolder.setWriteEnabled(folderOptions.isWriteEnabled());
    newFolder.setDeleteEnabled(folderOptions.isDeleteEnabled());

    OneDriveFolderDetails oneDriveFolderDetails = new OneDriveFolderDetails();
    oneDriveFolderDetails.setPath(storageFolder.getPath());
    oneDriveFolderDetails.setFolderId(storageFolder.getFolderId());
    oneDriveFolderDetails.setWebUrl(storageFolder.getUrl());
    newFolder.setDetails(oneDriveFolderDetails);

    return folderRepository.save(newFolder);
  }

  @Override
  public StorageDriveFolder saveStorageFolderRecord(StorageDrive drive,
      StorageFolder storageFolder) {
    return this.saveStorageFolderRecord(drive, storageFolder, new StorageDriveFolder());
  }

  private GraphServiceClient getClientFromDrive(StorageDrive drive) throws StudyStorageNotFoundException {
    OneDriveDriveDetails oneDriveDriveDetails = (OneDriveDriveDetails) drive.getDetails();
    MSGraphIntegration integration = integrationRepository.findById(oneDriveDriveDetails.getMsGraphIntegrationId())
        .orElseThrow(() -> new StudyStorageNotFoundException(
            "No integration found for drive with id: " + drive.getId()));
    return MSGraphClientFactory.fromIntegrationInstance(integration);
  }

}
