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

package com.decibeltx.studytracker.egnyte.rest;

import com.decibeltx.studytracker.egnyte.EgnyteClientOperations;
import com.decibeltx.studytracker.egnyte.EgnyteOptions;
import com.decibeltx.studytracker.egnyte.entity.EgnyteFile;
import com.decibeltx.studytracker.egnyte.entity.EgnyteFolder;
import com.decibeltx.studytracker.egnyte.entity.EgnyteObject;
import com.decibeltx.studytracker.egnyte.exception.DuplicateFolderException;
import com.decibeltx.studytracker.egnyte.exception.EgnyteException;
import com.decibeltx.studytracker.egnyte.exception.ObjectNotFoundException;
import com.decibeltx.studytracker.egnyte.exception.UnauthorizedException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class EgnyteRestApiClient implements EgnyteClientOperations {

  private static final Logger LOGGER = LoggerFactory.getLogger(EgnyteRestApiClient.class);

  private final RestTemplate restTemplate;
  private final EgnyteOptions options;

  public EgnyteRestApiClient(RestTemplate restTemplate, EgnyteOptions options) {
    this.restTemplate = restTemplate;
    this.options = options;
  }

  private void doBefore() {
    try {
      TimeUnit.MILLISECONDS.sleep(options.getSleep());
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public EgnyteFolder createFolder(String folderPath) throws EgnyteException {
    LOGGER.info("Making request to Egnyte API to create directory: " + folderPath);
    doBefore();
    URL url = joinUrls(options.getRootUrl(), "/pubapi/v1/fs/" + folderPath);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + options.getToken());
    headers.set("Content-Type", "application/json");
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("action", "add_folder");
    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
    try {
      ResponseEntity<EgnyteFolder> response = restTemplate
          .exchange(url.toString(), HttpMethod.POST, request, EgnyteFolder.class);
      return response.getBody();
    } catch (HttpStatusCodeException e) {
      String responseBody = e.getResponseBodyAsString();
      Map<String, String> json = null;
      try {
        TypeReference<HashMap<String, String>> typeReference = new TypeReference<HashMap<String, String>>() {
        };
        json = new ObjectMapper().readValue(responseBody, typeReference);
        LOGGER.warn(json.toString());
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      String errorMessage =
          json != null && json.containsKey("errorMessage") ? json.get("errorMessage")
              : responseBody;
      if (errorMessage.equals("Folder already exists at this location")) {
        throw new DuplicateFolderException(errorMessage);
      } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ObjectNotFoundException("Requested resource was not found.");
      } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
        throw new UnauthorizedException("You do not have permission to perform this operation.");
      } else {
        throw new EgnyteException(errorMessage);
      }
    }
  }

  @Override
  public EgnyteObject findObjectByPath(String path, int depth) throws EgnyteException {

    LOGGER.info("Making request to Egnyte API for object at path: " + path);

    doBefore();

    URL url = joinUrls(options.getRootUrl(), "/pubapi/v1/fs/" + path);
    LOGGER.debug("Request URL: " + url.toString());

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + options.getToken());
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(null, headers);
    ResponseEntity<EgnyteObject> response = null;
    try {
      response = restTemplate
          .exchange(url.toString(), HttpMethod.GET, request, EgnyteObject.class);
    } catch (HttpStatusCodeException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ObjectNotFoundException("Requested resource was not found.");
      } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
        throw new UnauthorizedException("You do not have permission to perform this operation.");
      } else {
        throw new EgnyteException(e.getResponseBodyAsString());
      }
    }

    EgnyteObject object = response.getBody();

    // Object is a folder
    if (object.isFolder()) {
      EgnyteFolder folder = (EgnyteFolder) object;
      if (depth >= 0 && depth < options.getMaxReadDepth()) {
        List<EgnyteFolder> subFolders = new ArrayList<>();
        for (int i = 0; i < folder.getSubFolders().size(); i++) {
          EgnyteFolder subFolder = folder.getSubFolders().get(i);
          subFolders.add((EgnyteFolder) findObjectByPath(subFolder.getPath(), depth + 1));
        }
        folder.setSubFolders(subFolders);
      }
      return folder;
    }

    // Object is a file
    else {
      return object;
    }
  }

  @Override
  public EgnyteObject findObjectByPath(String path) throws EgnyteException {
    return findObjectByPath(path, 0);
  }

  @Override
  public EgnyteFolder findFolderById(String folderId) throws EgnyteException {
    LOGGER.info("Making request to Egnyte API for folder with ID: " + folderId);
    doBefore();
    URL url = joinUrls(options.getRootUrl(), "/pubapi/v1/fs/ids/folder/" + folderId);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + options.getToken());
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(null, headers);
    try {
      ResponseEntity<EgnyteFolder> response = restTemplate
          .exchange(url.toString(), HttpMethod.GET, request, EgnyteFolder.class);
      LOGGER.debug("Successfully completed Egnyte API request.");
      return response.getBody();
    } catch (HttpStatusCodeException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ObjectNotFoundException("Requested resource was not found.");
      } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
        throw new UnauthorizedException("You do not have permission to perform this operation.");
      } else {
        throw new EgnyteException(e.getResponseBodyAsString());
      }
    }
  }

  @Override
  public EgnyteFile findFileById(String fileId) throws EgnyteException {
    LOGGER.info("Making request to Egnyte API for file with ID: " + fileId);
    doBefore();
    URL url = joinUrls(options.getRootUrl(), "/pubapi/v1/fs/ids/file/" + fileId);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + options.getToken());
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(null, headers);

    try {
      ResponseEntity<EgnyteFile> response = restTemplate
          .exchange(url.toString(), HttpMethod.GET, request, EgnyteFile.class);
      LOGGER.debug("Successfully completed Egnyte API request.");
      return response.getBody();
    } catch (HttpStatusCodeException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ObjectNotFoundException("Requested resource was not found.");
      } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
        throw new UnauthorizedException("You do not have permission to perform this operation.");
      } else {
        throw new EgnyteException(e.getResponseBodyAsString());
      }
    }
  }

  @Override
  public EgnyteFile uploadFile(File file, String path) throws EgnyteException {
    LOGGER.info(String.format("Making request to Egnyte API to upload file %s to directory %s",
        file.getName(), path));
    doBefore();
    URL url = joinUrls(options.getRootUrl(),
        "/pubapi/v1/fs-content/" + path + "/" + file.getName());
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + options.getToken());
    headers.set("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE);
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", new FileSystemResource(file));
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
    try {
      ResponseEntity<EgnyteFile> response = restTemplate
          .exchange(url.toString(), HttpMethod.POST, request, EgnyteFile.class);
      return response.getBody();
    } catch (HttpStatusCodeException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ObjectNotFoundException("Requested resource was not found.");
      } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
        throw new UnauthorizedException("You do not have permission to perform this operation.");
      } else {
        throw new EgnyteException(e.getResponseBodyAsString());
      }
    }
  }

  @Override
  public void deleteObjectByPath(String path) throws EgnyteException {
    LOGGER.info(String.format("Making request to Egnyte API to delete object at path: %s", path));
    doBefore();
    URL url = joinUrls(options.getRootUrl(), path);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + options.getToken());
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(null, headers);
    try {
      restTemplate.exchange(url.toString(), HttpMethod.DELETE, request, Object.class);
    } catch (HttpStatusCodeException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new ObjectNotFoundException("Requested resource was not found.");
      } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
        throw new UnauthorizedException("You do not have permission to perform this operation.");
      } else {
        throw new EgnyteException(e.getResponseBodyAsString());
      }
    }
  }

  private URL joinUrls(URL root, String path) {
    try {
      return new URL(root, path);
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex);
    }
  }

}
