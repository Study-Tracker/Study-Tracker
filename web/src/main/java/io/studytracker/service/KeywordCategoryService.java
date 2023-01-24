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

package io.studytracker.service;

import io.studytracker.exception.DuplicateRecordException;
import io.studytracker.model.KeywordCategory;
import io.studytracker.repository.KeywordCategoryRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KeywordCategoryService {

  private static final Logger LOGGER = LoggerFactory.getLogger(KeywordCategoryService.class);

  @Autowired private KeywordCategoryRepository keywordCategoryRepository;

  public Optional<KeywordCategory> findById(Long id) {
    return keywordCategoryRepository.findById(id);
  }

  public Optional<KeywordCategory> findByName(String name) {
    return keywordCategoryRepository.findByName(name);
  }

  public List<KeywordCategory> findAll() {
    return keywordCategoryRepository.findAll();
  }

  @Transactional
  public KeywordCategory create(KeywordCategory keywordCategory) {
    LOGGER.info("Creating new keyword category" + keywordCategory);
    Optional<KeywordCategory> optional = this.findByName(keywordCategory.getName());
    if (optional.isPresent()) {
      throw new DuplicateRecordException(
          String.format(
              "Keyword category '%s' already exists",
              keywordCategory.getName()));
    } else {
      return keywordCategoryRepository.save(keywordCategory);
    }
  }

  @Transactional
  public KeywordCategory update(KeywordCategory keywordCategory) {
    LOGGER.info("Updating keyword category" + keywordCategory);
    KeywordCategory k = keywordCategoryRepository.getById(keywordCategory.getId());
    k.setName(keywordCategory.getName());
    return keywordCategoryRepository.save(k);
  }

  @Transactional
  public void delete(KeywordCategory keywordCategory) {
    LOGGER.info("Deleting keyword category " + keywordCategory.getName());
    keywordCategoryRepository.delete(keywordCategory);
  }


}
