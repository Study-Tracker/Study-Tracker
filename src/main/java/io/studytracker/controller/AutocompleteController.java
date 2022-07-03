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

package io.studytracker.controller;

import io.studytracker.mapstruct.dto.response.StudySlimDto;
import io.studytracker.mapstruct.dto.response.UserSlimDto;
import io.studytracker.mapstruct.mapper.StudyMapper;
import io.studytracker.mapstruct.mapper.UserMapper;
import io.studytracker.service.StudyService;
import io.studytracker.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/autocomplete")
public class AutocompleteController {

  private UserService userService;

  private UserMapper userMapper;

  private StudyService studyService;

  private StudyMapper studyMapper;

  @GetMapping("/user")
  public List<UserSlimDto> userSearch(@RequestParam("q") String keyword) {
    return userMapper.toUserSlimList(userService.search(keyword));
  }

  @GetMapping("/study")
  public List<StudySlimDto> studySearch(@RequestParam("q") String keyword) {
    return studyMapper.toStudySlimList(studyService.search(keyword));
  }

  @Autowired
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  @Autowired
  public void setUserMapper(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  @Autowired
  public void setStudyService(StudyService studyService) {
    this.studyService = studyService;
  }

  @Autowired
  public void setStudyMapper(StudyMapper studyMapper) {
    this.studyMapper = studyMapper;
  }
}
