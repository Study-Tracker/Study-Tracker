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

package io.studytracker.controller.api;

import io.studytracker.exception.InsufficientPrivilegesException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.mapper.UserMapper;
import io.studytracker.model.PasswordResetToken;
import io.studytracker.model.User;
import io.studytracker.service.EmailService;
import io.studytracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractUserController extends AbstractApiController {

  private UserService userService;
  private EmailService emailService;
  private UserMapper userMapper;

  /**
   * Creates a new User, generates a password-reset request, and sends a welcome email.
   *
   * @param user the User to create
   * @return the created User
   */
  public User createNewUser(User user) {

    // Make sure user has appropriate privileges
    User authenticatedUser = this.getAuthenticatedUser();
    if (!authenticatedUser.isAdmin()) {
      throw new InsufficientPrivilegesException(
          "You do not have permission to perform this action.");
    }

    // Create the user
    userService.create(user);

    // Generate a password reset token and send a welcome email
    PasswordResetToken token = userService.createPasswordResetToken(user, 30);
    emailService.sendNewUserEmail(user.getEmail(), token.getToken());
    return user;
  }

  /**
   * Updates an existing user.
   *
   * @param user the User to update
   * @return the updated User
   */
  public User updateExistingUser(User user) {

    // Make sure user has appropriate privileges
    User authenticatedUser = this.getAuthenticatedUser();
    if (!authenticatedUser.isAdmin()) {
      throw new InsufficientPrivilegesException(
          "You do not have permission to perform this action.");
    }

    // Make sure the user exists
    if (!userService.exists(user.getId())) {
      throw new RecordNotFoundException("User not found.");
    }

    // Update the user
    userService.update(user);
    return userService.findById(user.getId())
        .orElseThrow(RecordNotFoundException::new);

  }

  public UserService getUserService() {
    return userService;
  }

  @Autowired
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  public EmailService getEmailService() {
    return emailService;
  }

  @Autowired
  public void setEmailService(EmailService emailService) {
    this.emailService = emailService;
  }

  public UserMapper getUserMapper() {
    return userMapper;
  }

  @Autowired
  public void setUserMapper(UserMapper userMapper) {
    this.userMapper = userMapper;
  }
}
