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

package io.studytracker.example;

import io.studytracker.model.Keyword;
import io.studytracker.model.KeywordCategory;
import io.studytracker.repository.KeywordCategoryRepository;
import io.studytracker.repository.KeywordRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ExampleKeywordGenerator implements ExampleDataGenerator<Keyword> {

  public static final int KEYWORD_CATEGORY_COUNT = 2;
  public static final int KEYWORD_COUNT = 7;

  @Autowired private KeywordCategoryRepository keywordCategoryRepository;
  @Autowired private KeywordRepository keywordRepository;

  @Override
  public List<Keyword> generateData(Object... args) {

    List<KeywordCategory> categories = new ArrayList<>();
    categories.add(new KeywordCategory("Cell Line"));
    categories.add(new KeywordCategory("Gene"));
    keywordCategoryRepository.saveAll(categories);

    KeywordCategory category = categories.get(0);
    KeywordCategory category2 = categories.get(1);

    List<Keyword> keywords = new ArrayList<>();
    keywords.add(new Keyword(category, "MCF7"));
    keywords.add(new Keyword(category, "HELA"));
    keywords.add(new Keyword(category, "A375"));
    keywords.add(new Keyword(category2, "AKT1"));
    keywords.add(new Keyword(category2, "AKT2"));
    keywords.add(new Keyword(category2, "AKT3"));
    keywords.add(new Keyword(category2, "PTEN"));
    return keywordRepository.saveAll(keywords);

  }

  @Override
  public void deleteData() {
    keywordRepository.deleteAll();
    keywordCategoryRepository.deleteAll();
  }
}
