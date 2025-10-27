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

package io.studytracker.test.benchling;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.studytracker.Application;
import io.studytracker.example.ExampleDataRunner;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles({"web-benchling-test", "example"})
public class NotebookServiceControllerTest {

  private static final int PROJECT_FOLDER_COUNT = 5;

  @Autowired private ExampleDataRunner exampleDataRunner;

  @Autowired private MockMvc mockMvc;

  @Autowired private UserService userService;

  private String username;

  @Before
  public void doBefore() {
    exampleDataRunner.populateDatabase();
    username = userService.findAll().get(0).getEmail();
  }

  @Test
  public void findAllProjectFolderTests() throws Exception {
    mockMvc
        .perform(get("/api/internal/eln/project-folders")
            .with(user(username)).with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(PROJECT_FOLDER_COUNT)))
//        .andExpect(jsonPath("$[0]", hasKey("parentFolder")))
//        .andExpect(jsonPath("$[0].parentFolder", nullValue()))
        .andExpect(jsonPath("$[0]", hasKey("name")))
        .andExpect(jsonPath("$[0].name", notNullValue()))
        .andExpect(jsonPath("$[0]", hasKey("url")))
        .andExpect(jsonPath("$[0].url", notNullValue()))
        .andExpect(jsonPath("$[0]", hasKey("referenceId")))
        .andExpect(jsonPath("$[0].referenceId", startsWith("lib_")))
    ;
  }

}
