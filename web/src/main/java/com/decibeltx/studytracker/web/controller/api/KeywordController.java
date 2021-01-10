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

package com.decibeltx.studytracker.web.controller.api;

import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.Keyword;
import com.decibeltx.studytracker.core.service.KeywordService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/keyword")
public class KeywordController {

  private static final Logger LOGGER = LoggerFactory.getLogger(KeywordController.class);

  @Autowired
  private KeywordService keywordService;

  @GetMapping("")
  public List<Keyword> findAll(@RequestParam(required = false) String category,
      @RequestParam(required = false, value = "q") String query) {
    if (query != null && category != null) {
      return keywordService.search(query, category);
    } else if (query != null) {
      return keywordService.search(query);
    } else if (category != null) {
      return keywordService.findByCategory(category);
    } else {
      return keywordService.findAll();
    }
  }

  @GetMapping("/{id}")
  public Keyword findById(@PathVariable("id") String assayId) throws RecordNotFoundException {
    Optional<Keyword> optional = keywordService.findById(assayId);
    if (optional.isPresent()) {
      return optional.get();
    } else {
      throw new RecordNotFoundException();
    }
  }

  @GetMapping("/categories")
  public Set<String> findKeywordCategories() {
    return keywordService.findAllCategories();
  }

  @PostMapping("")
  public HttpEntity<Keyword> create(@RequestBody Keyword keyword) {
    LOGGER.info("Creating keyword");
    LOGGER.info(keyword.toString());
    keywordService.create(keyword);
    return new ResponseEntity<>(keyword, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<Keyword> update(@PathVariable("id") String id, @RequestBody Keyword keyword) {
    LOGGER.info("Updating assay type");
    LOGGER.info(keyword.toString());
    keywordService.update(keyword);
    return new ResponseEntity<>(keyword, HttpStatus.CREATED);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> delete(@PathVariable("id") String id) {
    LOGGER.info("Deleting assay type: " + id);
    Keyword keyword = keywordService.findById(id).orElseThrow(RecordNotFoundException::new);
    keywordService.delete(keyword);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
