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

package com.decibeltx.studytracker.core.notebook;

import com.decibeltx.studytracker.core.exception.NotebookException;
import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.Study;
import java.util.Optional;

public interface NotebookService<T extends NotebookEntry> {

  Optional<T> findProgramEntry(Program program);

  Optional<T> findStudyEntry(Study study);

  Optional<T> findAssayEntry(Assay assay);

  T createProgramEntry(Program program) throws NotebookException;

  T createStudyEntry(Study study) throws NotebookException;

  T createAssayEntry(Assay assay) throws NotebookException;

}
