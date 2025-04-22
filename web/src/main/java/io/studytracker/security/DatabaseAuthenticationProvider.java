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

import io.studytracker.egnyte.exception.UnauthorizedException;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

//@Component
public class DatabaseAuthenticationProvider implements AuthenticationProvider {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DatabaseAuthenticationProvider.class);

  private final PasswordEncoder passwordEncoder;

  private final AppUserDetailsService userDetailsService;

//  @Autowired
  public DatabaseAuthenticationProvider(
      PasswordEncoder passwordEncoder, AppUserDetailsService userDetailsService) {
    this.passwordEncoder = passwordEncoder;
    this.userDetailsService = userDetailsService;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    String username = authentication.getPrincipal().toString();
    LOGGER.info("Attempting to authenticate user: " + username);
    AppUserDetails userDetails = userDetailsService.loadUserByUsername(username);

    // Check user status
    if (!userDetails.getUser().isActive() || !userDetails.isEnabled()) {
      LOGGER.warn("User is not active: " + username);
      throw new UnauthorizedException("User is not active: " + username);
    } else if (!userDetails.isCredentialsNonExpired()) {
      LOGGER.warn("User credentials have expired: " + username);
      throw new UnauthorizedException("User credentials have expired: " + username);
    } else if (!userDetails.isAccountNonLocked()) {
      LOGGER.warn("User account is locked: " + username);
      throw new UnauthorizedException("User account is locked: " + username);
    } else if (!userDetails.isAccountNonExpired()) {
      LOGGER.warn("User account has expired: " + username);
      throw new UnauthorizedException("User account has expired: " + username);
    }

    // Check credentials
    String pw =
        authentication.getCredentials() == null ? null : authentication.getCredentials().toString();
    if (passwordEncoder.matches(pw, userDetails.getPassword())) {
      LOGGER.info("User successfully logged in: {}", authentication.getPrincipal().toString());
      return new UsernamePasswordAuthenticationToken(
          userDetails.getUser().getUsername(), pw, Collections.emptyList());
    } else {
      LOGGER.warn("User failed to log in: {}", authentication.getPrincipal().toString());
      throw new BadCredentialsException("Bad password");
    }

  }

  @Override
  public boolean supports(Class<?> aClass) {
    return aClass.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
  }
}
