package com.decibeltx.studytracker.config.initialization;

import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.security.UserPasswordGenerator;
import com.decibeltx.studytracker.service.UserService;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Generates an admin user if no user records are present. A password can be provided with the
 *  {@code admin.password} property, or one will be automatically generated. A valid email should be
 *  provided with the {@code admin.email} property, to allow you to set a new password after
 *  initialization, and receive notification emails.
 */
@Component
public class AdminUserInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdminUserInitializer.class);

  @Autowired
  private Environment env;

  @Autowired
  private UserService userService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private UserPasswordGenerator passwordGenerator;

  @PostConstruct
  public void initializeAdminUser() {
    if (userService.count() > 0) {
      return;
    }
    LOGGER.info("No users present in the Study Tracker database. Initializing admin user...");
    String username = "admin";
    String password = env.containsProperty("admin.password")
        ? env.getRequiredProperty("admin.password") : passwordGenerator.generatePassword();
    String email = env.containsProperty("admin.email")
        ? env.getRequiredProperty("admin.email") : "admin@studytracker.com";
    User user = new User();
    user.setActive(true);
    user.setDisplayName("Study Tracker Admin");
    user.setUsername(username);
    user.setEmail(email);
    user.setAdmin(true);
    user.setPassword(passwordEncoder.encode(password));
    userService.create(user);
    LOGGER.info(String
        .format("Created admin user with username '%s' and password '%s'. You should change this "
            + "password from the login page as soon as possible.", username, password));
  }

}
