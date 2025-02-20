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
import io.studytracker.model.Comment;
import io.studytracker.model.ExternalLink;
import io.studytracker.model.Program;
import io.studytracker.model.Status;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.model.Study;
import io.studytracker.model.StudyConclusions;
import io.studytracker.model.StudyStorageFolder;
import io.studytracker.model.User;
import io.studytracker.model.UserType;
import io.studytracker.repository.ActivityRepository;
import io.studytracker.repository.CommentRepository;
import io.studytracker.repository.ELNFolderRepository;
import io.studytracker.repository.ExternalLinkRepository;
import io.studytracker.repository.ProgramRepository;
import io.studytracker.repository.StudyConclusionsRepository;
import io.studytracker.repository.StudyRepository;
import io.studytracker.repository.UserRepository;
import io.studytracker.service.ProgramService;
import io.studytracker.service.StudyService;
import io.studytracker.storage.StorageDriveFolderService;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StudyStorageService;
import io.studytracker.storage.StudyStorageServiceLookup;
import java.net.URL;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.EntityManagerFactory;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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
public class StudyRepositoryTests {

  @Autowired private UserRepository userRepository;
  @Autowired private ProgramRepository programRepository;
  @Autowired private ELNFolderRepository elnFolderRepository;
  @Autowired private StudyRepository studyRepository;
  @Autowired private EntityManagerFactory entityManagerFactory;
  @Autowired private CommentRepository commentRepository;
  @Autowired private ActivityRepository activityRepository;
  @Autowired private StudyConclusionsRepository studyConclusionsRepository;
  @Autowired private ExternalLinkRepository externalLinkRepository;
  @Autowired private StorageDriveFolderService storageDriveFolderService;
  @Autowired private StudyStorageServiceLookup studyStorageServiceLookup;

  @Before
  public void doBefore() {
    externalLinkRepository.deleteAll();
    studyConclusionsRepository.deleteAll();
    activityRepository.deleteAll();
    commentRepository.deleteAll();
    studyRepository.deleteAll();
    programRepository.deleteAll();
    elnFolderRepository.deleteAll();
    userRepository.deleteAll();
  }

  private void createUser() {
    User user = new User();
    user.setAdmin(false);
    user.setType(UserType.STANDARD_USER);
    user.setEmail("test@email.com");
    user.setUsername("test@email.com");
    user.setDisplayName("Joe Person");
    user.setActive(true);
    user.setPassword("password");
    user.addAttribute("key", "value");
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

  @Test
  public void newStudyTest() {

    SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
    Session session = sessionFactory.openSession();
    Transaction transaction = session.beginTransaction();
    Exception exception = null;

    try {

      createUser();
      createProgram();

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
      Assert.assertNotNull(study.getId());

      List<Study> studies = studyRepository.findAll();
      Assert.assertNotNull(studies);
      Assert.assertFalse(studies.isEmpty());

      Study created = studies.get(0);
      Assert.assertEquals("Test Study", created.getName());
      Assert.assertTrue(created.getCode().endsWith("-10001"));
      Assert.assertNotNull(created.getCreatedAt());
      Hibernate.initialize(created.getOwner());
      User owner = created.getOwner();
      Assert.assertNotNull(owner);
      Assert.assertEquals("test@email.com", owner.getEmail());

      created = studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);

      Set<User> users = created.getUsers();
      Assert.assertFalse(users.isEmpty());
      User studyUser = users.stream().findFirst().orElseThrow(RecordNotFoundException::new);
      Assert.assertEquals("test@email.com", studyUser.getEmail());

      Comment comment = new Comment();
      comment.setText("This is a test");
      comment.setCreatedBy(user);
      //      comment.setStudy(created);
      //      commentRepository.save(comment);
      created.addComment(comment);
      studyRepository.save(created);

      Study updated =
          studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);
      Assert.assertFalse(updated.getComments().isEmpty());

      transaction.commit();

    } catch (Exception e) {
      exception = e;
      e.printStackTrace();
      transaction.rollback();
    } finally {
      session.close();
    }

    Assert.assertNull(exception);
  }

  @Test
  public void updateStudyTest() {

    SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
    Session session = sessionFactory.openSession();
    Transaction transaction = session.beginTransaction();
    Exception exception = null;

    try {

      newStudyTest();

      Study study =
          studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);
      Assert.assertNotNull(study.getOwner());
      User user = study.getOwner();
      Assert.assertNotNull(user);
      study.removeUser(user);
      studyRepository.save(study);

      Study updated =
          studyRepository.findByCode("TST-10001").orElseThrow(ReflectiveOperationException::new);
      Assert.assertTrue(updated.getUsers().isEmpty());
      Assert.assertEquals(1, userRepository.count());

      transaction.commit();

    } catch (Exception e) {
      exception = e;
      e.printStackTrace();
      transaction.rollback();
    } finally {
      session.close();
    }

    Assert.assertNull(exception);
  }

  @Test
  public void addConclusionsTest() {

    SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
    Session session = sessionFactory.openSession();
    Transaction transaction = session.beginTransaction();
    Exception exception = null;

    try {

      newStudyTest();

      Study study =
          studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);
      User user = userRepository.findAll().get(0);
      Assert.assertNull(study.getConclusions());
      Assert.assertEquals(0, studyConclusionsRepository.count());

      StudyConclusions conclusions = new StudyConclusions();
      conclusions.setContent("This was a success.");
      conclusions.setCreatedBy(user);
      conclusions.setLastModifiedBy(user);
      study.setConclusions(conclusions);
      studyRepository.save(study);

      Study updated =
          studyRepository.findById(study.getId()).orElseThrow(RecordNotFoundException::new);
      Assert.assertNotNull(updated.getConclusions());

      conclusions = updated.getConclusions();
      Assert.assertNotNull(conclusions.getId());
      Assert.assertEquals(1, studyConclusionsRepository.count());

      updated.setConclusions(null);
      studyRepository.save(updated);

      updated = studyRepository.findById(study.getId()).orElseThrow(RecordNotFoundException::new);
      Assert.assertNull(updated.getConclusions());
      Assert.assertEquals(0, studyConclusionsRepository.count());

      transaction.commit();

    } catch (Exception e) {
      exception = e;
      e.printStackTrace();
      transaction.rollback();
    } finally {
      session.close();
    }

    Assert.assertNull(exception);
  }

  @Test
  public void updatingUsersTest() {

    SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
    Session session = sessionFactory.openSession();
    Transaction transaction = session.beginTransaction();
    Exception exception = null;

    try {

      newStudyTest();

      User user = new User();
      user.setAdmin(false);
      user.setEmail("jperson@email.com");
      user.setUsername(user.getEmail());
      user.setType(UserType.STANDARD_USER);
      user.setDisplayName("Joe Person");
      user.setActive(true);
      user.setPassword("password");
      user.addAttribute("key", "value");
      user.setTitle("Assistant");
      userRepository.save(user);

      Study study =
          studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);
      User owner = study.getOwner();
      User otherUser =
          userRepository.findByEmail("jperson@email.com").orElseThrow(RecordNotFoundException::new);

      study.addUser(otherUser);
      study.addUser(owner);
      studyRepository.save(study);

      Study updated =
          studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);
      Assert.assertEquals(2, updated.getUsers().size());

      transaction.commit();

    } catch (Exception e) {
      exception = e;
      e.printStackTrace();
      transaction.rollback();
    } finally {
      session.close();
    }

    Assert.assertNull(exception);
  }

  @Test
  public void updatingCommentsTest() {

    SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
    Session session = sessionFactory.openSession();
    Transaction transaction = session.beginTransaction();
    Exception exception = null;

    try {

      newStudyTest();

      Study study =
          studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);
      User user = userRepository.findAll().get(0);
      Assert.assertEquals(1, study.getComments().size());

      Comment comment2 = new Comment();
      comment2.setText("This is a test");
      comment2.setCreatedBy(user);
      study.addComment(comment2);

      studyRepository.save(study);
      Study updated =
          studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);
      Assert.assertEquals(2, updated.getComments().size());
      Assert.assertEquals(2, commentRepository.count());

      transaction.commit();

    } catch (Exception e) {
      exception = e;
      e.printStackTrace();
      transaction.rollback();
    } finally {
      session.close();
    }

    Assert.assertNull(exception);
  }

  @Test
  public void findStudiesTest() {
    newStudyTest();
    System.out.println("Start find study test");

    Optional<Study> optional = studyRepository.findByCode("TST-10001");
    Assert.assertTrue(optional.isPresent());
    Study study = optional.get();
    Assert.assertNotNull(study.getProgram());
    //    Program program = study.getProgram();

    //    Exception exception = null;
    //    try {
    //      Assert.assertNotNull(program.getCreatedBy());
    //      Assert.assertNotNull(program.getCreatedBy().getDisplayName());
    //    } catch (Exception e) {
    //      e.printStackTrace();
    //      exception = e;
    //    }
    //
    //    Assert.assertNotNull(exception);
    //    Assert.assertTrue(exception instanceof LazyInitializationException);

  }

  @Test
  public void externalLinkTest() throws Exception {
    newStudyTest();

    Study study = studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);

    ExternalLink link = new ExternalLink();
    link.setLabel("Google");
    link.setUrl(new URL("https://www.google.com"));
    study.addExternalLink(link);

    studyRepository.save(study);

    List<ExternalLink> links = externalLinkRepository.findByStudyId(study.getId());
    Assert.assertFalse(links.isEmpty());
    Assert.assertEquals(1, links.size());
    ExternalLink created = links.get(0);
    Assert.assertEquals("Google", created.getLabel());
    Assert.assertNotNull(created.getStudy().getId());
    Exception exception = null;
    try {
      created.getStudy().getCode();
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof LazyInitializationException);
  }

  @Test
  public void addStorageFolderTest() throws Exception {
    newStudyTest();
    Study study = studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(0, study.getStorageFolders().size());
    List<StorageDriveFolder> studyFolders = storageDriveFolderService.findByStudy(study);
    Assert.assertTrue(studyFolders.isEmpty());

    List<StorageDriveFolder> programFolders = storageDriveFolderService.findByProgram(study.getProgram());
    Assert.assertNotNull(programFolders);
    Assert.assertFalse(programFolders.isEmpty());
    Assert.assertEquals(1, programFolders.size());
    StorageDriveFolder programFolder = programFolders.get(0);

    StudyStorageService studyStorageService = studyStorageServiceLookup.lookup(programFolder)
        .orElseThrow(RecordNotFoundException::new);
    String folderName = StudyService.generateStudyStorageFolderName(study);
    StorageFolder storageFolder = studyStorageService.createFolder(programFolder, folderName);
    StorageDriveFolder folderOptions = new StorageDriveFolder();
    folderOptions.setWriteEnabled(true);
    StorageDriveFolder studyFolder = studyStorageService.saveStorageFolderRecord(programFolder.getStorageDrive(),
        storageFolder, folderOptions);
    Assert.assertNotNull(studyFolder);
    Assert.assertNotNull(studyFolder.getId());
    study.addStorageFolder(studyFolder, true);
    studyRepository.save(study);

    // you have to refetch the record to get updated child entities
    study = studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);
    StudyStorageFolder studyStorageFolder = study.getStorageFolders().stream().findFirst().get();
    Assert.assertNotNull(studyStorageFolder);
    Assert.assertNotNull(studyStorageFolder.getId());
    Assert.assertEquals(1, study.getStorageFolders().size());
    studyFolders = storageDriveFolderService.findByStudy(study);
    Assert.assertFalse(studyFolders.isEmpty());

  }

}
