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

import com.decibeltx.studytracker.benchling.eln.entities.BenchlingEntry;
import com.decibeltx.studytracker.benchling.eln.entities.BenchlingEntryList;
import com.decibeltx.studytracker.benchling.eln.entities.BenchlingEntryRequest;
import com.decibeltx.studytracker.benchling.eln.entities.BenchlingFolder;
import com.decibeltx.studytracker.benchling.eln.entities.BenchlingFolderList;
import com.decibeltx.studytracker.benchling.eln.entities.BenchlingProject;
import com.decibeltx.studytracker.benchling.eln.entities.BenchlingProjectList;
import com.decibeltx.studytracker.benchling.exception.BenchlingException;
import com.decibeltx.studytracker.core.exception.StudyTrackerException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class BenchlingElnRestClient implements BenchlingElnClientOperations {

  private static final Logger LOGGER = LoggerFactory.getLogger(BenchlingElnRestClient.class);

  private final RestTemplate restTemplate;

  private final URL rootUrl;

  private final String authenticationToken;


  public BenchlingElnRestClient(RestTemplate restTemplate, URL rootUrl,
      String authenticationToken) {
    this.restTemplate = restTemplate;
    this.rootUrl = rootUrl;
    this.authenticationToken = authenticationToken;
  }

  @Override
  public List<BenchlingProject> findProjects() {
    URL url = joinUrls(rootUrl, "/api/v2/projects");
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Basic " + authenticationToken);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingProjectList> response
        = restTemplate
        .exchange(url.toString(), HttpMethod.GET, request, BenchlingProjectList.class);
    if (!response.getStatusCode().equals(HttpStatus.OK)) {
      throw new StudyTrackerException("Failed o fetch project list.");
    }
    return response.getBody().getProjects();
  }

  @Override
  public Optional<BenchlingProject> findProjectById(String id) {
    URL url = joinUrls(rootUrl, "/api/v2/projects/" + id);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Basic " + authenticationToken);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingProject> response
        = restTemplate.exchange(url.toString(), HttpMethod.GET, request, BenchlingProject.class);
    BenchlingProject project = null;
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      project = response.getBody();
    }
    return Optional.ofNullable(project);
  }

  private List<BenchlingFolder> findFolders(String param, String value) {
    URL url = joinUrls(rootUrl, "/api/v2/folders?" + param + "=" + value);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Basic " + authenticationToken);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingFolderList> response
        = restTemplate.exchange(url.toString(), HttpMethod.GET, request, BenchlingFolderList.class);
    return response.getBody().getFolders();
  }

  @Override
  public List<BenchlingFolder> findRootFolders() {
    return findFolders("parentFolderId", "NO_PARENT");
  }

  @Override
  public List<BenchlingFolder> findFolderChildren(String folderId) {
    return findFolders("parentFolderId", folderId);
  }

  @Override
  public List<BenchlingFolder> findProjectFolderChildren(String projectId) {
    return findFolders("projectId", projectId);
  }

  @Override
  public Optional<BenchlingFolder> findFolderById(String id) {
    URL url = joinUrls(rootUrl, "/api/v2/folders/" + id);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Basic " + authenticationToken);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingFolder> response
        = restTemplate.exchange(url.toString(), HttpMethod.GET, request, BenchlingFolder.class);
    BenchlingFolder folder = null;
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      folder = response.getBody();
    }
    return Optional.ofNullable(folder);
  }

  @Override
  public BenchlingFolder createFolder(String name, String parentFolderId) {
    URL url = joinUrls(rootUrl, "/api/v2/folders");
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Basic " + authenticationToken);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("parentFolderId", parentFolderId);
    body.put("name", name);

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingFolder> response = restTemplate
        .exchange(url.toString(), HttpMethod.POST, request, BenchlingFolder.class);
    if (response.getStatusCode().equals(HttpStatus.CREATED)) {
      return response.getBody();
    }
    throw new BenchlingException("Failed to create new folder: " + name);
  }

  @Override
  public Optional<BenchlingEntry> findEntryById(String entryId) {
    LOGGER.info("Requesting entry with ID: " + entryId);
    URL url = joinUrls(rootUrl, "/api/v2/entries/" + entryId);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Basic " + authenticationToken);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingEntry> response = restTemplate
        .exchange(url.toString(), HttpMethod.GET, request, BenchlingEntry.class);
    BenchlingEntry entry = null;
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      entry = response.getBody();
    }
    return Optional.ofNullable(entry);
  }

  @Override
  public List<BenchlingEntry> findAllEntries() {
    URL url = joinUrls(rootUrl, "/api/v2/entries/");
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Basic " + authenticationToken);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingEntryList> response = restTemplate
        .exchange(url.toString(), HttpMethod.GET, request, BenchlingEntryList.class);
    return response.getBody().getEntries();
  }

  @Override
  public List<BenchlingEntry> findProjectEntries(String projectId) {
    URL url = joinUrls(rootUrl, "/api/v2/entries?projectId=" + projectId);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Basic " + authenticationToken);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingEntryList> response = restTemplate
        .exchange(url.toString(), HttpMethod.GET, request, BenchlingEntryList.class);
    return response.getBody().getEntries();
  }

  @Override
  public BenchlingEntry createEntry(BenchlingEntryRequest entryRequest) {
    URL url = joinUrls(rootUrl, "/api/v2/entries");
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Basic " + authenticationToken);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");

    HttpEntity<BenchlingEntryRequest> request = new HttpEntity<>(entryRequest, headers);
    ResponseEntity<BenchlingEntry> response = restTemplate
        .exchange(url.toString(), HttpMethod.POST, request, BenchlingEntry.class);
    if (response.getStatusCode().equals(HttpStatus.CREATED)) {
      return response.getBody();
    }
    throw new BenchlingException("Failed to create new entry: " + entryRequest.getName());
  }

  private URL joinUrls(URL root, String path) {
    try {
      return new URL(root, path);
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex);
    }
  }

}
