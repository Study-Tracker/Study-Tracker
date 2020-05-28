package com.decibeltx.studytracker.web.example;

import com.decibeltx.studytracker.core.config.UserRepositoryPopulator;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.core.service.UserService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

public class ExampleUserRepositoryPopulator implements UserRepositoryPopulator {

  @Autowired
  private UserService userService;

  @Override
  public void populateUserRepository() {
    Optional<User> optional = userService.findByAccountName("demo");
    if (!optional.isPresent()) {
      User user = new User();
      user.setAccountName("demo");
      user.setActive(true);
      user.setAdmin(true);
      user.setDisplayName("Demo User");
      user.setEmail("demo");
      userService.create(user);
    }
  }
}
