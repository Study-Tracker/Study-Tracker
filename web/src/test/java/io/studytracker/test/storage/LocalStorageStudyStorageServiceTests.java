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

package io.studytracker.test.storage;

import io.studytracker.Application;
import io.studytracker.example.ExampleDataRunner;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayType;
import io.studytracker.model.Program;
import io.studytracker.model.Status;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDrive.DriveType;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.AssayTypeRepository;
import io.studytracker.repository.ProgramRepository;
import io.studytracker.repository.StudyRepository;
import io.studytracker.repository.UserRepository;
import io.studytracker.storage.LocalFileSystemStorageService;
import io.studytracker.storage.StorageDriveFolderService;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StorageUtils;
import io.studytracker.storage.StudyStorageServiceLookup;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
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

  @Autowired private LocalFileSystemStorageService storageService;

  @Autowired private StudyRepository studyRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private ProgramRepository programRepository;

  @Autowired private AssayRepository assayRepository;

  @Autowired private AssayTypeRepository assayTypeRepository;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private ExampleDataRunner exampleDataRunner;

  @Autowired private StudyStorageServiceLookup storageServiceLookup;
  @Autowired private StorageDriveFolderService storageDriveFolderService;

  @Before
  public void doBefore() {
    exampleDataRunner.populateDatabase();
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

    StorageDriveFolder programFolder = storageDriveFolderService.findByProgram(program).get(0);
    Assert.assertNotNull(programFolder);
    StorageDriveFolder folder = null;
    Exception exception = null;

    try {
      folder = storageService.createStudyFolder(programFolder, study);
    } catch (Exception e) {
      exception = e;
      e.printStackTrace();
    }

    Assert.assertNull(exception);
    Assert.assertNotNull(folder);
    Assert.assertNotNull(folder.getId());
    StorageFile file = null;

    try {
      file = storageService.saveFile(folder, folder.getPath(), TEST_FILE.getFile());
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNull(exception);
    Assert.assertNotNull(file);
    Assert.assertTrue(file.getPath().endsWith("test.txt"));
  }

  @Test
  public void getInvalidStudyFolderTest() throws Exception {
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
    StorageDriveFolder programFolder = program.getStorageFolders().stream()
        .filter(f -> f.isPrimary())
        .findFirst()
        .get()
        .getStorageDriveFolder();
    StorageDrive drive = storageDriveFolderService.findDriveByFolder(programFolder)
        .orElseThrow(RecordNotFoundException::new);
    StorageFolder folder = null;
    Exception exception = null;
    try {
      folder = storageService.findFolderByPath(drive, "BAD-STUDY");
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

    AssayType assayType =
        assayTypeRepository.findByName("Generic").orElseThrow(RecordNotFoundException::new);

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

    StorageDriveFolder programFolder = program.getStorageFolders().stream()
        .filter(f -> f.isPrimary())
        .findFirst()
        .get()
        .getStorageDriveFolder();
    StorageDriveFolder studyFolder = storageService.createStudyFolder(programFolder, study);
    Assert.assertNotNull(studyFolder.getId());
    study.addStorageFolder(studyFolder, true);
    studyRepository.save(study);

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

    StorageDriveFolder assayFolder = null;
    Exception exception = null;
    try {
      assayFolder = storageService.createAssayFolder(studyFolder, assay);
    } catch (Exception e) {
      e.printStackTrace();
      exception = e;
    }

    Assert.assertNull(exception);
    Assert.assertNotNull(assayFolder);
    StorageFile file = null;

    try {
      file = storageService.saveFile(assayFolder, assayFolder.getPath(), TEST_FILE.getFile());
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNull(exception);
    Assert.assertNotNull(file);
    Assert.assertTrue(file.getPath().endsWith("test.txt"));
  }

  @Test
  public void getInvalidAssayFolderTest() throws Exception {

    Study study = studyRepository.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    StorageDriveFolder studyFolder = study.getStorageFolders().stream()
        .filter(f -> f.isPrimary())
        .findFirst()
        .get()
        .getStorageDriveFolder();
    StorageDrive drive = storageDriveFolderService.findDriveByFolder(studyFolder)
        .orElseThrow(RecordNotFoundException::new);
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
      folder = storageService.findFolderByPath(drive, "CPA-10001-XXXXX");
    } catch (Exception e) {
      exception = e;
      e.printStackTrace();
    }
    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof StudyStorageNotFoundException);
    Assert.assertNull(folder);
  }

  @Test
  public void renameFolderTest() throws Exception {
    StorageDrive drive = storageDriveFolderService.findAllDrives().stream()
        .filter(d -> d.getDriveType().equals(DriveType.LOCAL))
        .findFirst()
        .orElse(null);
    Assert.assertNotNull(drive);
    StorageFolder folder = null;
    Exception exception = null;
    String uuid = UUID.randomUUID().toString();
    try {
      folder = storageService.createFolder(drive, drive.getRootPath(), uuid);
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNull(exception);
    Assert.assertNotNull(folder);
    Assert.assertEquals(uuid, folder.getName());
    Assert.assertEquals(StorageUtils.joinPath(drive.getRootPath(), uuid), folder.getPath());

    StorageFolder updated = null;
    exception = null;
    try {
      updated = storageService.renameFolder(drive, folder.getPath(), uuid + "_renamed");
    } catch (Exception e) {
      exception = e;
    }

    Assert.assertNull(exception);
    Assert.assertNotNull(updated);
    Assert.assertEquals(uuid + "_renamed", updated.getName());
    Assert.assertEquals(StorageUtils.joinPath(drive.getRootPath(), uuid + "_renamed"), updated.getPath());
  }

  @Test
  public void moveFolderTest() throws Exception {
    StorageDrive drive = storageDriveFolderService.findAllDrives().stream()
        .filter(d -> d.getDriveType().equals(DriveType.LOCAL))
        .findFirst()
        .orElse(null);
    Assert.assertNotNull(drive);
    StorageFolder folder = null;
    StorageFolder parentFolder = null;
    Exception exception = null;
    String uuid = UUID.randomUUID().toString();
    try {
      folder = storageService.createFolder(drive, drive.getRootPath(), uuid + "_original");
      parentFolder = storageService.createFolder(drive, drive.getRootPath(), uuid + "_parent");
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNull(exception);
    Assert.assertNotNull(folder);
    Assert.assertNotNull(parentFolder);
    Assert.assertEquals(uuid + "_original", folder.getName());
    Assert.assertEquals(uuid + "_parent", parentFolder.getName());
    Assert.assertEquals(StorageUtils.joinPath(drive.getRootPath(), uuid + "_original"), folder.getPath());
    Assert.assertEquals(StorageUtils.joinPath(drive.getRootPath(), uuid + "_parent"), parentFolder.getPath());

    StorageFolder updated = null;
    exception = null;
    try {
      updated = storageService.moveFolder(drive, folder.getPath(), parentFolder.getPath());
    } catch (Exception e) {
      exception = e;
    }

    Assert.assertNull(exception);
    Assert.assertNotNull(updated);
    Assert.assertEquals(uuid + "_original", updated.getName());
    Assert.assertEquals(StorageUtils.joinPath(StorageUtils.joinPath(drive.getRootPath(), uuid + "_parent"), uuid + "_original"), updated.getPath());
  }

}
