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

import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.core.service.UserService;
import com.decibeltx.studytracker.ldap.LdapUser;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

  @Autowired
  private UserService userService;

  @GetMapping("/user")
  public HttpEntity<?> getAuthenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    LdapUser ldapUser = null;
    User user = null;
    if (authentication.isAuthenticated() && !authentication.getPrincipal()
        .equals("anonymousUser")) {
      Object principal = authentication.getPrincipal();
      if (principal instanceof LdapUser) {
        ldapUser = (LdapUser) principal;
        user = userService.findByEmail(ldapUser.getEmail()).orElse(null);
      } else {
        String username = principal.toString();
        user = userService.findByAccountName(username).orElse(null);
      }
    }
    Map<String, Object> payload = new HashMap<>();
    payload.put("user", user);
    payload.put("principal", ldapUser);
    return new ResponseEntity<>(payload, HttpStatus.OK);
  }

}
