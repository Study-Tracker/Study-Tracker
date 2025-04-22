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

package io.studytracker.controller;

import io.studytracker.config.properties.SingleSignOnProperties;
import io.studytracker.exception.InvalidConstraintException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.exception.UnauthorizedException;
import io.studytracker.exception.UnknownUserException;
import io.studytracker.model.PasswordResetToken;
import io.studytracker.model.User;
import io.studytracker.model.UserType;
import io.studytracker.security.ApiAuthorizationToken;
import io.studytracker.security.AppUserDetails;
import io.studytracker.security.AuthCredentials;
import io.studytracker.security.TokenUtils;
import io.studytracker.service.EmailService;
import io.studytracker.service.UserService;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthenticationController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

  @Autowired private UserService userService;

  @Autowired private TokenUtils tokenUtils;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private AuthenticationManager authenticationManager;

  @Autowired private EmailService emailService;

  @Autowired
  private SingleSignOnProperties ssoProperties;

  @PostMapping("/auth/login")
  public String submitLogin(@ModelAttribute AuthCredentials credentials) {
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
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

  @PostMapping("/auth/token")
  public HttpEntity<ApiAuthorizationToken> generateAuthToken(@RequestBody AuthCredentials credentials) {
    LOGGER.info("Processing token generation request for user: {}", credentials.getUsername());
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  credentials.getUsername(), credentials.getPassword()));
      if (authentication.isAuthenticated()) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } else {
        throw new UnauthorizedException("Unauthenticated");
      }
      ApiAuthorizationToken token = tokenUtils.generateToken(credentials.getUsername());
      return new ResponseEntity<>(token, HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
  }

  @GetMapping("/auth/user")
  public HttpEntity<?> getAuthenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = null;
    if (authentication.isAuthenticated()
        && !authentication.getPrincipal().equals("anonymousUser")) {
      Object principal = authentication.getPrincipal();
      if (principal instanceof User) {
        user = (User) principal;
        LOGGER.debug("Loaded user from principal: {}", user.getUsername());
      } else if (principal instanceof AppUserDetails) {
        AppUserDetails userDetails = (AppUserDetails) principal;
        user = userDetails.getUser();
        LOGGER.debug("Loaded user from userDetails: {}", userDetails.getUsername());
      } else if (principal instanceof DefaultSaml2AuthenticatedPrincipal) {
        DefaultSaml2AuthenticatedPrincipal samlPrincipal =
            (DefaultSaml2AuthenticatedPrincipal) principal;
        String username = samlPrincipal.getName();
        user = userService.findByUsername(username)
            .orElseThrow(() -> new UnknownUserException(username));
        LOGGER.debug("Loaded user from SAML principal: {}", username);
      } else {
        String username = principal.toString();
        user = userService.findByUsername(username)
            .orElseThrow(() -> new UnknownUserException(username));
        LOGGER.debug("Loaded user from username: {}", username);
      }
    } else {
      LOGGER.debug("User is anonymous");
    }
    Map<String, Object> payload = new HashMap<>();
    payload.put("user", user);
    return new ResponseEntity<>(payload, HttpStatus.OK);
  }

  @GetMapping("/auth/options")
  public HttpEntity<?> getAuthenticationOptions() {

    LOGGER.info("Processing authentication options request: {}", ssoProperties.toString());
    Map<String, Object> data = new LinkedHashMap<>();

    // Single sign-on
    if (StringUtils.hasText(ssoProperties.getMode())) {
      Map<String, Object> sso = new LinkedHashMap<>();
      if (ssoProperties.getMode().equals("okta-saml")
          && StringUtils.hasText(ssoProperties.getOkta().getUrl())) {
        sso.put("okta", ssoProperties.getOkta().getUrl());
      }
      data.put("sso", sso);
    }

    return new ResponseEntity<>(data, HttpStatus.OK);
  }

  @GetMapping("/auth/passwordresetrequest")
  public String passwordResetRequest() {
    return "index";
  }

  @PostMapping("/auth/passwordresetrequest")
  public String updatePasswordRequest(@RequestParam String email) {
    LOGGER.info("Received password reset request for user: {}", email);
    Optional<User> optional = userService.findByEmail(email);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException("Cannot find user with email: " + email);
    }
    User user = optional.get();
    if (user.getType() != UserType.STANDARD_USER) {
      throw new UnauthorizedException("Cannot reset password for non-standard user");
    }
    PasswordResetToken token = userService.createPasswordResetToken(user);
    emailService.sendPasswordResetEmail(user.getEmail(), token.getToken());
    return "redirect:/login?message=Password reset request successfully sent.";
  }

  @GetMapping("/auth/passwordreset")
  public String passwordReset(
      @RequestParam("token") String token, @RequestParam("email") String email) {
    boolean valid = userService.validatePasswordResetToken(email, token);
    if (valid) {
      return "index";
    } else {
      return "redirect:/login?message=The password reset token is invalid or expired. Please try again.";
    }
  }

  @PostMapping("/auth/passwordreset")
  public String updatePassword(
      @RequestParam String email,
      @RequestParam String password,
      @RequestParam String passwordAgain,
      @RequestParam String token) {
    LOGGER.info("Updating password for user: {}", email);
    Optional<User> optional = userService.findByEmail(email);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException("Cannot find user: " + email);
    }
    if (!password.equals(passwordAgain)) {
      throw new InvalidConstraintException("Passwords do not match.");
    }
    User user = optional.get();
    if (user.getType() != UserType.STANDARD_USER) {
      throw new UnauthorizedException("Cannot reset password for non-standard user");
    }
    boolean valid = userService.validatePasswordResetToken(email, token);
    if (valid) {
      userService.updatePassword(user, passwordEncoder.encode(password));
      userService.deletePasswordResetToken(token);
      return "redirect:/login?message=Password successfully updated.";
    } else {
      return "redirect:/login?message=The password reset token is invalid or expired. Please try again.";
    }
  }

}
