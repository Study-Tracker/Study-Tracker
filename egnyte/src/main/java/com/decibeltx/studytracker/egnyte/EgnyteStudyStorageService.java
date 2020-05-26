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

  @Override
  public StorageFolder getProgramFolder(Program program) throws StudyStorageNotFoundException {
    String path = getProgramFolderPath(program);
    try {
      EgnyteObject obj = egnyteClient.findObjectByPath(path);
      if (!obj.isFolder()) {
        throw new StudyTrackerException("Found resource is not a folder");
      }
      return (EgnyteFolder) obj;
    } catch (EgnyteException e) {
      throw new StudyStorageNotFoundException(e);
    }
  }

  @Override
  public EgnyteFolder getStudyFolder(Study study) throws StudyStorageNotFoundException {
    String path = getStudyFolderPath(study);
    try {
      EgnyteObject obj = egnyteClient.findObjectByPath(path);
      if (!obj.isFolder()) {
        throw new StudyTrackerException("Found resource is not a folder");
      }
      return (EgnyteFolder) obj;
    } catch (EgnyteException e) {
      throw new StudyStorageNotFoundException(e);
    }
  }

  @Override
  public EgnyteFolder getAssayFolder(Assay assay) throws StudyStorageNotFoundException {
    String path = getAssayFolderPath(assay);
    try {
      EgnyteObject obj = egnyteClient.findObjectByPath(path);
      if (!obj.isFolder()) {
        throw new StudyTrackerException("Found resource is not a folder");
      }
      return (EgnyteFolder) obj;
    } catch (EgnyteException e) {
      throw new StudyStorageNotFoundException(e);
    }
  }

  @Override
  public StorageFolder createProgramFolder(Program program) throws StudyStorageException {
    LOGGER.info(String.format("Creating folder for program %s", program.getName()));
    String path = getProgramFolderPath(program);
    try {
      return egnyteClient.createFolder(path);
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
  public EgnyteFolder createStudyFolder(Study study) throws StudyStorageException {
    Program program = study.getProgram();
    LOGGER.info(String.format("Creating folder for study %s in program folder %s",
        study.getCode(), program.getName()));
    String path = getStudyFolderPath(study);
    try {
      return egnyteClient.createFolder(path);
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
  public EgnyteFolder createAssayFolder(Assay assay) throws StudyStorageException {
    Study study = assay.getStudy();
    LOGGER.info(String.format("Creating folder for assay %s in study folder %s",
        assay.getCode(), study.getName() + " (" + study.getCode() + ")"));
    String path = getAssayFolderPath(assay);
    try {
      return egnyteClient.createFolder(path);
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
  public EgnyteFile saveStudyFile(File file, Study study) throws StudyStorageException {
    String path = getStudyFolderPath(study);
    try {
      return egnyteClient.uploadFile(file, path);
    } catch (EgnyteException e) {
      throw new StudyStorageException(e);
    }
  }

  @Override
  public EgnyteFile saveAssayFile(File file, Assay assay) throws StudyStorageException {
    String path = getAssayFolderPath(assay);
    try {
      return egnyteClient.uploadFile(file, path);
    } catch (EgnyteException e) {
      throw new StudyStorageException(e);
    }
  }
}
