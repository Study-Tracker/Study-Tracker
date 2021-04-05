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

package com.decibeltx.studytracker.config;

import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.service.UserService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class UserServiceAuditor implements AuditorAware<User> {

  @Autowired
  private UserService userService;

  @Override
  public Optional<User> getCurrentAuditor() {
    User user = null;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()
        && !authentication.getPrincipal().toString().equals("anonymousUser")) {
      String username;
      if (authentication instanceof UsernamePasswordAuthenticationToken) {
        username = authentication.getName();
      } else {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        username = userDetails.getUsername();
      }
      user = userService.findByUsername(username).orElse(null);
    }
    return Optional.ofNullable(user);
  }
}
