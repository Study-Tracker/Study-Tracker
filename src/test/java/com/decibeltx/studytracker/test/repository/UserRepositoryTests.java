package com.decibeltx.studytracker.test.repository;

import com.decibeltx.studytracker.Application;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.repository.ActivityRepository;
import com.decibeltx.studytracker.repository.UserRepository;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserRepositoryTests {

  @Autowired private UserRepository userRepository;
  @Autowired private ActivityRepository activityRepository;

  @Before
  public void doBefore() {
    activityRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  public void newUserTest() {

    User user = new User();
    user.setAdmin(false);
    user.setUsername("test");
    user.setEmail("test@email.com");
    user.setDisplayName("Joe Person");
    user.setActive(true);
    user.setPassword("password");
//    user.setAttributes(Collections.singletonMap("key", "value"));
    user.setTitle("Director");
    Map<String, String> attributes = new HashMap<>();
    attributes.put("key", "value");
    attributes.put("a", "b");
    user.setAttributes(attributes);

    userRepository.save(user);

    Assert.assertNotNull(user.getId());
    Assert.assertNotNull(user.getCreatedAt());

    Optional<User> optional = userRepository.findByUsername("test");
    Assert.assertTrue(optional.isPresent());
    User created = optional.get();
    Assert.assertEquals("Director", created.getTitle());

    System.out.println(user);
  }

  @Test
  public void badNewUserTest() {

    User user = new User();
    user.setAdmin(false);
    user.setUsername("test");
    user.setDisplayName("Joe Person");
    user.setActive(true);
    user.setPassword("password");
    user.setAttributes(Collections.singletonMap("key", "value"));
    user.setTitle("Director");

    Exception exception = null;
    try {
      userRepository.save(user);
    } catch (Exception e) {
      e.printStackTrace();
      exception = e;
    }

    Assert.assertNotNull(exception);

  }

  @Test
  public void updateUserTest() {
    newUserTest();
    Optional<User> optional = userRepository.findByUsername("test");
    Assert.assertTrue(optional.isPresent());
    User user = optional.get();
    user.setTitle("VP");
    userRepository.save(user);

    optional = userRepository.findByUsername("test");
    Assert.assertTrue(optional.isPresent());
    User updated = optional.get();
    Assert.assertEquals("VP", updated.getTitle());

  }

  @Test
  public void deleteUserTest() {
    Assert.assertEquals(0, userRepository.count());
    newUserTest();
    Assert.assertEquals(1, userRepository.count());
    Optional<User> optional = userRepository.findByUsername("test");
    Assert.assertTrue(optional.isPresent());
    User user = optional.get();
    userRepository.delete(user);
    Assert.assertEquals(0, userRepository.count());
  }

}
