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

package com.decibeltx.studytracker.controller.api;

import com.decibeltx.studytracker.controller.UserAuthenticationUtils;
import com.decibeltx.studytracker.exception.InsufficientPrivilegesException;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.mapstruct.dto.ActivityDetailsDto;
import com.decibeltx.studytracker.mapstruct.dto.UserDetailsDto;
import com.decibeltx.studytracker.mapstruct.dto.UserSummaryDto;
import com.decibeltx.studytracker.mapstruct.mapper.ActivityMapper;
import com.decibeltx.studytracker.mapstruct.mapper.UserMapper;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.service.ActivityService;
import com.decibeltx.studytracker.service.UserService;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
@RequestMapping("/api/user")
public class UserController {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private UserService userService;

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private ActivityMapper activityMapper;

  @Autowired
  private ActivityService activityService;

  @GetMapping("")
  public List<UserSummaryDto> getAllUsers() throws Exception {
    return userMapper.toUserSummaryList(userService.findAll());
  }

  @GetMapping("/{id}")
  public UserDetailsDto getUser(@PathVariable("id") String username) throws Exception {
    Optional<User> optional = userService.findByUsername(username);
    User user;
    if (optional.isPresent()) {
      user = optional.get();
    } else {
      try {
        optional = userService.findById(Long.parseLong(username));
      } catch (Exception e) {
        e.printStackTrace();
      }
      if (optional.isPresent()) {
        user = optional.get();
      } else {
        throw new RecordNotFoundException("User not found: " + username);
      }
    }
    return userMapper.toUserDetails(user);
  }

  @PostMapping("/{id}/status")
  public HttpEntity<UserDetailsDto> updateUserStatus(@PathVariable("id") Long userId,
      @RequestParam("active") boolean active) {

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User currentUser = userService.findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);
    if (!currentUser.isAdmin()) {
      throw new InsufficientPrivilegesException("You do not have permission to perform this action.");
    }

    Optional<User> optional = userService.findById(userId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException("User not found: " + userId);
    }
    User user = optional.get();
    user.setActive(active);
    userService.update(user);
    return new ResponseEntity<>(HttpStatus.OK);

  }

  @GetMapping("/{id}/activity")
  public List<ActivityDetailsDto> getUserActivity(@PathVariable("id") Long userId) {
    Optional<User> optional = userService.findById(userId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException("User not found: " + userId);
    }
    User user = optional.get();
    return activityMapper.toActivityDetailsList(activityService.findByUser(user));
  }

  @PostMapping("")
  public HttpEntity<UserDetailsDto> createUser(@RequestBody @Valid UserDetailsDto dto) {

    LOGGER.info("Registering new user: " + dto.toString());

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User currentUser = userService.findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);
    if (!currentUser.isAdmin()) {
      throw new InsufficientPrivilegesException("You do not have permission to perform this action.");
    }

    User user = userMapper.fromUserDetails(dto);
    userService.create(user);
    return new ResponseEntity<>(userMapper.toUserDetails(user), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<UserDetailsDto> updateUser(@PathVariable("id") Long userId,
      @RequestBody UserDetailsDto dto) {

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User currentUser = userService.findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);
    if (!currentUser.isAdmin()) {
      throw new InsufficientPrivilegesException("You do not have permission to perform this action.");
    }

    if (!userService.exists(userId)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    userService.update(userMapper.fromUserDetails(dto));
    User user = userService.findById(userId)
        .orElseThrow(RecordNotFoundException::new);
    return new ResponseEntity<>(userMapper.toUserDetails(user), HttpStatus.CREATED);

  }

}
