package com.decibeltx.studytracker.test.repository;

import com.decibeltx.studytracker.Application;
import com.decibeltx.studytracker.events.EventType;
import com.decibeltx.studytracker.events.util.EntityViewUtils;
import com.decibeltx.studytracker.events.util.StudyActivityUtils;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.Activity;
import com.decibeltx.studytracker.model.FileStoreFolder;
import com.decibeltx.studytracker.model.Program;
import com.decibeltx.studytracker.model.Status;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.repository.ActivityRepository;
import com.decibeltx.studytracker.repository.ELNFolderRepository;
import com.decibeltx.studytracker.repository.FileStoreFolderRepository;
import com.decibeltx.studytracker.repository.ProgramRepository;
import com.decibeltx.studytracker.repository.StudyRepository;
import com.decibeltx.studytracker.repository.UserRepository;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class ActivityRepositoryTests {

  @Autowired private UserRepository userRepository;
  @Autowired private ProgramRepository programRepository;
  @Autowired private ELNFolderRepository elnFolderRepository;
  @Autowired private FileStoreFolderRepository fileStoreFolderRepository;
  @Autowired private StudyRepository studyRepository;
  @Autowired private ActivityRepository activityRepository;

  @Before
  public void doBefore() {
    activityRepository.deleteAll();
    studyRepository.deleteAll();
    programRepository.deleteAll();
    fileStoreFolderRepository.deleteAll();
    elnFolderRepository.deleteAll();
    userRepository.deleteAll();
  }

  private void createUser() {
    User user = new User();
    user.setAdmin(false);
    user.setUsername("test");
    user.setEmail("test@email.com");
    user.setDisplayName("Joe Person");
    user.setActive(true);
    user.setPassword("password");
    user.setAttributes(Collections.singletonMap("key", "value"));
    user.setTitle("Director");
    userRepository.save(user);
  }

  private void createProgram() {

    User user = userRepository.findByUsername("test").orElseThrow(RecordNotFoundException::new);

    Program program = new Program();
    program.setActive(true);
    program.setCode("TST");
    program.setCreatedBy(user);
    program.setLastModifiedBy(user);
    program.setName("Test Program");
    program.addAttribute("key", "value");

    FileStoreFolder folder = new FileStoreFolder();
    folder.setPath("/path/to/test");
    folder.setName("test");
    folder.setUrl("http://test");
    program.setStorageFolder(folder);

    programRepository.save(program);

  }

  private void createStudy() {

    User user = userRepository.findByUsername("test").orElseThrow(RecordNotFoundException::new);
    Program program = programRepository.findByName("Test Program")
        .orElseThrow(RecordNotFoundException::new);

    Study study = new Study();
    study.setName("Test Study");
    study.setProgram(program);
    study.setCode(program.getCode() + "-10001");
    study.setDescription("This is a test");
    study.setCreatedBy(user);
    study.setLastModifiedBy(user);
    study.setOwner(user);
    study.addUser(user);
    study.setStatus(Status.ACTIVE);
    study.setStartDate(new Date());
    study.addAttribute("key", "value");
    studyRepository.save(study);

  }

  @Test
  public void newStudyActivityTest() {

    createUser();
    createProgram();
    createStudy();

    User user = userRepository.findAll().get(0);
    Study study = studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);

    Assert.assertEquals(0, activityRepository.count());
    Activity activity = StudyActivityUtils.fromNewStudy(study, user);
    activityRepository.save(activity);
    Assert.assertNotNull(activity.getId());
    Assert.assertEquals(1, activityRepository.count());

  }

  private static Map<String, Object> createStudyView(Study study) {
    Map<String, Object> view = new HashMap<>();
    view.put("id", study.getId());
    view.put("name", study.getName());
    view.put("code", study.getCode());
    view.put("externalCode", study.getExternalCode());
    view.put("program", study.getProgram().getName());
    view.put("description", study.getDescription());
    view.put("status", study.getStatus().toString());
    view.put("owner", study.getOwner().getDisplayName());
    view.put("createdBy", study.getCreatedBy().getDisplayName());
    view.put("lastModifiedBy", study.getLastModifiedBy().getDisplayName());
    view.put("createdAt", study.getCreatedAt());
    view.put("updatedAt", study.getUpdatedAt());
    view.put("legacy", study.isLegacy());
    view.put("active", study.isActive());
    view.put("keyword", study.getKeywords().stream()
        .map(EntityViewUtils::createKeywordView)
        .collect(Collectors.toSet()));
    view.put("startDate", study.getStartDate());
    view.put("endDate", study.getEndDate());
    view.put("users", study.getUsers().stream()
        .map(User::getDisplayName)
        .collect(Collectors.toSet()));
    view.put("attributes", study.getAttributes());
    if (study.getCollaborator() != null) {
      view.put("collaborator", study.getCollaborator().getLabel());
    }
    return view;
  }

  @Test
  public void studyStatusChangeTest() {

    createUser();
    createProgram();
    createStudy();

    User user = userRepository.findAll().get(0);
    Study study = studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);
    study.setStatus(Status.COMPLETE);
    studyRepository.save(study);
//    Activity activity = StudyActivityUtils.fromStudyStatusChange(study, user, Status.ACTIVE, Status.COMPLETE);

    Activity activity = new Activity();
    activity.setStudy(study);
    activity.setProgram(study.getProgram());
    activity.setEventType(EventType.STUDY_STATUS_CHANGED);
    activity.setDate(new Date());
    activity.setUser(user);
    activity.addData("study", createStudyView(study));
    activity.addData("oldStatus", Status.ACTIVE);
    activity.addData("newStatus", "COMPLETE");

    Assert.assertEquals(0, activityRepository.count());
    activityRepository.save(activity);
    Assert.assertNotNull(activity.getId());
    Assert.assertEquals(1, activityRepository.count());

    Activity created = activityRepository.findAll().get(0);
    Assert.assertEquals(EventType.STUDY_STATUS_CHANGED, created.getEventType());
    System.out.println(created.getData());

  }


}
