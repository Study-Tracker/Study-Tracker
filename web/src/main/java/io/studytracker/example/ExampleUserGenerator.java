/*
 * Copyright 2019-2023 the original author or authors.
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

package io.studytracker.example;

import io.studytracker.model.User;
import io.studytracker.model.UserType;
import io.studytracker.repository.PasswordResetTokenRepository;
import io.studytracker.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class ExampleUserGenerator implements ExampleDataGenerator<User> {

  public static final int USER_COUNT = 3;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordResetTokenRepository passwordResetTokenRepository;

  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  @Override
  public List<User> generateData(Object... args) {
    List<User> users = new ArrayList<>();

    User user = new User();
    user.setPassword(encoder.encode("password"));
    user.setDisplayName("Joe Smith");
    user.setEmail("jsmith@email.com");
    user.setUsername(user.getEmail());
    user.setType(UserType.STANDARD_USER);
    user.setTitle("Director");
    user.setAdmin(false);
    user.setDepartment("Biology");
    users.add(user);

    user = new User();
    user.setPassword(encoder.encode("password"));
    user.setDisplayName("Ann Johnson");
    user.setEmail("ajohnson@email.com");
    user.setUsername(user.getEmail());
    user.setType(UserType.STANDARD_USER);
    user.setTitle("Sr. Scientist");
    user.setAdmin(false);
    user.setDepartment("Biology");
    users.add(user);

    user = new User();
    user.setPassword(encoder.encode("password"));
    user.setDisplayName("Rob Black");
    user.setEmail("rblack@email.com");
    user.setUsername(user.getEmail());
    user.setType(UserType.STANDARD_USER);
    user.setTitle("IT Admin");
    user.setAdmin(true);
    user.setDepartment("IT");
    users.add(user);

    return userRepository.saveAll(users);
  }

  @Override
  public void deleteData() {
    passwordResetTokenRepository.deleteAll();
    userRepository.deleteAll();
  }
}
