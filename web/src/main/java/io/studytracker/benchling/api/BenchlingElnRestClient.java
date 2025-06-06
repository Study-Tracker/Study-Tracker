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

import io.studytracker.benchling.api.entities.BenchlingAuthenticationToken;
import io.studytracker.benchling.api.entities.BenchlingEntry;
import io.studytracker.benchling.api.entities.BenchlingEntryList;
import io.studytracker.benchling.api.entities.BenchlingEntryRequest;
import io.studytracker.benchling.api.entities.BenchlingEntrySchema;
import io.studytracker.benchling.api.entities.BenchlingEntrySchemaList;
import io.studytracker.benchling.api.entities.BenchlingEntryTemplate;
import io.studytracker.benchling.api.entities.BenchlingEntryTemplateList;
import io.studytracker.benchling.api.entities.BenchlingFolder;
import io.studytracker.benchling.api.entities.BenchlingFolderList;
import io.studytracker.benchling.api.entities.BenchlingProject;
import io.studytracker.benchling.api.entities.BenchlingProjectList;
import io.studytracker.benchling.api.entities.BenchlingUser;
import io.studytracker.benchling.api.entities.BenchlingUserList;
import io.studytracker.benchling.exception.BenchlingAuthenticationException;
import io.studytracker.benchling.exception.BenchlingException;
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.model.BenchlingIntegration;
import jakarta.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public final class BenchlingElnRestClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(BenchlingElnRestClient.class);

  private final RestTemplate restTemplate;
  private final URL rootUrl;
  private final BenchlingAuthenticationToken token;
  private final URL rootFolderUrl;
  
  public BenchlingElnRestClient(RestTemplate restTemplate, BenchlingIntegration integration) {
    this.restTemplate = restTemplate;
    try {
      this.rootUrl = new URL(integration.getRootUrl());
      this.rootFolderUrl = new URL(rootUrl, "/" + integration.getTenantName() + "/f_");
    } catch (MalformedURLException ex) {
      throw new StudyTrackerException(ex);
    }
    this.token = acquireApplicationAuthenticationToken(integration.getClientId(), integration.getClientSecret());
  }
  
  private String getAuthHeader() {
    return "Bearer " + token.getAccessToken();
  }

  private HttpHeaders getHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", getAuthHeader());
    headers.set("Accept", "application/json");
    headers.set("Cache-Control", "no-cache");
    return headers;
  }
  
  /**
   * Determines a folder URL based on the provided {@link BenchlingFolder} object.
   *
   * @param folder
   * @return
   */
  private String createFolderUrl(BenchlingFolder folder) {
    return rootFolderUrl
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
   * Generates an authentication token using client application authentication in the Benchling API.
   *
   * @param clientId the app client ID
   * @param clientSecret the app client secret
   * @return {@link BenchlingAuthenticationToken}
   */
  public BenchlingAuthenticationToken acquireApplicationAuthenticationToken(
      @NotNull String clientId, @NotNull String clientSecret) {
    LOGGER.debug("Acquiring application authentication token for client ID: {}", clientId);
    URL url = joinUrls(rootUrl, "api/v2/token");
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/x-www-form-urlencoded");
    MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
    data.add("client_id", clientId);
    data.add("client_secret", clientSecret);
    data.add("grant_type", "client_credentials");
    HttpEntity<?> request = new HttpEntity<>(data, headers);
    ResponseEntity<BenchlingAuthenticationToken> response =
        restTemplate.exchange(
            url.toString(), HttpMethod.POST, request, BenchlingAuthenticationToken.class);
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return response.getBody();
    } else {
      throw new BenchlingAuthenticationException("Failed to authenticate with Benchling API");
    }
  }

  /**
   * Returns a list of all projects as {@link BenchlingProject} registered in the tenant.
   *
   * @param nextToken
   * @return {@link BenchlingProjectList}
   */
  public BenchlingProjectList findProjects(String nextToken) {
    LOGGER.debug("Finding Benchling projects");
    String url = resolveUrl("/api/v2/projects", Collections.singletonMap("nextToken", nextToken));
    HttpHeaders headers = getHeaders();
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingProjectList> response =
        restTemplate.exchange(url, HttpMethod.GET, request, BenchlingProjectList.class);
    if (!response.getStatusCode().equals(HttpStatus.OK)) {
      throw new StudyTrackerException("Failed to fetch project list.");
    }
    return response.getBody();
  }

  /**
   * Returns project details, identified by a PKID. Project IDs typically have a {@code src_}
   * prefix.
   *
   * @param id Project ID
   * @return {@link BenchlingProject}
   */
  public Optional<BenchlingProject> findProjectById(@NotNull String id) {
    LOGGER.debug("Finding Benchling project by ID: {}", id);
    String url = resolveUrl("/api/v2/projects/" + id);
    HttpHeaders headers = getHeaders();
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingProject> response =
        restTemplate.exchange(url, HttpMethod.GET, request, BenchlingProject.class);
    BenchlingProject project = null;
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      project = response.getBody();
    }
    return Optional.ofNullable(project);
  }

  /**
   * Finds all folders where the provided {@code param} matches the provided {@code value}.
   *
   * @param param
   * @param value
   * @param nextToken
   * @return
   */
  private BenchlingFolderList findFolders(@NotNull String param, @NotNull String value, String nextToken) {
    LOGGER.debug("Finding Benchling folders by {} = {}", param, value);
    Map<String, String> map = new HashMap<>();
    map.put(param, value);
    map.put("nextToken", nextToken);
    String url = resolveUrl("/api/v2/folders", map);
    HttpHeaders headers = getHeaders();
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingFolderList> response =
        restTemplate.exchange(url, HttpMethod.GET, request, BenchlingFolderList.class);
    BenchlingFolderList folderList = response.getBody();
    folderList.setFolders(folderList.getFolders().stream()
        .peek(f -> f.setUrl(createFolderUrl(f)))
        .collect(Collectors.toList()));
    return folderList;
  }

  /**
   * Returns a list of all root folders in the tenant.
   *
   * @param nextToken
   * @return {@link BenchlingFolderList}
   */
  public BenchlingFolderList findRootFolders(String nextToken) {
    return findFolders("parentFolderId", "NO_PARENT", nextToken);
  }

  /**
   * Returns a list of child folders, identified by the parent {@code folderId}.
   *
   * @param folderId
   * @param nextToken
   * @return
   */
  public BenchlingFolderList findFolderChildren(@NotNull String folderId, String nextToken) {
    return findFolders("parentFolderId", folderId, nextToken);
  }

  /**
   * Returns a list of folders under a parent project folder, identified by the project ID.
   *
   * @param projectId
   * @param nextToken
   * @return
   */
  public BenchlingFolderList findProjectFolderChildren(@NotNull String projectId, String nextToken) {
    return findFolders("projectId", projectId, nextToken);
  }

  /**
   * Returns a folder object {@link BenchlingFolder}, identified by its PKID. Folder IDs typically
   * have a {@code lib_} prefix.
   *
   * @param id folder ID
   * @return {@link BenchlingFolder}
   */
  public Optional<BenchlingFolder> findFolderById(@NotNull String id) {
    LOGGER.debug("Finding Benchling folder by ID: {}", id);
    String url = resolveUrl("/api/v2/folders/" + id);
    HttpHeaders headers = getHeaders();
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingFolder> response =
        restTemplate.exchange(url, HttpMethod.GET, request, BenchlingFolder.class);
    BenchlingFolder folder = null;
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      folder = response.getBody();
      folder.setUrl(createFolderUrl(folder));
    } else {
      LOGGER.warn("Benchling API request failed: {}", response);
    }
    return Optional.ofNullable(folder);
  }

  /**
   * Creates a new folder within another folder, identified by {@code parentFolderId}.
   *
   * @param name
   * @param parentFolderId
   * @return
   */
  public BenchlingFolder createFolder(@NotNull String name, @NotNull String parentFolderId) {
    LOGGER.info("Creating Benchling folder {}  in parent folder with ID {}", name, parentFolderId);
    String url = resolveUrl("/api/v2/folders");
    HttpHeaders headers = getHeaders();
    headers.set("Content-Type", "application/json");
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("parentFolderId", parentFolderId);
    body.put("name", name);

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingFolder> response =
        restTemplate.exchange(url, HttpMethod.POST, request, BenchlingFolder.class);
    if (response.getStatusCode().equals(HttpStatus.CREATED)) {
      BenchlingFolder folder = response.getBody();
      folder.setUrl(createFolderUrl(folder));
      return folder;
    }
    throw new BenchlingException("Failed to create new folder: " + name);
  }

  /**
   * Fetches a notebook entry {@link BenchlingEntry}, identified by its PKID. Entry records
   * typically have IDs with a {@code ent_} prefix.
   *
   * @param entryId
   * @return
   */
  public Optional<BenchlingEntry> findEntryById(@NotNull String entryId) {
    LOGGER.debug("Requesting entry with ID: " + entryId);
    String url = resolveUrl("/api/v2/entries/" + entryId);
    HttpHeaders headers = getHeaders();
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingEntry> response =
        restTemplate.exchange(url, HttpMethod.GET, request, BenchlingEntry.class);
    BenchlingEntry entry = null;
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      entry = response.getBody();
    }
    return Optional.ofNullable(entry);
  }

  public BenchlingEntryList findEntriesByFolderId(@NotNull String folderId, String nextToken) {
    LOGGER.debug("Requesting all Benchling notebook entries for folder with ID: " + folderId);
    String url = resolveUrl("/api/v2/entries", Collections.singletonMap("folderId", folderId));
    HttpHeaders headers = getHeaders();
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingEntryList> response =
        restTemplate.exchange(url, HttpMethod.GET, request, BenchlingEntryList.class);
    return response.getBody();
  }

  /**
   * Returns all entry objects as {@link BenchlingEntry} in the tenant.
   *
   * @param nextToken
   * @return
   */
  public BenchlingEntryList findAllEntries(String nextToken) {
    LOGGER.debug("Requesting all Benchling notebook entries");
    String url = resolveUrl("/api/v2/entries", Collections.singletonMap("nextToken", nextToken));
    HttpHeaders headers = getHeaders();
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingEntryList> response =
        restTemplate.exchange(url, HttpMethod.GET, request, BenchlingEntryList.class);
    return response.getBody();
  }

  /**
   * Returns all entries associated with a project, identified by its ID.
   *
   * @param projectId
   * @return
   */
  public BenchlingEntryList findProjectEntries(@NotNull String projectId, String nextToken) {
    LOGGER.debug("Requesting all Benchling notebook entries for project with ID: " + projectId);
    Map<String, String> map = new HashMap<>();
    map.put("projectId", projectId);
    map.put("nextToken", nextToken);
    String url = resolveUrl("/api/v2/entries", map);
    HttpHeaders headers = getHeaders();
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingEntryList> response =
        restTemplate.exchange(url, HttpMethod.GET, request, BenchlingEntryList.class);
    return response.getBody();
  }
  
  /**
   * Creates a new notebook entry {@link BenchlingEntry} in the tenant.
   *
   * @param entryRequest request payload
   * @return {@link BenchlingEntry}
   */
  public BenchlingEntry createEntry(@NotNull BenchlingEntryRequest entryRequest) {
    LOGGER.info("Creating Benchling entry {}", entryRequest.toString());
    String url = resolveUrl("/api/v2/entries");
    HttpHeaders headers = getHeaders();
    headers.set("Content-Type", "application/json");
    HttpEntity<BenchlingEntryRequest> request = new HttpEntity<>(entryRequest, headers);
    ResponseEntity<BenchlingEntry> response =
        restTemplate.exchange(url, HttpMethod.POST, request, BenchlingEntry.class);
    if (response.getStatusCode().equals(HttpStatus.CREATED)) {
      return response.getBody();
    }
    throw new BenchlingException("Failed to create new entry: " + entryRequest.getName());
  }

  /**
   * Fetches all registered entry template objects ({@link BenchlingEntryTemplate}).
   *
   * @param nextToken
   * @return
   */
  public BenchlingEntryTemplateList findEntryTemplates(String nextToken) {
    LOGGER.debug("Requesting all Benchling entry templates");
    String url =
        resolveUrl("/api/v2/entry-templates", Collections.singletonMap("nextToken", nextToken));
    HttpHeaders headers = getHeaders();
    HttpEntity<BenchlingEntryTemplateList> request = new HttpEntity<>(headers);
    ResponseEntity<BenchlingEntryTemplateList> response =
        restTemplate.exchange(url, HttpMethod.GET, request, BenchlingEntryTemplateList.class);
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return response.getBody();
    } else {
      throw new BenchlingException("Failed to fetch entry template list.");
    }
  }

  /**
   * Fetches a single {@link BenchlingEntryTemplate} object, identified by its PKID.
   *
   * @param id
   * @return
   */
  public BenchlingEntryTemplate findEntryTemplateById(@NotNull String id) {
    LOGGER.debug("Requesting entry template with ID: " + id);
    String url = resolveUrl("/api/v2/entry-templates/" + id);
    HttpHeaders headers = getHeaders();
    HttpEntity<BenchlingEntryTemplate> request = new HttpEntity<>(headers);
    ResponseEntity<BenchlingEntryTemplate> response =
        restTemplate.exchange(url, HttpMethod.GET, request, BenchlingEntryTemplate.class);
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return response.getBody();
    } else {
      throw new BenchlingException("Failed to fetch entry template: " + id);
    }
  }
  
  /**
   * Returns all entry schemas ({@link BenchlingEntrySchema}) registered in the tenant.
   *
   * @param nextToken
   * @return
   */
  public BenchlingEntrySchemaList findEntrySchemas(String nextToken) {
    LOGGER.debug("Requesting all Benchling entry schemas");
    String url =
        resolveUrl("/api/v2/entry-schemas", Collections.singletonMap("nextToken", nextToken));
    HttpHeaders headers = getHeaders();
    HttpEntity<BenchlingEntrySchemaList> request = new HttpEntity<>(headers);
    ResponseEntity<BenchlingEntrySchemaList> response =
        restTemplate.exchange(url, HttpMethod.GET, request, BenchlingEntrySchemaList.class);
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return response.getBody();
    } else {
      throw new BenchlingException("Failed to fetch entry schemas");
    }
  }
  
  /**
   * Retrieves a single entry schema ({@link BenchlingEntrySchema}), identified by its PKID.
   *
   * @param id
   * @return
   */
  public Optional<BenchlingEntrySchema> findEntrySchemaById(@NotNull String id) {
    LOGGER.debug("Requesting entry schema with ID: " + id);
    String url = resolveUrl("/api/v2/entry-schemas/" + id);
    HttpHeaders headers = getHeaders();
    HttpEntity<BenchlingEntrySchema> request = new HttpEntity<>(headers);
    ResponseEntity<BenchlingEntrySchema> response =
        restTemplate.exchange(url, HttpMethod.GET, request, BenchlingEntrySchema.class);
    BenchlingEntrySchema schema = null;
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      schema = response.getBody();
    }
    return Optional.ofNullable(schema);
  }
  
  /**
   * Retrieved a list of all users in the tenant.
   *
   * @param nextToken
   * @return
   */
  public BenchlingUserList findUsers(String nextToken) {
    LOGGER.debug("Requesting all Benchling users");
    String url = resolveUrl("/api/v2/users", Collections.singletonMap("nextToken", nextToken));
    HttpHeaders headers = getHeaders();
    HttpEntity<BenchlingUserList> request = new HttpEntity<>(headers);
    ResponseEntity<BenchlingUserList> response =
        restTemplate.exchange(url, HttpMethod.GET, request, BenchlingUserList.class);
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return response.getBody();
    } else {
      throw new BenchlingException("Failed to fetch users");
    }
  }
  
  /**
   * Retrieves a list of users, identified by their username.
   *
   * @param username
   * @param nextToken
   * @return
   */
  public BenchlingUserList findUsersByUsername(@NotNull String username, String nextToken) {
    LOGGER.debug("Requesting all Benchling users with username: " + username);
    String url =
        resolveUrl(
            "/api/v2/users?handles=" + username, Collections.singletonMap("nextToken", nextToken));
    HttpHeaders headers = getHeaders();
    HttpEntity<BenchlingUserList> request = new HttpEntity<>(headers);
    ResponseEntity<BenchlingUserList> response =
        restTemplate.exchange(url, HttpMethod.GET, request, BenchlingUserList.class);
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return response.getBody();
    } else {
      throw new BenchlingException("Failed to fetch users");
    }
  }
  
  /**
   * Retrieves a single user ({@link BenchlingUser}), identified by their PKID.
   *
   * @param id
   * @return
   */
  public Optional<BenchlingUser> findUserById(@NotNull String id) {
    LOGGER.debug("Requesting user with ID: " + id);
    String url = resolveUrl("/api/v2/users/" + id);
    HttpHeaders headers = getHeaders();
    HttpEntity<BenchlingUser> request = new HttpEntity<>(headers);
    ResponseEntity<BenchlingUser> response =
        restTemplate.exchange(url, HttpMethod.GET, request, BenchlingUser.class);
    BenchlingUser user = null;
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      user = response.getBody();
    }
    return Optional.ofNullable(user);
  }

  private String resolveUrl(String endpoint, LinkedMultiValueMap<String, String> params) {
    URL url = joinUrls(rootUrl, endpoint);
    UriComponentsBuilder uriComponentsBuilder =
        UriComponentsBuilder.fromHttpUrl(url.toString()).queryParams(params);
    UriComponents components = uriComponentsBuilder.build(false);
    return components.toString();
  }

  private String resolveUrl(String endpoint) {
    return this.resolveUrl(endpoint, new LinkedMultiValueMap<>());
  }

  private String resolveUrl(String endpoint, Map<String, String> params) {
    LinkedMultiValueMap<String, String> linkedMap = new LinkedMultiValueMap<>();
    for (Map.Entry<String, String> entry : params.entrySet()) {
      if (StringUtils.hasText(entry.getValue())) {
        linkedMap.set(entry.getKey(), entry.getValue());
      }
    }
    return this.resolveUrl(endpoint, linkedMap);
  }

  private URL joinUrls(URL root, String path) {
    try {
      return new URL(root, path);
    } catch (MalformedURLException ex) {
      throw new StudyTrackerException(ex);
    }
  }
  
  public URL getRootUrl() {
    return rootUrl;
  }
  
  public URL getRootFolderUrl() {
    return rootFolderUrl;
  }
  
}
