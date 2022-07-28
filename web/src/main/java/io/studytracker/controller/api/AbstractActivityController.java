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
