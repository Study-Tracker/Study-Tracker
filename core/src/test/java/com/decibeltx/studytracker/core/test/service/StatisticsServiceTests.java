package com.decibeltx.studytracker.core.test.service;

import com.decibeltx.studytracker.core.example.ExampleDataGenerator;
import com.decibeltx.studytracker.core.model.Statistics;
import com.decibeltx.studytracker.core.service.StatisticsService;
import com.decibeltx.studytracker.core.test.TestConfiguration;
import java.util.Calendar;
import java.util.Date;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles({"example"})
public class StatisticsServiceTests {

  private static final long STUDY_COUNT = 6;

  private static final long ASSAY_COUNT = 2;

  private static final long PROGRAM_COUNT = 5;

  private static final long USER_COUNT = 3;

  private static final long ACTIVITY_COUNT = 0;

  @Autowired
  private StatisticsService statisticsService;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void currentTest() throws Exception {
    Statistics statistics = statisticsService.getCurrent();
    Assert.assertNotNull(statistics);
    System.out.println(statistics.toString());
    Assert.assertEquals(STUDY_COUNT, statistics.getStudyCount());
    Assert.assertEquals(ASSAY_COUNT, statistics.getAssayCount());
    Assert.assertEquals(PROGRAM_COUNT, statistics.getProgramCount());
    Assert.assertEquals(USER_COUNT, statistics.getUserCount());
    Assert.assertEquals(ACTIVITY_COUNT, statistics.getActivityCount());
  }

  @Test
  public void lastMonthTest() throws Exception {

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, -1);
    Date monthAgo = calendar.getTime();

    Statistics statistics = statisticsService.getAfterDate(monthAgo);
    Assert.assertNotNull(statistics);
    System.out.println(statistics.toString());

    Assert.assertEquals(STUDY_COUNT, statistics.getStudyCount());
    Assert.assertEquals(ASSAY_COUNT, statistics.getAssayCount());
    Assert.assertEquals(PROGRAM_COUNT, statistics.getProgramCount());
    Assert.assertEquals(USER_COUNT, statistics.getUserCount());
    Assert.assertEquals(ACTIVITY_COUNT, statistics.getActivityCount());

  }

}
