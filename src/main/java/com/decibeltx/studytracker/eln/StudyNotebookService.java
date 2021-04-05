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
import java.util.Optional;

public interface StudyNotebookService {

  Optional<NotebookFolder> findProgramFolder(Program program);

  Optional<NotebookFolder> findStudyFolder(Study study);

  Optional<NotebookFolder> findAssayFolder(Assay assay);

  NotebookFolder createProgramFolder(Program program) throws NotebookException;

  NotebookFolder createStudyFolder(Study study) throws NotebookException;

  NotebookFolder createAssayFolder(Assay assay) throws NotebookException;

  NotebookEntry createAssayNotebookEntry(Assay assay, String templateId, String benchlingUserId) throws NotebookException;

}
