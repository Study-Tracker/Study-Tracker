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

package com.decibeltx.studytracker.web.config;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class UserAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(UserAuthenticationSuccessHandler.class);

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
      Authentication authentication
  ) throws IOException, ServletException {
    UserDetails user = (UserDetails) authentication.getPrincipal();
    LOGGER.info(String.format(
        "Successfully authenticated user %s with granted authorities: %s",
        user.getUsername(),
        user.getAuthorities().toString()
    ));
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authentication) throws IOException, ServletException {
    UserDetails user = (UserDetails) authentication.getPrincipal();
    LOGGER.info(String.format(
        "Successfully authenticated user %s with granted authorities: %s",
        user.getUsername(),
        user.getAuthorities().toString()
    ));
  }
}
