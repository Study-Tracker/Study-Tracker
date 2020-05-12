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

package com.decibeltx.studytracker.egnyte.exception;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class EgnyteExceptionHandler implements ResponseErrorHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(EgnyteExceptionHandler.class);

  private final ObjectMapper objectMapper;

  public EgnyteExceptionHandler(ObjectMapper objectMapper) {
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
      LOGGER.warn(json.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
    String errorMessage =
        json != null && json.containsKey("errorMessage") ? json.get("errorMessage") : body;
    if (errorMessage.equals("Folder already exists at this location")) {
      throw new DuplicateFolderException(errorMessage);
    } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new ObjectNotFoundException("Requested resource was not found.");
    } else if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
      throw new UnauthorizedException("You do not have permission to perform this operation.");
    } else {
      throw new EgnyteException(errorMessage);
    }
  }

}
