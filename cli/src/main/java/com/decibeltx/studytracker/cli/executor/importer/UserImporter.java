package com.decibeltx.studytracker.cli.executor.importer;

import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserImporter extends RecordImporter<User> {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserImporter.class);

  @Autowired
  private UserService userService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  public UserImporter() {
    super(User.class);
  }

  @Override
  void importRecord(User user) throws Exception {
    if (userService.findByUsername(user.getUsername()).isPresent()
        || userService.findByEmail(user.getEmail()).isPresent()) {
      LOGGER.warn(String.format("A user with the username %s or email %s already exists. "
          + "Skipping record.", user.getUsername(), user.getEmail()));
    } else {
      if (user.getPassword() != null) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
      }
      this.validate(user);
      userService.create(user);
    }
  }
}
