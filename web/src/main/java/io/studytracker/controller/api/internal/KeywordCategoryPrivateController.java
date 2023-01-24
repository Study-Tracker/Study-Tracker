/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.studytracker.controller.api.internal;

import io.studytracker.controller.api.AbstractKeywordCategoryController;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.form.KeywordCategoryFormDto;
import io.studytracker.mapstruct.dto.response.KeywordCategoryDetailsDto;
import io.studytracker.model.KeywordCategory;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class KeywordCategoryPrivateController extends AbstractKeywordCategoryController {

  private static final Logger LOGGER = LoggerFactory.getLogger(KeywordCategoryPrivateController.class);

  @GetMapping("")
  public List<KeywordCategoryDetailsDto> findAll() {
    return this.getKeywordCategoryMapper().toKeywordCategoryDtoList(this.getKeywordCategoryService().findAll());
  }

  @GetMapping("/{id}")
  public KeywordCategoryDetailsDto findById(@PathVariable("id") Long id) throws RecordNotFoundException {
    Optional<KeywordCategory> optional = this.getKeywordCategoryService().findById(id);
    if (optional.isPresent()) {
      return this.getKeywordCategoryMapper().toKeywordCategoryDto(optional.get());
    } else {
      throw new RecordNotFoundException();
    }
  }

  @PostMapping("")
  public HttpEntity<KeywordCategoryDetailsDto> create(@RequestBody @Valid KeywordCategoryFormDto dto) {
    LOGGER.info("Creating keyword category: {}", dto);
    KeywordCategory category = this.createNewKeywordCategory(this.getKeywordCategoryMapper().fromKeywordCategoryFormDto(dto));
    return new ResponseEntity<>(this.getKeywordCategoryMapper().toKeywordCategoryDto(category), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<KeywordCategoryDetailsDto> update(
      @PathVariable("id") Long id, @RequestBody @Valid KeywordCategoryFormDto dto) {
    LOGGER.info("Updating keyword category: {}", dto);
    KeywordCategory updated =
        this.updateExistingKeywordCategory(this.getKeywordCategoryMapper().fromKeywordCategoryFormDto(dto), id);
    return new ResponseEntity<>(this.getKeywordCategoryMapper().toKeywordCategoryDto(updated), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> delete(@PathVariable("id") Long id) {
    LOGGER.info("Deleting keyword category: {}", id);
    KeywordCategory keywordCategory =
        this.getKeywordCategoryService()
            .findById(id)
            .orElseThrow(() -> new RecordNotFoundException("Cannot find keyword category with ID: " + id));
    this.getKeywordCategoryService().delete(keywordCategory);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
