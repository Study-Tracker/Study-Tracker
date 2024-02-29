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

package io.studytracker.eln;

import io.studytracker.exception.NotebookException;
import io.studytracker.model.Assay;
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import java.util.List;
import java.util.Optional;

public interface NotebookFolderService {

  /**
   * Returns a list of all folders for a program.
   *
   * @param program
   * @return
   */
  List<NotebookFolder> findProgramFolders(Program program);

  /**
   * Returns a list of all folders for a study.
   *
   * @param study
   * @return
   */
  List<NotebookFolder> findStudyFolders(Study study);

  /**
   * Returns a list of all folders for an assay.
   * @param assay
   * @return
   */
  List<NotebookFolder> findAssayFolders(Assay assay);

  /**
   * Returns a program's primary {@link NotebookFolder}, if one exists.
   *
   * @param program
   * @return
   */
  Optional<NotebookFolder> findPrimaryProgramFolder(Program program);

  /**
   * Returns a study's primary {@link NotebookFolder}, if one exists.
   *
   * @param study
   * @return
   */
  Optional<NotebookFolder> findPrimaryStudyFolder(Study study);

  /**
   * Returns a study's primary {@link NotebookFolder}, if one exists, optionally including its contents.
   *
   * @param study
   * @param includeContents
   * @return
   */
  Optional<NotebookFolder> findPrimaryStudyFolder(Study study, boolean includeContents);

  /**
   * Returns an assay's primary {@link NotebookFolder}, if one exists.
   *
   * @param assay
   * @return
   */
  Optional<NotebookFolder> findPrimaryAssayFolder(Assay assay);

  /**
   * Returns an assay's primary {@link NotebookFolder}, if one exists, optionally including its contents.
   *
   * @param assay
   * @return
   */
  Optional<NotebookFolder> findPrimaryAssayFolder(Assay assay, boolean includeContents);

  /**
   * Creates a folder for a program in the ELN and returns a {@link NotebookFolder}.
   *
   * @param program
   * @return
   * @throws NotebookException
   */
  NotebookFolder createProgramFolder(Program program) throws NotebookException;

  /**
   * Creates a folder for a study in the ELN and returns a {@link NotebookFolder}.
   *
   * @param study
   * @return
   * @throws NotebookException
   */
  NotebookFolder createStudyFolder(Study study) throws NotebookException;

  /**
   * Creates a folder for an assay in the ELN and returns a {@link NotebookFolder}.
   *
   * @param assay
   * @return
   * @throws NotebookException
   */
  NotebookFolder createAssayFolder(Assay assay) throws NotebookException;

  /**
   * Lists all project folders within the ELN.
   *
   * @return
   */
  List<NotebookFolder> listNotebookProjectFolders();


}
