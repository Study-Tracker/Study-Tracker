/*
 * Copyright 2022 the original author or authors.
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

import com.auth0.jwt.exceptions.JWTVerificationException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class TokenFilter extends OncePerRequestFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(TokenFilter.class);

  @Autowired
  private AppUserDetailsService userDetailsService;

  @Autowired
  private TokenUtils tokenUtils;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String authHeader = request.getHeader("Authorization");
    LOGGER.debug("Authenticating user with Authorization header: " + authHeader);
    if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      if (token.isBlank()) {
        LOGGER.warn("Invalid token in Authorization header.");
        response.sendError(HttpServletResponse.SC_BAD_REQUEST,
            "Invalid token in Authorization header.");
      } else {
        try {
          String username = tokenUtils.validateToken(token);
          AppUserDetails userDetails = userDetailsService.loadUserByUsername(username);
          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
                  userDetails.getPassword(), userDetails.getAuthorities());
          if (SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.getContext().setAuthentication(authToken);
          }
          LOGGER.info("User {} successfully authenticated with JWT", username);
        } catch (JWTVerificationException e) {
          LOGGER.warn("Invalid token");
          response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid token.");
        }
      }
    }
    filterChain.doFilter(request, response);
  }

}
