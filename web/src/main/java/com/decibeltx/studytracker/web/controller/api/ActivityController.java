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

package com.decibeltx.studytracker.web.controller.api;

import com.decibeltx.studytracker.core.model.Activity;
import com.decibeltx.studytracker.core.service.ActivityService;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/activity")
@RestController
public class ActivityController {

  @Autowired
  private ActivityService activityService;

  @GetMapping("")
  public HttpEntity<?> getActivity(Pageable pageable, HttpServletRequest request) {
    Map<String, String[]> params = request.getParameterMap();
    if (params.containsKey("page") || params.containsKey("size")) {
      Page<Activity> page = activityService.findAll(pageable);
      return new ResponseEntity<>(page, HttpStatus.OK);
    } else if (params.containsKey("sort")) {
      List<Activity> activities = activityService.findAll(pageable.getSort());
      return new ResponseEntity<>(activities, HttpStatus.OK);
    } else {
      List<Activity> activities = activityService.findAll();
      return new ResponseEntity<>(activities, HttpStatus.OK);
    }
  }

}
