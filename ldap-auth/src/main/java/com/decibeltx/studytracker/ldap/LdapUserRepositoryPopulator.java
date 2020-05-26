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

package com.decibeltx.studytracker.ldap;

import com.decibeltx.studytracker.core.config.UserRepositoryPopulator;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.core.service.UserService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class LdapUserRepositoryPopulator implements UserRepositoryPopulator {

  private static final Logger LOGGER = LoggerFactory.getLogger(LdapUserRepositoryPopulator.class);

  @Autowired
  private UserService userService;
  @Autowired
  private LdapUserDetailsService userDetailsService;

  @Override
  public void populateUserRepository() {
    LOGGER.info("Updating user repository from LDAP AD source...");
    int count = 0;
    for (LdapUser ldapUser : userDetailsService.loadAllUsers()) {
      Optional<User> optional = userService.findByEmail(ldapUser.getEmail());
      if (optional.isPresent()) {
        User user = optional.get();
        user.setDisplayName(ldapUser.getDisplayName());
        user.setDepartment((String) ldapUser.getAttributes().getOrDefault("department", null));
        user.setTitle((String) ldapUser.getAttributes().getOrDefault("title", null));
        userService.update(user);
      } else {
        User user = new User();
        user.setDisplayName(ldapUser.getDisplayName());
        user.setAccountName(ldapUser.getUsername());
        user.setEmail(ldapUser.getEmail());
        user.setDepartment((String) ldapUser.getAttributes().getOrDefault("department", null));
        user.setTitle((String) ldapUser.getAttributes().getOrDefault("title", null));
        userService.create(user);
      }
      count++;
    }
    LOGGER.info(String.format("Successfully updated %d user records.", count));
  }

}
