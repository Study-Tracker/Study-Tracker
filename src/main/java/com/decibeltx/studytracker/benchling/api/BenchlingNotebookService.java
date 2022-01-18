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

package com.decibeltx.studytracker.benchling.api;

import com.decibeltx.studytracker.benchling.api.entities.BenchlingAuthenticationToken;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingEntry;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingEntryList;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingEntryRequest;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingEntryRequest.CustomField;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingEntryTemplate;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingEntryTemplateList;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingFolder;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingFolderList;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingProject;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingUser;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingUserList;
import com.decibeltx.studytracker.benchling.exception.EntityNotFoundException;
import com.decibeltx.studytracker.eln.NotebookEntry;
import com.decibeltx.studytracker.eln.NotebookFolder;
import com.decibeltx.studytracker.eln.NotebookTemplate;
import com.decibeltx.studytracker.eln.NotebookUser;
import com.decibeltx.studytracker.eln.StudyNotebookService;
import com.decibeltx.studytracker.exception.MalformedEntityException;
import com.decibeltx.studytracker.exception.NotebookException;
import com.decibeltx.studytracker.model.Assay;
import com.decibeltx.studytracker.model.ELNFolder;
import com.decibeltx.studytracker.model.Program;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.repository.ELNFolderRepository;
import com.decibeltx.studytracker.service.NamingService;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.apache.commons.ssl.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

public final class BenchlingNotebookService implements StudyNotebookService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BenchlingNotebookService.class);

  @Autowired
  private BenchlingElnRestClient client;

  @Autowired
  private NamingService namingService;

  @Autowired
  private ELNFolderRepository elnFolderRepository;

  @Value("${benchling.tenant-name}")
  private String tenantName;

  @Value("${benchling.api.username:}")
  private String username;

  @Value("${benchling.api.password:}")
  private String password;

  @Value("${benchling.api.client-id:}")
  private String clientId;

  @Value("${benchling.api.client-secret:}")
  private String clientSecret;

  @Value("${benchling.api.token:}")
  private String token;

  private URL rootUrl;

  private URL rootFolderUrl;

  @PostConstruct
  public void init() throws MalformedURLException {
    rootUrl = new URL("https://" + tenantName + ".benchling.com");
    rootFolderUrl = new URL(rootUrl, "/" + tenantName + "/f_");
  }

  /**
   * Generates an Authorization header to be used in REST API requests. The header will acquired
   *  based on the provided configuration. If an application client is provided, a Bearer token
   *  will be generated. Otherwise, a HTTP Basic auth header will be used.
   *
   * @return token
   */
  private String generateAuthorizationHeader() {
    if (StringUtils.hasText(token)) {
      return "Basic " + token;
    } else if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
      String auth = username + ":" + password;
      byte[] bytes = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));
      return "Basic " + new String(bytes);
    } else {
      BenchlingAuthenticationToken benchlingAuthenticationToken
          = client.acquireApplicationAuthenticationToken(clientId, clientSecret);
      String token = benchlingAuthenticationToken.getAccessToken();
      return "Bearer " + token;
    }
  }

  /**
   * Generate the web client URL for the folder with the given ID.
   *
   * @param folder
   * @return
   */
  private String createFolderUrl(BenchlingFolder folder) {
    return rootFolderUrl + "/" + folder.getId().replace("lib_", "") + "-"
        + folder.getName().toLowerCase()
        .replaceAll(" ", "-")
        .replaceAll("[^A-Za-z0-9-_\\s()]+", "")
        .replaceAll("[\\()]", "")
        .trim();
  }

  /**
   * Converts a {@link BenchlingEntry} object into a {@link NotebookEntry} object.
   *
   * @param benchlingEntry
   * @return
   */
  private NotebookEntry convertBenchlingEntry(BenchlingEntry benchlingEntry) {
    NotebookEntry notebookEntry = new NotebookEntry();
    notebookEntry.setName(benchlingEntry.getName());
    notebookEntry.setReferenceId(benchlingEntry.getId());
    notebookEntry.setUrl(benchlingEntry.getWebURL());
    notebookEntry.getAttributes().put("folderId", benchlingEntry.getFolderId());
    return notebookEntry;
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
   * Converts a {@link BenchlingFolder} to a {@link NotebookFolder} via
   *  {@link #convertBenchlingFolder(BenchlingFolder)}, without loading the contents of linked
   *  entries.
   *
   * @param benchlingFolder
   * @return
   */
  private NotebookFolder convertFolder(BenchlingFolder benchlingFolder, String authHeader) {
    return convertFolder(benchlingFolder, null, authHeader);
  }

  /**
   * Converts a {@link BenchlingFolder} to a {@link NotebookFolder} via
   *  {@link #convertBenchlingFolder(BenchlingFolder)}, loading the contents of linked
   *  {@link BenchlingEntry} entries.
   *
   * @param benchlingFolder
   * @param entries
   * @return
   */
  private NotebookFolder convertFolder(BenchlingFolder benchlingFolder,
      List<BenchlingEntry> entries, String authHeader) {
    NotebookFolder notebookFolder = convertBenchlingFolder(benchlingFolder);
    if (entries != null) {
      loadContents(benchlingFolder, notebookFolder, entries, authHeader);
    }
    return notebookFolder;
  }

  private NotebookTemplate convertNotebookEntryTemplate(BenchlingEntryTemplate benchlingTemplate) {
    NotebookTemplate template = new NotebookTemplate();
    template.setName(benchlingTemplate.getName());
    template.setReferenceId(benchlingTemplate.getId());
    return template;
  }

  private List<NotebookTemplate> convertNotebookEntryTemplates(List<BenchlingEntryTemplate> benchlingEntryTemplates) {
    List<NotebookTemplate> templates = new ArrayList<>();
    for (BenchlingEntryTemplate benchlingEntryTemplate: benchlingEntryTemplates) {
      templates.add(this.convertNotebookEntryTemplate(benchlingEntryTemplate));
    }
    return templates;
  }

  private NotebookUser convertUser(BenchlingUser benchlingUser) {
    NotebookUser notebookUser = new NotebookUser();
    notebookUser.setName(benchlingUser.getName());
    notebookUser.setUsername(benchlingUser.getHandle());
    notebookUser.setEmail(benchlingUser.getEmail());
    notebookUser.setReferenceId(benchlingUser.getId());
    return notebookUser;
  }

  private List<NotebookUser> convertUsers(List<BenchlingUser> benchlingUsers) {
    List<NotebookUser> notebookUsers = new ArrayList<>();
    for (BenchlingUser benchlingUser: benchlingUsers) {
      notebookUsers.add(this.convertUser(benchlingUser));
    }
    return notebookUsers;
  }

  /**
   * Loads the contents of {@link BenchlingFolder} folders and appends them to the referenced
   *  {@link NotebookFolder} object.
   *
   * @param benchlingFolder
   * @param notebookFolder
   * @param entries
   */
  private void loadContents(BenchlingFolder benchlingFolder, NotebookFolder notebookFolder,
      List<BenchlingEntry> entries, String authHeader) {
    entries.stream()
            .filter(entry -> entry.getFolderId().equals(benchlingFolder.getId()))
            .forEach(entry -> notebookFolder.getEntries().add(convertBenchlingEntry(entry)));
    List<BenchlingFolder> childrenFolders = new ArrayList<>();
    boolean hasNext = true;
    String nextToken = null;
    while (hasNext) {
      BenchlingFolderList folderList = client.findFolderChildren(benchlingFolder.getId(),
          authHeader, nextToken);
      childrenFolders.addAll(folderList.getFolders());
      nextToken = folderList.getNextToken();
      hasNext = StringUtils.hasText(nextToken);
    }
    childrenFolders.forEach(folder ->
        notebookFolder.getSubFolders().add(convertFolder(folder, entries, authHeader)));
  }

  /**
   * Gets the folder path of the provided project's {@link NotebookFolder}.
   *
   * @param folder
   * @return
   */
  private String getProjectPath(NotebookFolder folder, String authHeader) {
    return client.findFolderById(folder.getReferenceId(), authHeader)
            .flatMap(benchlingFolder ->
                client.findProjectById(benchlingFolder.getProjectId(), authHeader))
            .map(BenchlingProject::getName).map(name -> name + "/").orElse("");
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
    path.append(getProjectPath(studyFolder, authHeader))
        .append(study.getName());
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
    path.append(study.getName())
        .append("/")
        .append(assay.getName());
    return path.toString();
  }

  /**
   * Converts a {@link BenchlingFolder} to a {@link NotebookFolder} and fetches it contents for the
   *  given {@link Assay}.
   *
   * @param benchlingFolder
   * @param assay
   * @return
   */
  private NotebookFolder getContentFullNotebookFolder(BenchlingFolder benchlingFolder, Assay assay,
      String authHeader) {

    // Get notebook entries
    List<BenchlingEntry> entries = new ArrayList<>();
    String nextToken = null;
    boolean hasNext = true;
    while (hasNext) {
      BenchlingEntryList entryList = client.findProjectEntries(benchlingFolder.getProjectId(),
          authHeader, nextToken);
      entries.addAll(entryList.getEntries());
      nextToken = entryList.getNextToken();
      hasNext = StringUtils.hasText(nextToken);
    }

    // Convert the folder object
    NotebookFolder notebookFolder = convertFolder(
        benchlingFolder,
        entries,
        authHeader
    );
    String path = getNotebookFolderPath(assay, authHeader);
    notebookFolder.setPath(path);

    return notebookFolder;
  }

  /**
   * Converts a {@link BenchlingFolder} to a {@link NotebookFolder} and fetches it contents for the
   *  given {@link Study}.
   *
   * @param benchlingFolder
   * @param study
   * @return
   */
  private NotebookFolder getContentFullNotebookFolder(BenchlingFolder benchlingFolder, Study study,
      String authHeader) {

    List<BenchlingEntry> entries = new ArrayList<>();
    String nextToken = null;
    boolean hasNext = true;
    while (hasNext) {
      BenchlingEntryList entryList = client.findProjectEntries(benchlingFolder.getProjectId(),
          authHeader, nextToken);
      entries.addAll(entryList.getEntries());
      nextToken = entryList.getNextToken();
      hasNext = StringUtils.hasText(nextToken);
    }

    NotebookFolder notebookFolder = convertFolder(
        benchlingFolder,
        entries,
        authHeader
    );
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
          client.findFolderById(elnFolderOptional.get().getReferenceId(), authHeader);
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
          client.findFolderById(studyFolder.getReferenceId(), authHeader);
      return optional.flatMap(folder -> {
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
          client.findFolderById(assayFolder.getReferenceId(), authHeader);
      return optional.flatMap(folder -> Optional.of(getContentFullNotebookFolder(folder, assay, authHeader)));
    } else {
      LOGGER.warn(String.format("Assay %s does not have a notebook folder set.", assay.getName()));
      return Optional.empty();
    }

  }

  @Override
  public NotebookFolder createProgramFolder(Program program) throws NotebookException {
    LOGGER.info("Registering new program folder. NOTE: Benchling does not support project "
        + "creation, so a valid folderId must be provided when registering new programs.");
    if (program.getNotebookFolder() != null
        && program.getNotebookFolder().getReferenceId() != null) {
      String authHeader = generateAuthorizationHeader();
      try {
        BenchlingFolder folder = client.findFolderById(
            program.getNotebookFolder().getReferenceId(),
            authHeader
        ).get();
        return this.convertFolder(folder, authHeader);
      } catch (Exception e) {
        LOGGER.error("Failed to register new program: " + program.getName());
        throw new NotebookException(e);
      }
    } else {
      throw new MalformedEntityException("Program folder ID is not set, cannot create "
          + "NotebookFolder record for program: " + program.getName());
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
    BenchlingFolder benchlingFolder = client.createFolder(
        namingService.getStudyNotebookFolderName(study),
        programFolder.getReferenceId(),
        authHeader
    );
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
    BenchlingFolder benchlingFolder = client.createFolder(
        namingService.getAssayNotebookFolderName(assay),
        studyFolder.getReferenceId(),
        authHeader
    );
    NotebookFolder assayFolder = this.convertFolder(benchlingFolder, authHeader);
    assayFolder.setParentFolder(studyFolder);

    return assayFolder;

  }

  @Override
  public List<NotebookTemplate> findEntryTemplates() {
    LOGGER.info("Fetching Benchling notebook entry templates.");
    String authHeader = generateAuthorizationHeader();
    List<BenchlingEntryTemplate> templates = new ArrayList<>();
    String nextToken = null;
    boolean hasNext = true;
    while (hasNext) {
      BenchlingEntryTemplateList templateList = client.findEntryTemplates(authHeader, nextToken);
      templates.addAll(templateList.getEntryTemplates());
      nextToken = templateList.getNextToken();
      hasNext = StringUtils.hasText(nextToken);
    }
    return this.convertNotebookEntryTemplates(templates);
  }

  @Override
  public Optional<NotebookTemplate> findEntryTemplateById(String id) {
    LOGGER.info("Fetching Benchling notebook entry template: " + id);
    String authHeader = generateAuthorizationHeader();
    BenchlingEntryTemplate template = client.findEntryTemplateById(id, authHeader);
    return Optional.of(this.convertNotebookEntryTemplate(template));
  }

  @Override
  public NotebookEntry createStudyNotebookEntry(Study study) throws NotebookException {
    return this.createStudyNotebookEntry(study, null);
  }

  @Override
  public NotebookEntry createStudyNotebookEntry(Study study, NotebookTemplate template)
      throws NotebookException {

    BenchlingEntryRequest request = new BenchlingEntryRequest();
    request.setName(study.getCode() + " Study Summary: " + study.getName());
    request.setFolderId(study.getNotebookFolder().getReferenceId());

    // Users
    List<String> userIds = new ArrayList<>();
    for (User user: study.getUsers()) {
      Optional<NotebookUser> userOptional = this.findNotebookUser(user);
      if (userOptional.isPresent()) {
        userIds.add(userOptional.get().getReferenceId());
      } else {
        LOGGER.warn("Could not find user registered in Benchling: " + user.toString());
      }
    }
    request.setAuthorIds(userIds);

    // Entry template
    if (template != null) {
      request.setEntryTemplateId(template.getReferenceId());
    }

    // Custom fields
    Map<String, CustomField> customFields = new LinkedHashMap<>();
    customFields.put("Name", new CustomField(study.getName()));
    customFields.put("Code", new CustomField(study.getCode()));
    customFields.put("Description",
        new CustomField(study.getDescription().replaceAll("<.+?>", "")));
    customFields.put("Program", new CustomField(study.getProgram().getName()));
    if (study.getExternalCode() != null) {
      customFields.put("External Code", new CustomField(study.getExternalCode()));
    }
    request.setCustomFields(customFields);

    String authHeader = generateAuthorizationHeader();
    return this.convertBenchlingEntry(client.createEntry(request, authHeader));

  }

  @Override
  public NotebookEntry createAssayNotebookEntry(Assay assay) throws NotebookException {
    return this.createAssayNotebookEntry(assay, null);
  }

  @Override
  public NotebookEntry createAssayNotebookEntry(Assay assay, NotebookTemplate template)
      throws NotebookException {

    BenchlingEntryRequest request = new BenchlingEntryRequest();
    request.setName(assay.getCode() + " Assay Summary: " + assay.getName());
    request.setFolderId(assay.getNotebookFolder().getReferenceId());

    // Users
    List<String> userIds = new ArrayList<>();
    for (User user: assay.getUsers()) {
      Optional<NotebookUser> userOptional = this.findNotebookUser(user);
      if (userOptional.isPresent()) {
        userIds.add(userOptional.get().getReferenceId());
      } else {
        LOGGER.warn("Could not find user registered in Benchling: " + user.toString());
      }
    }
    request.setAuthorIds(userIds);

    // Entry template
    if (template != null) {
      request.setEntryTemplateId(template.getReferenceId());
    }

    // Custom fields
    Map<String, CustomField> customFields = new LinkedHashMap<>();
    customFields.put("Name", new CustomField(assay.getName()));
    customFields.put("Code", new CustomField(assay.getCode()));
    customFields.put("Description",
        new CustomField(assay.getDescription().replaceAll("<.+?>", "")));
    customFields.put("Assay Type", new CustomField(assay.getAssayType().getName()));
    customFields.put("Study", new CustomField(assay.getStudy().getCode()));
    request.setCustomFields(customFields);

    String authHeader = generateAuthorizationHeader();
    return this.convertBenchlingEntry(client.createEntry(request, authHeader));

  }

  @Override
  public List<NotebookUser> findNotebookUsers() {
    LOGGER.info("Fetching Benchling user list.");
    String authHeader = generateAuthorizationHeader();
    List<BenchlingUser> users = new ArrayList<>();
    String nextToken = null;
    boolean hasNext = true;
    while (hasNext) {
      BenchlingUserList userList = client.findUsers(authHeader, nextToken);
      users.addAll(userList.getUsers());
      nextToken = userList.getNextToken();
      hasNext = StringUtils.hasText(nextToken);
    }
    return this.convertUsers(users);
  }

  @Override
  public Optional<NotebookUser> findNotebookUser(User user) {
    LOGGER.info("Looking up Benchling user: " + user.getDisplayName());
    String authHeader = generateAuthorizationHeader();

    // Look up user by username first
    List<BenchlingUser> users = new ArrayList<>();
    String nextToken = null;
    boolean hasNext = true;
    while (hasNext) {
      BenchlingUserList userList = client.findUsersByUsername(user.getUsername(), authHeader, nextToken);
      users.addAll(userList.getUsers());
      nextToken = userList.getNextToken();
      hasNext = StringUtils.hasText(nextToken);
    }
    if (!users.isEmpty()) {
      for (BenchlingUser benchlingUser: users) {
        if (benchlingUser.getEmail().equals(user.getEmail())) {
          return Optional.of(this.convertUser(benchlingUser));
        }
      }
    }

    // Otherwise, check all the users
    else {
      nextToken = null;
      hasNext = true;
      while (hasNext) {
        BenchlingUserList userList = client.findUsers(authHeader, nextToken);
        users.addAll(userList.getUsers());
        nextToken = userList.getNextToken();
        hasNext = StringUtils.hasText(nextToken);
      }
      if (!users.isEmpty()) {
        for (BenchlingUser benchlingUser: users) {
          if (benchlingUser.getEmail().equals(user.getEmail())) {
            return Optional.of(this.convertUser(benchlingUser));
          }
        }
      }
    }

    return Optional.empty();

  }
}
