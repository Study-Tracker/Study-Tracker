package com.decibeltx.studytracker.web.example;

import com.decibeltx.studytracker.config.UserRepositoryPopulator;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.service.UserService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ExampleUserRepositoryPopulator implements UserRepositoryPopulator {

  @Autowired
  private UserService userService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public void populateUserRepository() {
    Optional<User> optional = userService.findByUsername("demo");
    if (!optional.isPresent()) {
      User user = new User();
      user.setUsername("demo");
      user.setPassword(passwordEncoder.encode("password"));
      user.setActive(true);
      user.setAdmin(true);
      user.setDisplayName("Demo User");
      user.setEmail("demo");
      userService.create(user);
    }
  }
}
