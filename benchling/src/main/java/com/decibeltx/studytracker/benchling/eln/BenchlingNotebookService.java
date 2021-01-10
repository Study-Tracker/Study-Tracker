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

import com.decibeltx.studytracker.benchling.eln.entities.BenchlingFolder;
import com.decibeltx.studytracker.benchling.exception.EntityNotFoundException;
import com.decibeltx.studytracker.core.eln.NotebookFolder;
import com.decibeltx.studytracker.core.eln.StudyNotebookService;
import com.decibeltx.studytracker.core.exception.NotebookException;
import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.service.NamingService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public final class BenchlingNotebookService implements StudyNotebookService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BenchlingNotebookService.class);

  @Autowired
  private BenchlingElnRestClient client;

  @Autowired
  private BenchlingElnOptions options;

  @Autowired
  private NamingService namingService;

  private String createFolderUrl(BenchlingFolder folder) {
    return options.getRootFolderUrl() + "/" + folder.getId().replace("lib_", "") + "-"
        + folder.getName().toLowerCase()
        .replaceAll(" ", "-")
        .replaceAll("[^A-Za-z0-9-_\\s()]+", "")
        .replaceAll("[\\()]", "")
        .trim();
  }

  private NotebookFolder convertFolder(BenchlingFolder folder) {
    NotebookFolder notebookFolder = new NotebookFolder();
    notebookFolder.setName(folder.getName());
    notebookFolder.setReferenceId(folder.getId());
    notebookFolder.setUrl(this.createFolderUrl(folder));
    notebookFolder.getAttributes().put("projectId", folder.getProjectId());
    return notebookFolder;
  }

  @Override
  public Optional<NotebookFolder> findProgramFolder(Program program) {

    LOGGER.info("Fetching benchling notebook entry for program: " + program.getName());

    if (program.getNotebookFolder() != null) {
      Optional<BenchlingFolder> optional = client
          .findFolderById(program.getNotebookFolder().getReferenceId());
      if (optional.isPresent()) {
        return Optional.of(this.convertFolder(optional.get()));
      } else {
        return Optional.empty();
      }
    } else {
      LOGGER.warn(
          String.format("Program %s does not have a notebook folder set.", program.getName()));
      return Optional.empty();
    }

  }

  @Override
  public Optional<NotebookFolder> findStudyFolder(Study study) {

    LOGGER.info("Fetching notebook entry for study: " + study.getCode());

    // Does the study have the folder object set?
    if (study.getNotebookFolder() != null) {
      NotebookFolder studyFolder = study.getNotebookFolder();
      Optional<BenchlingFolder> optional = client.findFolderById(studyFolder.getReferenceId());
      if (optional.isPresent()) {
        return Optional.of(this.convertFolder(optional.get()));
      } else {
        return Optional.empty();
      }
    } else {
      LOGGER.warn(String.format("Study %s does not have a notebook folder set.", study.getName()));
      return Optional.empty();
    }

  }

  @Override
  public Optional<NotebookFolder> findAssayFolder(Assay assay) {

    LOGGER.info("Fetching notebook entry for assay: " + assay.getCode());

    if (assay.getNotebookFolder() != null) {
      NotebookFolder assayFolder = assay.getNotebookFolder();
      Optional<BenchlingFolder> optional = client.findFolderById(assayFolder.getReferenceId());
      if (optional.isPresent()) {
        return Optional.of(this.convertFolder(optional.get()));
      } else {
        return Optional.empty();
      }
    } else {
      LOGGER.warn(String.format("Assay %s does not have a notebook folder set.", assay.getName()));
      return Optional.empty();
    }

  }

  @Override
  public NotebookFolder createProgramFolder(Program program) throws NotebookException {
    LOGGER.info(
        "Registering new program folder. NOTE: Benchling does not support project creation, so a valid folderId must be provided when registering new programs.");
    if (program.getNotebookFolder() != null
        && program.getNotebookFolder().getReferenceId() != null) {
      try {
        BenchlingFolder folder = client.findFolderById(program.getNotebookFolder().getReferenceId())
            .get();
        return this.convertFolder(folder);
      } catch (Exception e) {
        LOGGER.error("Failed to register new program: " + program.getName());
        throw new NotebookException(e);
      }
    } else {
      LOGGER.warn("Program folder ID is not set, cannot create NotebookFolder record for program: "
          + program.getName());
    }
    return null;
  }

  @Override
  public NotebookFolder createStudyFolder(Study study) throws NotebookException {
    LOGGER.info("Creating Benchling folder for study: " + study.getCode());

    Optional<NotebookFolder> programFolderOptional = this.findProgramFolder(study.getProgram());
    if (!programFolderOptional.isPresent()) {
      throw new EntityNotFoundException(
          "Could not find folder for program: " + study.getProgram().getName());
    }
    NotebookFolder programFolder = programFolderOptional.get();

    BenchlingFolder benchlingFolder = client
        .createFolder(namingService.getStudyNotebookFolderName(study),
            programFolder.getReferenceId());
    NotebookFolder studyFolder = this.convertFolder(benchlingFolder);
    studyFolder.setParentFolder(programFolder);
    return studyFolder;

  }


  @Override
  public NotebookFolder createAssayFolder(Assay assay) throws NotebookException {
    LOGGER.info("Creating Benchling folder for assay: " + assay.getCode());

    Optional<NotebookFolder> studyFolderOptional = this.findStudyFolder(assay.getStudy());
    if (!studyFolderOptional.isPresent()) {
      throw new EntityNotFoundException(
          "Could not find folder for study: " + assay.getStudy().getCode());
    }
    NotebookFolder studyFolder = studyFolderOptional.get();

    BenchlingFolder benchlingFolder = client
        .createFolder(namingService.getAssayNotebookFolderName(assay),
            studyFolder.getReferenceId());
    NotebookFolder assayFolder = this.convertFolder(benchlingFolder);
    assayFolder.setParentFolder(studyFolder);

    return assayFolder;

  }
}
