/*
 * Copyright 2022 the original author or authors.
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

package io.studytracker.test.web.api;

import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.model.User;
import io.studytracker.repository.UserRepository;
import io.studytracker.security.TokenUtils;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractApiControllerTests {

  private TokenUtils tokenUtils;

  private ExampleDataGenerator exampleDataGenerator;

  private UserRepository userRepository;

  private String token;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
    if (token == null) {
      String email = userRepository.findAll().stream()
          .filter(User::isAdmin)
          .findFirst()
          .get()
          .getEmail();
      token = tokenUtils.generateToken(email);
    }
  }

  public TokenUtils getTokenUtils() {
    return tokenUtils;
  }

  @Autowired
  public void setTokenUtils(TokenUtils tokenUtils) {
    this.tokenUtils = tokenUtils;
  }

  public ExampleDataGenerator getExampleDataGenerator() {
    return exampleDataGenerator;
  }

  @Autowired
  public void setExampleDataGenerator(ExampleDataGenerator exampleDataGenerator) {
    this.exampleDataGenerator = exampleDataGenerator;
  }

  public UserRepository getUserRepository() {
    return userRepository;
  }

  @Autowired
  public void setUserRepository(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
