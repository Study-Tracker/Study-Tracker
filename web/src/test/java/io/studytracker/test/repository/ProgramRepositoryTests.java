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
import io.studytracker.model.*;
import io.studytracker.repository.ELNFolderRepository;
import io.studytracker.repository.ProgramRepository;
import io.studytracker.repository.ProgramStorageFolderRepository;
import io.studytracker.repository.UserRepository;
import io.studytracker.storage.StorageDriveFolderService;
import io.studytracker.storage.StudyStorageService;
import io.studytracker.storage.StudyStorageServiceLookup;
import org.hibernate.LazyInitializationException;
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

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProgramRepositoryTests {

  @Autowired private UserRepository userRepository;
  @Autowired private ProgramRepository programRepository;
  @Autowired private ELNFolderRepository elnFolderRepository;
  @Autowired private StorageDriveFolderService storageDriveFolderService;
  @Autowired private StudyStorageServiceLookup studyStorageServiceLookup;
  @Autowired private ProgramStorageFolderRepository programStorageFolderRepository;

  @Before
  public void doBefore() {
    programRepository.deleteAll();
    elnFolderRepository.deleteAll();
    userRepository.deleteAll();
  }

  private User createUser() {
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
    return user;
  }

  private StorageDriveFolder createProgramFolder(Program program) {
    try {
      StorageDriveFolder rootFolder = storageDriveFolderService.findStudyRootFolders()
          .stream()
          .min(Comparator.comparing(StorageDriveFolder::getId))
          .orElseThrow(RecordNotFoundException::new);
      StudyStorageService studyStorageService = studyStorageServiceLookup.lookup(rootFolder)
          .orElseThrow(RecordNotFoundException::new);
      return studyStorageService.createProgramFolder(rootFolder, program);
    } catch (Exception ex) {
      throw new StudyTrackerException(ex);
    }
  }

  @Test
  public void newProgramTest() {

    User user = createUser();
    
    Assert.assertEquals(0, programRepository.count());
    Assert.assertEquals(0, programStorageFolderRepository.count());
    Assert.assertEquals(0, elnFolderRepository.count());

    Program program = new Program();
    program.setActive(true);
    program.setCode("TST");
    program.setCreatedBy(user);
    program.setLastModifiedBy(user);
    program.setName("Test Program");
    program.addAttribute("key", "value");
    program.addStorageFolder(createProgramFolder(program), true);

    programRepository.save(program);

    Assert.assertEquals(1, programRepository.count());
    Assert.assertEquals(1, programStorageFolderRepository.count());
    Assert.assertEquals(0, elnFolderRepository.count());

    Assert.assertNotNull(program.getId());

    Optional<Program> optional = programRepository.findByName("Test Program");
    Assert.assertTrue(optional.isPresent());
    Program created = optional.get();
    Assert.assertEquals("Test Program", created.getName());
    Assert.assertNotNull(created.getCreatedAt());
    Assert.assertNotNull(created.getCreatedBy());

    User createdBy = program.getCreatedBy();
    Assert.assertNotNull(createdBy);
    Assert.assertEquals("test@email.com", createdBy.getEmail());

    ProgramStorageFolder programFolder = created.getStorageFolders().stream().findFirst().get();
    Assert.assertNotNull(programFolder.getId());
    Optional<ProgramStorageFolder> fileStoreFolderOptional =
        programStorageFolderRepository.findById(programFolder.getId());
    Assert.assertTrue(fileStoreFolderOptional.isPresent());

    created.setStorageFolders(new HashSet<>());
    programRepository.save(created);
    Assert.assertEquals(0, programStorageFolderRepository.count());

    Exception exception = null;
    Program duplicate = new Program();
    duplicate.setActive(true);
    duplicate.setCode("TST");
    duplicate.setCreatedBy(user);
    duplicate.setLastModifiedBy(user);
    duplicate.setName("Test Program");
    duplicate.setAttributes(Collections.singletonMap("key", "value"));
    try {
      programRepository.save(duplicate);
    } catch (Exception e) {
      exception = e;
    }

    Assert.assertNotNull(exception);
    Assert.assertEquals(1, programRepository.count());
  }

  @Test
  public void findProgramsTest() {

    newProgramTest();

    Exception exception = null;
    System.out.println("FInd programs test start");

    Optional<Program> optional =
        programRepository.findByName("Test Program"); // fetch the eagerly-loaded entity
    Assert.assertTrue(optional.isPresent());
    Program program = optional.get();
    Assert.assertEquals("Test Program", program.getName());

    try {
      Assert.assertNotNull(program.getCreatedBy()); // get the loaded entity
      Assert.assertNotNull(program.getCreatedBy().getDisplayName()); // this will not throw an error
    } catch (Exception e) {
      exception = e;
      e.printStackTrace();
    }

    Assert.assertNull(exception);

    List<Program> another = programRepository.findAll(); // get the lazy-loaded entity
    Assert.assertFalse(another.isEmpty());
    Program program2 = another.get(0);

    Assert.assertNotNull(program2.getCreatedBy()); // the reference is there, but not loaded
    Assert.assertNotNull(
        program2.getCreatedBy().getId()); // the ID of the reference is always present

    try {
      Assert.assertNotNull(
          program2
              .getCreatedBy()
              .getDisplayName()); // but the attributes are not loaded because the entity is not
                                  // fully loaded
    } catch (Exception e) {
      exception = e;
    }

    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof LazyInitializationException);
  }
}
