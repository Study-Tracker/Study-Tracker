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

package com.decibeltx.studytracker.egnyte;

import com.decibeltx.studytracker.core.exception.StudyTrackerException;
import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.storage.StorageFile;
import com.decibeltx.studytracker.core.storage.StorageFolder;
import com.decibeltx.studytracker.core.storage.StorageUtils;
import com.decibeltx.studytracker.core.storage.StudyStorageService;
import com.decibeltx.studytracker.core.storage.exception.StudyStorageDuplicateException;
import com.decibeltx.studytracker.core.storage.exception.StudyStorageException;
import com.decibeltx.studytracker.core.storage.exception.StudyStorageNotFoundException;
import com.decibeltx.studytracker.egnyte.entity.EgnyteFile;
import com.decibeltx.studytracker.egnyte.entity.EgnyteFolder;
import com.decibeltx.studytracker.egnyte.entity.EgnyteObject;
import com.decibeltx.studytracker.egnyte.exception.DuplicateFolderException;
import com.decibeltx.studytracker.egnyte.exception.EgnyteException;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriUtils;

public class EgnyteStudyStorageService implements StudyStorageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(EgnyteStudyStorageService.class);

  private final EgnyteClientOperations egnyteClient;
  private final EgnyteOptions options;

  public EgnyteStudyStorageService(EgnyteClientOperations egnyteClient, EgnyteOptions options) {
    this.egnyteClient = egnyteClient;
    this.options = options;
  }

  private String getProgramFolderName(Program program) {
    return StorageUtils.getProgramFolderName(program)
        .replaceAll("[^A-Za-z0-9-_\\s()]+", " ")
        .replaceAll("\\s+", " ")
        .trim();
  }

  private String getStudyFolderName(Study study) {
    return StorageUtils.getStudyFolderName(study)
        .replaceAll("[^A-Za-z0-9-_\\s()]+", " ")
        .replaceAll("\\s+", " ")
        .trim();
  }

  private String getAssayFolderName(Assay assay) {
    return StorageUtils.getAssayFolderName(assay)
        .replaceAll("[^A-Za-z0-9-_\\s()]+", " ")
        .replaceAll("\\s+", " ")
        .trim();
  }

  private String getProgramFolderPath(Program program) {
    String root = options.getRootPath();
    return root + getProgramFolderName(program) + "/";
  }

  private String getStudyFolderPath(Study study) {
    return this.getProgramFolderPath(study.getProgram()) + getStudyFolderName(study) + "/";
  }

  private String getAssayFolderPath(Assay assay) {
    Study study = assay.getStudy();
    String studyPath = this.getStudyFolderPath(study);
    return studyPath + getAssayFolderName(assay) + "/";
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
      storageFolder.getSubFolders().add(convertEgnyteFolder(subFolder));
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
    String path = getProgramFolderPath(program);
    try {
      EgnyteObject obj = egnyteClient.findObjectByPath(path);
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
    String path = getStudyFolderPath(study);
    try {
      EgnyteObject obj = egnyteClient.findObjectByPath(path);
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
    String path = getAssayFolderPath(assay);
    try {
      EgnyteObject obj = egnyteClient.findObjectByPath(path);
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
      if (options.isUseExisting()) {
        return this.getProgramFolder(program);
      }
      throw new StudyStorageDuplicateException(e);
    } catch (EgnyteException e) {
      throw new StudyStorageException(e);
    }
  }

  @Override
  public StorageFolder createStudyFolder(Study study) throws StudyStorageException {
    Program program = study.getProgram();
    LOGGER.info(String.format("Creating folder for study %s in program folder %s",
        study.getCode(), program.getName()));
    String path = getStudyFolderPath(study);
    try {
      return this.convertFolder(egnyteClient.createFolder(path));
    } catch (DuplicateFolderException e) {
      if (options.isUseExisting()) {
        return this.getStudyFolder(study);
      }
      throw new StudyStorageDuplicateException(e);
    } catch (EgnyteException e) {
      throw new StudyStorageException(e);
    }
  }

  @Override
  public StorageFolder createAssayFolder(Assay assay) throws StudyStorageException {
    Study study = assay.getStudy();
    LOGGER.info(String.format("Creating folder for assay %s in study folder %s",
        assay.getCode(), study.getName() + " (" + study.getCode() + ")"));
    String path = getAssayFolderPath(assay);
    try {
      return this.convertFolder(egnyteClient.createFolder(path));
    } catch (DuplicateFolderException e) {
      if (options.isUseExisting()) {
        return this.getAssayFolder(assay);
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
