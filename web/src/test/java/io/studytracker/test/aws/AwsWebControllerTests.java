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

package io.studytracker.test.aws;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.studytracker.Application;
import io.studytracker.aws.AwsIntegrationService;
import io.studytracker.example.ExampleDataRunner;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.form.AssayFormDto;
import io.studytracker.mapstruct.dto.form.S3BucketFormDto;
import io.studytracker.mapstruct.dto.form.StorageDriveFolderFormDto;
import io.studytracker.mapstruct.dto.form.StudyFormDto;
import io.studytracker.mapstruct.mapper.AssayMapper;
import io.studytracker.mapstruct.mapper.StudyMapper;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayType;
import io.studytracker.model.AwsIntegration;
import io.studytracker.model.Program;
import io.studytracker.model.Status;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDrive.DriveType;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.AssayTypeRepository;
import io.studytracker.repository.AwsIntegrationRepository;
import io.studytracker.repository.ProgramRepository;
import io.studytracker.repository.StorageDriveFolderRepository;
import io.studytracker.repository.StorageDriveRepository;
import io.studytracker.repository.StudyRepository;
import io.studytracker.repository.UserRepository;
import io.studytracker.service.OrganizationService;
import io.studytracker.storage.StorageDriveFolderService;
import java.util.Collections;
import java.util.Date;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles({"web-aws-test", "example"})
public class AwsWebControllerTests {

  @Value("${aws.example-s3-bucket}")
  private String bucketName;

  @Autowired private MockMvc mockMvc;

  @Autowired private ExampleDataRunner exampleDataRunner;

  @Autowired private StudyRepository studyRepository;

  @Autowired private ProgramRepository programRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private StudyMapper studyMapper;
  @Autowired private AssayMapper assayMapper;

  @Autowired private AwsIntegrationService awsIntegrationService;
  @Autowired private AwsIntegrationRepository awsIntegrationRepository;
  @Autowired private OrganizationService organizationService;
  @Autowired private StorageDriveRepository storageDriveRepository;
  @Autowired private StorageDriveFolderRepository folderRepository;
  @Autowired private StorageDriveFolderService storageDriveFolderService;

  private String username;

  @Autowired
  private AssayRepository assayRepository;

  @Autowired
  private AssayTypeRepository assayTypeRepository;

  @Before
  public void doBefore() {
    exampleDataRunner.populateDatabase();
    username = userRepository.findAll().get(0).getEmail();
  }

  @Test
  public void awsIntegrationTest() throws Exception {
    mockMvc.perform(get("/api/internal/integrations/aws")
        .with(user(username)).with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name", is("Default AWS Integration")))
        ;
  }

  @Test
  public void findAvailableBucketsTest() throws Exception {
    mockMvc.perform(get("/api/internal/autocomplete/aws/s3?q=")
            .with(user(username)).with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasItem(bucketName)))
        ;
  }

  @Test
  public void s3BucketTest() throws Exception {

    mockMvc.perform(get("/api/internal/drives/s3")
            .with(user(username)).with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)))
    ;

    AwsIntegration integration = awsIntegrationRepository.findAll()
        .stream()
        .findFirst()
        .orElseThrow(RecordNotFoundException::new);
    S3BucketFormDto form = new S3BucketFormDto();
    form.setBucketName("invalid-bucket");
    form.setAwsIntegrationId(integration.getId());
    form.setRootPath("");
    form.setDisplayName("Test Bucket");

    mockMvc.perform(post("/api/internal/drives/s3")
            .with(user(username)).with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(form)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().is5xxServerError());

    form.setBucketName(bucketName);

    mockMvc.perform(post("/api/internal/drives/s3")
            .with(user(username)).with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(form)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.displayName", is("Test Bucket")))
        .andExpect(jsonPath("$.driveType", is("S3")))
        .andExpect(jsonPath("$.rootPath", is("")))
        .andExpect(jsonPath("$.details.bucketName", is(bucketName)))
        .andExpect(jsonPath("$.details.awsIntegrationId", is(integration.getId().intValue())))
        ;

    mockMvc.perform(get("/api/internal/drives/s3")
            .with(user(username)).with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
    ;

  }

  @Test
  public void registerRootS3FolderTest() throws Exception {

    s3BucketTest();

    mockMvc.perform(get("/api/internal/storage-drive-folders?studyRoot=true")
        .with(user(username)).with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].storageDrive.driveType", is("LOCAL")))
        ;

    StorageDrive drive = storageDriveRepository.findAll()
        .stream()
        .filter(d -> d.getDriveType().equals(DriveType.S3))
        .findFirst()
        .orElseThrow(RecordNotFoundException::new);

    StorageDriveFolderFormDto form = new StorageDriveFolderFormDto();
    form.setBrowserRoot(true);
    form.setStudyRoot(true);
    form.setName("S3 Study Root");
    form.setPath("test/");
    form.setWriteEnabled(true);
    form.setStorageDriveId(drive.getId());

    mockMvc.perform(post("/api/internal/storage-drive-folders")
        .with(user(username)).with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(form)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name", is("S3 Study Root")))
        .andExpect(jsonPath("$.path", is("test/")))
        ;

  }

  @Test
  public void createStudyWithBucketFolderTest() throws Exception {

    registerRootS3FolderTest();

    // Create without S3 folder

    Program program = programRepository.findByName("Clinical Program A")
        .orElseThrow(RecordNotFoundException::new);
    User user = userRepository.findByEmail("jsmith@email.com")
        .orElseThrow(RecordNotFoundException::new);

    Study study = new Study();
    study.setStatus(Status.ACTIVE);
    study.setName("New Study X");
    study.setProgram(program);
    study.setDescription("This is a test");
    study.setLegacy(false);
    study.setStartDate(new Date());
    study.setOwner(user);
    study.setUsers(Collections.singleton(user));
    StudyFormDto form = studyMapper.toStudyForm(study);

    mockMvc
        .perform(
            post("/api/internal/study/")
                .with(user(user.getEmail())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(form)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is("CPA-103")))
        .andExpect(jsonPath("$.storageFolders", hasSize(1)))
    ;

    Study created = studyRepository.findByCode("CPA-103")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(1, created.getStorageFolders().size());
    StorageDriveFolder folder = folderRepository.findByStudyId(created.getId()).get(0);
    Assert.assertNotEquals(DriveType.S3, folder.getStorageDrive().getDriveType());

    // Create with S3 folder
    StorageDriveFolder s3Root = storageDriveFolderService.findStudyRootFolders()
        .stream()
        .filter(f -> f.getStorageDrive().getDriveType() == DriveType.S3)
        .findFirst()
        .orElseThrow(() -> new RecordNotFoundException("No S3 root folder found"));

    study = new Study();
    study.setStatus(Status.ACTIVE);
    study.setName("New Study Y");
    study.setProgram(program);
    study.setDescription("This is a test");
    study.setLegacy(false);
    study.setStartDate(new Date());
    study.setOwner(user);
    study.setUsers(Collections.singleton(user));
    form = studyMapper.toStudyForm(study);
    form.setUseS3(true);
    form.setS3FolderId(s3Root.getId());

    mockMvc
        .perform(
            post("/api/internal/study/")
                .with(user(user.getEmail())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(form)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is("CPA-104")))
        .andExpect(jsonPath("$.storageFolders", hasSize(2)))
    ;

    created = studyRepository.findByCode("CPA-104")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(2, created.getStorageFolders().size());
    folder = folderRepository.findByStudyId(created.getId())
        .stream()
        .filter(f -> f.getStorageDrive().getDriveType() == DriveType.S3)
        .findFirst()
        .orElseThrow(() -> new RecordNotFoundException("No S3 folder found"));
    Assert.assertNotNull(folder);
    Assert.assertEquals(s3Root.getStorageDrive().getId(), folder.getStorageDrive().getId());

  }

  @Test
  public void createAssayWithBucketFolderTest() throws Exception {

    createStudyWithBucketFolderTest();

    // Create without S3 folder

    Study study = studyRepository.findByCode("CPA-103")
        .orElseThrow(RecordNotFoundException::new);
    AssayType assayType = assayTypeRepository.findById(assayTypeRepository.findAll().get(0).getId())
        .orElseThrow(RecordNotFoundException::new);
    User user = userRepository.findByEmail("jsmith@email.com")
        .orElseThrow(RecordNotFoundException::new);

    Assay assay = new Assay();
    assay.setStatus(Status.ACTIVE);
    assay.setName("New Assay X");
    assay.setStudy(study);
    assay.setDescription("This is a test");
    assay.setStartDate(new Date());
    assay.setOwner(user);
    assay.setUsers(Collections.singleton(user));
    assay.setAssayType(assayType);
    AssayFormDto form = assayMapper.toAssayForm(assay);
    form.setUseS3(false);

    mockMvc
        .perform(
            post("/api/internal/study/" + study.getCode() + "/assays/")
                .with(user(user.getEmail())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(form)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is("CPA-103-101")))
        .andExpect(jsonPath("$.storageFolders", hasSize(1)))
    ;

    Assay created = assayRepository.findByCode("CPA-103-101")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(1, created.getStorageFolders().size());
    StorageDriveFolder folder = folderRepository.findByAssayId(created.getId()).get(0);
    Assert.assertNotEquals(DriveType.S3, folder.getStorageDrive().getDriveType());

    // Create with S3 folder
    StorageDriveFolder s3Root = storageDriveFolderService.findStudyRootFolders()
        .stream()
        .filter(f -> f.getStorageDrive().getDriveType() == DriveType.S3)
        .findFirst()
        .orElseThrow(() -> new RecordNotFoundException("No S3 root folder found"));

    assay = new Assay();
    assay.setStatus(Status.ACTIVE);
    assay.setName("New Assay Y");
    assay.setStudy(study);
    assay.setDescription("This is a test");
    assay.setStartDate(new Date());
    assay.setOwner(user);
    assay.setAssayType(assayType);
    assay.setUsers(Collections.singleton(user));
    form = assayMapper.toAssayForm(assay);
    form.setUseS3(true);
    form.setS3FolderId(s3Root.getId());

    mockMvc
        .perform(
            post("/api/internal/study/" + study.getCode() + "/assays/")
                .with(user(user.getEmail())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(form)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is("CPA-103-102")))
        .andExpect(jsonPath("$.storageFolders", hasSize(2)))
    ;

    created = assayRepository.findByCode("CPA-103-102")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(2, created.getStorageFolders().size());
    folder = folderRepository.findByAssayId(created.getId())
        .stream()
        .filter(f -> f.getStorageDrive().getDriveType() == DriveType.S3)
        .findFirst()
        .orElseThrow(() -> new RecordNotFoundException("No S3 folder found"));
    Assert.assertNotNull(folder);
    Assert.assertEquals(s3Root.getStorageDrive().getId(), folder.getStorageDrive().getId());

  }

}
