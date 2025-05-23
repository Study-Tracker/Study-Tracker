/*
 * Copyright 2019-2025 the original author or authors.
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

package io.studytracker.test.repository;

import io.studytracker.Application;
import io.studytracker.example.ExampleDataRunner;
import io.studytracker.model.User;
import io.studytracker.model.UserType;
import io.studytracker.repository.ActivityRepository;
import io.studytracker.repository.UserRepository;
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
@ActiveProfiles({"test", "example"})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserRepositoryTests {

  @Autowired private UserRepository userRepository;
  @Autowired private ActivityRepository activityRepository;
  @Autowired private ExampleDataRunner exampleDataRunner;

  @Before
  public void doBefore() {
    exampleDataRunner.clearDatabase();
  }

  @Test
  public void newUserTest() {

    User user = new User();
    user.setAdmin(false);
    user.setEmail("test@email.com");
    user.setUsername(user.getEmail());
    user.setType(UserType.STANDARD_USER);
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

    Optional<User> optional = userRepository.findByEmail("test@email.com");
    Assert.assertTrue(optional.isPresent());
    User created = optional.get();
    Assert.assertEquals("Director", created.getTitle());

    System.out.println(user);
  }

  @Test
  public void badNewUserTest() {

    User user = new User();
    user.setAdmin(false);
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
    Optional<User> optional = userRepository.findByEmail("test@email.com");
    Assert.assertTrue(optional.isPresent());
    User user = optional.get();
    user.setTitle("VP");
    userRepository.save(user);

    optional = userRepository.findByEmail("test@email.com");
    Assert.assertTrue(optional.isPresent());
    User updated = optional.get();
    Assert.assertEquals("VP", updated.getTitle());
  }

  @Test
  public void deleteUserTest() {
    Assert.assertEquals(0, userRepository.count());
    newUserTest();
    Assert.assertEquals(1, userRepository.count());
    Optional<User> optional = userRepository.findByEmail("test@email.com");
    Assert.assertTrue(optional.isPresent());
    User user = optional.get();
    userRepository.delete(user);
    Assert.assertEquals(0, userRepository.count());
  }
}
