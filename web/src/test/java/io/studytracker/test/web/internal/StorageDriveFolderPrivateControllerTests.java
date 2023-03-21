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

package io.studytracker.test.web.internal;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.studytracker.Application;
import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.form.StorageDriveFolderFormDto;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.model.Study;
import io.studytracker.repository.StudyRepository;
import io.studytracker.service.UserService;
import io.studytracker.storage.StorageDriveFolderService;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles({"web-test", "example"})
public class StorageDriveFolderPrivateControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private ExampleDataGenerator exampleDataGenerator;

  @Autowired private UserService userService;

  @Autowired private StudyRepository studyRepository;

  @Autowired private StorageDriveFolderService storageDriveFolderService;

  @Autowired private ObjectMapper objectMapper;

  private String username;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
    username = userService.findAll().get(0).getEmail();
  }

  @Test
  public void findAllFoldersTest() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders.get("/api/internal/storage-drive-folders")
        .with(user(username))
        .with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(ExampleDataGenerator.STORAGE_DRIVE_FOLDER_COUNT)))
        ;

    mockMvc.perform(MockMvcRequestBuilders.get("/api/internal/storage-drive-folders?studyRoot=true")
            .with(user(username))
            .with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(ExampleDataGenerator.STUDY_ROOT_FOLDER_COUNT)))
        .andExpect(jsonPath("$[0]", hasKey("studyRoot")))
        .andExpect(jsonPath("$[0].studyRoot", is(true)))
    ;

    mockMvc.perform(MockMvcRequestBuilders.get("/api/internal/storage-drive-folders?browserRoot=true")
            .with(user(username))
            .with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(ExampleDataGenerator.BROWSER_ROOT_FOLDER_COUNT)))
        .andExpect(jsonPath("$[0]", hasKey("browserRoot")))
        .andExpect(jsonPath("$[0].browserRoot", is(true)))
    ;

  }

  @Test
  public void findByIdTest() throws Exception {

    Study study = studyRepository.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    StorageDriveFolder folder = study.getStorageFolders()
        .stream()
        .filter(f -> f.isPrimary())
        .findFirst()
        .orElseThrow(RecordNotFoundException::new)
        .getStorageDriveFolder();

    mockMvc.perform(MockMvcRequestBuilders.get("/api/internal/storage-drive-folders/" + folder.getId())
            .with(user(username))
            .with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", is(folder.getId().intValue())))
        .andExpect(jsonPath("$", hasKey("name")))
        .andExpect(jsonPath("$.name", is(folder.getName())))
        .andExpect(jsonPath("$", hasKey("path")))
        .andExpect(jsonPath("$.path", is(folder.getPath())))
        .andExpect(jsonPath("$", hasKey("studyRoot")))
        .andExpect(jsonPath("$.studyRoot", is(false)))
        ;
  }

  @Test
  public void registerRootFolderTest() throws Exception {
    List<StorageDriveFolder> rootFolders = storageDriveFolderService.findBrowserRootFolders();
    Assert.assertFalse(rootFolders.isEmpty());
    Assert.assertEquals(ExampleDataGenerator.BROWSER_ROOT_FOLDER_COUNT, rootFolders.size());
    StorageDriveFolder rootFolder = rootFolders.get(0);
    System.out.println(rootFolder.getPath());

    StorageDriveFolderFormDto dto = new StorageDriveFolderFormDto();
    dto.setStorageDriveId(rootFolder.getStorageDrive().getId());
    dto.setName("test");
    dto.setPath(rootFolder.getPath() + "/test");
    dto.setWriteEnabled(true);
    dto.setBrowserRoot(true);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/internal/storage-drive-folders")
            .with(user(username))
            .with(csrf())
            .contentType("application/json")
            .content(objectMapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$", hasKey("name")))
        .andExpect(jsonPath("$.name", is(dto.getName())))
        .andExpect(jsonPath("$", hasKey("path")))
        .andExpect(jsonPath("$.path", is(dto.getPath())))
        .andExpect(jsonPath("$", hasKey("browserRoot")))
        .andExpect(jsonPath("$.browserRoot", is(true)))
        .andExpect(jsonPath("$", hasKey("studyRoot")))
        .andExpect(jsonPath("$.studyRoot", is(false)))
        .andExpect(jsonPath("$", hasKey("writeEnabled")))
        .andExpect(jsonPath("$.writeEnabled", is(true)))
        .andExpect(jsonPath("$", hasKey("deleteEnabled")))
        .andExpect(jsonPath("$.deleteEnabled", is(false)))
        ;

    rootFolders = storageDriveFolderService.findBrowserRootFolders();
    Assert.assertFalse(rootFolders.isEmpty());
    Assert.assertEquals(ExampleDataGenerator.BROWSER_ROOT_FOLDER_COUNT+1, rootFolders.size());

  }


}
