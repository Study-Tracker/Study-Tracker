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
import com.decibeltx.studytracker.benchling.eln.entities.EntityList;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

//import java.util.Collections;

public class BenchlingRestElnClient implements BenchlingElnOperations {

  private static final Logger LOGGER = LoggerFactory.getLogger(BenchlingRestElnClient.class);

  private final RestTemplate restTemplate;

  private final URL rootUrl;

  private final String authenticationToken;


  public BenchlingRestElnClient(RestTemplate restTemplate, URL rootUrl, String authenticationToken) {
    this.restTemplate = restTemplate;
    this.rootUrl = rootUrl;
    this.authenticationToken = authenticationToken;
  }

  @Override
  public BenchlingNotebookEntry findEntityById(String entityId) {
    LOGGER.info("Requesting entity with ID: " + entityId);
    URL url = joinUrls(rootUrl, "/api/v2/folder/");
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Basic " + authenticationToken);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<BenchlingNotebookEntry> response = restTemplate
        .exchange(url.toString(), HttpMethod.GET, request, BenchlingNotebookEntry.class);
//    if (!response.getStatusCode().equals(HttpStatus.OK)) {
//      throw new RecordNotFoundException(
//          String.format("Failed to fetch notebook entry with ID %s, returned status code %s",
//              entityId, response.getStatusCode().toString()));
//    }
    return response.getBody();
  }

  @Override
  public List<BenchlingNotebookEntry> findEntityChildren(String entityId) {
    LOGGER.info("Requesting children of entity with ID: " + entityId);
    URL url = joinUrls(rootUrl, "/api/v2/folder/");
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Basic " + authenticationToken);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<EntityList<BenchlingNotebookEntry>> response = restTemplate
        .exchange(url.toString(), HttpMethod.GET, request,
            new ParameterizedTypeReference<EntityList<BenchlingNotebookEntry>>() {
            });
//    if (!response.getStatusCode().equals(HttpStatus.OK)) {
//      throw new RecordNotFoundException(
//          String.format("Failed to fetch notebook entry with ID %s, returned status code %s",
//              entityId, response.getStatusCode().toString()));
//    }
    return response.getBody().getEntities();
  }

  @Override
  public String createStudyFolder(String studyName, String programEntityId) {

    LOGGER.info(String
        .format("Making request to Benchling ELN API to create study folder for program %s with name %s",
            programEntityId,
            studyName
        ));
    URL url = joinUrls(rootUrl, "/api/v2/folders" );
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Basic " + authenticationToken);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("parentFolderId", programEntityId);
    body.put("name", studyName);

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
    ParameterizedTypeReference<Map<String, String>> responseType =
        new ParameterizedTypeReference<Map<String, String>>() {
        };
    ResponseEntity<Map<String, String>> response = restTemplate
        .exchange(url.toString(), HttpMethod.POST, request, responseType);

    return response.getBody().get("id");
  }

  private URL joinUrls(URL root, String path) {
    try {
      return new URL(root, path);
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex);
    }
  }

}
