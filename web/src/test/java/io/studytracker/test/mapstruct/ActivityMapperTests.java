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

package io.studytracker.test.mapstruct;

import io.studytracker.Application;
import io.studytracker.example.ExampleDataRunner;
import io.studytracker.mapstruct.dto.response.ActivitySummaryDto;
import io.studytracker.mapstruct.mapper.ActivityMapper;
import io.studytracker.model.Activity;
import io.studytracker.repository.ActivityRepository;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "example"})
public class ActivityMapperTests {

  @Autowired private ExampleDataRunner exampleDataRunner;

  @Autowired private ActivityRepository activityRepository;

  @Autowired private ActivityMapper activityMapper;

  @Before
  public void doBefore() {
    exampleDataRunner.populateDatabase();
  }

  @Test
  public void activitySummaryTest() {
    List<Activity> programs = activityRepository.findAll();
    Assert.assertFalse(programs.isEmpty());
    List<ActivitySummaryDto> dtos = activityMapper.toActivitySummaryList(programs);
    Assert.assertNotNull(dtos);
    Assert.assertFalse(dtos.isEmpty());
    System.out.println(dtos);
  }
}
