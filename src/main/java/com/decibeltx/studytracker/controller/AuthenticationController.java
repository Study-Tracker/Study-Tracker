/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.decibeltx.studytracker.controller;

import com.decibeltx.studytracker.exception.InvalidConstraintException;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.exception.UnauthorizedException;
import com.decibeltx.studytracker.exception.UnknownUserException;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.security.AppUserDetails;
import com.decibeltx.studytracker.security.AuthCredentials;
import com.decibeltx.studytracker.service.UserService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthenticationController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

  @Autowired
  private UserService userService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AuthenticationManager authenticationManager;

  @GetMapping("/login")
  public String login() {
    return "index";
  }

  @PostMapping("/login")
  public String submitLogin(@ModelAttribute AuthCredentials credentials) {
    try {
      Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
          credentials.getUsername(), credentials.getPassword()));
      if (authentication.isAuthenticated()) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } else {
        throw new UnauthorizedException("Unauthenticated");
      }
      return "redirect:/";
    } catch (Exception e) {
      return "redirect:/login?error=Invalid username or password";
    }
  }

  @GetMapping("/auth/user")
  public HttpEntity<?> getAuthenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = null;
    if (authentication.isAuthenticated() && !authentication.getPrincipal()
        .equals("anonymousUser")) {
      Object principal = authentication.getPrincipal();
      if (principal instanceof User) {
        user = (User) principal;
        LOGGER.debug("Loaded user from principal: {}", user.getUsername());
      } else if (principal instanceof AppUserDetails) {
        AppUserDetails userDetails = (AppUserDetails) principal;
        user = userDetails.getUser();
        LOGGER.debug("Loaded user from userDetails: {}", userDetails.getUsername());
      } else {
        String username = principal.toString();
        user = userService.findByUsername(username)
            .orElseThrow(UnknownUserException::new);
        LOGGER.debug("Loaded user from username: {}", username);
      }
    } else {
      LOGGER.debug("User is anonymous");
    }
    Map<String, Object> payload = new HashMap<>();
    payload.put("user", user);
    return new ResponseEntity<>(payload, HttpStatus.OK);
  }

  @GetMapping("/auth/passwordreset")
  public String passwordReset() {
    return "index";
  }

  @PostMapping("/auth/passwordreset")
  public String updatePassword(@RequestParam String username, @RequestParam String password,
      @RequestParam String passwordAgain) {
    Optional<User> optional = userService.findByUsername(username);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException("Cannot find user: " + username);
    }
    if (!password.equals(passwordAgain)) {
      throw new InvalidConstraintException("Passwords do not match.");
    }
    User user = optional.get();
    user.setPassword(passwordEncoder.encode(password));
    user.setCredentialsExpired(false);
    userService.update(user);
    return "redirect:/login?message=Password successfully updated.";
  }

}
