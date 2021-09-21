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

package com.decibeltx.studytracker.storage;

import com.decibeltx.studytracker.model.Assay;
import com.decibeltx.studytracker.model.Program;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.storage.exception.StudyStorageDuplicateException;
import com.decibeltx.studytracker.storage.exception.StudyStorageException;
import com.decibeltx.studytracker.storage.exception.StudyStorageNotFoundException;
import java.io.File;

/**
 * Base interface for a service that reads and writes study files to a connected file system.
 */
public interface StudyStorageService {

  /**
   * Returns reference to a {@link Program} instance's storage folder. Throws a {@link
   * StudyStorageNotFoundException} if the folder does not exist;
   *
   * @param program
   * @return
   */
  StorageFolder getProgramFolder(Program program) throws StudyStorageNotFoundException;

  /**
   * Returns reference to a {@link Program} instance's storage folder, optionally including its
   *    contents. Throws a {@link StudyStorageNotFoundException} if the folder does not exist;
   *
   * @param program
   * @param includeContents
   * @return
   * @throws StudyStorageNotFoundException
   */
  StorageFolder getProgramFolder(Program program, boolean includeContents) throws StudyStorageNotFoundException;

  /**
   * Returns reference to a {@link Study} instance's storage folder. Throws a {@link
   * StudyStorageNotFoundException} if the folder does not exist;
   *
   * @param study
   * @return
   */
  StorageFolder getStudyFolder(Study study) throws StudyStorageNotFoundException;

  /**
   * Returns reference to a {@link Study} instance's storage folder, optionally including its
   * contents. Throws a {@link StudyStorageNotFoundException} if the folder does not exist;
   *
   * @param study
   * @return
   */
  StorageFolder getStudyFolder(Study study, boolean includeContents) throws StudyStorageNotFoundException;

  /**
   * Returns reference to a {@link Assay} instance's storage folder. Throws a {@link
   * StudyStorageNotFoundException} if the folder does not exist;
   *
   * @param assay
   * @return
   */
  StorageFolder getAssayFolder(Assay assay) throws StudyStorageNotFoundException;

  /**
   * Returns reference to a {@link Assay} instance's storage folder, optionally including its
   * contents. Throws a {@link StudyStorageNotFoundException} if the folder does not exist;
   *
   * @param assay
   * @return
   */
  StorageFolder getAssayFolder(Assay assay, boolean includeContents) throws StudyStorageNotFoundException;

  /**
   * Creates a folder for the target {@link Program}. Throws a {@link
   * StudyStorageDuplicateException} if the folder already exists and a {@link
   * com.decibeltx.studytracker.storage.exception.StudyStorageWriteException} if the folder cannot
   * be created.
   *
   * @param program
   * @return
   */
  StorageFolder createProgramFolder(Program program) throws StudyStorageException;

  /**
   * Creates a new directory in the storage file system for the target {@link Study}. Throws a
   * {@link StudyStorageDuplicateException} if the folder already exists and a {@link
   * com.decibeltx.studytracker.storage.exception.StudyStorageWriteException} if the folder cannot
   * be created.
   *
   * @param study
   * @return
   */
  StorageFolder createStudyFolder(Study study) throws StudyStorageException;

  /**
   * Creates a new directory in the storage file system for the target {@link Assay}. Throws a
   * {@link StudyStorageDuplicateException} if the folder already exists and a {@link
   * com.decibeltx.studytracker.storage.exception.StudyStorageWriteException} if the folder cannot
   * be created.
   *
   * @param assay
   * @return
   */
  StorageFolder createAssayFolder(Assay assay) throws StudyStorageException;

  /**
   * Uploads the target file to the appropriate directory for the target {@link Study}. Throws a
   * {@link StudyStorageDuplicateException} if the file already exists, a {@link
   * com.decibeltx.studytracker.storage.exception.StudyStorageWriteException} if the file cannot be
   * written, and a {@link StudyStorageNotFoundException} if the target folder cannot be found.
   *
   * @param file
   * @param study
   * @return
   */
  StorageFile saveStudyFile(File file, Study study) throws StudyStorageException;

  /**
   * Uploads the target file to the appropriate directory for the target {@link Assay}. Throws a *
   * {@link StudyStorageDuplicateException} if the file already exists, a *   {@link
   * com.decibeltx.studytracker.storage.exception.StudyStorageWriteException} if the file
   * cannot be written, and a *   {@link StudyStorageNotFoundException} if the target folder cannot
   * be found.
   *
   * @param file
   * @param assay
   * @return
   */
  StorageFile saveAssayFile(File file, Assay assay) throws StudyStorageException;

}
