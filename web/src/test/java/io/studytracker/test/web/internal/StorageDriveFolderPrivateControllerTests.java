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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.studytracker.Application;
import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.service.UserService;
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
        .andExpect(jsonPath("$", hasSize(999999)))
        ;

    mockMvc.perform(MockMvcRequestBuilders.get("/api/internal/storage-drive-folders?studyRoot=true")
            .with(user(username))
            .with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(999999)))
    ;

    mockMvc.perform(MockMvcRequestBuilders.get("/api/internal/storage-drive-folders?browserRoot=true")
            .with(user(username))
            .with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(999999)))
    ;

  }

  @Test
  public void findByIdTest() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/internal/storage-drive-folders/999999999")
            .with(user(username))
            .with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        ;
  }


}
