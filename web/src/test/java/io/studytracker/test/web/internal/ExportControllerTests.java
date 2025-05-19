/*
 * Copyright 2019-2025 the original author or authors.
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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.studytracker.Application;
import io.studytracker.example.ExampleDataRunner;
import io.studytracker.model.User;
import io.studytracker.model.UserType;
import io.studytracker.service.UserService;
import io.studytracker.test.web.api.AbstractApiControllerTests;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles({"example", "test"})
public class ExportControllerTests extends AbstractApiControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserService userService;

  @Autowired
  private ExampleDataRunner exampleDataRunner;

  private User nonAdminUser;
  private User adminUser;

  @Before
  public void setUp() {
    exampleDataRunner.clearDatabase();
    exampleDataRunner.populateDatabase();
    nonAdminUser = userService.findByEmail("jsmith@email.com").orElseThrow();
    adminUser = userService.findByEmail("rblack@email.com").orElseThrow();
  }

  @Test
  public void unauthorizedFetchTest() throws Exception {
    mockMvc.perform(get("/api/internal/export")
        .with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().is3xxRedirection());
  }

  @Test
  public void nonAdminFetchTest() throws Exception {
    mockMvc.perform(get("/api/internal/export")
            .with(user(nonAdminUser.getEmail()))
            .with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isForbidden());
  }

  @Test
  public void adminFetchTest() throws Exception {
    mockMvc.perform(get("/api/internal/export")
        .with(user(adminUser.getEmail()))
            .with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$", hasKey("jobId")))
        .andExpect(jsonPath("$.jobId", not(nullValue())))
        .andExpect(jsonPath("$", hasKey("status")))
        .andExpect(jsonPath("$.status", is("STARTED")))
        .andExpect(jsonPath("$", hasKey("message")))
        .andExpect(jsonPath("$.message", not(nullValue())));
  }
}