package com.decibeltx.studytracker.cli.test;

import com.decibeltx.studytracker.cli.Application;
import com.decibeltx.studytracker.cli.exception.RecordImportException;
import com.decibeltx.studytracker.cli.executor.importer.ProgramImporter;
import com.decibeltx.studytracker.core.example.ExampleDataGenerator;
import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.core.repository.ProgramRepository;
import com.decibeltx.studytracker.core.repository.UserRepository;
import java.util.Collections;
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
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.NONE)
@ActiveProfiles({"example", "test"})
public class ProgramImporterTests {

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Autowired
  private ProgramImporter programImporter;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private UserRepository userRepository;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void newProgramTest() throws Exception {

    Assert.assertEquals(ExampleDataGenerator.PROGRAM_COUNT, programRepository.count());

    User user = userRepository.findByUsername("rblack").orElseThrow(RecordNotFoundException::new);

    Program program = new Program();
    program.setName("Test program");
    program.setCode("TST");
    program.setDescription("This is a test");

    programImporter.importRecords(Collections.singletonList(program), user);

    Assert.assertEquals(ExampleDataGenerator.PROGRAM_COUNT + 1, programRepository.count());

    Optional<Program> optional = programRepository.findByName("Test program");
    Assert.assertTrue(optional.isPresent());
    Program newProgram = optional.get();
    Assert.assertEquals("Test program", newProgram.getName());
    Assert.assertEquals("TST", newProgram.getCode());
    Assert.assertNotNull(newProgram.getCreatedAt());
    Assert.assertTrue(newProgram.isActive());

  }

  @Test
  public void existingProgramTest() throws Exception {
    this.newProgramTest();
    Assert.assertEquals(ExampleDataGenerator.PROGRAM_COUNT + 1, programRepository.count());

    User user = userRepository.findByUsername("rblack").orElseThrow(RecordNotFoundException::new);

    Program program = new Program();
    program.setName("Test program");
    program.setCode("TST");
    program.setDescription("This is a test");

    Exception exception = null;

    try {
      programImporter.importRecords(Collections.singletonList(program), user);
    } catch (Exception e) {
      e.printStackTrace();
      exception = e;
    }

    Assert.assertNull(exception);
    Assert.assertEquals(ExampleDataGenerator.PROGRAM_COUNT + 1, programRepository.count());

  }

  @Test
  public void missingUserTest() throws Exception {

    Assert.assertEquals(ExampleDataGenerator.PROGRAM_COUNT, programRepository.count());

    Program program = new Program();
    program.setName("Test program");
    program.setCode("TST");
    program.setDescription("This is a test");

    Exception exception = null;

    try {
      programImporter.importRecords(Collections.singletonList(program));
    } catch (Exception e) {
      e.printStackTrace();
      exception = e;
    }

    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof RecordImportException);

  }

  @Test
  public void missingFieldTest() throws Exception {

    Assert.assertEquals(ExampleDataGenerator.PROGRAM_COUNT, programRepository.count());

    User user = userRepository.findByUsername("rblack").orElseThrow(RecordNotFoundException::new);

    Program program = new Program();
    program.setName("Test program");
    program.setDescription("This is a test");

    Exception exception = null;

    try {
      programImporter.importRecords(Collections.singletonList(program), user);
    } catch (Exception e) {
      e.printStackTrace();
      exception = e;
    }

    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof RecordImportException);

  }


}
