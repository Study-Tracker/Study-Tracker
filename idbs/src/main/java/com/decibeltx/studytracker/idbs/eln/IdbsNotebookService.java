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

package com.decibeltx.studytracker.idbs.eln;

import com.decibeltx.studytracker.core.exception.NotebookException;
import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.notebook.NotebookService;
import com.decibeltx.studytracker.idbs.eln.entities.IdbsNotebookEntry;
import com.decibeltx.studytracker.idbs.exception.EntityNotFoundException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public final class IdbsNotebookService implements NotebookService<IdbsNotebookEntry> {

  private static final Logger LOGGER = LoggerFactory.getLogger(IdbsNotebookService.class);
  private static final String ENTITY_PROPERTY = "idbsElnEntity";

  @Autowired
  private IdbsRestElnClient client;

  @Autowired
  private IdbsElnOptions options;

  private static String getProgramFolderName(Program program) {
    return program.getName();
  }

  private static String getStudyFolderName(Study study) {
    return study.getName() + " (" + study.getCode() + ")";
  }

  @Override
  public Optional<IdbsNotebookEntry> findProgramEntry(Program program) {
    LOGGER.info("Fetching notebook entry for program: " + program.getName());
    if (program.getAttributes().containsKey(ENTITY_PROPERTY)) {
      return Optional
          .of(client.findEntityById((String) program.getAttributes().get(ENTITY_PROPERTY)));
    } else {
      return client.findEntityChildren(options.getRootEntity()).stream()
          .filter(e -> e.getEntityName().equals(getProgramFolderName(program)))
          .findFirst();
    }
  }

  @Override
  public Optional<IdbsNotebookEntry> findStudyEntry(Study study) {
    LOGGER.info("Fetching notebook entry for study: " + study.getCode());
    if (study.getAttributes().containsKey(ENTITY_PROPERTY)) {
      return Optional
          .of(client.findEntityById((String) study.getAttributes().get(ENTITY_PROPERTY)));
    } else {
      Optional<IdbsNotebookEntry> programOptional = this.findProgramEntry(study.getProgram());
      if (!programOptional.isPresent()) {
        throw new EntityNotFoundException("Program notebook entry not found: "
            + study.getProgram().getName());
      }
      IdbsNotebookEntry programFolder = programOptional.get();
      return client.findEntityChildren(programFolder.getEntityId()).stream()
          .filter(e -> e.getEntityName().equals(getStudyFolderName(study)))
          .findFirst();
    }
  }

  @Override
  public Optional<IdbsNotebookEntry> findAssayEntry(Assay assay) {
    LOGGER.warn("Method not implemented.");
    return Optional.empty();
  }

  @Override
  public IdbsNotebookEntry createProgramEntry(Program program) throws NotebookException {
    LOGGER.warn("Method not implemented.");
    return null;
  }

  @Override
  public IdbsNotebookEntry createStudyEntry(Study study) throws NotebookException {
    LOGGER.info("Creating notebook entry for study: " + study.getCode());
    Optional<IdbsNotebookEntry> programOptional = this.findProgramEntry(study.getProgram());
    if (!programOptional.isPresent()) {
      throw new EntityNotFoundException("Program notebook entry not found: "
          + study.getProgram().getName());
    }
    IdbsNotebookEntry programFolder = programOptional.get();
    String studyEntityId = client
        .createStudyFolder(getStudyFolderName(study), programFolder.getEntityId());
    return client.findEntityById(studyEntityId);
  }

  @Override
  public IdbsNotebookEntry createAssayEntry(Assay assay) throws NotebookException {
    LOGGER.warn("Method not implemented.");
    return null;
  }
}
