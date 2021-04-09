package com.decibeltx.studytracker.security;

import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.security.AppUserDetails.AuthMethod;
import com.decibeltx.studytracker.service.UserService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AppUserDetailsService.class);

  @Autowired
  private UserService userService;

  @Override
  public AppUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<User> optional = userService.findByUsername(username);
    if (optional.isPresent()) {
      LOGGER.info("Loaded user details for username: {}", username);
      return new AppUserDetails(optional.get(), AuthMethod.DATABASE);
    } else {
      LOGGER.warn("Could not load user details for username: {}", username);
      throw new UsernameNotFoundException(username);
    }
  }
}
