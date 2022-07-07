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

package io.studytracker.test.web.controller;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles({"web-test", "example"})
public class AssayControllerTests {

  private static final int NUM_ASSAYS = ExampleDataGenerator.ASSAY_COUNT;

  @Autowired private MockMvc mockMvc;

  @Autowired private ExampleDataGenerator exampleDataGenerator;

  @Autowired private UserService userService;

  private String username;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
    username = userService.findAll().get(0).getUsername();
  }

  // Study methods

  @Test
  public void findAllTest() throws Exception {
    mockMvc
        .perform(get("/api/assay").with(user(username)).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(NUM_ASSAYS)))
        .andExpect(jsonPath("$[0]", hasKey("id")))
        .andExpect(jsonPath("$[0]", hasKey("name")))
        .andExpect(jsonPath("$[0]", hasKey("description")));
  }

  @Test
  public void findByIdTest() throws Exception {
    mockMvc
        .perform(get("/api/assay/PPB-10001-001").with(user(username)).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is("PPB-10001-001")))
        .andExpect(jsonPath("$", hasKey("status")))
        .andExpect(jsonPath("$.status", is("ACTIVE")))
        .andExpect(jsonPath("$", hasKey("assayType")))
        .andExpect(jsonPath("$.assayType", hasKey("name")))
        .andExpect(jsonPath("$.assayType.name", is("Generic")));
  }

  @Test
  public void findNonExistentAssayTest() throws Exception {
    mockMvc
        .perform(get("/api/assay/CPA-XXXX-XXXX").with(user(username)).with(csrf()))
        .andExpect(status().isNotFound());
  }
}
