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

package com.decibeltx.studytracker.idbs.eln;

import com.decibeltx.studytracker.idbs.eln.entities.EntityList;
import com.decibeltx.studytracker.idbs.eln.entities.IdbsNotebookEntry;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
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

public class IdbsRestElnClient implements IdbsElnOperations {

  private static final Logger LOGGER = LoggerFactory.getLogger(IdbsRestElnClient.class);

  private final RestTemplate restTemplate;

  private final URL rootUrl;

  private final String authenticationToken;

  public IdbsRestElnClient(RestTemplate restTemplate, URL rootUrl, String authenticationToken) {
    this.restTemplate = restTemplate;
    this.rootUrl = rootUrl;
    this.authenticationToken = authenticationToken;
  }

  @Override
  public IdbsNotebookEntry findEntityById(String entityId) {
    URL url = joinUrls(rootUrl, "/ewb/services/1.0/entities/" + entityId);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Basic " + authenticationToken);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<IdbsNotebookEntry> response = restTemplate
        .exchange(url.toString(), HttpMethod.GET, request, IdbsNotebookEntry.class);
//    if (!response.getStatusCode().equals(HttpStatus.OK)) {
//      throw new RecordNotFoundException(
//          String.format("Failed to fetch notebook entry with ID %s, returned status code %s",
//              entityId, response.getStatusCode().toString()));
//    }
    return response.getBody();
  }

  @Override
  public List<IdbsNotebookEntry> findEntityChildren(String entityId) {
    URL url = joinUrls(rootUrl, "/ewb/services/1.0/entitytree/" + entityId);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Basic " + authenticationToken);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");
    Map<String, Object> body = new LinkedHashMap<>();
    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<EntityList<IdbsNotebookEntry>> response = restTemplate
        .exchange(url.toString(), HttpMethod.GET, request,
            new ParameterizedTypeReference<EntityList<IdbsNotebookEntry>>() {
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
        .format("Making request to IDBS ELN API to create study folder for program %s with name %s",
            programEntityId,
            studyName
        ));
    URL url = joinUrls(rootUrl, "/ewb/services/1.0/entities/" + programEntityId + "/children");
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Basic " + authenticationToken);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    headers.set("Cache-Control", "no-cache");
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("entityType", "STUDY");
    body.put("entityName", studyName);
    Map<String, Object> attribute = new LinkedHashMap<>();
    attribute.put("name", "Name");
    attribute
        .put("values", Collections.singletonMap("value", Collections.singletonList(studyName)));
    body.put("attributes",
        Collections.singletonMap("attribute", Collections.singletonList(attribute)));
    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
    ResponseEntity<String> response = restTemplate
        .exchange(url.toString(), HttpMethod.POST, request, String.class);
    return response.getBody();
  }

  private URL joinUrls(URL root, String path) {
    try {
      return new URL(root, path);
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex);
    }
  }

}
