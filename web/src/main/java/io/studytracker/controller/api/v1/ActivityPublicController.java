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

import io.studytracker.controller.api.AbstractActivityController;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.ActivityDto;
import io.studytracker.model.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/activity")
public class ActivityPublicController extends AbstractActivityController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActivityPublicController.class);

  @GetMapping("")
  public Page<ActivityDto> findAllActivity(Pageable pageable) {
    LOGGER.debug("Fetching all activities");
    Page<Activity> page = this.getActivityService().findAll(pageable);
    return new PageImpl<>(this.getActivityMapper()
        .toActivityDtoList(page.getContent()), pageable, page.getTotalElements());
  }

  @GetMapping("/{id}")
  public ActivityDto findById(@PathVariable Long id) {
    LOGGER.debug("Fetching activity with id {}", id);
    Activity activity = this.getActivityService().findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Activity not found: " + id));
    return this.getActivityMapper().toActivityDto(activity);
  }

}
