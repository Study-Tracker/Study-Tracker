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
import com.decibeltx.studytracker.benchling.api.entities.BenchlingEntrySchema;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingEntrySchemaList;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingEntryTemplate;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingEntryTemplateList;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingFolder;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingFolderList;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingProject;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingProjectList;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingUser;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingUserList;
import com.decibeltx.studytracker.benchling.exception.BenchlingAuthenticationException;
import com.decibeltx.studytracker.benchling.exception.BenchlingException;
import com.decibeltx.studytracker.exception.StudyTrackerException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

  @Autowired
  @Qualifier("benchlingElnRestTemplate")
  private RestTemplate restTemplate;

  @Value("${benchling.root-url}")
  private URL rootUrl;

  /**
   * Generates an authentication token using client application authentication in the Benchling API.
   *
   * @param clientId Registered application client ID
   * @param clientSecret Registered application client secret token
   * @return
   */
  public BenchlingAuthenticationToken acquireApplicationAuthenticationToken(
      @NotNull String clientId, @NotNull String clientSecret) {
    URL url = joinUrls(rootUrl, "api/v2/token");
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/x-www-form-urlencoded");
    MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
    data.add("client_id", clientId);
    data.add("client_secret", clientSecret);
    data.add("grant_type", "client_credentials");
    HttpEntity<?> request = new HttpEntity<>(data, headers);
    ResponseEntity<BenchlingAuthenticationToken> response = restTemplate.exchange(
        url.toString(),
        HttpMethod.POST,
        request,
        BenchlingAuthenticationToken.class
    );
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return response.getBody();
    } else {
      throw new BenchlingAuthenticationException("Failed to authenticate with Benchling API");
    }
  }

  /**
   * Returns a list of all projects as {@link BenchlingProject} registered in the tenant.
   *
   * @param authHeader
   * @return
   */
  public BenchlingProjectList findProjects(@NotNull String authHeader, String nextToken) {
    String url = resolveUrl("/api/v2/projects", Collections.singletonMap("nextToken", nextToken));
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", authHeader);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingProjectList> response
        = restTemplate
        .exchange(url, HttpMethod.GET, request, BenchlingProjectList.class);
    if (!response.getStatusCode().equals(HttpStatus.OK)) {
      throw new StudyTrackerException("Failed to fetch project list.");
    }
    return response.getBody();
  }

  /**
   * Returns project details, identified by a PKID. Project IDs typically have a {@code src_}
   *  prefix.
   *
   * @param id Project ID
   * @param authHeader
   * @return
   */
  public Optional<BenchlingProject> findProjectById(@NotNull String id,
      @NotNull String authHeader) {
    String url = resolveUrl("/api/v2/projects/" + id);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", authHeader);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingProject> response
        = restTemplate.exchange(url, HttpMethod.GET, request, BenchlingProject.class);
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
   * @param authHeader
   * @return
   */
  private BenchlingFolderList findFolders(@NotNull String param, @NotNull String value,
      @NotNull String authHeader, String nextToken) {
    Map<String, String> map = new HashMap<>();
    map.put(param, value);
    map.put("nextToken", nextToken);
    String url = resolveUrl("/api/v2/folders", map);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", authHeader);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingFolderList> response
        = restTemplate.exchange(url, HttpMethod.GET, request, BenchlingFolderList.class);
    return response.getBody();
  }

  /**
   * Returns a list of all root folders in the tenant.
   *
   * @param authHeader
   * @return
   */
  public BenchlingFolderList findRootFolders(@NotNull String authHeader, String nextToken) {
    return findFolders("parentFolderId", "NO_PARENT", authHeader, nextToken);
  }

  /**
   * Returns a list of child folders, identified by the parent {@code folderId}.
   *
   * @param folderId
   * @param authHeader
   * @return
   */
  public BenchlingFolderList findFolderChildren(@NotNull String folderId,
      @NotNull String authHeader, String nextToken) {
    return findFolders("parentFolderId", folderId, authHeader, nextToken);
  }

  /**
   * Returns a list of folders under a parent project folder, identified by the project ID.
   *
   * @param projectId
   * @param authHeader
   * @return
   */
  public BenchlingFolderList findProjectFolderChildren(@NotNull String projectId,
      @NotNull String authHeader, String nextToken) {
    return findFolders("projectId", projectId, authHeader, nextToken);
  }

  /**
   * Returns a folder object {@link BenchlingFolder}, identified by its PKID. Folder IDs typically
   *  have a {@code lib_} prefix.
   *
   * @param id
   * @param authHeader
   * @return
   */
  public Optional<BenchlingFolder> findFolderById(@NotNull String id, @NotNull String authHeader) {
    String url = resolveUrl("/api/v2/folders/" + id);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", authHeader);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingFolder> response
        = restTemplate.exchange(url, HttpMethod.GET, request, BenchlingFolder.class);
    BenchlingFolder folder = null;
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      folder = response.getBody();
    }
    return Optional.ofNullable(folder);
  }

  /**
   * Creates a new folder within another folder, identified by {@code parentFolderId}.
   *
   * @param name
   * @param parentFolderId
   * @param authHeader
   * @return
   */
  public BenchlingFolder createFolder(@NotNull String name, @NotNull String parentFolderId,
      @NotNull String authHeader) {
    String url = resolveUrl("/api/v2/folders");
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", authHeader);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("parentFolderId", parentFolderId);
    body.put("name", name);

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingFolder> response = restTemplate
        .exchange(url, HttpMethod.POST, request, BenchlingFolder.class);
    if (response.getStatusCode().equals(HttpStatus.CREATED)) {
      return response.getBody();
    }
    throw new BenchlingException("Failed to create new folder: " + name);
  }

  /**
   * Fetches a notebook entry {@link BenchlingEntry}, identified by its PKID. Entry records
   *   typically have IDs with a {@code ent_} prefix.
   *
   * @param entryId
   * @param authHeader
   * @return
   */
  public Optional<BenchlingEntry> findEntryById(@NotNull String entryId,
      @NotNull String authHeader) {
    LOGGER.info("Requesting entry with ID: " + entryId);
    String url = resolveUrl("/api/v2/entries/" + entryId);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", authHeader);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingEntry> response = restTemplate
        .exchange(url, HttpMethod.GET, request, BenchlingEntry.class);
    BenchlingEntry entry = null;
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      entry = response.getBody();
    }
    return Optional.ofNullable(entry);
  }

  /**
   * Returns all entry objects as {@link BenchlingEntry} in the tenant.
   *
   * @param authHeader
   * @return
   */
  public BenchlingEntryList findAllEntries(@NotNull String authHeader, String nextToken) {
    String url = resolveUrl("/api/v2/entries", Collections.singletonMap("nextToken", nextToken));
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", authHeader);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingEntryList> response = restTemplate
        .exchange(url, HttpMethod.GET, request, BenchlingEntryList.class);
    return response.getBody();
  }

  /**
   * Returns all entries associated with a project, identified by its ID.
   *
   * @param projectId
   * @param authHeader
   * @return
   */
  public BenchlingEntryList findProjectEntries(@NotNull String projectId,
      @NotNull String authHeader, String nextToken) {
    Map<String, String> map = new HashMap<>();
    map.put("projectId", projectId);
    map.put("nextToken", nextToken);
    String url = resolveUrl("/api/v2/entries", map);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", authHeader);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingEntryList> response = restTemplate
        .exchange(url, HttpMethod.GET, request, BenchlingEntryList.class);
    return response.getBody();
  }

  
  public BenchlingEntry createEntry(@NotNull BenchlingEntryRequest entryRequest,
      @NotNull String authHeader) {
    String url = resolveUrl("/api/v2/entries");
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", authHeader);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");

    HttpEntity<BenchlingEntryRequest> request = new HttpEntity<>(entryRequest, headers);
    ResponseEntity<BenchlingEntry> response = restTemplate
        .exchange(url, HttpMethod.POST, request, BenchlingEntry.class);
    if (response.getStatusCode().equals(HttpStatus.CREATED)) {
      return response.getBody();
    }
    throw new BenchlingException("Failed to create new entry: " + entryRequest.getName());
  }

  /**
   * Fetches all registered entry template objects ({@link BenchlingEntryTemplate}).
   *
   * @param authHeader
   * @return
   */
  public BenchlingEntryTemplateList findEntryTemplates(@NotNull String authHeader,
      String nextToken) {
    String url = resolveUrl("/api/v2/entry-templates",
        Collections.singletonMap("nextToken", nextToken));
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", authHeader);
    headers.set("Accept", "application/json");
    headers.set("Cache-Control", "no-cache");

    HttpEntity<BenchlingEntryTemplateList> request = new HttpEntity<>(headers);
    ResponseEntity<BenchlingEntryTemplateList> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        request,
        BenchlingEntryTemplateList.class
    );
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return response.getBody();
    } else {
      throw new BenchlingException("Failed to fetch entry template list.");
    }
  }

  /**
   * Fetches a single {@link BenchlingEntryTemplate} object, identified by its PKID.
   *
   * @param authHeader
   * @param id
   * @return
   */
  public BenchlingEntryTemplate findEntryTemplateById(@NotNull String id,
      @NotNull String authHeader) {
    String url = resolveUrl("/api/v2/entry-templates/" + id);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", authHeader);
    headers.set("Accept", "application/json");
    headers.set("Cache-Control", "no-cache");

    HttpEntity<BenchlingEntryTemplate> request = new HttpEntity<>(headers);
    ResponseEntity<BenchlingEntryTemplate> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        request,
        BenchlingEntryTemplate.class
    );
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return response.getBody();
    } else {
      throw new BenchlingException("Failed to fetch entry template: " + id);
    }
  }

  public BenchlingEntrySchemaList findEntrySchemas(@NotNull String authHeader, String nextToken) {
    String url = resolveUrl("/api/v2/entry-schemas",
        Collections.singletonMap("nextToken", nextToken));
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", authHeader);
    headers.set("Accept", "application/json");
    headers.set("Cache-Control", "no-cache");

    HttpEntity<BenchlingEntrySchemaList> request = new HttpEntity<>(headers);
    ResponseEntity<BenchlingEntrySchemaList> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        request,
        BenchlingEntrySchemaList.class
    );
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return response.getBody();
    } else {
      throw new BenchlingException("Failed to fetch entry schemas");
    }
  }

  public Optional<BenchlingEntrySchema> findEntrySchemaById(@NotNull String id,
      @NotNull String authHeader) {
    String url = resolveUrl("/api/v2/entry-schemas/" + id);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", authHeader);
    headers.set("Accept", "application/json");
    headers.set("Cache-Control", "no-cache");

    HttpEntity<BenchlingEntrySchema> request = new HttpEntity<>(headers);
    ResponseEntity<BenchlingEntrySchema> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        request,
        BenchlingEntrySchema.class
    );
    BenchlingEntrySchema schema = null;
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      schema = response.getBody();
    }
    return Optional.ofNullable(schema);
  }

  public BenchlingUserList findUsers(@NotNull String authHeader, String nextToken) {
    String url = resolveUrl("/api/v2/users",
        Collections.singletonMap("nextToken", nextToken));
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", authHeader);
    headers.set("Accept", "application/json");
    headers.set("Cache-Control", "no-cache");

    HttpEntity<BenchlingUserList> request = new HttpEntity<>(headers);
    ResponseEntity<BenchlingUserList> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        request,
        BenchlingUserList.class
    );
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return response.getBody();
    } else {
      throw new BenchlingException("Failed to fetch users");
    }
  }

  public BenchlingUserList findUsersByUsername(@NotNull String username,
      @NotNull String authHeader, String nextToken) {
    String url = resolveUrl("/api/v2/users?handles=" + username,
        Collections.singletonMap("nextToken", nextToken));
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", authHeader);
    headers.set("Accept", "application/json");
    headers.set("Cache-Control", "no-cache");

    HttpEntity<BenchlingUserList> request = new HttpEntity<>(headers);
    ResponseEntity<BenchlingUserList> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        request,
        BenchlingUserList.class
    );
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return response.getBody();
    } else {
      throw new BenchlingException("Failed to fetch users");
    }
  }

  public Optional<BenchlingUser> findUserById(@NotNull String id,
      @NotNull String authHeader) {
    String url = resolveUrl("/api/v2/users/" + id);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", authHeader);
    headers.set("Accept", "application/json");
    headers.set("Cache-Control", "no-cache");

    HttpEntity<BenchlingUser> request = new HttpEntity<>(headers);
    ResponseEntity<BenchlingUser> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        request,
        BenchlingUser.class
    );
    BenchlingUser user = null;
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      user = response.getBody();
    }
    return Optional.ofNullable(user);
  }

  private String resolveUrl(String endpoint, LinkedMultiValueMap<String, String> params) {
    URL url = joinUrls(rootUrl, endpoint);
    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url.toString())
        .queryParams(params);
    UriComponents components = uriComponentsBuilder.build().encode();
    return components.toString();
  }

  private String resolveUrl(String endpoint) {
    return this.resolveUrl(endpoint, new LinkedMultiValueMap<>());
  }

  private String resolveUrl(String endpoint, Map<String, String> params) {
    LinkedMultiValueMap<String, String> linkedMap = new LinkedMultiValueMap<>();
    for (Map.Entry<String, String> entry: params.entrySet()) {
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
      throw new RuntimeException(ex);
    }
  }

}
