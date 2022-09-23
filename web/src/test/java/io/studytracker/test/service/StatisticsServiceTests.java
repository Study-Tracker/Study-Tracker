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

package io.studytracker.test.service;

import io.studytracker.Application;
import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.mapstruct.dto.response.SummaryStatisticsDto;
import io.studytracker.service.StatisticsService;
import java.util.Calendar;
import java.util.Date;
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
public class StatisticsServiceTests {

  private static final long STUDY_COUNT = 6;

  private static final long ASSAY_COUNT = 2;

  private static final long PROGRAM_COUNT = 5;

  private static final long USER_COUNT = 3;

  private static final long ACTIVITY_COUNT = 13;

  @Autowired private StatisticsService statisticsService;

  @Autowired private ExampleDataGenerator exampleDataGenerator;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void currentTest() throws Exception {
    SummaryStatisticsDto summaryStatisticsDto = statisticsService.getCurrent();
    Assert.assertNotNull(summaryStatisticsDto);
    System.out.println(summaryStatisticsDto);
    Assert.assertEquals(STUDY_COUNT, summaryStatisticsDto.getStudyCount());
    Assert.assertEquals(ASSAY_COUNT, summaryStatisticsDto.getAssayCount());
    Assert.assertEquals(PROGRAM_COUNT, summaryStatisticsDto.getProgramCount());
    Assert.assertEquals(USER_COUNT, summaryStatisticsDto.getUserCount());
    Assert.assertEquals(ACTIVITY_COUNT, summaryStatisticsDto.getActivityCount());
  }

  @Test
  public void lastMonthTest() throws Exception {

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, -1);
    Date monthAgo = calendar.getTime();

    SummaryStatisticsDto summaryStatisticsDto = statisticsService.getAfterDate(monthAgo);
    Assert.assertNotNull(summaryStatisticsDto);
    System.out.println(summaryStatisticsDto);

    Assert.assertEquals(STUDY_COUNT, summaryStatisticsDto.getStudyCount());
    Assert.assertEquals(ASSAY_COUNT, summaryStatisticsDto.getAssayCount());
    Assert.assertEquals(PROGRAM_COUNT, summaryStatisticsDto.getProgramCount());
    Assert.assertEquals(USER_COUNT, summaryStatisticsDto.getUserCount());
    Assert.assertEquals(ACTIVITY_COUNT, summaryStatisticsDto.getActivityCount());
  }
}
