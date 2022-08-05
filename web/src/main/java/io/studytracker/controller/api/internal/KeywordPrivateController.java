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

import io.studytracker.controller.api.AbstractKeywordController;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.form.KeywordFormDto;
import io.studytracker.mapstruct.dto.response.KeywordDetailsDto;
import io.studytracker.model.Keyword;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/keyword")
public class KeywordPrivateController extends AbstractKeywordController {

  private static final Logger LOGGER = LoggerFactory.getLogger(KeywordPrivateController.class);

  @GetMapping("")
  public List<KeywordDetailsDto> findAll(
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false, value = "q") String query) {
    List<Keyword> keywords;
    if (query != null && categoryId != null) {
      keywords = this.getKeywordService().search(query, categoryId);
    } else if (query != null) {
      keywords = this.getKeywordService().search(query);
    } else if (categoryId != null) {
      keywords = this.getKeywordService().findByCategoryId(categoryId);
    } else {
      keywords = this.getKeywordService().findAll();
    }
    return this.getKeywordMapper().toDetailsDtoList(keywords);
  }

  @GetMapping("/{id}")
  public KeywordDetailsDto findById(@PathVariable("id") Long keywordId) throws RecordNotFoundException {
    Optional<Keyword> optional = this.getKeywordService().findById(keywordId);
    if (optional.isPresent()) {
      return this.getKeywordMapper().toDetailsDto(optional.get());
    } else {
      throw new RecordNotFoundException();
    }
  }

  @PostMapping("")
  public HttpEntity<KeywordDetailsDto> create(@RequestBody @Valid KeywordFormDto dto) {

    LOGGER.info("Creating keyword");
    LOGGER.info(dto.toString());
    Keyword keyword = this.createNewKeyword(this.getKeywordMapper().fromFormDto(dto));
    return new ResponseEntity<>(this.getKeywordMapper().toDetailsDto(keyword), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<KeywordDetailsDto> update(
      @PathVariable("id") Long id, @RequestBody @Valid KeywordDetailsDto dto) {
    LOGGER.info("Updating keyword");
    LOGGER.info(dto.toString());
    Keyword updated = this.updateExistingKeyword(this.getKeywordMapper().fromDetailsDto(dto), id);
    return new ResponseEntity<>(this.getKeywordMapper().toDetailsDto(updated), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> delete(@PathVariable("id") Long id) {
    LOGGER.info("Deleting assay type: " + id);
    Keyword keyword =
        this.getKeywordService()
            .findById(id)
            .orElseThrow(() -> new RecordNotFoundException("Cannot find keyword with ID: " + id));
    this.getKeywordService().delete(keyword);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
