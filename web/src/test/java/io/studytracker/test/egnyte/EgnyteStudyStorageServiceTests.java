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

package io.studytracker.test.egnyte;

import io.studytracker.Application;
import io.studytracker.egnyte.EgnyteIntegrationService;
import io.studytracker.egnyte.EgnyteStudyStorageService;
import io.studytracker.egnyte.exception.DuplicateFolderException;
import io.studytracker.egnyte.exception.ObjectNotFoundException;
import io.studytracker.example.ExampleDataRunner;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayStorageFolder;
import io.studytracker.model.AssayType;
import io.studytracker.model.EgnyteIntegration;
import io.studytracker.model.Program;
import io.studytracker.model.Status;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.model.Study;
import io.studytracker.model.StudyStorageFolder;
import io.studytracker.model.User;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.AssayTypeRepository;
import io.studytracker.repository.ProgramRepository;
import io.studytracker.repository.StorageDriveRepository;
import io.studytracker.repository.StudyRepository;
import io.studytracker.repository.UserRepository;
import io.studytracker.service.AssayService;
import io.studytracker.service.ProgramService;
import io.studytracker.service.StudyService;
import io.studytracker.storage.StorageDriveFolderService;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"egnyte-test", "example"})
public class EgnyteStudyStorageServiceTests {

  private static final Resource TEST_FILE = new ClassPathResource("test.txt");

  @Autowired private EgnyteStudyStorageService storageService;

  @Autowired private StudyRepository studyRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private ProgramRepository programRepository;

  @Autowired private AssayRepository assayRepository;

  @Autowired private AssayTypeRepository assayTypeRepository;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private ExampleDataRunner exampleDataRunner;

  @Autowired private EgnyteIntegrationService egnyteIntegrationService;
  @Autowired private StorageDriveRepository driveRepository;
  @Autowired private StorageDriveFolderService storageDriveFolderService;

  @Before
  public void doBefore() throws Exception {
    exampleDataRunner.populateDatabase();
  }

  @Test
  public void folderPathNameTest() {

    EgnyteIntegration integration = egnyteIntegrationService.findAll().get(0);
    StorageDrive drive = egnyteIntegrationService.listIntegrationDrives(integration).get(0);

    Optional<Program> optionalProgram = programRepository.findByName("Clinical Program A");
    Assert.assertTrue(optionalProgram.isPresent());
    Program program = optionalProgram.get();
    String path = storageService.getProgramFolderPath(program, drive.getRootPath());
    System.out.println(path);
    Assert.assertTrue(path.endsWith("/Clinical Program A/"));

    Study study = new Study();
    study.setProgram(program);
    study.setName("Test Study");
    study.setCode(program.getCode() + "-12345");
    path = storageService.getStudyFolderPath(study, drive.getRootPath());
    System.out.println(path);
    Assert.assertTrue(
        path.endsWith("/Clinical Program A/" + study.getCode() + " - " + study.getName() + "/"));

    Assay assay = new Assay();
    assay.setStudy(study);
    assay.setName("Test Assay");
    assay.setCode(program.getCode() + "-12345-123");
    path = storageService.getAssayFolderPath(assay, drive.getRootPath());
    System.out.println(path);
    Assert.assertTrue(
        path.endsWith(
            "/Clinical Program A/"
                + study.getCode()
                + " - "
                + study.getName()
                + "/"
                + assay.getCode()
                + " - "
                + assay.getName()
                + "/"));
  }

  @Test
  public void studyFolderTests() throws Exception {

    Optional<Program> optionalProgram = programRepository.findByName("Clinical Program A");
    Assert.assertTrue(optionalProgram.isPresent());
    Program program = optionalProgram.get();
    Optional<User> optionalUser = userRepository.findByEmail("jsmith@email.com");
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
    StorageDriveFolder parentFolder = program.getStorageFolders().stream()
        .filter(pf -> pf.isPrimary())
        .findFirst()
        .get()
        .getStorageDriveFolder();

    StorageDriveFolder folder = null;
    Exception exception = null;
    try {
      String folderName = ProgramService.generateProgramStorageFolderName(program);
      StorageFolder storageFolder = storageService.createFolder(parentFolder, folderName);
      StorageDriveFolder folderOptions = new StorageDriveFolder();
      folderOptions.setWriteEnabled(true);
      folder = storageService.saveStorageFolderRecord(parentFolder.getStorageDrive(),
          storageFolder, folderOptions);
    } catch (Exception e) {
      exception = e;
    }

    if (exception != null) {
      Assert.assertNull(folder);
      Assert.assertTrue(exception.getCause() instanceof DuplicateFolderException);
    } else {
      Assert.assertNotNull(folder);
      StorageFolder studyFolder = storageService.findFolderByPath(parentFolder, folder.getPath());
      Assert.assertNotNull(studyFolder);
      Assert.assertEquals(folder.getPath(), studyFolder.getPath());
    }

    exception = null;
    StorageFile file = null;
    try {
      file = storageService.saveFile(folder, folder.getPath(), TEST_FILE.getFile());
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
    Program program =
        programRepository
            .findByName("Clinical Program A")
            .orElseThrow(RecordNotFoundException::new);
    User user = userRepository.findByEmail("jsmith@email.com").orElseThrow(RecordNotFoundException::new);
    Study study = new Study();
    study.setName("Test study");
    study.setProgram(program);
    study.setOwner(user);
    study.setCode("BAD-STUDY");

    StorageDriveFolder parentFolder = program.getStorageFolders().stream()
        .filter(pf -> pf.isPrimary())
        .findFirst()
        .get()
        .getStorageDriveFolder();

    StorageFolder folder = null;
    Exception exception = null;
    try {
      folder = storageService.findFolderByPath(parentFolder, storageService.getStudyFolderPath(study, parentFolder.getPath()));
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
    Optional<User> optionalUser = userRepository.findByEmail("jsmith@email.com");
    Assert.assertTrue(optionalUser.isPresent());
    AssayType assayType =
        assayTypeRepository.findByName("Generic").orElseThrow(RecordNotFoundException::new);
    User user = optionalUser.get();

    StorageDriveFolder programFolder = program.getStorageFolders().stream()
        .filter(pf -> pf.isPrimary())
        .findFirst()
        .get()
        .getStorageDriveFolder();

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


    Exception exception = null;
    try {
      String folderName = StudyService.generateStudyStorageFolderName(study);
      StorageFolder storageFolder = storageService.createFolder(programFolder, folderName);
      StorageDriveFolder folderOptions = new StorageDriveFolder();
      folderOptions.setWriteEnabled(true);
      StorageDriveFolder studyFolder = storageService.saveStorageFolderRecord(programFolder.getStorageDrive(),
          storageFolder, folderOptions);
      study.addStorageFolder(studyFolder, true);
      studyRepository.save(study);
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNull(exception);
    Assert.assertNotNull(study.getId());

    study = studyRepository.findByCode("CPA-12345").orElseThrow(RecordNotFoundException::new);
    StudyStorageFolder studyFolder = study.getStorageFolders().stream()
        .filter(sf -> sf.isPrimary())
        .findFirst()
        .get();
    Assert.assertNotNull(studyFolder);
    Assert.assertNotNull(studyFolder.getId());

    Assay assay = new Assay();
    assay.setName("Test assay");
    assay.setCode("CPA-12345-12345");
    assay.setStatus(Status.IN_PLANNING);
    assay.setCreatedBy(study.getOwner());
    assay.setOwner(study.getOwner());
    assay.setLastModifiedBy(study.getOwner());
    assay.setAssayType(assayType);
    assay.setStudy(study);
    assay.setDescription("This is a test");
    assay.setStartDate(new Date());
    assayRepository.save(assay);
    Assert.assertNotNull(assay.getId());

    try {
      String folderName = AssayService.generateAssayStorageFolderName(assay);
      StorageFolder storageFolder = storageService.createFolder(studyFolder.getStorageDriveFolder(), folderName);
      StorageDriveFolder folderOptions = new StorageDriveFolder();
      folderOptions.setWriteEnabled(true);
      StorageDriveFolder assayFolder = storageService.saveStorageFolderRecord(programFolder.getStorageDrive(),
          storageFolder, folderOptions);
      assay.addStorageFolder(assayFolder, true);
      assayRepository.save(assay);
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNull(exception);
    Assert.assertNotNull(assay.getId());

    assay = assayRepository.findByCode("CPA-12345-12345").orElseThrow(RecordNotFoundException::new);
    AssayStorageFolder assayStorageFolder = assay.getStorageFolders().stream()
        .filter(sf -> sf.isPrimary())
        .findFirst()
        .get();
    Assert.assertNotNull(assayStorageFolder);
    Assert.assertNotNull(assayStorageFolder.getId());

    StorageFile file = null;
    try {
      file = storageService.saveFile(assayStorageFolder.getStorageDriveFolder(),
          assayStorageFolder.getStorageDriveFolder().getPath(), TEST_FILE.getFile());
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNull(exception);
    Assert.assertNotNull(file);
    Assert.assertTrue(file.getPath().endsWith("test.txt"));
  }

  @Test
  public void getInvalidAssayFolderTest() {

    Study study = studyRepository.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    StorageDriveFolder studyFolder = study.getStorageFolders().stream()
        .filter(pf -> pf.isPrimary())
        .findFirst()
        .get()
        .getStorageDriveFolder();
    AssayType assayType =
        assayTypeRepository.findByName("Generic").orElseThrow(RecordNotFoundException::new);
    Assay assay = new Assay();
    assay.setName("Test assay");
    assay.setCode("CPA-10001-XXXXX");
    assay.setAssayType(assayType);
    assay.setStudy(study);

    StorageFolder folder = null;
    Exception exception = null;
    try {
      folder = storageService.findFolderByPath(studyFolder, storageService.getAssayFolderPath(assay, studyFolder.getPath()));
    } catch (Exception e) {
      exception = e;
      e.printStackTrace();
    }
    Assert.assertNotNull(exception);
    Assert.assertTrue(exception.getCause() instanceof ObjectNotFoundException);
    Assert.assertNull(folder);
  }

}
