package com.decibeltx.studytracker.test.mapstruct;

import com.decibeltx.studytracker.Application;
import com.decibeltx.studytracker.example.ExampleDataGenerator;
import com.decibeltx.studytracker.mapstruct.dto.ActivitySummaryDto;
import com.decibeltx.studytracker.mapstruct.mapper.ActivityMapper;
import com.decibeltx.studytracker.model.Activity;
import com.decibeltx.studytracker.repository.ActivityRepository;
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

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Autowired
  private ActivityRepository activityRepository;

  @Autowired
  private ActivityMapper activityMapper;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
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
