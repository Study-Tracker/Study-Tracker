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

package io.studytracker.config.initialization;

import io.studytracker.config.properties.AdminProperties;
import io.studytracker.model.User;
import io.studytracker.model.UserType;
import io.studytracker.security.UserPasswordGenerator;
import io.studytracker.service.UserService;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Generates an admin user if no user records are present. A password can be provided with the
 * {@code admin.password} property, or one will be automatically generated. A valid email should be
 * provided with the {@code admin.email} property, to allow you to set a new password after
 * initialization, and receive notification emails.
 */
@Component
public class AdminUserInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdminUserInitializer.class);

  @Autowired private UserService userService;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private UserPasswordGenerator passwordGenerator;

  @Autowired private AdminProperties adminProperties;

  @PostConstruct
  public void initializeAdminUser() {
    if (userService.count() > 0) {
      return;
    }
    LOGGER.info("No users present in the Study Tracker database. Initializing admin user...");
    String password = adminProperties.getPassword() != null
            ? adminProperties.getPassword()
            : passwordGenerator.generatePassword();
    String email = adminProperties.getEmail();
    User user = new User();
    user.setActive(true);
    user.setDisplayName("Study Tracker Admin");
    user.setEmail(email);
    user.setUsername(email);
    user.setAdmin(true);
    user.setPassword(passwordEncoder.encode(password));
    user.setType(UserType.STANDARD_USER);
    userService.create(user);
    LOGGER.info(
        String.format(
            "Created admin user with username '%s' and password '%s'. You should change this "
                + "password from the login page as soon as possible.",
            email, password));
  }
}
