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

package io.studytracker.controller.api;

import io.studytracker.exception.DuplicateRecordException;
import io.studytracker.mapstruct.mapper.KeywordMapper;
import io.studytracker.model.Keyword;
import io.studytracker.service.KeywordService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractKeywordController extends AbstractApiController{

  private KeywordService keywordService;

  private KeywordMapper keywordMapper;

  /**
   * Creates a new keyword.
   *
   * @param keyword the keyword to create
   * @return the created keyword
   */
  protected Keyword createNewKeyword(Keyword keyword) {

    // Check to see if the keyword already exists
    Optional<Keyword> optional =
        keywordService.findByKeywordAndCategory(keyword.getKeyword(), keyword.getCategory());
    if (optional.isPresent()) {
      throw new DuplicateRecordException(
          String.format(
              "Keyword already exists: %s %s", keyword.getCategory(), keyword.getKeyword()));
    }
    return keywordService.create(keyword);
  }

  protected Keyword updateExistingKeyword(Keyword updated, Long id) {
    Optional<Keyword> optional =
        keywordService.findByKeywordAndCategory(updated.getKeyword(), updated.getCategory());
    if (optional.isPresent()) {
      Keyword keyword = optional.get();
      if (!keyword.getId().equals(id)) {
        throw new DuplicateRecordException(
            String.format(
                "Keyword already exists: %s %s", updated.getCategory(), updated.getKeyword()));
      }
    }
    return keywordService.update(updated);
  }

  public KeywordService getKeywordService() {
    return keywordService;
  }

  @Autowired
  public void setKeywordService(KeywordService keywordService) {
    this.keywordService = keywordService;
  }

  public KeywordMapper getKeywordMapper() {
    return keywordMapper;
  }

  @Autowired
  public void setKeywordMapper(KeywordMapper keywordMapper) {
    this.keywordMapper = keywordMapper;
  }

}
