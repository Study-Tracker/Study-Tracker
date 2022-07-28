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
