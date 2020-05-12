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

package com.decibeltx.studytracker.idbs.inventory;

import com.decibeltx.studytracker.idbs.inventory.models.InventoryObject;
import com.decibeltx.studytracker.idbs.inventory.models.InventoryResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class IdbsRestApiInventoryService implements IdbsInventoryService {

  private static final Logger LOGGER = LoggerFactory.getLogger(IdbsRestApiInventoryService.class);

  private final RestTemplate restTemplate;

  private final InventoryRestApiAuthenticationService authenticationService;

  private final URL rootUrl;

  public IdbsRestApiInventoryService(RestTemplate restTemplate,
      InventoryRestApiAuthenticationService authenticationService, URL rootUrl) {
    this.restTemplate = restTemplate;
    this.authenticationService = authenticationService;
    this.rootUrl = rootUrl;
  }

  @Override
  public List<InventoryObject> findInventoryItemsByType(String type) {
    URL url = joinUrls(rootUrl, "/rest/v1/material/virtual/query");
    String token = authenticationService.getAuthenticationToken();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", "application/json");
    headers.set("Cache-Control", "no-cache");
    headers.set("Connection", "keep-alive");
    headers.set("Content-Type", "application/json");
    headers.set("Authorization", "Bearer " + token);
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url.toString())
        .queryParam("format", "brief").queryParam("page", "0").queryParam("size", "1000")
        .queryParam("sort", "hierarchical,desc");
    String finalUrl = builder.build().toString();
    LOGGER.info(String.format(
        "Making request to IDBS Inventory service for inventory items of type %s to URL: %s", type,
        finalUrl));
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("location", Collections.emptyList());
    body.put("filter", Collections.singletonList(Collections
        .singletonMap("type", Collections.singletonList(Collections.singletonMap("is", type)))));
    body.put("itemFilter", Collections.emptyList());
    body.put("defaultFiltersOn", "true");
    body.put("text", ""); // this needs to be empty
    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
    ResponseEntity<InventoryResponse> response = restTemplate
        .exchange(finalUrl, HttpMethod.POST, request, InventoryResponse.class);
    return response.getBody().getItems();
  }

  private URL joinUrls(URL root, String path) {
    try {
      return new URL(root, path);
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex);
    }
  }

}
