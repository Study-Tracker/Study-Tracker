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

package io.studytracker.benchling.api;

import io.studytracker.benchling.api.entities.*;
import io.studytracker.benchling.exception.EntityNotFoundException;
import io.studytracker.eln.NotebookFolder;
import io.studytracker.eln.NotebookFolderService;
import io.studytracker.exception.MalformedEntityException;
import io.studytracker.exception.NotebookException;
import io.studytracker.model.Assay;
import io.studytracker.model.ELNFolder;
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import io.studytracker.repository.ELNFolderRepository;
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
    implements NotebookFolderService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BenchlingNotebookFolderService.class);

  @Autowired 
  private NamingService namingService;

  @Autowired 
  private ELNFolderRepository elnFolderRepository;

  /**
   * Converts a {@link BenchlingFolder} to a {@link NotebookFolder} via {@link
   * #convertBenchlingFolder(BenchlingFolder)}, without loading the contents of linked entries.
   *
   * @param benchlingFolder
   * @return
   */
  private NotebookFolder convertFolder(BenchlingFolder benchlingFolder) {
    return convertFolder(benchlingFolder, null);
  }

  /**
   * Converts a {@link BenchlingFolder} to a {@link NotebookFolder} via {@link
   * #convertBenchlingFolder(BenchlingFolder)}, loading the contents of linked {@link
   * BenchlingEntry} entries.
   *
   * @param benchlingFolder
   * @param entries
   * @return
   */
  private NotebookFolder convertFolder(BenchlingFolder benchlingFolder, List<BenchlingEntry> entries) {
    NotebookFolder notebookFolder = this.convertBenchlingFolder(benchlingFolder);
    if (entries != null) {
      loadContents(benchlingFolder, notebookFolder, entries);
    }
    return notebookFolder;
  }

  /**
   * Loads the contents of {@link BenchlingFolder} folders and appends them to the referenced {@link
   * NotebookFolder} object.
   *
   * @param benchlingFolder
   * @param notebookFolder
   * @param entries
   */
  private void loadContents(BenchlingFolder benchlingFolder, NotebookFolder notebookFolder,
          List<BenchlingEntry> entries) {
    entries.stream()
        .filter(entry -> entry.getFolderId().equals(benchlingFolder.getId()))
        .forEach(entry -> notebookFolder.getEntries().add(convertBenchlingEntry(entry)));
    List<BenchlingFolder> childrenFolders = new ArrayList<>();
    boolean hasNext = true;
    String nextToken = null;
    while (hasNext) {
      BenchlingFolderList folderList =
          this.getClient().findFolderChildren(benchlingFolder.getId(), nextToken);
      childrenFolders.addAll(folderList.getFolders());
      nextToken = folderList.getNextToken();
      hasNext = StringUtils.hasText(nextToken);
    }
    childrenFolders.forEach(
        folder -> notebookFolder.getSubFolders().add(convertFolder(folder, entries)));
  }

  /**
   * Gets the folder path of the provided project's {@link NotebookFolder}.
   *
   * @param folder
   * @return
   */
  private String getProjectPath(NotebookFolder folder) {
    return this.getClient()
        .findFolderById(folder.getReferenceId())
        .flatMap(
            benchlingFolder -> this.getClient().findProjectById(benchlingFolder.getProjectId()))
        .map(BenchlingProject::getName)
        .map(name -> name + "/")
        .orElse("");
  }

  /**
   * Returns the folder path of the provided {@link Study}.
   *
   * @param study
   * @return
   */
  private String getNotebookFolderPath(Study study) {
    StringBuilder path = new StringBuilder("/");
    NotebookFolder studyFolder = NotebookFolder.from(study.getNotebookFolder());
    path.append(getProjectPath(studyFolder)).append(study.getName());
    return path.toString();
  }

  /**
   * Returns the folder path of the provided {@link Assay}.
   *
   * @param assay
   * @return
   */
  private String getNotebookFolderPath(Assay assay) {
    StringBuilder path = new StringBuilder("/");
    NotebookFolder assayFolder = NotebookFolder.from(assay.getNotebookFolder());
    path.append(getProjectPath(assayFolder));
    Study study = assay.getStudy();
    path.append(study.getName()).append("/").append(assay.getName());
    return path.toString();
  }

  /**
   * Converts a {@link BenchlingFolder} to a {@link NotebookFolder} and fetches it contents for the
   * given {@link Assay}.
   *
   * @param benchlingFolder
   * @param assay
   * @return
   */
  private NotebookFolder getContentFullNotebookFolder(BenchlingFolder benchlingFolder, Assay assay) {

    // Get notebook entries
    List<BenchlingEntry> entries = new ArrayList<>();
    String nextToken = null;
    boolean hasNext = true;
    while (hasNext) {
      BenchlingEntryList entryList =
          this.getClient().findProjectEntries(benchlingFolder.getProjectId(), nextToken);
      entries.addAll(entryList.getEntries());
      nextToken = entryList.getNextToken();
      hasNext = StringUtils.hasText(nextToken);
    }

    // Convert the folder object
    NotebookFolder notebookFolder = convertFolder(benchlingFolder, entries);
    String path = getNotebookFolderPath(assay);
    notebookFolder.setPath(path);

    return notebookFolder;
  }

  /**
   * Converts a {@link BenchlingFolder} to a {@link NotebookFolder} and fetches it contents for the
   * given {@link Study}.
   *
   * @param benchlingFolder
   * @param study
   * @return
   */
  private NotebookFolder getContentFullNotebookFolder(BenchlingFolder benchlingFolder, Study study) {

    List<BenchlingEntry> entries = new ArrayList<>();
    String nextToken = null;
    boolean hasNext = true;
    while (hasNext) {
      BenchlingEntryList entryList =
          this.getClient().findProjectEntries(benchlingFolder.getProjectId(), nextToken);
      entries.addAll(entryList.getEntries());
      nextToken = entryList.getNextToken();
      hasNext = StringUtils.hasText(nextToken);
    }

    NotebookFolder notebookFolder = convertFolder(benchlingFolder, entries);
    String path = getNotebookFolderPath(study);
    notebookFolder.setPath(path);

    return notebookFolder;
  }

  @Override
  public Optional<NotebookFolder> findProgramFolder(Program program) {

    LOGGER.info("Fetching benchling notebook folder for program: " + program.getName());
    Optional<ELNFolder> elnFolderOptional = elnFolderRepository.findByProgramId(program.getId());
    BenchlingElnRestClient client = this.getClient();
    if (elnFolderOptional.isPresent()) {
      Optional<BenchlingFolder> optional = client.findFolderById(elnFolderOptional.get().getReferenceId());
      return Optional.of(convertFolder(optional.get()));
    } else {
      LOGGER.warn(
          String.format("Program %s does not have a notebook folder set.", program.getName()));
      return Optional.empty();
    }
  }

  @Override
  public Optional<NotebookFolder> findStudyFolder(Study study) {
    return findStudyFolder(study, false);
  }

  @Override
  public Optional<NotebookFolder> findStudyFolder(Study study, boolean includeContents) {

    LOGGER.info("Fetching notebook folder for study: " + study.getCode());
    Optional<ELNFolder> elnFolderOptional = elnFolderRepository.findByStudyId(study.getId());
    BenchlingElnRestClient client = this.getClient();

    // Does the study have the folder object set?
    if (elnFolderOptional.isPresent()) {
      NotebookFolder studyFolder = NotebookFolder.from(elnFolderOptional.get());
      Optional<BenchlingFolder> optional = client.findFolderById(studyFolder.getReferenceId());
      return optional.flatMap(
          folder -> {
            if (includeContents) {
              return Optional.of(getContentFullNotebookFolder(folder, study));
            } else {
              return Optional.of(this.convertFolder(folder));
            }
          });
    } else {
      LOGGER.warn(String.format("Study %s does not have a notebook folder set.", study.getName()));
      return Optional.empty();
    }
  }

  @Override
  public Optional<NotebookFolder> findAssayFolder(Assay assay) {
    return findAssayFolder(assay, false);
  }

  @Override
  public Optional<NotebookFolder> findAssayFolder(Assay assay, boolean includeContents) {

    LOGGER.info("Fetching notebook folder for assay: " + assay.getCode());
    Optional<ELNFolder> elnFolderOptional = elnFolderRepository.findByAssayId(assay.getId());
    BenchlingElnRestClient client = this.getClient();

    if (elnFolderOptional.isPresent()) {
      NotebookFolder assayFolder = NotebookFolder.from(elnFolderOptional.get());
      Optional<BenchlingFolder> optional = client.findFolderById(assayFolder.getReferenceId());
      return optional.flatMap(
          folder -> {
            if (includeContents) {
              return Optional.of(getContentFullNotebookFolder(folder, assay));
            } else {
              return Optional.of(this.convertFolder(folder));
            }
          });
    } else {
      LOGGER.warn(String.format("Assay %s does not have a notebook folder set.", assay.getName()));
      return Optional.empty();
    }
  }

  @Override
  public NotebookFolder createProgramFolder(Program program) throws NotebookException {
    LOGGER.info(
        "Registering new program folder. NOTE: Benchling does not support project "
            + "creation, so a valid folderId must be provided when registering new programs.");
    BenchlingElnRestClient client = this.getClient();
    if (program.getNotebookFolder() != null
        && program.getNotebookFolder().getReferenceId() != null) {
      try {
        BenchlingFolder folder = client.findFolderById(program.getNotebookFolder().getReferenceId()).get();
        return this.convertFolder(folder);
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
  public NotebookFolder createStudyFolder(Study study) {
    LOGGER.info("Creating Benchling folder for study: " + study.getCode());

    Optional<NotebookFolder> programFolderOptional = this.findProgramFolder(study.getProgram());
    BenchlingElnRestClient client = this.getClient();
    if (!programFolderOptional.isPresent()) {
      throw new EntityNotFoundException(
          "Could not find folder for program: " + study.getProgram().getName());
    }
    NotebookFolder programFolder = programFolderOptional.get();

    BenchlingFolder benchlingFolder = client.createFolder(
            namingService.getStudyNotebookFolderName(study), programFolder.getReferenceId());
    NotebookFolder studyFolder = this.convertFolder(benchlingFolder);
    studyFolder.setParentFolder(programFolder);
    return studyFolder;
  }

  @Override
  public NotebookFolder createAssayFolder(Assay assay) {
    LOGGER.info("Creating Benchling folder for assay: " + assay.getCode());

    Optional<NotebookFolder> studyFolderOptional = this.findStudyFolder(assay.getStudy(), false);
    if (!studyFolderOptional.isPresent()) {
      throw new EntityNotFoundException(
          "Could not find folder for study: " + assay.getStudy().getCode());
    }
    BenchlingElnRestClient client = this.getClient();
    NotebookFolder studyFolder = studyFolderOptional.get();

    BenchlingFolder benchlingFolder = client.createFolder(
            namingService.getAssayNotebookFolderName(assay), studyFolder.getReferenceId());
    NotebookFolder assayFolder = this.convertFolder(benchlingFolder);
    assayFolder.setParentFolder(studyFolder);

    return assayFolder;
  }

  @Override
  public List<NotebookFolder> listNotebookProjectFolders() {
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
}
