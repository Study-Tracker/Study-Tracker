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
import io.studytracker.model.Keyword;
import io.studytracker.repository.KeywordRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KeywordService {

  private static final Logger LOGGER = LoggerFactory.getLogger(KeywordService.class);

  @Autowired private KeywordRepository keywordRepository;

  public Optional<Keyword> findById(Long id) {
    return keywordRepository.findById(id);
  }

  public Page<Keyword> findAll(Pageable pageable) {
    return keywordRepository.findAll(pageable);
  }

  public List<Keyword> findAll() {
    return keywordRepository.findAll();
  }

  public List<Keyword> findByKeyword(String keyword) {
    return keywordRepository.findByKeyword(keyword);
  }

  public List<Keyword> findByCategory(String category) {
    return keywordRepository.findByCategory(category);
  }

  public Optional<Keyword> findByKeywordAndCategory(String keyword, String category) {
    return keywordRepository.findByKeywordAndCategory(keyword, category);
  }

  public List<Keyword> search(String fragment) {
    return keywordRepository.search(fragment);
  }

  public List<Keyword> search(String fragment, Pageable pageable) {
    return keywordRepository.search(fragment, pageable);
  }

  public List<Keyword> search(String fragment, String category) {
    return keywordRepository.search(fragment, category);
  }

  public List<Keyword> search(String fragment, String category, Pageable pageable) {
    return keywordRepository.search(fragment, category, pageable);
  }


  @Transactional
  public Keyword create(Keyword keyword) {
    LOGGER.info("Registering new keyword: " + keyword.toString());
    Optional<Keyword> optional =
        this.findByKeywordAndCategory(keyword.getKeyword(), keyword.getCategory());
    if (optional.isPresent()) {
      throw new DuplicateRecordException(
          String.format(
              "Keyword '%s' already exists in category '%s'",
              keyword.getKeyword(), keyword.getCategory()));
    } else {
      return keywordRepository.save(keyword);
    }
  }

  @Transactional
  public Keyword update(Keyword keyword) {
    Keyword k = keywordRepository.getById(keyword.getId());
    k.setKeyword(keyword.getKeyword());
    k.setCategory(keyword.getCategory());
    return keywordRepository.save(k);
  }

  @Transactional
  public void delete(Keyword keyword) {
    keywordRepository.delete(keyword);
  }
}
