package com.decibeltx.studytracker.cli.test;

import com.decibeltx.studytracker.cli.Application;
import com.decibeltx.studytracker.cli.ImportArguments;
import com.decibeltx.studytracker.cli.ImportExecutor;
import com.decibeltx.studytracker.core.example.ExampleDataGenerator;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.repository.ProgramRepository;
import java.util.Collections;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.NONE)
@ActiveProfiles({"example"})
public class ImportCommandTests {

  private static final Resource EXAMPLE_DATA_FILE = new ClassPathResource("example-data.yml");

  @Autowired
  private ImportExecutor executor;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Autowired
  private ProgramRepository programRepository;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void dataImportTest() throws Exception {
    long count = programRepository.count();
    Assert.assertTrue(count > 0);
    ImportArguments importArguments = new ImportArguments();
    importArguments
        .setFiles(Collections.singletonList(EXAMPLE_DATA_FILE.getFile().getAbsolutePath()));
    Exception exception = null;
    try {
      executor.execute(importArguments);
    } catch (Exception e) {
      e.printStackTrace();
      exception = e;
    }
    Assert.assertNull(exception);
    Assert.assertEquals(count + 2, programRepository.count());
    Optional<Program> optional = programRepository.findByName("Program X");
    Assert.assertTrue(optional.isPresent());
    Assert.assertTrue(optional.get().isActive());
    optional = programRepository.findByName("Program Y");
    Assert.assertTrue(optional.isPresent());
    Assert.assertFalse(optional.get().isActive());
  }

}
