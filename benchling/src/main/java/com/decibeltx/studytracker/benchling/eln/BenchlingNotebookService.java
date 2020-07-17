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

package com.decibeltx.studytracker.benchling.eln;

import com.decibeltx.studytracker.benchling.eln.entities.BenchlingNotebookEntry;
import com.decibeltx.studytracker.benchling.exception.EntityNotFoundException;
import com.decibeltx.studytracker.core.exception.NotebookException;
import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.notebook.NotebookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public final class BenchlingNotebookService implements NotebookService<BenchlingNotebookEntry> {

  private static final Logger LOGGER = LoggerFactory.getLogger(BenchlingNotebookService.class);
  private static final String ENTITY_PROPERTY = "benchlingELNEntity";

  @Autowired
  private BenchlingRestElnClient client;

  @Autowired
  private BenchlingElnOptions options;

  private static String getProgramFolderName(Program program) {
    return program.getName();
  }

  private static String getStudyFolderName(Study study) {
    return study.getName() + " (" + study.getCode() + ")";
  }

  @Override
  public Optional<BenchlingNotebookEntry> findProgramEntry(Program program) {
    LOGGER.info("Fetching benchling notebook entry for program: " + program.getName());
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
  public Optional<BenchlingNotebookEntry> findStudyEntry(Study study) {
    LOGGER.info("Fetching notebook entry for study: " + study.getCode());
    if (study.getAttributes().containsKey(ENTITY_PROPERTY)) {
      return Optional
          .of(client.findEntityById((String) study.getAttributes().get(ENTITY_PROPERTY)));
    } else {
      Optional<BenchlingNotebookEntry> programOptional = this.findProgramEntry(study.getProgram());
      if (!programOptional.isPresent()) {
        throw new EntityNotFoundException("Program notebook entry not found: "
            + study.getProgram().getName());
      }
      BenchlingNotebookEntry programFolder = programOptional.get();
      return client.findEntityChildren(programFolder.getEntityId()).stream()
          .filter(e -> e.getEntityName().equals(getStudyFolderName(study)))
          .findFirst();
    }
  }

  @Override
  public Optional<BenchlingNotebookEntry> findAssayEntry(Assay assay) {
    LOGGER.warn("Method not implemented.");
    return Optional.empty();
  }

  @Override
  public BenchlingNotebookEntry createProgramEntry(Program program) throws NotebookException {
    LOGGER.warn("Method not implemented.");
    return null;
  }

  @Override
  public BenchlingNotebookEntry createStudyEntry(Study study) throws NotebookException {
    LOGGER.info("Creating benchling notebook entry for study: " + study.getCode());

    Program program = study.getProgram();
    BenchlingNotebookEntry programFolder = new BenchlingNotebookEntry();
    if (program.getAttributes().containsKey(ENTITY_PROPERTY)) {
      programFolder.setEntityId((String) program.getAttributes().get(ENTITY_PROPERTY));
    }
    BenchlingNotebookEntry studyEntry = new BenchlingNotebookEntry();
    String studyEntityId = client.createStudyFolder(getStudyFolderName(study), programFolder.getEntityId());

    studyEntry.setFolderId(studyEntityId);
    LOGGER.info("Created study entity id: " + studyEntityId);
    final String entityHead = "lib_";
    final int entityHeadSize = entityHead.length();
    String entityIdForURL = studyEntityId.substring(entityHeadSize);

    //lower case, replase spaces with - and remove ':'
    String name = getStudyFolderName(study)
            .toLowerCase()
            .replaceAll(" ","-")
            .replaceAll("[^A-Za-z0-9-_\\s()]+", "")
            .replaceAll("[\\()]","")
            .trim();

    String urlBase = options.getRootFolderUrl();
    String transformedURL = urlBase+entityIdForURL+'-'+name;
    studyEntry.setLabel("Benchling");
    studyEntry.setUrl(transformedURL);


    return studyEntry;
  }


  @Override
  public BenchlingNotebookEntry createAssayEntry(Assay assay) throws NotebookException {
    LOGGER.warn("Method not implemented.");
    return null;
  }
}
