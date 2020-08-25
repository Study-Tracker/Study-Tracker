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

package com.decibeltx.studytracker.web.controller;

import com.decibeltx.studytracker.core.exception.InvalidConstraintException;
import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.core.service.UserService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

  @Autowired
  private UserService userService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @GetMapping("/user")
  public HttpEntity<?> getAuthenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = null;
    if (authentication.isAuthenticated() && !authentication.getPrincipal()
        .equals("anonymousUser")) {
      Object principal = authentication.getPrincipal();
      if (principal instanceof User) {
        user = (User) principal;
      } else {
        String username = principal.toString();
        user = userService.findByUsername(username).orElse(null);
      }
    }
    Map<String, Object> payload = new HashMap<>();
    payload.put("user", user);
    return new ResponseEntity<>(payload, HttpStatus.OK);
  }

  @GetMapping("/passwordreset")
  public String passwordReset() {
    return "index";
  }

  @PostMapping("/passwordreset")
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
