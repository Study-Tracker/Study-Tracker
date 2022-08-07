package io.studytracker.controller.api.internal.autocomplete;

import io.studytracker.mapstruct.dto.response.UserSlimDto;
import io.studytracker.mapstruct.mapper.UserMapper;
import io.studytracker.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/autocomplete/user")
public class UserAutocompleteController {

  private UserService userService;

  private UserMapper userMapper;

  @GetMapping("")
  public List<UserSlimDto> userSearch(@RequestParam("q") String keyword) {
    return userMapper.toUserSlimList(userService.search(keyword));
  }

  @Autowired
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  @Autowired
  public void setUserMapper(UserMapper userMapper) {
    this.userMapper = userMapper;
  }


}
