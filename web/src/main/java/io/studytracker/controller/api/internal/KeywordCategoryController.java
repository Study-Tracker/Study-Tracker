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

package io.studytracker.controller.api.internal;

import io.studytracker.exception.DuplicateRecordException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.form.KeywordCategoryFormDto;
import io.studytracker.mapstruct.dto.response.KeywordCategoryDto;
import io.studytracker.mapstruct.mapper.KeywordCategoryMapper;
import io.studytracker.model.KeywordCategory;
import io.studytracker.service.KeywordCategoryService;
import java.util.List;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/keyword-category")
public class KeywordCategoryController {

  private static final Logger LOGGER = LoggerFactory.getLogger(KeywordCategoryController.class);

  @Autowired private KeywordCategoryService categoryService;

  @Autowired private KeywordCategoryMapper categoryMapper;

  @GetMapping("")
  public List<KeywordCategoryDto> findAll() {
    return categoryMapper.toKeywordCategoryDtoList(categoryService.findAll());
  }

  @GetMapping("/{id}")
  public KeywordCategoryDto findById(@PathVariable("id") Long id) throws RecordNotFoundException {
    Optional<KeywordCategory> optional = categoryService.findById(id);
    if (optional.isPresent()) {
      return categoryMapper.toKeywordCategoryDto(optional.get());
    } else {
      throw new RecordNotFoundException();
    }
  }

  @PostMapping("")
  public HttpEntity<KeywordCategoryDto> create(@RequestBody @Valid KeywordCategoryFormDto dto) {
    LOGGER.info("Creating keyword category: {}", dto);
    KeywordCategory category = categoryMapper.fromKeywordCategoryFormDto(dto);
    Optional<KeywordCategory> optional = categoryService.findByName(category.getName());
    if (optional.isPresent()) {
      throw new DuplicateRecordException(
          String.format(
              "Keyword category already exists: %s", category.getName()));
    }
    categoryService.create(category);
    return new ResponseEntity<>(categoryMapper.toKeywordCategoryDto(category), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<KeywordCategoryDto> update(
      @PathVariable("id") Long id, @RequestBody @Valid KeywordCategoryFormDto dto) {
    LOGGER.info("Updating keyword category: {}", dto);
    KeywordCategory updated = categoryMapper.fromKeywordCategoryFormDto(dto);
    Optional<KeywordCategory> optional = categoryService.findByName(updated.getName());
    if (optional.isPresent()) {
      KeywordCategory keywordCategory = optional.get();
      if (!keywordCategory.getId().equals(id)) {
        throw new DuplicateRecordException(
            String.format("Keyword category already exists: %s", keywordCategory.getName()));
      }
    }
    categoryService.update(updated);
    return new ResponseEntity<>(categoryMapper.toKeywordCategoryDto(updated), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> delete(@PathVariable("id") Long id) {
    LOGGER.info("Deleting keyword category: {}", id);
    KeywordCategory keywordCategory =
        categoryService
            .findById(id)
            .orElseThrow(() -> new RecordNotFoundException("Cannot find keyword category with ID: " + id));
    categoryService.delete(keywordCategory);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
