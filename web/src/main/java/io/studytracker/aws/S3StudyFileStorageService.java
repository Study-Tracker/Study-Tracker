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

package io.studytracker.aws;

import io.studytracker.model.Assay;
import io.studytracker.model.FileStorageLocation;
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import io.studytracker.repository.StudyRepository;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StudyStorageService;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class S3StudyFileStorageService implements StudyStorageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(S3StudyFileStorageService.class);

  @Autowired
  private S3DataFileStorageService s3Service;

  @Autowired
  private StudyRepository studyRepository;

  private void updateStudyAttributes(Study study, StorageFolder folder) {
    study.setAttribute(AWSAttributes.S3_BUCKET, folder.getName());
    study.setAttribute(AWSAttributes.S3_KEY, folder.getPath());
    studyRepository.save(study);
  }

  @Override
  public StorageFolder findFolder(FileStorageLocation location, Study study) throws StudyStorageNotFoundException {
    return null;
  }

  @Override
  public StorageFolder createFolder(FileStorageLocation location, Study study) throws StudyStorageException {
    LOGGER.info("Creating S3 storage folder for study: {}", study.getCode());
    String path = location.getRootFolderPath();
    String folderName = study.getProgram().getName() + "/" + study.getCode() + " - " + study.getName();
    StorageFolder storageFolder =  s3Service.createFolder(location, path, folderName);
    updateStudyAttributes(study, storageFolder);
    return storageFolder;
  }

  @Override
  public StorageFolder findFolder(FileStorageLocation location, Program program) throws StudyStorageNotFoundException {
    return null;
  }

  @Override
  public StorageFolder createFolder(FileStorageLocation location, Program program) throws StudyStorageException {
    return null;
  }

  @Override
  public StorageFolder findFolder(FileStorageLocation location, Assay assay) throws StudyStorageNotFoundException {
    return null;
  }

  @Override
  public StorageFolder createFolder(FileStorageLocation location, Assay assay) throws StudyStorageException {
    return null;
  }

  @Override
  public StorageFile saveFile(FileStorageLocation location, File file, Study study) throws StudyStorageException {
    return null;
  }

  @Override
  public StorageFile saveFile(FileStorageLocation location, File file, Assay assay) throws StudyStorageException {
    return null;
  }

}
