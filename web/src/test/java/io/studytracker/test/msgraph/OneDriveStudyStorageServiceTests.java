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

package io.studytracker.test.msgraph;

import io.studytracker.Application;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayType;
import io.studytracker.model.MSGraphIntegration;
import io.studytracker.model.OneDriveFolderDetails;
import io.studytracker.model.Program;
import io.studytracker.model.SharePointSite;
import io.studytracker.model.Status;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDrive.DriveType;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import io.studytracker.model.UserType;
import io.studytracker.msgraph.MSGraphIntegrationService;
import io.studytracker.msgraph.OneDriveStorageService;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.AssayStorageFolderRepository;
import io.studytracker.repository.AssayTypeRepository;
import io.studytracker.repository.MSGraphIntegrationRepository;
import io.studytracker.repository.ProgramRepository;
import io.studytracker.repository.ProgramStorageFolderRepository;
import io.studytracker.repository.SharePointSiteRepository;
import io.studytracker.repository.StorageDriveFolderRepository;
import io.studytracker.repository.StorageDriveRepository;
import io.studytracker.repository.StudyRepository;
import io.studytracker.repository.StudyStorageFolderRepository;
import io.studytracker.repository.UserRepository;
import io.studytracker.storage.StorageDriveFolderService;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"msgraph-test"})
public class OneDriveStudyStorageServiceTests {

  @Value("${ms-graph.tenant-id}")
  private String tenantId;

  @Value("${ms-graph.client-id}")
  private String clientId;

  @Value("${ms-graph.secret}")
  private String secret;

  @Autowired
  private StorageDriveFolderService storageDriveFolderService;

  @Autowired
  private MSGraphIntegrationRepository msGraphIntegrationRepository;

  @Autowired
  private SharePointSiteRepository sharePointSiteRepository;

  @Autowired
  private MSGraphIntegrationService integrationService;

  @Autowired
  private OneDriveStorageService storageService;

  @Autowired
  private StorageDriveFolderRepository storageDriveFolderRepository;

  @Autowired
  private StorageDriveRepository storageDriveRepository;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private AssayRepository assayRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AssayTypeRepository assayTypeRepository;

  @Autowired
  private ProgramStorageFolderRepository programStorageFolderRepository;

  @Autowired
  private StudyStorageFolderRepository studyStorageFolderRepository;

  @Autowired
  private AssayStorageFolderRepository assayStorageFolderRepository;

  @Before
  public void setup() {

    assayStorageFolderRepository.deleteAll();
    studyStorageFolderRepository.deleteAll();
    programStorageFolderRepository.deleteAll();
    storageDriveFolderRepository.deleteAll();
    storageDriveRepository.deleteAll();
    sharePointSiteRepository.deleteAll();
    msGraphIntegrationRepository.deleteAll();
    assayRepository.deleteAll();
    studyRepository.deleteAll();
    programRepository.deleteAll();
    userRepository.deleteAll();

    User user = new User();
    user.setDisplayName("Joe Person");
    user.setUsername("jperson");
    user.setEmail("jperson@email.com");
    user.setActive(true);
    user.setType(UserType.STANDARD_USER);
    user.setAdmin(true);
    user.setPassword("password");
    userRepository.save(user);

    MSGraphIntegration integration = new MSGraphIntegration();
    integration.setName("Azure");
    integration.setDomain("myorg.onmicrosoft.com");
    integration.setClientId(clientId);
    integration.setClientSecret(secret);
    integration.setTenantId(tenantId);
    integration.setActive(true);
    integration = integrationService.register(integration);

    SharePointSite site = integrationService.listAvailableSharepointSites(integration).stream()
        .filter(s -> s.getName().equals("Study Tracker Development"))
        .findFirst()
        .orElse(null);
    Assert.assertNotNull(site);
    SharePointSite created = integrationService.registerSharePointSite(site);
    List<StorageDrive> drives = integrationService.registerSharePointDrives(created);
    StorageDrive storageDrive = drives.get(0);

    StorageDriveFolder rootFolder = new StorageDriveFolder();
    rootFolder.setStorageDrive(storageDrive);
    rootFolder.setPath("/");
    rootFolder.setName("OneDrive Root");
    rootFolder.setStudyRoot(true);
    rootFolder.setBrowserRoot(true);
    rootFolder.setWriteEnabled(true);

    OneDriveFolderDetails root = new OneDriveFolderDetails();
    root.setPath("/");
    root.setFolderId("root");
    root.setWebUrl("www.test.com");
    rootFolder.setDetails(root);
    storageDriveFolderRepository.save(rootFolder);

  }

  @Test
  public void findFolderByPathTest() throws Exception {

    List<StorageDrive> drives = storageDriveRepository.findByDriveType(DriveType.ONEDRIVE);
    Assert.assertNotNull(drives);
    Assert.assertTrue(drives.size() > 0);
    StorageDrive drive = drives.get(0);

    StorageFolder folder = null;
    Exception exception = null;

    try {
      folder = storageService.findFolderByPath(drive, "/");
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNull(exception);
    Assert.assertNotNull(folder);
    Assert.assertEquals("/", folder.getPath());
    Assert.assertEquals("root", folder.getName());

    try {
      folder = storageService.findFolderByPath(drive, "/Project A");
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNull(exception);
    Assert.assertNotNull(folder);
    Assert.assertEquals("Project A", folder.getName());
    Assert.assertEquals("/Project A", folder.getPath());
    Assert.assertFalse(folder.getFiles().isEmpty());
    Assert.assertEquals(1, folder.getFiles().size());
    StorageFile file = folder.getFiles().get(0);
    Assert.assertEquals("test.txt", file.getName());
    Assert.assertEquals("/Project A/test.txt", file.getPath());
    Assert.assertEquals(1, folder.getSubFolders().size());
    StorageFolder subFolder = folder.getSubFolders().get(0);
    Assert.assertEquals("Test", subFolder.getName());
    Assert.assertEquals("/Project A/Test", subFolder.getPath());

    folder = null;
    exception = null;
    try {
      folder = storageService.findFolderByPath(drive, "/BAD_FOLDER");
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof StudyStorageNotFoundException);
    Assert.assertNull(folder);

  }

  @Test
  public void findFileByPathTest() throws Exception {
    List<StorageDrive> drives = storageDriveRepository.findByDriveType(DriveType.ONEDRIVE);
    Assert.assertNotNull(drives);
    Assert.assertTrue(drives.size() > 0);
    StorageDrive drive = drives.get(0);

    StorageFile file = null;
    Exception exception = null;

    try {
      file = storageService.findFileByPath(drive, "/Project A/test.txt");
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNull(exception);
    Assert.assertNotNull(file);
    Assert.assertEquals("test.txt", file.getName());
    Assert.assertEquals("/Project A/test.txt", file.getPath());

    file = null;
    exception = null;
    try {
      file = storageService.findFileByPath(drive, "/BAD_FILE.txt");
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof StudyStorageNotFoundException);
    Assert.assertNull(file);
  }

  @Test
  public void createProgramFolderTest() throws Exception {
    User user = userRepository.findAll().get(0);
    Program program = new Program();
    program.setName("Test Program");
    program.setCode("TEST");
    program.setCreatedBy(user);
    program.setLastModifiedBy(user);
    programRepository.save(program);

    List<StorageDriveFolder> rootFolders = storageDriveFolderService.findStudyRootFolders();
    Assert.assertEquals(1, rootFolders.size());
    StorageDriveFolder rootFolder = rootFolders.get(0);

    StorageDriveFolder programFolder = storageService.createProgramFolder(rootFolder, program);
    Assert.assertNotNull(programFolder);
    program.addStorageFolder(programFolder, true);
    programRepository.save(program);

    Program created = programRepository.findByName("Test Program")
        .orElseThrow(() -> new StudyStorageNotFoundException("Test Program"));
    Assert.assertEquals(1, created.getStorageFolders().size());

    System.out.println("Program folder: " + programFolder.getPath());
    StorageFolder folder = storageService.findFolderByPath(programFolder.getStorageDrive(), programFolder.getPath());
    Assert.assertNotNull(folder);
    Assert.assertEquals(programFolder.getName(), folder.getName());
    Assert.assertEquals(programFolder.getPath(), folder.getPath());

  }

  @Test
  public void createStudyFolderTest() throws Exception {
    createProgramFolderTest();
    Program program = programRepository.findByName("Test Program")
        .orElseThrow(() -> new StudyStorageNotFoundException("Test Program"));
    User user = userRepository.findAll().get(0);
    Study study = new Study();
    study.setName("Test Study");
    study.setDescription("Test Study");
    study.setProgram(program);
    study.setCode(program.getCode() + "-101");
    study.setStatus(Status.IN_PLANNING);
    study.setCreatedBy(user);
    study.setLastModifiedBy(user);
    study.setOwner(user);
    study.setStartDate(new Date());
    studyRepository.save(study);

    StorageDriveFolder programFolder = storageDriveFolderService.findPrimaryProgramFolder(program)
        .orElseThrow(() -> new StudyStorageNotFoundException("Test Program"));
    StorageDriveFolder studyFolder = storageService.createStudyFolder(programFolder, study);
    Assert.assertNotNull(studyFolder);
    study.addStorageFolder(studyFolder, true);
    studyRepository.save(study);

    Study created = studyRepository.findByCode("TEST-101")
        .orElseThrow(() -> new StudyStorageNotFoundException("Test Study"));
    Assert.assertEquals(1, created.getStorageFolders().size());

    System.out.println("Study folder: " + studyFolder.getPath());
    StorageFolder folder = storageService.findFolderByPath(studyFolder.getStorageDrive(),
        studyFolder.getPath());
    Assert.assertNotNull(folder);
    Assert.assertEquals(studyFolder.getName(), folder.getName());
    Assert.assertEquals(studyFolder.getPath(), folder.getPath());
  }

  @Test
  public void createAssayFolderTest() throws Exception {
    createStudyFolderTest();
    Study study = studyRepository.findByCode("TEST-101")
        .orElseThrow(() -> new StudyStorageNotFoundException("Test Study"));
    User user = userRepository.findAll().get(0);
    AssayType assayType = assayTypeRepository.findAll().get(0);
    Assay assay = new Assay();
    assay.setName("Test Assay");
    assay.setStudy(study);
    assay.setCode(study.getCode() + "-01");
    assay.setStatus(Status.IN_PLANNING);
    assay.setOwner(user);
    assay.setCreatedBy(user);
    assay.setLastModifiedBy(user);
    assay.setAssayType(assayType);
    assay.setDescription("This is a test assay");
    assay.setStartDate(new Date());
    assayRepository.save(assay);

    StorageDriveFolder studyFolder = storageDriveFolderService.findPrimaryStudyFolder(study)
        .orElseThrow(() -> new StudyStorageNotFoundException("Test Study"));
    StorageDriveFolder assayFolder = storageService.createAssayFolder(studyFolder, assay);
    Assert.assertNotNull(assayFolder);
    assay.addStorageFolder(assayFolder, true);
    assayRepository.save(assay);

    System.out.println("Assay folder: " + assayFolder.getPath());
    StorageFolder folder = storageService.findFolderByPath(assayFolder.getStorageDrive(),
        assayFolder.getPath());
    Assert.assertNotNull(folder);
    Assert.assertEquals(assayFolder.getName(), folder.getName());
    Assert.assertEquals(assayFolder.getPath(), folder.getPath());

  }

  @Test
  public void createFolderTest() throws Exception {
    List<StorageDrive> drives = storageDriveRepository.findByDriveType(DriveType.ONEDRIVE);
    Assert.assertNotNull(drives);
    Assert.assertTrue(drives.size() > 0);
    List<StorageDriveFolder> rootFolders = storageDriveFolderService.findStudyRootFolders();
    Assert.assertEquals(1, rootFolders.size());
    StorageDriveFolder rootFolder = rootFolders.get(0);
    StorageFolder folder = null;
    Exception exception = null;
    try {
      folder = storageService.createFolder(rootFolder, "/", "Test Create Folder");
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNull(exception);
    Assert.assertNotNull(folder);
    Assert.assertEquals("Test Create Folder", folder.getName());
    Assert.assertEquals("/Test Create Folder", folder.getPath());
  }
  
  @Test
  public void renameFolderTest() throws Exception {
    List<StorageDrive> drives = storageDriveRepository.findByDriveType(DriveType.ONEDRIVE);
    Assert.assertNotNull(drives);
    Assert.assertTrue(drives.size() > 0);
    List<StorageDriveFolder> rootFolders = storageDriveFolderService.findStudyRootFolders();
    Assert.assertEquals(1, rootFolders.size());
    StorageDriveFolder rootFolder = rootFolders.get(0);
    StorageFolder folder = null;
    Exception exception = null;
    String uuid = UUID.randomUUID().toString();
    try {
      folder = storageService.createFolder(rootFolder, "/", uuid);
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNull(exception);
    Assert.assertNotNull(folder);
    Assert.assertEquals(uuid, folder.getName());
    Assert.assertEquals("/" + uuid, folder.getPath());
    
    StorageFolder updated = null;
    exception = null;
    try {
      updated = storageService.renameFolder(rootFolder.getStorageDrive(), folder.getPath(), uuid + "_renamed");
    } catch (Exception e) {
      exception = e;
    }
    
    Assert.assertNull(exception);
    Assert.assertNotNull(updated);
    Assert.assertEquals(uuid + "_renamed", updated.getName());
    Assert.assertEquals("/" + uuid + "_renamed", updated.getPath());
    
  }

  @Test
  public void moveFolderTest() {
    List<StorageDrive> drives = storageDriveRepository.findByDriveType(DriveType.ONEDRIVE);
    Assert.assertNotNull(drives);
    Assert.assertTrue(drives.size() > 0);
    List<StorageDriveFolder> rootFolders = storageDriveFolderService.findStudyRootFolders();
    Assert.assertEquals(1, rootFolders.size());
    StorageDriveFolder rootFolder = rootFolders.get(0);
    StorageFolder folder = null;
    StorageFolder parentFolder = null;
    Exception exception = null;
    String uuid = UUID.randomUUID().toString();
    try {
      folder = storageService.createFolder(rootFolder, "/", uuid + "_original");
      parentFolder = storageService.createFolder(rootFolder, "/", uuid + "_parent");
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNull(exception);
    Assert.assertNotNull(folder);
    Assert.assertNotNull(parentFolder);
    Assert.assertEquals(uuid + "_original", folder.getName());
    Assert.assertEquals("/" + uuid + "_original", folder.getPath());
    Assert.assertEquals(uuid + "_parent", parentFolder.getName());
    Assert.assertEquals("/" + uuid + "_parent", parentFolder.getPath());

    StorageFolder updated = null;
    exception = null;
    try {
      updated = storageService.moveFolder(rootFolder.getStorageDrive(), folder.getPath(), parentFolder.getPath());
    } catch (Exception e) {
      exception = e;
    }

    Assert.assertNull(exception);
    Assert.assertNotNull(updated);
    Assert.assertEquals(uuid + "_original", updated.getName());
    Assert.assertEquals( "/" + uuid + "_parent/" + uuid + "_original", updated.getPath());
  }

  @Test
  public void saveFileTest() throws Exception {
    List<StorageDrive> drives = storageDriveRepository.findByDriveType(DriveType.ONEDRIVE);
    Assert.assertNotNull(drives);
    Assert.assertTrue(drives.size() > 0);
    ClassPathResource resource = new ClassPathResource("test.txt");
    List<StorageDriveFolder> rootFolders = storageDriveFolderService.findStudyRootFolders();
    Assert.assertEquals(1, rootFolders.size());
    StorageDriveFolder rootFolder = rootFolders.get(0);
    StorageFile storageFile = null;
    Exception exception = null;
    try {
      storageFile = storageService.saveFile(rootFolder, "/", resource.getFile());
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNull(exception);
    Assert.assertNotNull(storageFile);
    Assert.assertEquals("test.txt", storageFile.getName());
    Assert.assertEquals("/test.txt", storageFile.getPath());
  }

  @Test
  public void fetchFileTest() throws Exception {
    saveFileTest();
    List<StorageDriveFolder> rootFolders = storageDriveFolderService.findStudyRootFolders();
    Assert.assertEquals(1, rootFolders.size());
    StorageDriveFolder rootFolder = rootFolders.get(0);
    Resource file = null;
    Exception exception = null;
    try {
      file = storageService.fetchFile(rootFolder, "/test.txt");
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNull(exception);
    Assert.assertNotNull(file);
  }

  @Test
  public void fileExistsTest() throws Exception {
    List<StorageDrive> drives = storageDriveRepository.findByDriveType(DriveType.ONEDRIVE);
    Assert.assertNotNull(drives);
    Assert.assertTrue(drives.size() > 0);
    StorageDrive drive = drives.get(0);
    boolean exists = storageService.fileExists(drive, "/Project A/test.txt");
    Assert.assertTrue(exists);
    exists = storageService.fileExists(drive, "/Project A/BAD_FILE.txt");
    Assert.assertFalse(exists);
  }

  @Test
  public void folderExistsTest() throws Exception {
    List<StorageDrive> drives = storageDriveRepository.findByDriveType(DriveType.ONEDRIVE);
    Assert.assertNotNull(drives);
    Assert.assertTrue(drives.size() > 0);
    StorageDrive drive = drives.get(0);
    boolean exists = storageService.folderExists(drive, "/Project A");
    Assert.assertTrue(exists);
    exists = storageService.folderExists(drive, "/Project XYZ");
    Assert.assertFalse(exists);
  }

}
