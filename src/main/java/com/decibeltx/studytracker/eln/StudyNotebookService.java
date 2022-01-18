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

package com.decibeltx.studytracker.eln;

import com.decibeltx.studytracker.exception.NotebookException;
import com.decibeltx.studytracker.model.Assay;
import com.decibeltx.studytracker.model.Program;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.User;
import java.util.List;
import java.util.Optional;

public interface StudyNotebookService {

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

  /**
   * Fetches a list of notebook entry templates.
   *
   * @return
   */
  List<NotebookTemplate> findEntryTemplates();

  /**
   * Finds and returns a {@link NotebookTemplate} if one exists with the provided ID.
   *
   * @param id
   * @return
   */
  Optional<NotebookTemplate> findEntryTemplateById(String id);

  /**
   * Creates a blank notebook entry for a study in the ELN and returns a {@link NotebookEntry}.
   *
   * @param study
   * @return
   * @throws NotebookException
   */
  NotebookEntry createStudyNotebookEntry(Study study) throws NotebookException;

  /**
   * Creates a blank notebook entry from the provided template for a study in the ELN and returns a
   *   {@link NotebookEntry}.
   *
   * @param study
   * @return
   * @throws NotebookException
   */
  NotebookEntry createStudyNotebookEntry(Study study, NotebookTemplate template)
      throws NotebookException;

  /**
   * Creates a blank notebook entry for an assay in the ELN and returns a {@link NotebookEntry}.
   *
   * @param assay
   * @return
   * @throws NotebookException
   */
  NotebookEntry createAssayNotebookEntry(Assay assay) throws NotebookException;

  /**
   * Creates a blank notebook entry from the provided template for an assay in the ELN and returns a
   *   {@link NotebookEntry}.
   *
   * @param assay
   * @return
   * @throws NotebookException
   */
  NotebookEntry createAssayNotebookEntry(Assay assay, NotebookTemplate template)
      throws NotebookException;

  /**
   * Returns a list of all users registered in the ELN.
   *
   * @return
   */
  List<NotebookUser> findNotebookUsers();

  /**
   * Attempts to find a Study Tracker user in the ELN.
   *
   * @param user
   * @return
   */
  Optional<NotebookUser> findNotebookUser(User user);

}
