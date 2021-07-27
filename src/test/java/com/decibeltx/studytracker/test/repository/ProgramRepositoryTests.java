package com.decibeltx.studytracker.test.repository;

import com.decibeltx.studytracker.Application;
import com.decibeltx.studytracker.model.FileStoreFolder;
import com.decibeltx.studytracker.model.Program;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.repository.ELNFolderRepository;
import com.decibeltx.studytracker.repository.FileStoreFolderRepository;
import com.decibeltx.studytracker.repository.ProgramRepository;
import com.decibeltx.studytracker.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProgramRepositoryTests {

  @Autowired private UserRepository userRepository;
  @Autowired private ProgramRepository programRepository;
  @Autowired private ELNFolderRepository elnFolderRepository;
  @Autowired private FileStoreFolderRepository fileStoreFolderRepository;

  @Before
  public void doBefore() {
    programRepository.deleteAll();
    fileStoreFolderRepository.deleteAll();
    elnFolderRepository.deleteAll();
    userRepository.deleteAll();
  }

  private User createUser() {
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
    return user;
  }

  @Test
  public void newProgramTest() {

    User user = createUser();

    Assert.assertEquals(0, programRepository.count());
    Assert.assertEquals(0, fileStoreFolderRepository.count());
    Assert.assertEquals(0, elnFolderRepository.count());

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

    Assert.assertEquals(1, programRepository.count());
    Assert.assertEquals(1, fileStoreFolderRepository.count());
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
    Assert.assertEquals("test", createdBy.getUsername());

    FileStoreFolder programFolder = created.getStorageFolder();
    Assert.assertNotNull(programFolder.getId());
    Optional<FileStoreFolder> fileStoreFolderOptional = fileStoreFolderRepository.findById(programFolder.getId());
    Assert.assertTrue(fileStoreFolderOptional.isPresent());

    created.setStorageFolder(null);
    programRepository.save(created);
    Assert.assertEquals(0, fileStoreFolderRepository.count());

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

    Optional<Program> optional = programRepository.findByName("Test Program"); //fetch the eagerly-loaded entity
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
    Assert.assertNotNull(program2.getCreatedBy().getId()); // the ID of the reference is always present

    try {
      Assert.assertNotNull(program2.getCreatedBy().getDisplayName()); // but the attributes are not loaded because the entity is not fully loaded
    } catch (Exception e) {
      exception = e;
    }

    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof LazyInitializationException);

  }

}
