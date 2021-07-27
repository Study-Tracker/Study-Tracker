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

package com.decibeltx.studytracker.controller.api;

import com.decibeltx.studytracker.exception.DuplicateRecordException;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.mapstruct.dto.KeywordDto;
import com.decibeltx.studytracker.mapstruct.mapper.KeywordMapper;
import com.decibeltx.studytracker.model.Keyword;
import com.decibeltx.studytracker.service.KeywordService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.Valid;
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

  @Autowired
  private KeywordMapper keywordMapper;

  @GetMapping("")
  public List<KeywordDto> findAll(@RequestParam(required = false) String category,
      @RequestParam(required = false, value = "q") String query) {
    List<Keyword> keywords;
    if (query != null && category != null) {
      keywords = keywordService.search(query, category);
    } else if (query != null) {
      keywords = keywordService.search(query);
    } else if (category != null) {
      keywords = keywordService.findByCategory(category);
    } else {
      keywords = keywordService.findAll();
    }
    return keywordMapper.toDtoList(keywords);
  }

  @GetMapping("/{id}")
  public KeywordDto findById(@PathVariable("id") Long keywordId) throws RecordNotFoundException {
    Optional<Keyword> optional = keywordService.findById(keywordId);
    if (optional.isPresent()) {
      return keywordMapper.toDto(optional.get());
    } else {
      throw new RecordNotFoundException();
    }
  }

  @GetMapping("/categories")
  public Set<String> findKeywordCategories() {
    return keywordService.findAllCategories();
  }

  @PostMapping("")
  public HttpEntity<KeywordDto> create(@RequestBody @Valid KeywordDto dto) {
    LOGGER.info("Creating keyword");
    LOGGER.info(dto.toString());
    Keyword keyword = keywordMapper.fromDto(dto);
    Optional<Keyword> optional = keywordService
        .findByKeywordAndCategory(keyword.getKeyword(), keyword.getCategory());
    if (optional.isPresent()) {
      throw new DuplicateRecordException(String.format("Keyword already exists: %s %s",
          keyword.getCategory(), keyword.getKeyword()));
    }
    keywordService.create(keyword);
    return new ResponseEntity<>(keywordMapper.toDto(keyword), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<KeywordDto> update(@PathVariable("id") Long id,
      @RequestBody @Valid KeywordDto dto) {
    LOGGER.info("Updating keyword");
    LOGGER.info(dto.toString());
    Keyword updated = keywordMapper.fromDto(dto);
    Optional<Keyword> optional = keywordService
        .findByKeywordAndCategory(updated.getKeyword(), updated.getCategory());
    if (optional.isPresent()) {
      Keyword keyword = optional.get();
      if (!keyword.getId().equals(id)) {
        throw new DuplicateRecordException(String.format("Keyword already exists: %s %s",
            updated.getCategory(), updated.getKeyword()));
      }
    }
    keywordService.update(updated);
    return new ResponseEntity<>(keywordMapper.toDto(updated), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> delete(@PathVariable("id") Long id) {
    LOGGER.info("Deleting assay type: " + id);
    Keyword keyword = keywordService.findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Cannot find keyword with ID: " + id));
    keywordService.delete(keyword);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
