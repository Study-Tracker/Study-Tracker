package io.studytracker.security;

import io.studytracker.model.User;
import io.studytracker.security.AppUserDetails.AuthMethod;
import io.studytracker.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AppUserDetailsService.class);

  @Autowired private UserService userService;

  @Override
  public AppUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userService.findByEmail(username).orElse(null);
    if (user != null) {
      LOGGER.info("Loaded user details for username: {}", username);
      return new AppUserDetails(user, AuthMethod.DATABASE);
    } else {
      LOGGER.warn("Could not load user details for username: {}", username);
      throw new UsernameNotFoundException(username);
    }
  }
}
