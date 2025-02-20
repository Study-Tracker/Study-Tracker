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

package io.studytracker.test.service;

import io.studytracker.Application;
import io.studytracker.example.ExampleDataRunner;
import io.studytracker.exception.DuplicateRecordException;
import io.studytracker.model.Keyword;
import io.studytracker.service.KeywordService;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "example"})
public class KeywordServiceTests {

  private static final int KEYWORD_COUNT = 7;

  @Autowired private KeywordService keywordService;

  @Autowired private ExampleDataRunner exampleDataRunner;

  @Before
  public void doBefore() {
    exampleDataRunner.populateDatabase();
  }

  @Test
  public void findTest() throws Exception {

    Optional<Keyword> optional = keywordService.findByKeywordAndCategory("AKT1", "Gene");
    Assert.assertTrue(optional.isPresent());
    optional = keywordService.findByKeywordAndCategory("AKT4", "Gene");
    Assert.assertFalse(optional.isPresent());

    List<Keyword> keywords = keywordService.findByKeyword("AKT1");
    Assert.assertEquals(1, keywords.size());
    keywords = keywordService.findByKeyword("AKT4");
    Assert.assertEquals(0, keywords.size());

    keywords = keywordService.search("AKT");
    Assert.assertEquals(3, keywords.size());
    keywords = keywordService.search("AKT", "Gene");
    Assert.assertEquals(3, keywords.size());
    keywords = keywordService.search("AKT", "Cell Line");
    Assert.assertEquals(0, keywords.size());

  }

  @Test
  public void createTest() throws Exception {
    Assert.assertEquals(KEYWORD_COUNT, keywordService.findAll().size());
    keywordService.create(new Keyword("TP53", "Gene"));
    Assert.assertEquals(KEYWORD_COUNT + 1, keywordService.findAll().size());
    Exception exception = null;
    try {
      keywordService.create(new Keyword("TP53", "Gene"));
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof DuplicateRecordException);
  }
}
