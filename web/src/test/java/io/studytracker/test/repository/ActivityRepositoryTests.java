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

package io.studytracker.test.repository;

import io.studytracker.Application;
import io.studytracker.events.EventType;
import io.studytracker.events.util.EntityViewUtils;
import io.studytracker.events.util.StudyActivityUtils;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.model.Activity;
import io.studytracker.model.Program;
import io.studytracker.model.Status;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import io.studytracker.model.UserType;
import io.studytracker.repository.ActivityRepository;
import io.studytracker.repository.ELNFolderRepository;
import io.studytracker.repository.ProgramRepository;
import io.studytracker.repository.StudyRepository;
import io.studytracker.repository.UserRepository;
import io.studytracker.service.ProgramService;
import io.studytracker.storage.StorageDriveFolderService;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StudyStorageService;
import io.studytracker.storage.StudyStorageServiceLookup;
import java.util.Collections;
import java.util.Comparator;
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
//@EnableConfigurationProperties({StudyTrackerProperties.class})
public class ActivityRepositoryTests {

  @Autowired private UserRepository userRepository;
  @Autowired private ProgramRepository programRepository;
  @Autowired private ELNFolderRepository elnFolderRepository;
  @Autowired private StudyRepository studyRepository;
  @Autowired private ActivityRepository activityRepository;

  @Autowired private StorageDriveFolderService storageDriveFolderService;
  @Autowired private StudyStorageServiceLookup studyStorageServiceLookup;

  @Before
  public void doBefore() {
    activityRepository.deleteAll();
    studyRepository.deleteAll();
    programRepository.deleteAll();
    elnFolderRepository.deleteAll();
    userRepository.deleteAll();
  }

  private void createUser() {
    User user = new User();
    user.setAdmin(false);
    user.setEmail("test@email.com");
    user.setDisplayName("Joe Person");
    user.setActive(true);
    user.setUsername(user.getEmail());
    user.setType(UserType.STANDARD_USER);
    user.setPassword("password");
    user.setAttributes(Collections.singletonMap("key", "value"));
    user.setTitle("Director");
    userRepository.save(user);
  }

  private StorageDriveFolder createProgramFolder(Program program) {
    try {
      StorageDriveFolder rootFolder = storageDriveFolderService.findStudyRootFolders()
          .stream()
          .min(Comparator.comparing(StorageDriveFolder::getId))
          .orElseThrow(RecordNotFoundException::new);
      StudyStorageService studyStorageService = studyStorageServiceLookup.lookup(rootFolder)
          .orElseThrow(RecordNotFoundException::new);
      String folderName = ProgramService.generateProgramStorageFolderName(program);
      StorageFolder storageFolder = studyStorageService.createFolder(rootFolder, folderName);
      StorageDriveFolder folderOptions = new StorageDriveFolder();
      folderOptions.setWriteEnabled(true);
      return studyStorageService.saveStorageFolderRecord(rootFolder.getStorageDrive(),
          storageFolder, folderOptions);
    } catch (Exception ex) {
      throw new StudyTrackerException(ex);
    }
  }

  private void createProgram() {
    User user = userRepository.findByEmail("test@email.com").orElseThrow(RecordNotFoundException::new);
    Program program = new Program();
    program.setActive(true);
    program.setCode("TST");
    program.setCreatedBy(user);
    program.setLastModifiedBy(user);
    program.setName("Test Program");
    program.addAttribute("key", "value");
    program.addStorageFolder(createProgramFolder(program), true);
    programRepository.save(program);
  }

  private void createStudy() {

    User user = userRepository.findByEmail("test@email.com").orElseThrow(RecordNotFoundException::new);
    Program program =
        programRepository.findByName("Test Program").orElseThrow(RecordNotFoundException::new);

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
    view.put(
        "keyword",
        study.getKeywords().stream()
            .map(EntityViewUtils::createKeywordView)
            .collect(Collectors.toSet()));
    view.put("startDate", study.getStartDate());
    view.put("endDate", study.getEndDate());
    view.put(
        "users", study.getUsers().stream().map(User::getDisplayName).collect(Collectors.toSet()));
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
    //    Activity activity = StudyActivityUtils.fromStudyStatusChange(study, user, Status.ACTIVE,
    // Status.COMPLETE);

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
