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

package io.studytracker.controller.api.internal;

import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.response.StudyCollectionSummaryDto;
import io.studytracker.mapstruct.mapper.StudyCollectionMapper;
import io.studytracker.model.StudyCollection;
import io.studytracker.model.User;
import io.studytracker.service.StudyCollectionService;
import io.studytracker.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/user/{userId}/studycollection")
public class UserStudyCollectionPrivateController {

  @Autowired private StudyCollectionService studyCollectionService;

  @Autowired private UserService userService;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private StudyCollectionMapper mapper;

  @GetMapping("")
  public List<StudyCollectionSummaryDto> getUserStudyCollections(
      @PathVariable("userId") Long userId) {
    User user = userService.findById(userId)
        .orElseThrow(() -> new RecordNotFoundException("User not found: " + userId));
    List<StudyCollection> collections = studyCollectionService.findByUser(user);
    return mapper.toSummaryDtoList(collections);
  }
}
