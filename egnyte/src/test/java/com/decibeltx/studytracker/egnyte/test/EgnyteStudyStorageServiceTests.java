/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.decibeltx.studytracker.egnyte.test;

import com.decibeltx.studytracker.core.example.ExampleDataGenerator;
import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.AssayType;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.Status;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.core.repository.AssayRepository;
import com.decibeltx.studytracker.core.repository.AssayTypeRepository;
import com.decibeltx.studytracker.core.repository.ProgramRepository;
import com.decibeltx.studytracker.core.repository.StudyRepository;
import com.decibeltx.studytracker.core.repository.UserRepository;
import com.decibeltx.studytracker.core.storage.StorageFile;
import com.decibeltx.studytracker.core.storage.StorageFolder;
import com.decibeltx.studytracker.egnyte.EgnyteOptions;
import com.decibeltx.studytracker.egnyte.EgnyteStudyStorageService;
import com.decibeltx.studytracker.egnyte.exception.DuplicateFolderException;
import com.decibeltx.studytracker.egnyte.exception.ObjectNotFoundException;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles({"example"})
public class EgnyteStudyStorageServiceTests {

  private static final Resource TEST_FILE = new ClassPathResource("test.txt");

  @Autowired
  private EgnyteStudyStorageService storageService;

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private AssayRepository assayRepository;

  @Autowired
  private AssayTypeRepository assayTypeRepository;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Autowired
  private EgnyteOptions egnyteOptions;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void folderPathNameTest() {
    Optional<Program> optionalProgram = programRepository.findByName("Clinical Program A");
    Assert.assertTrue(optionalProgram.isPresent());
    Program program = optionalProgram.get();
    String path = storageService.getProgramFolderPath(program);
    System.out.println(path);
    Assert.assertTrue(path.endsWith("/Clinical Program A/"));

    Study study = new Study();
    study.setProgram(program);
    study.setName("Test Study");
    study.setCode(program.getCode() + "-12345");
    path = storageService.getStudyFolderPath(study);
    System.out.println(path);
    Assert.assertTrue(path.endsWith("/Clinical Program A/" + study.getCode() + " - "
        + study.getName() + "/"));

    Assay assay = new Assay();
    assay.setStudy(study);
    assay.setName("Test Assay");
    assay.setCode(program.getCode() + "-12345-123");
    path = storageService.getAssayFolderPath(assay);
    System.out.println(path);
    Assert.assertTrue(path.endsWith("/Clinical Program A/" + study.getCode() + " - "
        + study.getName() + "/" + assay.getCode() + " - " + assay.getName() + "/"));
  }

  @Test
  public void studyFolderTests() throws Exception {

    Optional<Program> optionalProgram = programRepository.findByName("Clinical Program A");
    Assert.assertTrue(optionalProgram.isPresent());
    Program program = optionalProgram.get();
    Optional<User> optionalUser = userRepository.findByUsername("jsmith");
    Assert.assertTrue(optionalUser.isPresent());
    User user = optionalUser.get();
    Study study = new Study();
    study.setStatus(Status.IN_PLANNING);
    study.setName("Study X");
    study.setCode("CPA-12345");
    study.setProgram(program);
    study.setDescription("This is a test");
    study.setLegacy(true);
    study.setActive(true);
    study.setCreatedBy(user);
    study.setLastModifiedBy(user);
    study.setStartDate(new Date());
    study.setOwner(user);
    study.setUsers(Collections.singletonList(user));
    studyRepository.insert(study);
    Assert.assertNotNull(study.getId());
    Assert.assertEquals("CPA-12345", study.getCode());

    StorageFolder folder = null;
    Exception exception = null;
    try {
      folder = storageService.createStudyFolder(study);
    } catch (Exception e) {
      exception = e;
    }

    if (exception != null) {
      Assert.assertNull(folder);
      Assert.assertTrue(exception.getCause() instanceof DuplicateFolderException);
    } else {
      Assert.assertNotNull(folder);
      StorageFolder studyFolder = storageService.getStudyFolder(study);
      Assert.assertNotNull(studyFolder);
      Assert.assertEquals(folder.getPath(), studyFolder.getPath());
    }

    exception = null;
    StorageFile file = null;
    try {
      file = storageService.saveStudyFile(TEST_FILE.getFile(), study);
    } catch (Exception e) {
      exception = e;
    }
    System.out.println(file.toString());
    Assert.assertNull(exception);
    Assert.assertNotNull(file);
    Assert.assertTrue(file.getPath().endsWith("test.txt"));
    Assert.assertEquals(TEST_FILE.getFilename(), file.getName());
    Assert.assertNotNull(file.getUrl());

  }

  @Test
  public void getInvalidStudyFolderTest() {
    Program program = programRepository.findByName("Clinical Program A")
        .orElseThrow(RecordNotFoundException::new);
    User user = userRepository.findByUsername("jsmith")
        .orElseThrow(RecordNotFoundException::new);
    Study study = new Study();
    study.setName("Test study");
    study.setProgram(program);
    study.setOwner(user);
    study.setCode("BAD-STUDY");

    StorageFolder folder = null;
    Exception exception = null;
    try {
      folder = storageService.getStudyFolder(study);
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNotNull(exception);
    Assert.assertTrue(exception.getCause() instanceof ObjectNotFoundException);
    Assert.assertNull(folder);
  }

  @Test
  public void assayFolderTests() throws Exception {

    Optional<Program> optionalProgram = programRepository.findByName("Clinical Program A");
    Assert.assertTrue(optionalProgram.isPresent());
    Program program = optionalProgram.get();
    Optional<User> optionalUser = userRepository.findByUsername("jsmith");
    Assert.assertTrue(optionalUser.isPresent());
    AssayType assayType = assayTypeRepository.findByName("Generic")
        .orElseThrow(RecordNotFoundException::new);
    User user = optionalUser.get();
    Study study = new Study();
    study.setStatus(Status.IN_PLANNING);
    study.setName("Study X");
    study.setCode("CPA-12345");
    study.setProgram(program);
    study.setDescription("This is a test");
    study.setLegacy(true);
    study.setActive(true);
    study.setCreatedBy(user);
    study.setLastModifiedBy(user);
    study.setStartDate(new Date());
    study.setOwner(user);
    study.setUsers(Collections.singletonList(user));
    studyRepository.insert(study);
    Assert.assertNotNull(study.getId());
    Assert.assertEquals("CPA-12345", study.getCode());

    Assay assay = new Assay();
    assay.setName("Test assay");
    assay.setCode("CPA-12345-12345");
    assay.setStatus(Status.IN_PLANNING);
    assay.setCreatedBy(study.getOwner());
    assay.setAssayType(assayType);
    assay.setStudy(study);
    assay.setDescription("This is a test");
    assay.setStartDate(new Date());
    assayRepository.insert(assay);
    Assert.assertNotNull(assay.getId());

    StorageFolder folder = null;
    Exception exception = null;
    try {
      folder = storageService.createAssayFolder(assay);
    } catch (Exception e) {
      exception = e;
    }

    if (exception != null) {
      Assert.assertNull(folder);
      Assert.assertTrue(exception.getCause() instanceof DuplicateFolderException);
    } else {
      Assert.assertNotNull(folder);
      StorageFolder assayFolder = storageService.getAssayFolder(assay);
      Assert.assertNotNull(assayFolder);
      Assert.assertEquals(folder.getPath(), assayFolder.getPath());
    }

    exception = null;
    StorageFile file = null;
    try {
      file = storageService.saveAssayFile(TEST_FILE.getFile(), assay);
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNull(exception);
    Assert.assertNotNull(file);
    Assert.assertTrue(file.getPath().endsWith("test.txt"));

  }

  @Test
  public void getInvalidAssayFolderTest() {

    Study study = studyRepository.findByCode("CPA-10001")
        .orElseThrow(RecordNotFoundException::new);
    AssayType assayType = assayTypeRepository.findByName("Generic")
        .orElseThrow(RecordNotFoundException::new);
    Assay assay = new Assay();
    assay.setName("Test assay");
    assay.setCode("CPA-10001-XXXXX");
    assay.setAssayType(assayType);
    assay.setStudy(study);

    StorageFolder folder = null;
    Exception exception = null;
    try {
      folder = storageService.getAssayFolder(assay);
    } catch (Exception e) {
      exception = e;
      e.printStackTrace();
    }
    Assert.assertNotNull(exception);
    Assert.assertTrue(exception.getCause() instanceof ObjectNotFoundException);
    Assert.assertNull(folder);
  }

  @Test
  public void studyFolderDepthTest() throws Exception {

    System.out.println("Read depth: " + egnyteOptions.getMaxReadDepth());
    Study study = studyRepository.findByCode("PPB-10001")
        .orElseThrow(RecordNotFoundException::new);
    StorageFolder studyFolder = storageService.getStudyFolder(study, true);
    System.out.println(studyFolder.toString());
    Assert.assertNotNull(studyFolder);
    Assert.assertFalse(studyFolder.getFiles().isEmpty());
    Assert.assertFalse(studyFolder.getSubFolders().isEmpty());
    StorageFolder assayFolder = studyFolder.getSubFolders().stream()
        .filter(f -> f.getName().equals("PPB-10001-001 - Histology assay"))
        .findFirst()
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertNotNull(assayFolder);
    System.out.println(assayFolder.toString());
    Assert.assertFalse(assayFolder.getFiles().isEmpty());

  }

}
