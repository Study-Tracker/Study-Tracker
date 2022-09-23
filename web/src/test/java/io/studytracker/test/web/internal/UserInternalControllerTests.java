/*
 * Copyright 2022 the original author or authors.
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
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.studytracker.Application;
import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.mapstruct.dto.form.UserFormDto;
import io.studytracker.mapstruct.mapper.UserMapper;
import io.studytracker.model.User;
import io.studytracker.service.UserService;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles({"web-test", "example"})
public class UserInternalControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private ExampleDataGenerator exampleDataGenerator;

  @Autowired private UserService userService;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserMapper userMapper;

  private String username;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
    if (username == null) {
      username = userService.findAll().stream()
          .filter(User::isAdmin)
          .findFirst()
          .get()
          .getEmail();
    }
  }

  @Test
  public void unauthorizedFetchTest() throws Exception {
    mockMvc
        .perform(get("/api/internal/user"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isFound());
  }

  @Test
  public void findAllTest() throws Exception {
    mockMvc
        .perform(get("/api/internal/user").with(user(username)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0]", hasKey("id")));
  }

  @Test
  public void findUserByIdTest() throws Exception {

    User user = new User();
    user.setEmail("newperson@email.com");
    user.setDisplayName("New Person");
    user.setDepartment("IT");
    user.setTitle("IT Admin");
    user.setAdmin(true);
    userService.create(user);
    Assert.assertNotNull(user.getId());

    mockMvc
        .perform(
            get("/api/internal/user/" + user.getId())
                .with(user(username)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", not(nullValue())))
        .andExpect(jsonPath("$", hasKey("email")))
        .andExpect(jsonPath("$.email", is("newperson@email.com")));
  }

  @Test
  public void createUserTest() throws Exception {

    UserFormDto dto = new UserFormDto();
    dto.setEmail("newperson@email.com");
    dto.setDepartment("IT");
    dto.setTitle("IT Admin");
    dto.setAdmin(true);

    mockMvc.perform(post("/api/internal/user")
            .with(user(username))
            .with(csrf())
            .contentType("application/json")
            .content(objectMapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isBadRequest());

    dto.setDisplayName("New Person");

    mockMvc.perform(post("/api/internal/user")
            .with(user(username))
            .with(csrf())
        .contentType("application/json")
        .content(objectMapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", not(nullValue())))
        .andExpect(jsonPath("$", hasKey("email")))
        .andExpect(jsonPath("$.email", is("newperson@email.com")));

  }

  @Test
  public void updateUserTest() throws Exception {

    User user = new User();
    user.setEmail("newperson@email.com");
    user.setDisplayName("New Person");
    user.setDepartment("IT");
    user.setTitle("IT Admin");
    user.setAdmin(true);
    userService.create(user);
    Assert.assertNotNull(user.getId());

    UserFormDto dto = userMapper.toUserForm(user);
    dto.setTitle("Director of IT");

    mockMvc
        .perform(
            put("/api/v1/user/" + user.getId())
                .with(user(username))
                .with(csrf())
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", not(nullValue())))
        .andExpect(jsonPath("$", hasKey("title")))
        .andExpect(jsonPath("$.title", is("Director of IT")));
    }

}
