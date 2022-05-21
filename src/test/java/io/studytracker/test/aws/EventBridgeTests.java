package io.studytracker.test.aws;

import io.studytracker.Application;
import io.studytracker.aws.EventBridgeService;
import io.studytracker.events.EventType;
import io.studytracker.events.EventsService;
import io.studytracker.events.util.StudyActivityUtils;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.Activity;
import io.studytracker.model.Study;
import io.studytracker.service.StudyService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"aws-test", "example"})
public class EventBridgeTests {

  @Autowired(required = false)
  private EventsService eventsService;

  @Autowired
  private StudyService studyService;

  @Test
  public void configTest() throws Exception {
    Assert.assertNotNull(eventsService);
    Assert.assertTrue(eventsService instanceof EventBridgeService);
  }

  @Test
  public void newStudyEventTest() throws Exception {
    Study study = studyService.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);
    Activity activity = StudyActivityUtils.fromNewStudy(study, study.getCreatedBy());
    activity.setEventType(EventType.TEST_EVENT);
    eventsService.dispatchEvent(activity);
  }

}
