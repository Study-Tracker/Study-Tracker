package com.decibeltx.studytracker.cli.test;

import com.decibeltx.studytracker.cli.Application;
import com.decibeltx.studytracker.cli.exception.RecordImportException;
import com.decibeltx.studytracker.cli.executor.importer.KeywordImporter;
import com.decibeltx.studytracker.core.example.ExampleDataGenerator;
import com.decibeltx.studytracker.core.model.Keyword;
import com.decibeltx.studytracker.core.repository.KeywordRepository;
import java.util.Collections;
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
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.NONE)
@ActiveProfiles({"example", "test"})
public class KeywordImportTests {

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Autowired
  private KeywordImporter keywordImporter;

  @Autowired
  private KeywordRepository keywordRepository;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void newKeywordTest() throws Exception {

    Assert.assertEquals(ExampleDataGenerator.KEYWORD_COUNT, keywordRepository.count());

    Keyword keyword = new Keyword("Test", "Test");
    keywordImporter.importRecords(Collections.singletonList(keyword));

    Assert.assertEquals(ExampleDataGenerator.KEYWORD_COUNT + 1, keywordRepository.count());

  }

  @Test
  public void existingUserTest() throws Exception {
    this.newKeywordTest();
    Assert.assertEquals(ExampleDataGenerator.KEYWORD_COUNT + 1, keywordRepository.count());

    Keyword keyword = new Keyword("Test", "Test");
    Exception exception = null;

    try {
      keywordImporter.importRecords(Collections.singletonList(keyword));
    } catch (Exception e) {
      e.printStackTrace();
      exception = e;
    }

    Assert.assertNull(exception);
    Assert.assertEquals(ExampleDataGenerator.KEYWORD_COUNT + 1, keywordRepository.count());

  }

  @Test
  public void missingFieldTest() throws Exception {

    Assert.assertEquals(ExampleDataGenerator.KEYWORD_COUNT, keywordRepository.count());

    Keyword keyword = new Keyword();
    keyword.setCategory("Test");
    Exception exception = null;

    try {
      keywordImporter.importRecords(Collections.singletonList(keyword));
    } catch (Exception e) {
      e.printStackTrace();
      exception = e;
    }

    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof RecordImportException);

  }


}
