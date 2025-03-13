/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.studytracker.security;

import io.studytracker.model.User;
import io.studytracker.security.AppUserDetails.AuthMethod;
import io.studytracker.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AppUserDetailsService.class);

  @Autowired private UserService userService;

  @Override
  public AppUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userService.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Could not find user with username: " + username));
    LOGGER.debug("Loaded user details for username: {}", username);
    return new AppUserDetails(user, AuthMethod.DATABASE);
  }

  public AppUserDetails loadUserBySAML(Saml2AuthenticatedPrincipal principal) throws UsernameNotFoundException {
    String username = principal.getName();
    User user = userService.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Could not find user with username: " + username));
    LOGGER.debug("Loading user by SAMLCredentials: {}", username);
    return new AppUserDetails(user, AuthMethod.SAML);
  }

}
