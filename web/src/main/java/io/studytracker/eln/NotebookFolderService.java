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

package io.studytracker.eln;

import io.studytracker.exception.NotebookException;
import io.studytracker.model.Assay;
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import java.util.Optional;

public interface NotebookFolderService {

  /**
   * Returns a program's {@link NotebookFolder}, if one exists.
   *
   * @param program
   * @return
   */
  Optional<NotebookFolder> findProgramFolder(Program program);

  /**
   * Returns a study's {@link NotebookFolder}, if one exists.
   *
   * @param study
   * @return
   */
  Optional<NotebookFolder> findStudyFolder(Study study);

  /**
   * Returns an assay's {@link NotebookFolder}, if one exists.
   *
   * @param assay
   * @return
   */
  Optional<NotebookFolder> findAssayFolder(Assay assay);

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
   * Creates a folder for a assay in the ELN and returns a {@link NotebookFolder}.
   *
   * @param assay
   * @return
   * @throws NotebookException
   */
  NotebookFolder createAssayFolder(Assay assay) throws NotebookException;


}
