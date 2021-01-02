package com.decibeltx.studytracker.cli.test;

import com.decibeltx.studytracker.cli.Application;
import com.decibeltx.studytracker.cli.argument.ImportArguments;
import com.decibeltx.studytracker.cli.executor.ImportExecutor;
import com.decibeltx.studytracker.core.example.ExampleDataGenerator;
import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.Collaborator;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.core.repository.CollaboratorRepository;
import com.decibeltx.studytracker.core.repository.ProgramRepository;
import com.decibeltx.studytracker.core.repository.UserRepository;
import com.decibeltx.studytracker.core.service.UserService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.NONE)
@ActiveProfiles({"example", "test"})
public class ImportCommandTests {

  private static final Resource EXAMPLE_DATA_FILE = new ClassPathResource("example-data.yml");

  @Autowired
  private ImportExecutor executor;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CollaboratorRepository collaboratorRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void dataImportTest() throws Exception {
    User user = userService.findByUsername("jsmith").orElseThrow(RecordNotFoundException::new);
    long count = programRepository.count();
    Assert.assertTrue(count > 0);
    ImportArguments importArguments = new ImportArguments();
    importArguments
        .setFiles(Collections.singletonList(EXAMPLE_DATA_FILE.getFile().getAbsolutePath()));
    Exception exception = null;
    try {
      executor.execute(importArguments, user);
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

    Optional<User> userOptional = userRepository.findByUsername("jperson");
    Assert.assertTrue(userOptional.isPresent());
    User newUser = userOptional.get();
    System.out.println(newUser.toString());
    Assert.assertEquals("jperson@email.com", newUser.getEmail());
    Assert.assertTrue(passwordEncoder.matches("password", newUser.getPassword()));

    Optional<Collaborator> collaboratorOptional = collaboratorRepository
        .findByLabel("CRO Corp - East");
    Assert.assertTrue(collaboratorOptional.isPresent());
    Collaborator collaborator = collaboratorOptional.get();
    Assert.assertEquals("CRO Corp", collaborator.getOrganizationName());

  }

}
