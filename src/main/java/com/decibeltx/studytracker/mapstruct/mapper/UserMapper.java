package com.decibeltx.studytracker.mapstruct.mapper;

import com.decibeltx.studytracker.mapstruct.dto.UserDetailsDto;
import com.decibeltx.studytracker.mapstruct.dto.UserFormDto;
import com.decibeltx.studytracker.mapstruct.dto.UserSlimDto;
import com.decibeltx.studytracker.mapstruct.dto.UserSummaryDto;
import com.decibeltx.studytracker.model.User;
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
