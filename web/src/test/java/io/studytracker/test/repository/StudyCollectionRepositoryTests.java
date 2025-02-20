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
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.model.Program;
import io.studytracker.model.Status;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.model.Study;
import io.studytracker.model.StudyCollection;
import io.studytracker.model.User;
import io.studytracker.model.UserType;
import io.studytracker.repository.ELNFolderRepository;
import io.studytracker.repository.ProgramRepository;
import io.studytracker.repository.StudyCollectionRepository;
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
public class StudyCollectionRepositoryTests {

  @Autowired private UserRepository userRepository;
  @Autowired private ProgramRepository programRepository;
  @Autowired private ELNFolderRepository elnFolderRepository;
  @Autowired private StorageDriveFolderService storageDriveFolderService;
  @Autowired private StudyRepository studyRepository;
  @Autowired private StudyCollectionRepository studyCollectionRepository;
  @Autowired private StudyStorageServiceLookup studyStorageServiceLookup;

  @Before
  public void doBefore() {
    studyCollectionRepository.deleteAll();
    studyRepository.deleteAll();
    programRepository.deleteAll();
    elnFolderRepository.deleteAll();
    userRepository.deleteAll();
  }

  private void createUser() {
    User user = new User();
    user.setAdmin(false);
    user.setEmail("test@email.com");
    user.setUsername(user.getEmail());
    user.setType(UserType.STANDARD_USER);
    user.setDisplayName("Joe Person");
    user.setActive(true);
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
  public void newStudyCollectionTest() {

    createUser();
    createProgram();
    createStudy();

    User user = userRepository.findAll().get(0);
    Study study = studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);

    Assert.assertEquals(0, studyCollectionRepository.count());

    StudyCollection studyCollection = new StudyCollection();
    studyCollection.setCreatedBy(user);
    studyCollection.setLastModifiedBy(user);
    studyCollection.setName("Favorites");
    studyCollection.setDescription("This is a test");
    studyCollection.addStudy(study);
    studyCollectionRepository.save(studyCollection);

    Assert.assertEquals(1, studyCollectionRepository.count());
    Assert.assertNotNull(studyCollection.getId());
    StudyCollection created =
        studyCollectionRepository
            .findById(studyCollection.getId())
            .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(1, created.getStudies().size());
  }

  @Test
  public void updatedStudyCollectionTest() {
    newStudyCollectionTest();
    Study study = studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);
    StudyCollection created = studyCollectionRepository.findAll().get(0);
    StudyCollection c =
        studyCollectionRepository
            .findById(created.getId())
            .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(1, c.getStudies().size());
    c.removeStudy(study);
    Assert.assertEquals(0, c.getStudies().size());
    studyCollectionRepository.save(c);
    StudyCollection updated =
        studyCollectionRepository.findById(c.getId()).orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(0, updated.getStudies().size());
  }

  @Test
  public void deleteStudyCollectionTest() {
    newStudyCollectionTest();
    Assert.assertEquals(1, studyCollectionRepository.count());
    StudyCollection studyCollection = studyCollectionRepository.findAll().get(0);
    studyCollectionRepository.deleteById(studyCollection.getId());
    Assert.assertEquals(0, studyCollectionRepository.count());
  }
}
