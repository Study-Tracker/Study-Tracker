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
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.model.Assay;
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StudyStorageService;
import io.studytracker.storage.exception.StudyStorageDuplicateException;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriUtils;

public class EgnyteStudyStorageService implements StudyStorageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(EgnyteStudyStorageService.class);

  private final EgnyteClientOperations egnyteClient;

  private final EgnyteOptions options;

  @Autowired private EgnyteFolderNamingService egnyteFolderNamingService;

  public EgnyteStudyStorageService(EgnyteClientOperations egnyteClient, EgnyteOptions options) {
    this.egnyteClient = egnyteClient;
    this.options = options;
  }

  public String getProgramFolderPath(Program program) {
    LOGGER.debug("getProgramFolderPath({})", program.getName());
    String path;
    if (program.getStorageFolder() != null && program.getStorageFolder().getPath() != null) {
      path = program.getStorageFolder().getPath();
      if (!path.endsWith("/")) {
        path = path + "/";
      }
    } else {
      String root = options.getRootPath();
      path = root + egnyteFolderNamingService.getProgramStorageFolderName(program) + "/";
    }
    LOGGER.debug("Program folder path: " + path);
    return path;
  }

  public String getStudyFolderPath(Study study) {
    LOGGER.debug("getStudyFolderPath({})", study.getName());
    String path;
    if (study.getStorageFolder() != null && study.getStorageFolder().getPath() != null) {
      path = study.getStorageFolder().getPath();
      if (!path.endsWith("/")) {
        path = path + "/";
      }
    } else {
      path =
          this.getProgramFolderPath(study.getProgram())
              + egnyteFolderNamingService.getStudyStorageFolderName(study)
              + "/";
    }
    LOGGER.debug("Study folder path: " + path);
    return path;
  }

  public String getAssayFolderPath(Assay assay) {
    LOGGER.debug("getAssayFolderPath({})", assay.getName());
    String path;
    if (assay.getStorageFolder() != null && assay.getStorageFolder().getPath() != null) {
      path = assay.getStorageFolder().getPath();
      if (!path.endsWith("/")) {
        path = path + "/";
      }
    } else {
      Study study = assay.getStudy();
      String studyPath = this.getStudyFolderPath(study);
      path = studyPath + egnyteFolderNamingService.getAssayStorageFolderName(assay) + "/";
    }
    LOGGER.debug("Assay folder path: " + path);
    return path;
  }

  private StorageFolder convertEgnyteFolder(EgnyteFolder egnyteFolder) {
    StorageFolder storageFolder = new StorageFolder();
    storageFolder.setName(egnyteFolder.getName());
    storageFolder.setPath(egnyteFolder.getPath());
    storageFolder.setUrl(egnyteFolder.getUrl());
    return storageFolder;
  }

  private StorageFile convertEgnyteFile(EgnyteFile egnyteFile) {

    StorageFile storageFile = new StorageFile();
    storageFile.setPath(egnyteFile.getPath());

    if (egnyteFile.getName() == null) {
      storageFile.setName(new File(egnyteFile.getPath()).getName());
    } else {
      storageFile.setName(egnyteFile.getName());
    }

    if (egnyteFile.getUrl() == null) {
      try {
        String path = egnyteFile.getPath().replace("/" + storageFile.getName(), "");
        path = UriUtils.encodePath(path, "UTF-8").replace("&", "%26");
        String url = options.getRootUrl().toString();
        if (url.endsWith("/")) {
          url = url.substring(0, url.length() - 1);
        }
        url = url + "/app/index.do#storage/files/1" + path;
        storageFile.setUrl(url);
      } catch (Exception e) {
        throw new StudyTrackerException(e);
      }
    } else {
      storageFile.setUrl(egnyteFile.getUrl());
    }

    return storageFile;
  }

  private StorageFolder convertFolder(EgnyteFolder egnyteFolder) {
    StorageFolder storageFolder = convertEgnyteFolder(egnyteFolder);
    for (EgnyteFile file : egnyteFolder.getFiles()) {
      storageFolder.getFiles().add(convertEgnyteFile(file));
    }
    for (EgnyteFolder subFolder : egnyteFolder.getSubFolders()) {
      storageFolder.getSubFolders().add(convertFolder(subFolder));
    }
    EgnyteFolder parentFolder = null;
    if (egnyteFolder.getParentId() != null) {
      try {
        parentFolder = egnyteClient.findFolderById(egnyteFolder.getParentId());
      } catch (Exception e) {
        LOGGER.warn("No Egnyte folder found with ID: " + egnyteFolder.getParentId());
      }
    }
    if (parentFolder != null) {
      storageFolder.setParentFolder(convertEgnyteFolder(parentFolder));
    }
    return storageFolder;
  }

  @Override
  public StorageFolder getProgramFolder(Program program) throws StudyStorageNotFoundException {
    return this.getProgramFolder(program, true);
  }

  @Override
  public StorageFolder getProgramFolder(Program program, boolean includeContents)
      throws StudyStorageNotFoundException {
    LOGGER.debug("getProgramFolder({}, {})", program.getName(), includeContents);
    String path = getProgramFolderPath(program);
    StorageFolder storageFolder;
    try {
      EgnyteObject obj;
      if (includeContents) {
        obj = egnyteClient.findObjectByPath(path);
      } else {
        obj = egnyteClient.findObjectByPath(path, -1);
      }
      if (!obj.isFolder()) {
        throw new StudyTrackerException("Found resource is not a folder");
      }
      LOGGER.debug("Egnyte folder: " + ((EgnyteFolder) obj));
      storageFolder = convertFolder((EgnyteFolder) obj);
    } catch (EgnyteException e) {
      throw new StudyStorageNotFoundException(e);
    }
    LOGGER.debug("Program folder: " + storageFolder);
    return storageFolder;
  }

  @Override
  public StorageFolder getStudyFolder(Study study) throws StudyStorageNotFoundException {
    return this.getStudyFolder(study, true);
  }

  @Override
  public StorageFolder getStudyFolder(Study study, boolean includeContents)
      throws StudyStorageNotFoundException {
    LOGGER.debug("getStudyFolder({}, {})", study.getName(), includeContents);
    String path = getStudyFolderPath(study);
    StorageFolder storageFolder;
    try {
      EgnyteObject obj;
      if (includeContents) {
        obj = egnyteClient.findObjectByPath(path);
      } else {
        obj = egnyteClient.findObjectByPath(path, -1);
      }
      if (!obj.isFolder()) {
        throw new StudyTrackerException("Found resource is not a folder");
      }
      LOGGER.debug("Egnyte folder: " + ((EgnyteFolder) obj));
      storageFolder = this.convertFolder((EgnyteFolder) obj);
    } catch (EgnyteException e) {
      throw new StudyStorageNotFoundException(e);
    }
    LOGGER.debug("Study folder: " + storageFolder);
    return storageFolder;
  }

  @Override
  public StorageFolder getAssayFolder(Assay assay) throws StudyStorageNotFoundException {
    return this.getAssayFolder(assay, true);
  }

  @Override
  public StorageFolder getAssayFolder(Assay assay, boolean includeContents)
      throws StudyStorageNotFoundException {
    LOGGER.debug("getAssayFolder({}, {})", assay.getName(), includeContents);
    String path = getAssayFolderPath(assay);
    StorageFolder storageFolder;
    try {
      EgnyteObject obj;
      if (includeContents) {
        obj = egnyteClient.findObjectByPath(path);
      } else {
        obj = egnyteClient.findObjectByPath(path, -1);
      }
      if (!obj.isFolder()) {
        throw new StudyTrackerException("Found resource is not a folder");
      }
      LOGGER.debug("Egnyte folder: " + ((EgnyteFolder) obj));
      storageFolder = this.convertFolder((EgnyteFolder) obj);
    } catch (EgnyteException e) {
      throw new StudyStorageNotFoundException(e);
    }
    LOGGER.debug("Assay folder: " + storageFolder);
    return storageFolder;
  }

  @Override
  public StorageFolder createProgramFolder(Program program) throws StudyStorageException {
    LOGGER.info(String.format("Creating folder for program %s", program.getName()));
    String path = getProgramFolderPath(program);
    StorageFolder storageFolder;
    try {
      storageFolder = this.convertFolder(egnyteClient.createFolder(path));
    } catch (DuplicateFolderException e) {
      LOGGER.warn("Duplicate folder found: " + path);
      if (options.isUseExisting()) {
        LOGGER.warn("Existing folder will be used.");
        storageFolder = this.getProgramFolder(program, false);
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
  public StorageFolder createStudyFolder(Study study) throws StudyStorageException {
    Program program = study.getProgram();
    String path = getStudyFolderPath(study);
    StorageFolder storageFolder;
    LOGGER.info(
        String.format(
            "Creating folder for study %s in program folder %s with path: %s",
            study.getCode(), program.getName(), path));
    try {
      storageFolder = this.convertFolder(egnyteClient.createFolder(path));
    } catch (DuplicateFolderException e) {
      if (options.isUseExisting()) {
        LOGGER.warn("Existing folder will be used.");
        storageFolder = this.getStudyFolder(study, false);
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
  public StorageFolder createAssayFolder(Assay assay) throws StudyStorageException {
    Study study = assay.getStudy();
    LOGGER.info(
        String.format(
            "Creating folder for assay %s in study folder %s",
            assay.getCode(), study.getName() + " (" + study.getCode() + ")"));
    String path = getAssayFolderPath(assay);
    StorageFolder storageFolder;
    try {
      storageFolder = this.convertFolder(egnyteClient.createFolder(path));
    } catch (DuplicateFolderException e) {
      if (options.isUseExisting()) {
        LOGGER.warn("Existing folder will be used.");
        storageFolder = this.getAssayFolder(assay, false);
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
  public StorageFile saveStudyFile(File file, Study study) throws StudyStorageException {
    LOGGER.debug("saveStudyFile({}, {})", file.getName(), study.getName());
    String path = getStudyFolderPath(study);
    StorageFile storageFile;
    try {
      storageFile = this.convertEgnyteFile(egnyteClient.uploadFile(file, path));
    } catch (EgnyteException e) {
      throw new StudyStorageException(e);
    }
    LOGGER.debug("Study file: " + storageFile);
    return storageFile;
  }

  @Override
  public StorageFile saveAssayFile(File file, Assay assay) throws StudyStorageException {
    LOGGER.debug("saveAssayFile({}, {})", file.getName(), assay.getName());
    String path = getAssayFolderPath(assay);
    StorageFile storageFile;
    try {
      storageFile = this.convertEgnyteFile(egnyteClient.uploadFile(file, path));
    } catch (EgnyteException e) {
      throw new StudyStorageException(e);
    }
    LOGGER.debug("Assay file: " + storageFile);
    return storageFile;
  }

  @Override
  public StorageFile saveFileToFolder(File file, StorageFolder folder)
      throws StudyStorageException {
    try {
      return this.convertEgnyteFile(egnyteClient.uploadFile(file, folder.getPath()));
    } catch (EgnyteException e) {
      throw new StudyStorageException(e);
    }
  }
}
