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
import io.studytracker.repository.KeywordRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ExampleKeywordGenerator implements ExampleDataGenerator<Keyword> {

  public static final int KEYWORD_COUNT = 7;

  @Autowired private KeywordRepository keywordRepository;

  @Override
  public List<Keyword> generateData(Object... args) {

    List<Keyword> keywords = new ArrayList<>();
    keywords.add(new Keyword("MCF7", "Cell Line"));
    keywords.add(new Keyword("HELA", "Cell Line"));
    keywords.add(new Keyword("A375", "Cell Line"));
    keywords.add(new Keyword("AKT1", "Gene"));
    keywords.add(new Keyword("AKT2", "Gene"));
    keywords.add(new Keyword("AKT3", "Gene"));
    keywords.add(new Keyword("PTEN", "Gene"));
    return keywordRepository.saveAll(keywords);

  }

  @Override
  public void deleteData() {
    keywordRepository.deleteAll();
  }
}
