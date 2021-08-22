package com.decibeltx.studytracker.test.repository;

import com.decibeltx.studytracker.Application;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.FileStoreFolder;
import com.decibeltx.studytracker.model.Program;
import com.decibeltx.studytracker.model.Status;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.StudyCollection;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.repository.ELNFolderRepository;
import com.decibeltx.studytracker.repository.FileStoreFolderRepository;
import com.decibeltx.studytracker.repository.ProgramRepository;
import com.decibeltx.studytracker.repository.StudyCollectionRepository;
import com.decibeltx.studytracker.repository.StudyRepository;
import com.decibeltx.studytracker.repository.UserRepository;
import java.util.Collections;
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
  @Autowired private FileStoreFolderRepository fileStoreFolderRepository;
  @Autowired private StudyRepository studyRepository;
  @Autowired private StudyCollectionRepository studyCollectionRepository;

  @Before
  public void doBefore() {
    studyCollectionRepository.deleteAll();
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
    StudyCollection created = studyCollectionRepository.findById(studyCollection.getId())
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(1, created.getStudies().size());

  }

  @Test
  public void updatedStudyCollectionTest() {
    newStudyCollectionTest();
    Study study = studyRepository.findByCode("TST-10001").orElseThrow(RecordNotFoundException::new);
    StudyCollection created = studyCollectionRepository.findAll().get(0);
    StudyCollection c = studyCollectionRepository.findById(created.getId())
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(1, c.getStudies().size());
    c.removeStudy(study);
    Assert.assertEquals(0, c.getStudies().size());
    studyCollectionRepository.save(c);
    StudyCollection updated = studyCollectionRepository.findById(c.getId())
        .orElseThrow(RecordNotFoundException::new);
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
