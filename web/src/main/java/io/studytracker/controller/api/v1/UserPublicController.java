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

package io.studytracker.controller.api.v1;

import io.studytracker.controller.api.AbstractUserController;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.UserDto;
import io.studytracker.mapstruct.dto.api.UserPayloadDto;
import io.studytracker.model.User;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserPublicController extends AbstractUserController {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserPublicController.class);

  @GetMapping("")
  public Page<UserDto> findAllUsers(Pageable pageable) {
    LOGGER.debug("Fetching all users");
    Page<User> users = this.getUserService().findAll(pageable);
    return new PageImpl<>(getUserMapper()
        .toUserApiDtoList(users.getContent()), pageable, users.getTotalElements());
  }

  @GetMapping("/{id}")
  public UserDto findUserById(@PathVariable Long id) {
    LOGGER.debug("Fetching user with id {}", id);
    User user = this.getUserService().findById(id)
        .orElseThrow(() -> new RecordNotFoundException("User not found: " + id));
    return this.getUserMapper().toUserDetailsApi(user);
  }

  @PostMapping("")
  public HttpEntity<UserDto> createUser(@Valid @RequestBody UserPayloadDto dto) {
    LOGGER.info("Creating user {}", dto);
    User newUser = this.createNewUser(this.getUserMapper().fromUserPayload(dto));
    return new ResponseEntity<>(this.getUserMapper().toUserDetailsApi(newUser), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<UserDto> updateUser(
      @PathVariable Long id,
      @Valid @RequestBody UserPayloadDto dto
  ) {
    LOGGER.info("Updating user {}", dto);
    User updatedUser = this.updateExistingUser(this.getUserMapper().fromUserPayload(dto));
    return new ResponseEntity<>(this.getUserMapper().toUserDetailsApi(updatedUser), HttpStatus.OK);
  }

}
