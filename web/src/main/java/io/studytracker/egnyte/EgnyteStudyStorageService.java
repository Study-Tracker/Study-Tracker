/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
    return path;
  }

  public String getStudyFolderPath(Study study) {
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
    return path;
  }

  public String getAssayFolderPath(Assay assay) {
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
    try {
      parentFolder = egnyteClient.findFolderById(egnyteFolder.getParentId());
    } catch (Exception e) {
      LOGGER.warn("No Egnyte folder found with ID: " + egnyteFolder.getParentId());
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
    String path = getProgramFolderPath(program);
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
      return convertFolder((EgnyteFolder) obj);
    } catch (EgnyteException e) {
      throw new StudyStorageNotFoundException(e);
    }
  }

  @Override
  public StorageFolder getStudyFolder(Study study) throws StudyStorageNotFoundException {
    return this.getStudyFolder(study, true);
  }

  @Override
  public StorageFolder getStudyFolder(Study study, boolean includeContents)
      throws StudyStorageNotFoundException {
    String path = getStudyFolderPath(study);
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
      return this.convertFolder((EgnyteFolder) obj);
    } catch (EgnyteException e) {
      throw new StudyStorageNotFoundException(e);
    }
  }

  @Override
  public StorageFolder getAssayFolder(Assay assay) throws StudyStorageNotFoundException {
    return this.getAssayFolder(assay, true);
  }

  @Override
  public StorageFolder getAssayFolder(Assay assay, boolean includeContents)
      throws StudyStorageNotFoundException {
    String path = getAssayFolderPath(assay);
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
      return this.convertFolder((EgnyteFolder) obj);
    } catch (EgnyteException e) {
      throw new StudyStorageNotFoundException(e);
    }
  }

  @Override
  public StorageFolder createProgramFolder(Program program) throws StudyStorageException {
    LOGGER.info(String.format("Creating folder for program %s", program.getName()));
    String path = getProgramFolderPath(program);
    try {
      return this.convertFolder(egnyteClient.createFolder(path));
    } catch (DuplicateFolderException e) {
      LOGGER.warn("Duplicate folder found: " + path);
      if (options.isUseExisting()) {
        LOGGER.warn("Existing folder will be used.");
        return this.getProgramFolder(program, false);
      }
      throw new StudyStorageDuplicateException(e);
    } catch (EgnyteException e) {
      throw new StudyStorageException(e);
    }
  }

  @Override
  public StorageFolder createStudyFolder(Study study) throws StudyStorageException {
    Program program = study.getProgram();
    String path = getStudyFolderPath(study);
    LOGGER.info(
        String.format(
            "Creating folder for study %s in program folder %s with path: %s",
            study.getCode(), program.getName(), path));
    try {
      return this.convertFolder(egnyteClient.createFolder(path));
    } catch (DuplicateFolderException e) {
      if (options.isUseExisting()) {
        LOGGER.warn("Existing folder will be used.");
        return this.getStudyFolder(study, false);
      }
      throw new StudyStorageDuplicateException(e);
    } catch (EgnyteException e) {
      throw new StudyStorageException(e);
    }
  }

  @Override
  public StorageFolder createAssayFolder(Assay assay) throws StudyStorageException {
    Study study = assay.getStudy();
    LOGGER.info(
        String.format(
            "Creating folder for assay %s in study folder %s",
            assay.getCode(), study.getName() + " (" + study.getCode() + ")"));
    String path = getAssayFolderPath(assay);
    try {
      return this.convertFolder(egnyteClient.createFolder(path));
    } catch (DuplicateFolderException e) {
      if (options.isUseExisting()) {
        LOGGER.warn("Existing folder will be used.");
        return this.getAssayFolder(assay, false);
      }
      throw new StudyStorageDuplicateException(e);
    } catch (EgnyteException e) {
      throw new StudyStorageException(e);
    }
  }

  @Override
  public StorageFile saveStudyFile(File file, Study study) throws StudyStorageException {
    String path = getStudyFolderPath(study);
    try {
      return this.convertEgnyteFile(egnyteClient.uploadFile(file, path));
    } catch (EgnyteException e) {
      throw new StudyStorageException(e);
    }
  }

  @Override
  public StorageFile saveAssayFile(File file, Assay assay) throws StudyStorageException {
    String path = getAssayFolderPath(assay);
    try {
      return this.convertEgnyteFile(egnyteClient.uploadFile(file, path));
    } catch (EgnyteException e) {
      throw new StudyStorageException(e);
    }
  }
}
