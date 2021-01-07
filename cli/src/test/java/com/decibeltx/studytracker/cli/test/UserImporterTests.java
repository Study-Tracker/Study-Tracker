package com.decibeltx.studytracker.cli.test;

import com.decibeltx.studytracker.cli.Application;
import com.decibeltx.studytracker.cli.exception.RecordImportException;
import com.decibeltx.studytracker.cli.executor.importer.UserImporter;
import com.decibeltx.studytracker.core.example.ExampleDataGenerator;
import com.decibeltx.studytracker.core.model.User;
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
public class UserImporterTests {

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Autowired
  private UserImporter userImporter;

  @Autowired
  private UserRepository userRepository;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void newUserTest() throws Exception {

    Assert.assertEquals(ExampleDataGenerator.USER_COUNT, userRepository.count());

    User user = new User();
    user.setUsername("test");
    user.setDisplayName("Test User");
    user.setEmail("testuser@email.com");

    userImporter.importRecords(Collections.singletonList(user));

    Assert.assertEquals(ExampleDataGenerator.USER_COUNT + 1, userRepository.count());
    Optional<User> optional = userRepository.findByUsername("test");
    Assert.assertTrue(optional.isPresent());
    User newUser = optional.get();
    Assert.assertEquals("test", newUser.getUsername());
    Assert.assertEquals("Test User", newUser.getDisplayName());

  }

  @Test
  public void existingUserTest() throws Exception {
    this.newUserTest();
    Assert.assertEquals(ExampleDataGenerator.USER_COUNT + 1, userRepository.count());

    User user = new User();
    user.setUsername("test");
    user.setDisplayName("Test User");
    user.setEmail("testuser@email.com");

    userImporter.importRecords(Collections.singletonList(user));

    Exception exception = null;

    try {
      userImporter.importRecords(Collections.singletonList(user));
    } catch (Exception e) {
      e.printStackTrace();
      exception = e;
    }

    Assert.assertNull(exception);
    Assert.assertEquals(ExampleDataGenerator.USER_COUNT + 1, userRepository.count());

  }

  @Test
  public void missingFieldTest() throws Exception {

    Assert.assertEquals(ExampleDataGenerator.USER_COUNT, userRepository.count());

    User user = new User();
    user.setUsername("test");
    user.setDisplayName("Test User");

    Exception exception = null;

    try {
      userImporter.importRecords(Collections.singletonList(user));
    } catch (Exception e) {
      e.printStackTrace();
      exception = e;
    }

    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof RecordImportException);

  }


}
