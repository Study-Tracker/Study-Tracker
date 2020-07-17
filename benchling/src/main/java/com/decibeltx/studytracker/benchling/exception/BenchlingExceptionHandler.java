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

package com.decibeltx.studytracker.benchling.exception;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class BenchlingExceptionHandler implements ResponseErrorHandler {

  private final ObjectMapper objectMapper;

  public BenchlingExceptionHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    return response.getStatusCode().series() == Series.CLIENT_ERROR
        || response.getStatusCode().series() == Series.SERVER_ERROR;
  }

  @Override
  public void handleError(ClientHttpResponse response) throws IOException {
    String body = IOUtils.toString(response.getBody(), StandardCharsets.UTF_8.name());
    Map<String, String> json = null;
    try {
      TypeReference<HashMap<String, String>> typeReference = new TypeReference<HashMap<String, String>>() {
      };
      json = objectMapper.readValue(body, typeReference);
    } catch (Exception e) {
      e.printStackTrace();
    }
    String errorMessage =
        json != null && json.containsKey("errorMessage") ? json.get("errorMessage") : body;
    if (response.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
      throw new EntityNotFoundException(errorMessage);
    } else {
      throw new BenchlingException(errorMessage);
    }
  }

}
