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

package io.studytracker.egnyte;

import io.studytracker.egnyte.entity.EgnyteFile;
import io.studytracker.egnyte.entity.EgnyteFolder;
import io.studytracker.egnyte.entity.EgnyteObject;
import io.studytracker.egnyte.exception.DuplicateFolderException;
import io.studytracker.egnyte.exception.EgnyteException;
import io.studytracker.egnyte.integration.EgnyteIntegrationOptions;
import io.studytracker.egnyte.integration.EgnyteIntegrationOptionsFactory;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.model.Assay;
import io.studytracker.model.FileStorageLocation;
import io.studytracker.model.IntegrationInstance;
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import io.studytracker.repository.IntegrationInstanceRepository;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StudyStorageService;
import io.studytracker.storage.exception.StudyStorageDuplicateException;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.io.File;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriUtils;

public class EgnyteStudyStorageService implements StudyStorageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(EgnyteStudyStorageService.class);

  @Autowired
  private EgnyteClientOperations egnyteClient;

  @Autowired
  private EgnyteFolderNamingService egnyteFolderNamingService;

  @Autowired
  private IntegrationInstanceRepository integrationInstanceRepository;

  private EgnyteIntegrationOptions getOptionsFromLocation(FileStorageLocation location) {
    IntegrationInstance instance = integrationInstanceRepository
        .findById(location.getIntegrationInstance().getId())
        .orElseThrow(() -> new RecordNotFoundException("Integration instance not found: "
            + location.getIntegrationInstance().getId()));
    return EgnyteIntegrationOptionsFactory.create(instance);
  }

  public String getProgramFolderPath(Program program, String rootPath) {
    LOGGER.debug("getProgramFolderPath({})", program.getName());
    String path;
    if (program.getPrimaryStorageFolder() != null && program.getPrimaryStorageFolder().getPath() != null) {
      path = program.getPrimaryStorageFolder().getPath();
      if (!path.endsWith("/")) {
        path = path + "/";
      }
    } else {
      path = rootPath + egnyteFolderNamingService.getProgramStorageFolderName(program) + "/";
    }
    LOGGER.debug("Program folder path: " + path);
    return path;
  }

  public String getStudyFolderPath(Study study, String rootPath) {
    LOGGER.debug("getStudyFolderPath({})", study.getName());
    String path;
    if (study.getPrimaryStorageFolder() != null && study.getPrimaryStorageFolder().getPath() != null) {
      path = study.getPrimaryStorageFolder().getPath();
      if (!path.endsWith("/")) {
        path = path + "/";
      }
    } else {
      path =
          this.getProgramFolderPath(study.getProgram(), rootPath)
              + egnyteFolderNamingService.getStudyStorageFolderName(study)
              + "/";
    }
    LOGGER.debug("Study folder path: " + path);
    return path;
  }

  public String getAssayFolderPath(Assay assay, String rootPath) {
    LOGGER.debug("getAssayFolderPath({})", assay.getName());
    String path;
    if (assay.getPrimaryStorageFolder() != null && assay.getPrimaryStorageFolder().getPath() != null) {
      path = assay.getPrimaryStorageFolder().getPath();
      if (!path.endsWith("/")) {
        path = path + "/";
      }
    } else {
      Study study = assay.getStudy();
      String studyPath = this.getStudyFolderPath(study, rootPath);
      path = studyPath + egnyteFolderNamingService.getAssayStorageFolderName(assay) + "/";
    }
    LOGGER.debug("Assay folder path: " + path);
    return path;
  }

  private StorageFolder convertEgnyteFolder(EgnyteFolder egnyteFolder, URL rootUrl) {
    StorageFolder storageFolder = new StorageFolder();
    storageFolder.setName(egnyteFolder.getName());
    storageFolder.setPath(egnyteFolder.getPath());
    if (egnyteFolder.getUrl() == null) {
      storageFolder.setUrl(buildEgnyteUrl(rootUrl, egnyteFolder.getPath(), egnyteFolder.getName()));
    } else {
      storageFolder.setUrl(egnyteFolder.getUrl());
    }
    return storageFolder;
  }

  private String buildEgnyteUrl(URL rootUrl, String path, String name) {
    if (name != null) {
      path = path.replace("/" + name, "");
    } else if (path.endsWith("/")) {
      path = path.substring(0, path.length() - 1);
    }
    path = UriUtils.encodePath(path, "UTF-8").replace("&", "%26");
    String url = rootUrl.toString();
    if (url.endsWith("/")) {
      url = url.substring(0, url.length() - 1);
    }
    url = url + "/app/index.do#storage/files/1" + path;
    return url;
  }

  private String buildEgnyteUrl(URL rootUrl, String path) {
    return this.buildEgnyteUrl(rootUrl, path, null);
  }

  private StorageFile convertEgnyteFile(EgnyteFile egnyteFile, URL rootUrl) {

    StorageFile storageFile = new StorageFile();
    storageFile.setPath(egnyteFile.getPath());

    if (egnyteFile.getName() == null) {
      storageFile.setName(new File(egnyteFile.getPath()).getName());
    } else {
      storageFile.setName(egnyteFile.getName());
    }

    if (egnyteFile.getUrl() == null) {
      storageFile.setUrl(buildEgnyteUrl(rootUrl, egnyteFile.getPath(), storageFile.getName()));
    } else {
      storageFile.setUrl(egnyteFile.getUrl());
    }

    return storageFile;
  }

  private StorageFolder convertFolder(EgnyteFolder egnyteFolder, URL rootUrl) {
    StorageFolder storageFolder = convertEgnyteFolder(egnyteFolder, rootUrl);
    for (EgnyteFile file : egnyteFolder.getFiles()) {
      storageFolder.getFiles().add(convertEgnyteFile(file, rootUrl));
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
      storageFolder.setParentFolder(convertEgnyteFolder(parentFolder, rootUrl));
    }
    return storageFolder;
  }

  @Override
  public StorageFolder findFolder(FileStorageLocation location, Program program)
      throws StudyStorageNotFoundException {
    LOGGER.debug("Find folder {}", program.getName());
    EgnyteIntegrationOptions options = this.getOptionsFromLocation(location);
    String path = getProgramFolderPath(program, options.getRootPath());
    StorageFolder storageFolder;
    try {
      EgnyteObject obj = egnyteClient.findObjectByPath(options.getRootUrl(), path, options.getToken());
      if (!obj.isFolder()) {
        throw new StudyTrackerException("Found resource is not a folder");
      }
      LOGGER.debug("Egnyte folder: " + ((EgnyteFolder) obj));
      storageFolder = convertFolder((EgnyteFolder) obj, options.getRootUrl());
    } catch (EgnyteException e) {
      throw new StudyStorageNotFoundException(e);
    }
    LOGGER.debug("Program folder: " + storageFolder);
    return storageFolder;
  }

  @Override
  public StorageFolder findFolder(FileStorageLocation location, Study study)
      throws StudyStorageNotFoundException {
    LOGGER.debug("Find study folder: {}", study.getName());
    EgnyteIntegrationOptions options = this.getOptionsFromLocation(location);
    String path = getStudyFolderPath(study, options.getRootPath());
    StorageFolder storageFolder;
    try {
      EgnyteObject obj = egnyteClient.findObjectByPath(options.getRootUrl(), path, options.getToken());
      if (!obj.isFolder()) {
        throw new StudyTrackerException("Found resource is not a folder");
      }
      LOGGER.debug("Egnyte folder: " + ((EgnyteFolder) obj));
      storageFolder = this.convertFolder((EgnyteFolder) obj, options.getRootUrl());
    } catch (EgnyteException e) {
      throw new StudyStorageNotFoundException(e);
    }
    LOGGER.debug("Study folder: " + storageFolder);
    return storageFolder;
  }

  @Override
  public StorageFolder findFolder(FileStorageLocation location, Assay assay)
      throws StudyStorageNotFoundException {
    LOGGER.debug("getAssayFolder({})", assay.getName());
    EgnyteIntegrationOptions options = this.getOptionsFromLocation(location);
    String path = getAssayFolderPath(assay, options.getRootPath());
    StorageFolder storageFolder;
    try {
      EgnyteObject obj = egnyteClient.findObjectByPath(options.getRootUrl(), path, options.getToken());
      LOGGER.debug("Egnyte folder: " + ((EgnyteFolder) obj));
      storageFolder = this.convertFolder((EgnyteFolder) obj, options.getRootUrl());
    } catch (EgnyteException e) {
      throw new StudyStorageNotFoundException(e);
    }
    LOGGER.debug("Assay folder: " + storageFolder);
    return storageFolder;
  }

  @Override
  public StorageFolder createFolder(FileStorageLocation location, Program program) throws StudyStorageException {
    LOGGER.info(String.format("Creating folder for program %s", program.getName()));
    EgnyteIntegrationOptions options = this.getOptionsFromLocation(location);
    String path = getProgramFolderPath(program, options.getRootPath());
    StorageFolder storageFolder;
    try {
      EgnyteFolder egnyteFolder = egnyteClient.createFolder(options.getRootUrl(), path, options.getToken());
      storageFolder = this.convertFolder(egnyteFolder, options.getRootUrl());
    } catch (DuplicateFolderException e) {
      LOGGER.warn("Duplicate folder found: " + path);
      if (options.isUseExisting()) {
        LOGGER.warn("Existing folder will be used.");
        storageFolder = this.findFolder(location, program);
      } else {
        throw new StudyStorageDuplicateException(e);
      }
    } catch (EgnyteException e) {
      throw new StudyStorageException(e);
    }
    LOGGER.debug("Program folder: " + storageFolder);
    return storageFolder;
  }

  @Override
  public StorageFolder createFolder(FileStorageLocation location, Study study) throws StudyStorageException {
    EgnyteIntegrationOptions options = this.getOptionsFromLocation(location);
    Program program = study.getProgram();
    String path = getStudyFolderPath(study, options.getRootPath());
    StorageFolder storageFolder;
    LOGGER.info(
        String.format(
            "Creating folder for study %s in program folder %s with path: %s",
            study.getCode(), program.getName(), path));
    try {
      EgnyteFolder egnyteFoler = egnyteClient.createFolder(options.getRootUrl(), path, options.getToken());
      storageFolder = this.convertFolder(egnyteFoler, options.getRootUrl());
    } catch (DuplicateFolderException e) {
      if (options.isUseExisting()) {
        LOGGER.warn("Existing folder will be used.");
        storageFolder = this.findFolder(location, study);
      } else {
        throw new StudyStorageDuplicateException(e);
      }
    } catch (EgnyteException e) {
      throw new StudyStorageException(e);
    }
    LOGGER.debug("Study folder: " + storageFolder);
    return storageFolder;
  }

  @Override
  public StorageFolder createFolder(FileStorageLocation location, Assay assay) throws StudyStorageException {
    Study study = assay.getStudy();
    LOGGER.info(
        String.format(
            "Creating folder for assay %s in study folder %s",
            assay.getCode(), study.getName() + " (" + study.getCode() + ")"));
    EgnyteIntegrationOptions options = this.getOptionsFromLocation(location);
    String path = getAssayFolderPath(assay, options.getRootPath());
    StorageFolder storageFolder;
    try {
      EgnyteFolder egnyteFolder = egnyteClient.createFolder(options.getRootUrl(), path, options.getToken());
      storageFolder = this.convertFolder(egnyteFolder, options.getRootUrl());
    } catch (DuplicateFolderException e) {
      if (options.isUseExisting()) {
        LOGGER.warn("Existing folder will be used.");
        storageFolder = this.findFolder(location, assay);
      } else {
        throw new StudyStorageDuplicateException(e);
      }
    } catch (EgnyteException e) {
      throw new StudyStorageException(e);
    }
    LOGGER.debug("Assay folder: " + storageFolder);
    return storageFolder;
  }

  @Override
  public StorageFile saveFile(FileStorageLocation location, File file, Study study) throws StudyStorageException {
    LOGGER.debug("saveStudyFile({}, {})", file.getName(), study.getName());
    EgnyteIntegrationOptions options = this.getOptionsFromLocation(location);
    String path = getStudyFolderPath(study, options.getRootPath());
    StorageFile storageFile;
    try {
      EgnyteFile egnyteFile = egnyteClient.uploadFile(options.getRootUrl(), file, path, options.getToken());
      storageFile = this.convertEgnyteFile(egnyteFile, options.getRootUrl());
    } catch (EgnyteException e) {
      throw new StudyStorageException(e);
    }
    LOGGER.debug("Study file: " + storageFile);
    return storageFile;
  }

  @Override
  public StorageFile saveFile(FileStorageLocation location, File file, Assay assay) throws StudyStorageException {
    LOGGER.debug("saveAssayFile({}, {})", file.getName(), assay.getName());
    EgnyteIntegrationOptions options = this.getOptionsFromLocation(location);
    String path = getAssayFolderPath(assay, options.getRootPath());
    StorageFile storageFile;
    try {
      EgnyteFile egnyteFile = egnyteClient.uploadFile(options.getRootUrl(), file, path, options.getToken());
      storageFile = this.convertEgnyteFile(egnyteFile, options.getRootUrl());
    } catch (EgnyteException e) {
      throw new StudyStorageException(e);
    }
    LOGGER.debug("Assay file: " + storageFile);
    return storageFile;
  }

}
