package com.decibeltx.studytracker.aws.test;

import com.decibeltx.studytracker.aws.EventBridgeService;
import com.decibeltx.studytracker.core.events.util.StudyActivityUtils;
import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.Activity;
import com.decibeltx.studytracker.core.model.EventType;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.service.EventsService;
import com.decibeltx.studytracker.core.service.StudyService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles({"example"})
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
