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

package com.decibeltx.studytracker.web.controller.api;

import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.Activity;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.core.service.ActivityService;
import com.decibeltx.studytracker.core.service.UserService;
import java.util.List;
import java.util.Optional;
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
@RequestMapping("/api/user")
public class UserController {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private UserService userService;

  @Autowired
  private ActivityService activityService;

  private Optional<User> findByIdentifier(String id) {
    Optional<User> optional = userService.findById(id);
    if (optional.isPresent()) {
      return optional;
    } else {
      return userService.findByUsername(id);
    }
  }

  @GetMapping("")
  public List<User> getAllUsers() throws Exception {
    return userService.findAll();
  }

  @GetMapping("/{id}")
  public User getUser(@PathVariable("id") String id) throws Exception {
    Optional<User> optional = this.findByIdentifier(id);
    if (optional.isPresent()) {
      return optional.get();
    } else {
      throw new RecordNotFoundException();
    }
  }

  @PostMapping("/{id}/status")
  public HttpEntity<?> updateUserStatus(@PathVariable("id") String userId,
      @RequestParam("active") boolean active) {
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
  public HttpEntity<List<Activity>> getUserActivity(@PathVariable("id") String userId) {
    Optional<User> optional = userService.findById(userId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException("User not found: " + userId);
    }
    User user = optional.get();
    List<Activity> activities = activityService.findByUser(user);
    return new ResponseEntity<>(activities, HttpStatus.OK);
  }

  @PostMapping("")
  public HttpEntity<User> createUser(@RequestBody User user) {
    LOGGER.info("Registering new user: " + user.toString());
    userService.create(user);
    return new ResponseEntity<>(user, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<User> updateUser(@PathVariable("id") String userId,
      @RequestBody User updatedUser) {

    Optional<User> optional = userService.findById(userId);
    if (!optional.isPresent()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    User user = optional.get();
    user.setDisplayName(updatedUser.getDisplayName());
    user.setActive(updatedUser.isActive());
    user.setAdmin(updatedUser.isAdmin());
    user.setDepartment(updatedUser.getDepartment());
    user.setTitle(updatedUser.getTitle());
    user.setAttributes(updatedUser.getAttributes());
    userService.update(user);
    return new ResponseEntity<>(user, HttpStatus.CREATED);

  }

}
