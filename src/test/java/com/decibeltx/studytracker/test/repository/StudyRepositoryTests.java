package com.decibeltx.studytracker.test.repository;

import com.decibeltx.studytracker.Application;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.Comment;
import com.decibeltx.studytracker.model.ExternalLink;
import com.decibeltx.studytracker.model.FileStoreFolder;
import com.decibeltx.studytracker.model.Program;
import com.decibeltx.studytracker.model.Status;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.StudyConclusions;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.repository.ActivityRepository;
import com.decibeltx.studytracker.repository.CommentRepository;
import com.decibeltx.studytracker.repository.ELNFolderRepository;
import com.decibeltx.studytracker.repository.ExternalLinkRepository;
import com.decibeltx.studytracker.repository.FileStoreFolderRepository;
import com.decibeltx.studytracker.repository.ProgramRepository;
import com.decibeltx.studytracker.repository.StudyConclusionsRepository;
import com.decibeltx.studytracker.repository.StudyRepository;
import com.decibeltx.studytracker.repository.UserRepository;
import java.net.URL;
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
  @Autowired private FileStoreFolderRepository fileStoreFolderRepository;
  @Autowired private StudyRepository studyRepository;
  @Autowired private EntityManagerFactory entityManagerFactory;
  @Autowired private CommentRepository commentRepository;
  @Autowired private ActivityRepository activityRepository;
  @Autowired private StudyConclusionsRepository studyConclusionsRepository;
  @Autowired private ExternalLinkRepository externalLinkRepository;

  @Before
  public void doBefore() {
    externalLinkRepository.deleteAll();
    studyConclusionsRepository.deleteAll();
    activityRepository.deleteAll();
    commentRepository.deleteAll();
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
    user.addAttribute("key", "value");
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

  @Test
  public void newStudyTest() {

    SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
    Session session = sessionFactory.openSession();
    Transaction transaction = session.beginTransaction();
    Exception exception = null;

    try {

      createUser();
      createProgram();

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
      Assert.assertEquals("test", owner.getUsername());

      created = studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);

      Set<User> users = created.getUsers();
      Assert.assertFalse(users.isEmpty());
      User studyUser = users.stream().findFirst().orElseThrow(RecordNotFoundException::new);
      Assert.assertEquals("test", studyUser.getUsername());

      Comment comment = new Comment();
      comment.setText("This is a test");
      comment.setCreatedBy(user);
//      comment.setStudy(created);
//      commentRepository.save(comment);
      created.addComment(comment);
      studyRepository.save(created);

      Study updated = studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);
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

      Study study = studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);
      Assert.assertNotNull(study.getOwner());
      User user = study.getOwner();
      Assert.assertNotNull(user);
      study.removeUser(user);
      studyRepository.save(study);

      Study updated = studyRepository.findByCode("TST-10001").orElseThrow(ReflectiveOperationException::new);
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

      Study study = studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);
      User user = userRepository.findAll().get(0);
      Assert.assertNull(study.getConclusions());
      Assert.assertEquals(0, studyConclusionsRepository.count());

      StudyConclusions conclusions = new StudyConclusions();
      conclusions.setContent("This was a success.");
      conclusions.setCreatedBy(user);
      conclusions.setLastModifiedBy(user);
      study.setConclusions(conclusions);
      studyRepository.save(study);

      Study updated = studyRepository.findById(study.getId()).orElseThrow(RecordNotFoundException::new);
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
      user.setUsername("jperson");
      user.setEmail("jperson@email.com");
      user.setDisplayName("Joe Person");
      user.setActive(true);
      user.setPassword("password");
      user.addAttribute("key", "value");
      user.setTitle("Assistant");
      userRepository.save(user);

      Study study = studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);
      User owner = study.getOwner();
      User otherUser = userRepository.findByUsername("jperson").orElseThrow(RecordNotFoundException::new);

      study.addUser(otherUser);
      study.addUser(owner);
      studyRepository.save(study);

      Study updated = studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);
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

      Study study = studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);
      User user = userRepository.findAll().get(0);
      Assert.assertEquals(1, study.getComments().size());

      Comment comment2 = new Comment();
      comment2.setText("This is a test");
      comment2.setCreatedBy(user);
      study.addComment(comment2);

      studyRepository.save(study);
      Study updated = studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);
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


}
