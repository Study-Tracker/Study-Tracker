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

package io.studytracker.controller.api;

import io.studytracker.events.EventsService;
import io.studytracker.model.Activity;
import io.studytracker.model.User;
import io.studytracker.security.AppUserDetails;
import io.studytracker.security.AppUserDetailsService;
import io.studytracker.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public abstract class AbstractApiController {

  private AppUserDetailsService userDetailsService;

  private ActivityService activityService;

  private EventsService eventsService;

  /**
   * Returns the currently logged in user, or throws a {@link UsernameNotFoundException} if no user
   *   is logged in.
   *
   * @return the currently logged in user
   */
  protected User getAuthenticatedUser() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    try {
      AppUserDetails userDetails = userDetailsService.loadUserByUsername(username);
      return userDetails.getUser();
    } catch (Exception e) {
      throw new UsernameNotFoundException("User not found: " + username);
    }
  }

  /**
   * Saves an activity record and dispatches the corresponding event.
   *
   * @param activity the activity record to save
   */
  protected void logActivity(Activity activity) {
    activityService.create(activity);
    eventsService.dispatchEvent(activity);
  }

  public UserDetailsService getUserDetailsService() {
    return userDetailsService;
  }

  @Autowired
  public void setUserDetailsService(AppUserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  public ActivityService getActivityService() {
    return activityService;
  }

  @Autowired
  public void setActivityService(ActivityService activityService) {
    this.activityService = activityService;
  }

  public EventsService getEventsService() {
    return eventsService;
  }

  @Autowired
  public void setEventsService(EventsService eventsService) {
    this.eventsService = eventsService;
  }
}
