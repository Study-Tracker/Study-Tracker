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

package io.studytracker.controller.api.internal;

import io.studytracker.controller.api.AbstractUserController;
import io.studytracker.exception.InsufficientPrivilegesException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.form.UserFormDto;
import io.studytracker.mapstruct.dto.response.ActivityDetailsDto;
import io.studytracker.mapstruct.dto.response.UserDetailsDto;
import io.studytracker.mapstruct.dto.response.UserSummaryDto;
import io.studytracker.mapstruct.mapper.ActivityMapper;
import io.studytracker.model.PasswordResetToken;
import io.studytracker.model.User;
import io.studytracker.service.ActivityService;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RestController
@RequestMapping("/api/internal/user")
public class UserPrivateController extends AbstractUserController {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserPrivateController.class);

  @Autowired private ActivityMapper activityMapper;

  @Autowired private ActivityService activityService;


  @GetMapping("")
  public List<UserSummaryDto> getAllUsers() throws Exception {
    return this.getUserMapper().toUserSummaryList(this.getUserService().findAll());
  }

  @GetMapping("/{id}")
  public UserDetailsDto getUser(@PathVariable("id") Long id) throws Exception {
    Optional<User> optional = this.getUserService().findById(id);
    if (optional.isEmpty()) {
      throw new RecordNotFoundException("User not found: " + id);
    }
    return this.getUserMapper().toUserDetails(optional.get());
  }

  @PostMapping("/{id}/status")
  public HttpEntity<UserDetailsDto> updateUserStatus(
      @PathVariable("id") Long userId,
      @RequestParam("active") boolean active
  ) {
    User authenticatedUser = this.getAuthenticatedUser();
    if (!authenticatedUser.isAdmin()) {
      throw new InsufficientPrivilegesException(
          "You do not have permission to perform this action.");
    }

    Optional<User> optional = this.getUserService().findById(userId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException("User not found: " + userId);
    }
    User user = optional.get();
    user.setActive(active);
    this.getUserService().update(user);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/{id}/password-reset")
  public HttpEntity<?> resetUserPassword(@PathVariable("id") Long userId) {
    User authenticatedUser = this.getAuthenticatedUser();
    if (!authenticatedUser.isAdmin()) {
      throw new InsufficientPrivilegesException(
          "You do not have permission to perform this action.");
    }

    Optional<User> optional = this.getUserService().findById(userId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException("User not found: " + userId);
    }
    User user = optional.get();

    LOGGER.info("Generating password reset request for user: " + user.getEmail());
    PasswordResetToken token = this.getUserService().createPasswordResetToken(user);
    this.getEmailService().sendPasswordResetEmail(user.getEmail(), token.getToken());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/{id}/activity")
  public List<ActivityDetailsDto> getUserActivity(@PathVariable("id") Long userId) {
    Optional<User> optional = this.getUserService().findById(userId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException("User not found: " + userId);
    }
    User user = optional.get();
    return activityMapper.toActivityDetailsList(activityService.findByUser(user));
  }

  @PostMapping("")
  public HttpEntity<UserDetailsDto> createUser(@RequestBody @Valid UserFormDto dto) {
    LOGGER.info("Registering new user: " + dto.toString());
    User user = this.createNewUser(this.getUserMapper().fromUserForm(dto));
    return new ResponseEntity<>(this.getUserMapper().toUserDetails(user), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<UserDetailsDto> updateUser(
      @PathVariable("id") Long userId,
      @RequestBody @Valid UserFormDto dto
  ) {
    User user = this.updateExistingUser(this.getUserMapper().fromUserForm(dto));
    return new ResponseEntity<>(this.getUserMapper().toUserDetails(user), HttpStatus.CREATED);
  }
}
