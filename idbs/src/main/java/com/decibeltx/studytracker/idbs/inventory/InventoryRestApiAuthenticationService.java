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

import com.decibeltx.studytracker.idbs.exception.IdbsAuthenticationException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class InventoryRestApiAuthenticationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      InventoryRestApiAuthenticationService.class);

  private final RestTemplate restTemplate;

  private final URL rootUrl;

  private final String username;

  private final String password;

  private String token;

  private Date lastAuthenticated;

  public InventoryRestApiAuthenticationService(RestTemplate restTemplate, URL rootUrl,
      String username, String password) {
    this.restTemplate = restTemplate;
    this.rootUrl = rootUrl;
    this.username = username;
    this.password = password;
  }

  public String getAuthenticationToken() {
    Date now = new Date();
    if (lastAuthenticated == null || TimeUnit.MINUTES
        .convert(Math.abs(now.getTime() - lastAuthenticated.getTime()), TimeUnit.MILLISECONDS)
        > 10) {
      authenticate();
    }
    return token;
  }

  private void authenticate() {
    try {
      URL url = new URL(rootUrl, "/apilogin");
      LOGGER
          .info("Making authentication request to IDBS Inventory system at URL: " + url.toString());
      HttpHeaders headers = new HttpHeaders();
      headers.set("Accept", "application/json");
      headers.set("Cache-Control", "no-cache");
      headers.set("Content-Type", "application/x-www-form-urlencoded");
      MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      body.set("username", username);
      body.set("password", password);
      HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
      ResponseEntity<Object> response = restTemplate
          .exchange(url.toString(), HttpMethod.POST, request, Object.class);
      token = response.getHeaders().getFirst("X-AUTH-TOKEN");
      lastAuthenticated = new Date();
    } catch (Exception e) {
      e.printStackTrace();
      throw new IdbsAuthenticationException(e);
    }
    LOGGER.info("Authentication succeeded, token generated: " + token);
  }

}
