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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.studytracker.Application;
import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.mapper.AssayTaskMapper;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayTask;
import io.studytracker.model.TaskStatus;
import io.studytracker.model.User;
import io.studytracker.repository.AssayRepository;
import io.studytracker.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
@ActiveProfiles({"web-test", "example"})
public class AssayTaskControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private AssayRepository assayRepository;

  @Autowired private ExampleDataGenerator exampleDataGenerator;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserService userService;

  @Autowired private AssayTaskMapper mapper;

  private String username;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
    username = userService.findAll().get(0).getUsername();
  }

  @Test
  public void findAssayTasksTest() throws Exception {
    Assay assay =
        assayRepository.findByCode("PPB-10001-001").orElseThrow(RecordNotFoundException::new);

    mockMvc
        .perform(get("/api/assay/" + assay.getCode() + "/tasks").with(user(username)).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", hasKey("label")))
        .andExpect(jsonPath("$[0].label", is("My task")))
        .andExpect(jsonPath("$[0]", hasKey("order")))
        .andExpect(jsonPath("$[0].order", is(0)))
        .andExpect(jsonPath("$[0]", hasKey("status")))
        .andExpect(jsonPath("$[0].status", is("TODO")));
  }

  @Test
  public void createTaskTest() throws Exception {
    Assay assay =
        assayRepository.findByCode("PPB-10001-001").orElseThrow(RecordNotFoundException::new);
    User user = assay.getOwner();

    AssayTask task = new AssayTask();
    task.setLabel("New task");
    task.setStatus(TaskStatus.TODO);

    mockMvc
        .perform(
            post("/api/assay/xxxxxxx/tasks")
                .with(user(user.getUsername())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mapper.toDto(task))))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNotFound());

    mockMvc
        .perform(
            post("/api/assay/" + assay.getCode() + "/tasks")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mapper.toDto(task))))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isUnauthorized());

    mockMvc
        .perform(
            post("/api/assay/" + assay.getCode() + "/tasks")
                .with(user(user.getUsername())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mapper.toDto(task))))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/api/assay/" + assay.getCode() + "/tasks").with(user(username)).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  public void updateTaskTest() throws Exception {
    Assay assay =
        assayRepository.findByCode("PPB-10001-001").orElseThrow(RecordNotFoundException::new);
    User user = assay.getOwner();

    AssayTask task = assay.getTasks().stream().findFirst().get();
    task.setStatus(TaskStatus.COMPLETE);

    mockMvc
        .perform(
            put("/api/assay/xxxxxxx/tasks")
                .with(user(user.getUsername())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mapper.toDto(task))))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNotFound());

    mockMvc
        .perform(
            put("/api/assay/" + assay.getCode() + "/tasks")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mapper.toDto(task))))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isUnauthorized());

    mockMvc
        .perform(
            put("/api/assay/" + assay.getCode() + "/tasks")
                .with(user(user.getUsername())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mapper.toDto(task))))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/api/assay/" + assay.getCode() + "/tasks").with(user(username)).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", hasKey("status")))
        .andExpect(jsonPath("$[0].status", is("COMPLETE")));
  }

  @Test
  public void deleteTaskTest() throws Exception {
    Assay assay =
        assayRepository.findByCode("PPB-10001-001").orElseThrow(RecordNotFoundException::new);
    User user = assay.getOwner();

    AssayTask task = assay.getTasks().stream().findFirst().get();

    mockMvc
        .perform(
            delete("/api/assay/xxxxxxx/tasks")
                .with(user(user.getUsername())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mapper.toDto(task))))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNotFound());

    mockMvc
        .perform(
            delete("/api/assay/" + assay.getCode() + "/tasks")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mapper.toDto(task))))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isUnauthorized());

    mockMvc
        .perform(
            delete("/api/assay/" + assay.getCode() + "/tasks")
                .with(user(user.getUsername())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mapper.toDto(task))))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/api/assay/" + assay.getCode() + "/tasks").with(user(username)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }
}
