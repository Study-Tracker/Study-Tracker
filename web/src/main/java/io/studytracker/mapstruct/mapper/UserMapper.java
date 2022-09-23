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

package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.api.UserDto;
import io.studytracker.mapstruct.dto.api.UserPayloadDto;
import io.studytracker.mapstruct.dto.form.UserFormDto;
import io.studytracker.mapstruct.dto.response.UserSlimDto;
import io.studytracker.mapstruct.dto.response.UserSummaryDto;
import io.studytracker.model.User;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

  User fromUserForm(UserFormDto dto);

  UserFormDto toUserForm(User dto);

  // UserSummary
  User fromUserSummary(UserSummaryDto dto);

  Set<User> fromUserSummarySet(Set<UserSummaryDto> dtos);

  List<User> fromUserSummaryList(List<UserSummaryDto> dtos);

  UserSummaryDto toUserSummary(User user);

  Set<UserSummaryDto> toUserSummarySet(Set<User> users);

  List<UserSummaryDto> toUserSummaryList(List<User> users);

  // UserDetails
  User fromUserDetails(io.studytracker.mapstruct.dto.response.UserDetailsDto dto);

  List<User> fromUserDetailsList(List<io.studytracker.mapstruct.dto.response.UserDetailsDto> dtos);

  io.studytracker.mapstruct.dto.response.UserDetailsDto toUserDetails(User user);

  List<io.studytracker.mapstruct.dto.response.UserDetailsDto> toUserDetailsList(List<User> users);

  User fromUserSlim(UserSlimDto dto);

  UserSlimDto toUserSlim(User user);

  List<UserSlimDto> toUserSlimList(List<User> users);

  // API
  UserDto toUserDetailsApi(User user);
  List<UserDto> toUserApiDtoList(List<User> users);
  User fromUserPayload(UserPayloadDto dto);

  UserPayloadDto toUserPayload(User user);
}
