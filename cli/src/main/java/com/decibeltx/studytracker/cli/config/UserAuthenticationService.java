package com.decibeltx.studytracker.cli.config;

import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.egnyte.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAuthenticationService {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private UserDetailsService userDetailsService;

  public User authenticateUser(String username, String password) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    if (userDetails == null) {
      throw new UnauthorizedException(
          "User is not registered in the Study Tracker database: " + username);
    }
    if (!passwordEncoder.matches(password, userDetails.getPassword())) {
      throw new UnauthorizedException("Invalid password for user: " + username);
    }
    return (User) userDetails;

  }

}
