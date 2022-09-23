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

package io.studytracker.controller.api;

import io.studytracker.mapstruct.mapper.ActivityMapper;
import io.studytracker.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractActivityController {

  private ActivityService activityService;

  private ActivityMapper activityMapper;

  public ActivityService getActivityService() {
    return activityService;
  }

  @Autowired
  public void setActivityService(ActivityService activityService) {
    this.activityService = activityService;
  }

  public ActivityMapper getActivityMapper() {
    return activityMapper;
  }

  @Autowired
  public void setActivityMapper(ActivityMapper activityMapper) {
    this.activityMapper = activityMapper;
  }
}
