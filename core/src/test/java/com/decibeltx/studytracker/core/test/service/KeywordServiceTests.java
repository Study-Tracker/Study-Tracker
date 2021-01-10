package com.decibeltx.studytracker.core.test.service;

import com.decibeltx.studytracker.core.example.ExampleDataGenerator;
import com.decibeltx.studytracker.core.exception.DuplicateRecordException;
import com.decibeltx.studytracker.core.model.Keyword;
import com.decibeltx.studytracker.core.service.KeywordService;
import com.decibeltx.studytracker.core.test.TestConfiguration;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles({"example"})
public class KeywordServiceTests {

  private static final int KEYWORD_COUNT = 7;

  @Autowired
  private KeywordService keywordService;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
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

    Assert.assertEquals(2, keywordService.findAllCategories().size());

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
