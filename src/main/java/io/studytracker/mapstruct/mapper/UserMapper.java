package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.form.UserFormDto;
import io.studytracker.mapstruct.dto.response.UserDetailsDto;
import io.studytracker.mapstruct.dto.response.UserSlimDto;
import io.studytracker.mapstruct.dto.response.UserSummaryDto;
import io.studytracker.model.User;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

  User fromUserForm(UserFormDto dto);

  // UserSummary
  User fromUserSummary(UserSummaryDto dto);

  Set<User> fromUserSummarySet(Set<UserSummaryDto> dtos);

  List<User> fromUserSummaryList(List<UserSummaryDto> dtos);

  UserSummaryDto toUserSummary(User user);

  Set<UserSummaryDto> toUserSummarySet(Set<User> users);

  List<UserSummaryDto> toUserSummaryList(List<User> users);

  // UserDetails
  User fromUserDetails(UserDetailsDto dto);

  List<User> fromUserDetailsList(List<UserDetailsDto> dtos);

  UserDetailsDto toUserDetails(User user);

  List<UserDetailsDto> toUserDetailsList(List<User> users);

  User fromUserSlim(UserSlimDto dto);

  UserSlimDto toUserSlim(User user);

  List<UserSlimDto> toUserSlimList(List<User> users);
}
