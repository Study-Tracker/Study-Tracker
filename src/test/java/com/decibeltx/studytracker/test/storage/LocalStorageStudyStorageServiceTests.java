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

package com.decibeltx.studytracker.test.storage;

import com.decibeltx.studytracker.Application;
import com.decibeltx.studytracker.example.ExampleDataGenerator;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.Assay;
import com.decibeltx.studytracker.model.AssayType;
import com.decibeltx.studytracker.model.Program;
import com.decibeltx.studytracker.model.Status;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.repository.AssayRepository;
import com.decibeltx.studytracker.repository.AssayTypeRepository;
import com.decibeltx.studytracker.repository.ProgramRepository;
import com.decibeltx.studytracker.repository.StudyRepository;
import com.decibeltx.studytracker.repository.UserRepository;
import com.decibeltx.studytracker.storage.LocalFileSystemStudyStorageService;
import com.decibeltx.studytracker.storage.StorageFile;
import com.decibeltx.studytracker.storage.StorageFolder;
import com.decibeltx.studytracker.storage.exception.StudyStorageDuplicateException;
import com.decibeltx.studytracker.storage.exception.StudyStorageNotFoundException;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "example"})
public class LocalStorageStudyStorageServiceTests {

  private static final Resource TEST_FILE = new ClassPathResource("test.txt");

  @Autowired
  private LocalFileSystemStudyStorageService storageService;

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

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
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
    study.setUsers(Collections.singleton(user));
    studyRepository.save(study);
    Assert.assertNotNull(study.getId());
    Assert.assertEquals("CPA-12345", study.getCode());

    StorageFolder folder = null;
    Exception exception = null;
    try {
      folder = storageService.createStudyFolder(study);
    } catch (Exception e) {
      exception = e;
      e.printStackTrace();
    }

    if (exception != null) {
      Assert.assertNull(folder);
      Assert.assertTrue(exception instanceof StudyStorageDuplicateException);
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
    Assert.assertNull(exception);
    Assert.assertNotNull(file);
    Assert.assertTrue(file.getPath().endsWith("test.txt"));

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
      e.printStackTrace();
    }
    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof StudyStorageNotFoundException);
    Assert.assertNull(folder);
  }


  @Test
  public void assayFolderTests() throws Exception {

    AssayType assayType = assayTypeRepository.findByName("Generic")
        .orElseThrow(RecordNotFoundException::new);

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
    study.setUsers(Collections.singleton(user));
    studyRepository.save(study);
    Assert.assertNotNull(study.getId());
    Assert.assertEquals("CPA-12345", study.getCode());
    storageService.createStudyFolder(study);

    Assay assay = new Assay();
    assay.setName("Test assay");
    assay.setCode("CPA-12345-12345");
    assay.setStatus(Status.IN_PLANNING);
    assay.setOwner(study.getOwner());
    assay.setCreatedBy(study.getOwner());
    assay.setLastModifiedBy(study.getOwner());
    assay.setAssayType(assayType);
    assay.setStudy(study);
    assay.setDescription("This is a test");
    assay.setStartDate(new Date());
    assayRepository.save(assay);
    Assert.assertNotNull(assay.getId());

    StorageFolder folder = null;
    Exception exception = null;
    try {
      folder = storageService.createAssayFolder(assay);
    } catch (Exception e) {
      e.printStackTrace();
      exception = e;
    }

    if (exception != null) {
      Assert.assertNull(folder);
      Assert.assertTrue(exception instanceof StudyStorageDuplicateException);
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
    Assert.assertTrue(exception instanceof StudyStorageNotFoundException);
    Assert.assertNull(folder);
  }


}
