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

package io.studytracker.benchling.api;

import io.studytracker.benchling.api.entities.BenchlingEntry;
import io.studytracker.benchling.api.entities.BenchlingEntryList;
import io.studytracker.benchling.api.entities.BenchlingFolder;
import io.studytracker.benchling.api.entities.BenchlingFolderList;
import io.studytracker.benchling.api.entities.BenchlingProject;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

public final class BenchlingNotebookFolderService 
    extends AbstractBenchlingApiService
    implements NotebookFolderService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BenchlingNotebookFolderService.class);

  @Autowired 
  private NamingService namingService;

  @Autowired 
  private ELNFolderRepository elnFolderRepository;

  /**
   * Generate the web this.getClient() URL for the folder with the given ID.
   *
   * @param folder
   * @return
   */
  private String createFolderUrl(BenchlingFolder folder) {
    return this.getRootFolderUrl()
        + "/"
        + folder.getId().replace("lib_", "")
        + "-"
        + folder
            .getName()
            .toLowerCase()
            .replaceAll(" ", "-")
            .replaceAll("[^A-Za-z0-9-_\\s()]+", "")
            .replaceAll("[\\()]", "")
            .trim();
  }

  /**
   * Converts a {@link BenchlingFolder} object into a {@link NotebookFolder} object.
   *
   * @param benchlingFolder
   * @return
   */
  private NotebookFolder convertBenchlingFolder(BenchlingFolder benchlingFolder) {
    NotebookFolder notebookFolder = new NotebookFolder();
    notebookFolder.setName(benchlingFolder.getName());
    notebookFolder.setUrl(this.createFolderUrl(benchlingFolder));
    notebookFolder.setReferenceId(benchlingFolder.getId());
    notebookFolder.getAttributes().put("projectId", benchlingFolder.getProjectId());
    return notebookFolder;
  }

  /**
   * Converts a {@link BenchlingFolder} to a {@link NotebookFolder} via {@link
   * #convertBenchlingFolder(BenchlingFolder)}, without loading the contents of linked entries.
   *
   * @param benchlingFolder
   * @return
   */
  private NotebookFolder convertFolder(BenchlingFolder benchlingFolder, String authHeader) {
    return convertFolder(benchlingFolder, null, authHeader);
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
  private NotebookFolder convertFolder(
      BenchlingFolder benchlingFolder, List<BenchlingEntry> entries, String authHeader) {
    NotebookFolder notebookFolder = convertBenchlingFolder(benchlingFolder);
    if (entries != null) {
      loadContents(benchlingFolder, notebookFolder, entries, authHeader);
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
  private void loadContents(
      BenchlingFolder benchlingFolder,
      NotebookFolder notebookFolder,
      List<BenchlingEntry> entries,
      String authHeader) {
    entries.stream()
        .filter(entry -> entry.getFolderId().equals(benchlingFolder.getId()))
        .forEach(entry -> notebookFolder.getEntries().add(convertBenchlingEntry(entry)));
    List<BenchlingFolder> childrenFolders = new ArrayList<>();
    boolean hasNext = true;
    String nextToken = null;
    while (hasNext) {
      BenchlingFolderList folderList =
          this.getClient().findFolderChildren(benchlingFolder.getId(), authHeader, nextToken);
      childrenFolders.addAll(folderList.getFolders());
      nextToken = folderList.getNextToken();
      hasNext = StringUtils.hasText(nextToken);
    }
    childrenFolders.forEach(
        folder -> notebookFolder.getSubFolders().add(convertFolder(folder, entries, authHeader)));
  }

  /**
   * Gets the folder path of the provided project's {@link NotebookFolder}.
   *
   * @param folder
   * @return
   */
  private String getProjectPath(NotebookFolder folder, String authHeader) {
    return this.getClient()
        .findFolderById(folder.getReferenceId(), authHeader)
        .flatMap(
            benchlingFolder -> this.getClient().findProjectById(benchlingFolder.getProjectId(), authHeader))
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
  private String getNotebookFolderPath(Study study, String authHeader) {
    StringBuilder path = new StringBuilder("/");
    NotebookFolder studyFolder = NotebookFolder.from(study.getNotebookFolder());
    path.append(getProjectPath(studyFolder, authHeader)).append(study.getName());
    return path.toString();
  }

  /**
   * Returns the folder path of the provided {@link Assay}.
   *
   * @param assay
   * @return
   */
  private String getNotebookFolderPath(Assay assay, String authHeader) {
    StringBuilder path = new StringBuilder("/");
    NotebookFolder assayFolder = NotebookFolder.from(assay.getNotebookFolder());
    path.append(getProjectPath(assayFolder, authHeader));
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
  private NotebookFolder getContentFullNotebookFolder(
      BenchlingFolder benchlingFolder, Assay assay, String authHeader) {

    // Get notebook entries
    List<BenchlingEntry> entries = new ArrayList<>();
    String nextToken = null;
    boolean hasNext = true;
    while (hasNext) {
      BenchlingEntryList entryList =
          this.getClient().findProjectEntries(benchlingFolder.getProjectId(), authHeader, nextToken);
      entries.addAll(entryList.getEntries());
      nextToken = entryList.getNextToken();
      hasNext = StringUtils.hasText(nextToken);
    }

    // Convert the folder object
    NotebookFolder notebookFolder = convertFolder(benchlingFolder, entries, authHeader);
    String path = getNotebookFolderPath(assay, authHeader);
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
  private NotebookFolder getContentFullNotebookFolder(
      BenchlingFolder benchlingFolder, Study study, String authHeader) {

    List<BenchlingEntry> entries = new ArrayList<>();
    String nextToken = null;
    boolean hasNext = true;
    while (hasNext) {
      BenchlingEntryList entryList =
          this.getClient().findProjectEntries(benchlingFolder.getProjectId(), authHeader, nextToken);
      entries.addAll(entryList.getEntries());
      nextToken = entryList.getNextToken();
      hasNext = StringUtils.hasText(nextToken);
    }

    NotebookFolder notebookFolder = convertFolder(benchlingFolder, entries, authHeader);
    String path = getNotebookFolderPath(study, authHeader);
    notebookFolder.setPath(path);

    return notebookFolder;
  }

  @Override
  public Optional<NotebookFolder> findProgramFolder(Program program) {

    LOGGER.info("Fetching benchling notebook entry for program: " + program.getName());
    Optional<ELNFolder> elnFolderOptional = elnFolderRepository.findByProgramId(program.getId());

    if (elnFolderOptional.isPresent()) {
      String authHeader = generateAuthorizationHeader();
      Optional<BenchlingFolder> optional =
          this.getClient().findFolderById(elnFolderOptional.get().getReferenceId(), authHeader);
      return Optional.of(convertFolder(optional.get(), authHeader));
    } else {
      LOGGER.warn(
          String.format("Program %s does not have a notebook folder set.", program.getName()));
      return Optional.empty();
    }
  }

  @Override
  public Optional<NotebookFolder> findStudyFolder(Study study) {
    return findStudyFolder(study, true);
  }

  private Optional<NotebookFolder> findStudyFolder(Study study, boolean includeContents) {

    LOGGER.info("Fetching notebook entry for study: " + study.getCode());
    Optional<ELNFolder> elnFolderOptional = elnFolderRepository.findByStudyId(study.getId());

    // Does the study have the folder object set?
    if (elnFolderOptional.isPresent()) {
      String authHeader = generateAuthorizationHeader();
      NotebookFolder studyFolder = NotebookFolder.from(elnFolderOptional.get());
      Optional<BenchlingFolder> optional =
          this.getClient().findFolderById(studyFolder.getReferenceId(), authHeader);
      return optional.flatMap(
          folder -> {
            if (includeContents) {
              return Optional.of(getContentFullNotebookFolder(folder, study, authHeader));
            } else {
              return Optional.of(this.convertFolder(folder, authHeader));
            }
          });
    } else {
      LOGGER.warn(String.format("Study %s does not have a notebook folder set.", study.getName()));
      return Optional.empty();
    }
  }

  @Override
  public Optional<NotebookFolder> findAssayFolder(Assay assay) {

    LOGGER.info("Fetching notebook entry for assay: " + assay.getCode());
    Optional<ELNFolder> elnFolderOptional = elnFolderRepository.findByAssayId(assay.getId());

    if (elnFolderOptional.isPresent()) {
      String authHeader = generateAuthorizationHeader();
      NotebookFolder assayFolder = NotebookFolder.from(elnFolderOptional.get());
      Optional<BenchlingFolder> optional =
          this.getClient().findFolderById(assayFolder.getReferenceId(), authHeader);
      return optional.flatMap(
          folder -> Optional.of(getContentFullNotebookFolder(folder, assay, authHeader)));
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
    if (program.getNotebookFolder() != null
        && program.getNotebookFolder().getReferenceId() != null) {
      String authHeader = generateAuthorizationHeader();
      try {
        BenchlingFolder folder =
            this.getClient().findFolderById(program.getNotebookFolder().getReferenceId(), authHeader).get();
        return this.convertFolder(folder, authHeader);
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
    if (!programFolderOptional.isPresent()) {
      throw new EntityNotFoundException(
          "Could not find folder for program: " + study.getProgram().getName());
    }
    NotebookFolder programFolder = programFolderOptional.get();

    String authHeader = generateAuthorizationHeader();
    BenchlingFolder benchlingFolder =
        this.getClient().createFolder(
            namingService.getStudyNotebookFolderName(study),
            programFolder.getReferenceId(),
            authHeader);
    NotebookFolder studyFolder = this.convertFolder(benchlingFolder, authHeader);
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
    NotebookFolder studyFolder = studyFolderOptional.get();

    String authHeader = generateAuthorizationHeader();
    BenchlingFolder benchlingFolder =
        this.getClient().createFolder(
            namingService.getAssayNotebookFolderName(assay),
            studyFolder.getReferenceId(),
            authHeader);
    NotebookFolder assayFolder = this.convertFolder(benchlingFolder, authHeader);
    assayFolder.setParentFolder(studyFolder);

    return assayFolder;
  }

}
