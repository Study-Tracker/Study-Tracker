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

package com.decibeltx.studytracker.elasticsearch;

import com.decibeltx.studytracker.core.exception.StudyTrackerException;
import com.decibeltx.studytracker.core.keyword.Keyword;
import com.decibeltx.studytracker.core.keyword.KeywordService;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class ElasticsearchKeywordService implements KeywordService {

  private final RestTemplate restTemplate;
  private final URL rootUrl;

  public ElasticsearchKeywordService(URL rootUrl, RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
    this.rootUrl = rootUrl;
  }

  public ElasticsearchKeywordService(URL rootUrl) {
    this.rootUrl = rootUrl;
    this.restTemplate = new RestTemplate();
  }

  @Override
  public List<Keyword> findAll() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", "application/json");
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(null, headers);
    URL url = joinUrls(rootUrl, "_search");
    ResponseEntity<KeywordHits> response = restTemplate.exchange(url.toString(), HttpMethod.GET,
        request, KeywordHits.class);
    if (!response.getStatusCode().equals(HttpStatus.OK)) {
      throw new StudyTrackerException("Failed to fetch keyword data.");
    }
    return response.getBody().getHits();
  }

  @Override
  public Optional<Keyword> findByReferenceId(String referenceId) {
    return Optional.empty();
  }

  @Override
  public List<Keyword> findByKeyword(String keyword) {
    return null;
  }

  @Override
  public List<Keyword> findBySource(String source) {
    return null;
  }

  @Override
  public List<Keyword> findByType(String type) {
    return null;
  }

  @Override
  public List<Keyword> search(String fragment) {
    return null;
  }

  @Override
  public List<Keyword> search(String fragment, String type) {
    return null;
  }

  private URL joinUrls(URL root, String path) {
    try {
      return new URL(root, path);
    } catch (MalformedURLException e) {
      throw new StudyTrackerException(e);
    }
  }
}
