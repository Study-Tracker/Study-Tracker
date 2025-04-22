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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class UserAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(UserAuthenticationSuccessHandler.class);

  private void logAuthenticationSuccess(Object principal) {
    String username;
    if (principal instanceof DefaultSaml2AuthenticatedPrincipal) {
      DefaultSaml2AuthenticatedPrincipal samlPrincipal =
          (DefaultSaml2AuthenticatedPrincipal) principal;
      username = samlPrincipal.getName();
    } else {
      UserDetails user = (UserDetails) principal;
      username = user.getUsername();
    }
    LOGGER.info("Successfully authenticated user {}", username);
  }

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse,
      Authentication authentication)
      throws IOException, ServletException {
    logAuthenticationSuccess(authentication.getPrincipal());
  }

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authentication)
      throws IOException, ServletException {
    logAuthenticationSuccess(authentication.getPrincipal());
  }
}
