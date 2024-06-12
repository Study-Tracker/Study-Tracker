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

package io.studytracker.benchling;

import io.studytracker.benchling.api.AbstractBenchlingApiService;
import io.studytracker.benchling.api.BenchlingElnRestClient;
import io.studytracker.benchling.api.entities.BenchlingEntryList;
import io.studytracker.benchling.api.entities.BenchlingFolder;
import io.studytracker.benchling.api.entities.BenchlingFolderList;
import io.studytracker.benchling.exception.EntityNotFoundException;
import io.studytracker.eln.NotebookEntry;
import io.studytracker.eln.NotebookFolderService;
import io.studytracker.exception.MalformedEntityException;
import io.studytracker.exception.NotebookException;
import io.studytracker.model.*;
import io.studytracker.repository.AssayNotebookFolderRepository;
import io.studytracker.repository.ELNFolderRepository;
import io.studytracker.repository.ProgramNotebookFolderRepository;
import io.studytracker.repository.StudyNotebookFolderRepository;
import io.studytracker.service.NamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public final class BenchlingNotebookFolderService 
    extends AbstractBenchlingApiService
    implements NotebookFolderService<ELNFolder> {

  private static final Logger LOGGER = LoggerFactory.getLogger(BenchlingNotebookFolderService.class);

  @Autowired 
  private NamingService namingService;

  @Autowired 
  private ELNFolderRepository elnFolderRepository;

  @Autowired
  private ProgramNotebookFolderRepository programNotebookFolderRepository;

  @Autowired
  private StudyNotebookFolderRepository studyNotebookFolderRepository;

  @Autowired
  private AssayNotebookFolderRepository assayNotebookFolderRepository;

  @Override
  public List<ELNFolder> findProgramFolders(Program program) {
    List<ProgramNotebookFolder> folderSet = programNotebookFolderRepository.findByProgramId(program.getId());
    BenchlingElnRestClient client = this.getClient();
    List<ELNFolder> folders = new ArrayList<>();
    for (ProgramNotebookFolder programNotebookFolder : folderSet) {
      Optional<BenchlingFolder> optional = client.findFolderById(programNotebookFolder.getElnFolder().getReferenceId());
      optional.ifPresent(
          benchlingFolder -> folders.add(this.convertBenchlingFolder(benchlingFolder)));
    }
    return folders;
  }

  @Override
  public List<ELNFolder> findStudyFolders(Study study) {
    List<StudyNotebookFolder> folderSet = studyNotebookFolderRepository.findByStudyId(study.getId());
    BenchlingElnRestClient client = this.getClient();
    List<ELNFolder> folders = new ArrayList<>();
    for (StudyNotebookFolder studyNotebookFolder : folderSet) {
      client
          .findFolderById(studyNotebookFolder.getElnFolder().getReferenceId())
          .ifPresent(folder -> folders.add(this.convertBenchlingFolder(folder)));
    }
    return folders;
  }

  @Override
  public List<ELNFolder> findAssayFolders(Assay assay) {
    List<AssayNotebookFolder> folderSet = assayNotebookFolderRepository.findByAssayId(assay.getId());
    BenchlingElnRestClient client = this.getClient();
    List<ELNFolder> folders = new ArrayList<>();
    for (AssayNotebookFolder assayNotebookFolder : folderSet) {
      Optional<BenchlingFolder> optional = client.findFolderById(assayNotebookFolder.getElnFolder().getReferenceId());
      if (optional.isPresent()) {
        folders.add(this.convertBenchlingFolder(optional.get()));
      }
    }
    return folders;
  }

  @Override
  public Optional<ELNFolder> findPrimaryProgramFolder(Program program) {
    LOGGER.info("Fetching benchling notebook folder for program: " + program.getName());
    ELNFolder programNotebookFolder = elnFolderRepository.findPrimaryByProgramId(program.getId())
        .orElse(null);
    if (programNotebookFolder != null) {
      Optional<BenchlingFolder> optional = this.getClient()
          .findFolderById(programNotebookFolder.getReferenceId());
      return optional.map(this::convertBenchlingFolder);
    } else {
      LOGGER.warn(
          String.format("Program %s does not have a notebook folder set.", program.getName()));
      return Optional.empty();
    }
  }

  @Override
  public Optional<ELNFolder> findPrimaryStudyFolder(Study study) {

    LOGGER.info("Fetching notebook folder for study: " + study.getCode());
    ELNFolder studyFolder = elnFolderRepository.findPrimaryByStudyId(study.getId()).orElse(null);
    BenchlingElnRestClient client = this.getClient();

    // Does the study have the folder object set?
    if (studyFolder != null) {
      Optional<BenchlingFolder> optional = client.findFolderById(studyFolder.getReferenceId());
      return optional.flatMap(f -> Optional.of(this.convertBenchlingFolder(f)));
    } else {
      LOGGER.warn(String.format("Study %s does not have a notebook folder set.", study.getName()));
      return Optional.empty();
    }
  }

  @Override
  public Optional<ELNFolder> findPrimaryAssayFolder(Assay assay) {

    LOGGER.info("Fetching notebook folder for assay: " + assay.getCode());
    ELNFolder assayFolder = elnFolderRepository.findPrimaryByAssayId(assay.getId()).orElse(null);
    BenchlingElnRestClient client = this.getClient();

    if (assayFolder != null) {
      Optional<BenchlingFolder> optional = client.findFolderById(assayFolder.getReferenceId());
      return optional.flatMap(f -> Optional.of(this.convertBenchlingFolder(f)));
    } else {
      LOGGER.warn(String.format("Assay %s does not have a notebook folder set.", assay.getName()));
      return Optional.empty();
    }
  }

  @Override
  public ELNFolder createProgramFolder(Program program) throws NotebookException {
    LOGGER.info(
        "Registering new program folder. NOTE: Benchling does not support project "
            + "creation, so a valid folderId must be provided when registering new programs.");
    BenchlingElnRestClient client = this.getClient();
    ProgramOptions options = program.getOptions();
    if (options.getNotebookFolder() != null
        && options.getNotebookFolder().getReferenceId() != null) {
      try {
        BenchlingFolder folder = client.findFolderById(options.getNotebookFolder().getReferenceId()).get();
        return this.convertBenchlingFolder(folder);
      } catch (Exception e) {
        LOGGER.error("Failed to register new program: " + program.getName());
        throw new NotebookException(e);
      }
    } else {
      throw new MalformedEntityException(
          "Program folder ID is not set, cannot create "
              + "NotebookFolder record for program: "
              + program.getName());
    }
  }

  @Override
  public ELNFolder createStudyFolder(Study study) {
    LOGGER.info("Creating Benchling folder for study: " + study.getCode());

    Optional<ELNFolder> programFolderOptional = this.findPrimaryProgramFolder(study.getProgram());
    BenchlingElnRestClient client = this.getClient();
    if (!programFolderOptional.isPresent()) {
      throw new EntityNotFoundException(
          "Could not find folder for program: " + study.getProgram().getName());
    }
    ELNFolder programFolder = programFolderOptional.get();

    BenchlingFolder benchlingFolder = client.createFolder(
            NamingService.getStudyNotebookFolderName(study), programFolder.getReferenceId());
    ELNFolder studyFolder = this.convertBenchlingFolder(benchlingFolder);
    studyFolder.setParentFolder(programFolder);
    return studyFolder;
  }

  @Override
  public ELNFolder createAssayFolder(Assay assay) {
    LOGGER.info("Creating Benchling folder for assay: " + assay.getCode());

    Optional<ELNFolder> studyFolderOptional = this.findPrimaryStudyFolder(assay.getStudy());
    if (!studyFolderOptional.isPresent()) {
      throw new EntityNotFoundException(
          "Could not find folder for study: " + assay.getStudy().getCode());
    }
    BenchlingElnRestClient client = this.getClient();
    ELNFolder studyFolder = studyFolderOptional.get();

    BenchlingFolder benchlingFolder = client.createFolder(
            NamingService.getAssayNotebookFolderName(assay), studyFolder.getReferenceId());
    ELNFolder assayFolder = this.convertBenchlingFolder(benchlingFolder);
    assayFolder.setParentFolder(studyFolder);

    return assayFolder;
  }

  @Override
  public List<ELNFolder> listProjectFolders() {
    LOGGER.debug("Listing all notebook projects");
    BenchlingElnRestClient client = this.getClient();
    List<BenchlingFolder> folders = new ArrayList<>();
    String nextToken = null;
    boolean hasNext = true;
    while (hasNext) {
      BenchlingFolderList folderList = client.findRootFolders(nextToken);
      nextToken = folderList.getNextToken();
      folders.addAll(folderList.getFolders());
      hasNext = StringUtils.hasText(nextToken);
    }
    return folders.stream().map(this::convertBenchlingFolder).collect(Collectors.toList());
  }

  @Override
  public ELNFolder loadFolderContents(ELNFolder folder) {
    LOGGER.debug("Loading contents of notebook folder: " + folder.getReferenceId());
    BenchlingElnRestClient client = this.getClient();

    // Get the notebook entries
    List<NotebookEntry> entries = new ArrayList<>();
    boolean hasNext = true;
    String nextToken = null;
    while (hasNext) {
      BenchlingEntryList entryList =
          client.findEntriesByFolderId(folder.getReferenceId(), nextToken);
      entries.addAll(
          entryList.getEntries().stream()
              .map(e -> convertBenchlingEntry(e))
              .collect(Collectors.toList()));
      nextToken = entryList.getNextToken();
      hasNext = StringUtils.hasText(nextToken);
    }
    folder.setEntries(entries);

    // Get the subfolders
    List<ELNFolder> childrenFolders = new ArrayList<>();
    hasNext = true;
    nextToken = null;
    while (hasNext) {
      BenchlingFolderList folderList =
          client.findFolderChildren(folder.getReferenceId(), nextToken);
      childrenFolders.addAll(
          folderList.getFolders().stream()
              .map(f -> convertBenchlingFolder(f))
              .collect(Collectors.toList()));
      nextToken = folderList.getNextToken();
      hasNext = StringUtils.hasText(nextToken);
    }
    folder.setSubFolders(childrenFolders);

    return folder;
  }
  
  @Override
  public ELNFolder findFolderById(String folderId) {
    LOGGER.debug("Finding notebook folder by ID: " + folderId);
    BenchlingElnRestClient client = this.getClient();
    Optional<BenchlingFolder> optional = client.findFolderById(folderId);
    return optional.map(this::convertBenchlingFolder).orElse(null);
  }

}
