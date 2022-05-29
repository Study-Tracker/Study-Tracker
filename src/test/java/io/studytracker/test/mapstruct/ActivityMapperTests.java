package io.studytracker.test.mapstruct;

import io.studytracker.Application;
import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.mapstruct.dto.ActivitySummaryDto;
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

  @Autowired private ExampleDataGenerator exampleDataGenerator;

  @Autowired private ActivityRepository activityRepository;

  @Autowired private ActivityMapper activityMapper;

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
