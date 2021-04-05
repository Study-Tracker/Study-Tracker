/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.decibeltx.studytracker.test.core.service;

import com.decibeltx.studytracker.Application;
import com.decibeltx.studytracker.example.ExampleDataGenerator;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.repository.UserRepository;
import com.decibeltx.studytracker.service.UserService;
import java.util.List;
import java.util.Optional;
import javax.validation.ConstraintViolationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "example"})
public class UserServiceTests {

  private static final int USER_COUNT = 3;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void findAlltest() {
    List<User> users = userService.findAll();
    Assert.assertTrue(!users.isEmpty());
    Assert.assertEquals(USER_COUNT, users.size());
    System.out.println(users.toString());
  }

  @Test
  public void findByEmailTest() {
    Optional<User> optional = userService.findByEmail("jsmith@email.com");
    Assert.assertTrue(optional.isPresent());
    Assert.assertEquals("jsmith@email.com", optional.get().getEmail());
    optional = userService.findByEmail("bad@email.com");
    Assert.assertTrue(!optional.isPresent());
  }

  @Test
  public void findByUsernameTest() {
    Optional<User> optional = userService.findByUsername("jsmith");
    Assert.assertTrue(optional.isPresent());
    Assert.assertEquals("jsmith", optional.get().getUsername());
    optional = userService.findByUsername("bad");
    Assert.assertTrue(!optional.isPresent());
  }

  @Test
  public void createNewUserTest() {
    User user = new User();
    user.setUsername("jperson");
    user.setPassword(new BCryptPasswordEncoder().encode("test"));
    user.setDisplayName("Joe Person");
    user.setEmail("jperson@email.com");
    user.setTitle("Director");
    user.setAdmin(false);
    user.setDepartment("Chemistry");
    userService.create(user);
    Assert.assertNotNull(user.getId());
    Assert.assertEquals(USER_COUNT + 1, userService.findAll().size());
  }

  @Test
  public void fieldValidationTest() {
    Exception exception = null;
    User user = new User();
    user.setUsername("jperson");
    user.setDisplayName("Joe Person");
    user.setTitle("Director");
    user.setAdmin(false);
    user.setDepartment("Chemistry");
    try {
      userService.create(user);
    } catch (Exception e) {
      exception = e;
      e.printStackTrace();
    }
    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof ConstraintViolationException);
  }

  @Test
  public void duplicateUsernameTest() {
    Assert.assertEquals(USER_COUNT, userRepository.count());
    Exception exception = null;
    User user = new User();
    user.setUsername("jsmith");
    user.setPassword(new BCryptPasswordEncoder().encode("test"));
    user.setDisplayName("Joe Smith");
    user.setEmail("jperson@email.com");
    user.setTitle("Director");
    user.setAdmin(false);
    user.setDepartment("Biology");
    try {
      userService.create(user);
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof DuplicateKeyException);
  }

  @Test
  public void userModificationTest() {
    Assert.assertEquals(USER_COUNT, userRepository.count());
    Optional<User> optional = userService.findByUsername("jsmith");
    Assert.assertTrue(optional.isPresent());
    User user = optional.get();
    user.setTitle("VP");
    userService.update(user);
    optional = userService.findByUsername("jsmith");
    Assert.assertTrue(optional.isPresent());
    Assert.assertEquals("VP", optional.get().getTitle());
    userService.delete(optional.get());
    Assert.assertEquals(USER_COUNT - 1, userRepository.count());
    optional = userService.findByUsername("jsmith");
    Assert.assertFalse(optional.isPresent());
  }

  @Test
  public void userSearchTest() {
    List<User> users = userService.search("joe");
    Assert.assertNotNull(users);
    Assert.assertEquals(1, users.size());
    Assert.assertEquals("Joe Smith", users.get(0).getDisplayName());
    System.out.println(users.toString());

    users = userService.search("frank");
    Assert.assertNotNull(users);
    Assert.assertEquals(0, users.size());
  }

  @Test
  public void activeUserCountTest() {
    long count = userService.countActiveUsers();
    Assert.assertEquals(USER_COUNT, count);
  }

}
