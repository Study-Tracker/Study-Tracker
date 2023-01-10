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
import static org.hamcrest.Matchers.notNullValue;
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
import io.studytracker.mapstruct.dto.form.AssayTaskFieldFormDto;
import io.studytracker.mapstruct.dto.form.AssayTaskFormDto;
import io.studytracker.mapstruct.dto.response.UserSlimDto;
import io.studytracker.mapstruct.mapper.AssayTaskMapper;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayTask;
import io.studytracker.model.CustomEntityFieldType;
import io.studytracker.model.TaskStatus;
import io.studytracker.model.User;
import io.studytracker.repository.AssayRepository;
import io.studytracker.service.UserService;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
    username = userService.findAll().get(0).getEmail();
  }

  @Test
  public void findAssayTasksTest() throws Exception {
    Assay assay =
        assayRepository.findByCode("PPB-10001-001").orElseThrow(RecordNotFoundException::new);

    mockMvc
        .perform(get("/api/internal/assay/" + assay.getCode() + "/tasks").with(user(username)).with(csrf()))
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

    AssayTaskFormDto task = new AssayTaskFormDto();
    task.setLabel("New task");
    task.setStatus(TaskStatus.TODO);

    mockMvc
        .perform(
            post("/api/internal/assay/xxxxxxx/tasks")
                .with(user(user.getEmail())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(task)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNotFound());

    mockMvc
        .perform(
            post("/api/internal/assay/" + assay.getCode() + "/tasks")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(task)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().is3xxRedirection());

    mockMvc
        .perform(
            post("/api/internal/assay/" + assay.getCode() + "/tasks")
                .with(user(user.getEmail())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(task)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());

    mockMvc
        .perform(get("/api/internal/assay/" + assay.getCode() + "/tasks")
            .with(user(username)).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  public void createTaskWithFieldsTest() throws Exception {
    Assay assay =
        assayRepository.findByCode("PPB-10001-001").orElseThrow(RecordNotFoundException::new);
    User user = assay.getOwner();

    AssayTaskFormDto task = new AssayTaskFormDto();
    task.setStatus(TaskStatus.TODO);
    task.setLabel("Test task");
    task.setAssignedTo(new UserSlimDto(user.getId()));
    task.setDueDate(new Date());

    Set<AssayTaskFieldFormDto> fields = new HashSet<>();
    AssayTaskFieldFormDto field = new AssayTaskFieldFormDto();
    field.setDisplayName("Test field 1");
    field.setFieldName("test_field_1");
    field.setType(CustomEntityFieldType.STRING);
    field.setFieldOrder(1);
    field.setActive(true);
    field.setDescription("This is a test");
    field.setRequired(true);
    fields.add(field);
    task.setFields(fields);

    mockMvc
        .perform(
            post("/api/internal/assay/" + assay.getCode() + "/tasks")
                .with(user(user.getEmail())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(task)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$", hasKey("label")))
        .andExpect(jsonPath("$.label", is("Test task")))
        .andExpect(jsonPath("$", hasKey("status")))
        .andExpect(jsonPath("$.status", is("TODO")))
        .andExpect(jsonPath("$", hasKey("assignedTo")))
        .andExpect(jsonPath("$.assignedTo", hasKey("id")))
        .andExpect(jsonPath("$.assignedTo.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$", hasKey("dueDate")))
        .andExpect(jsonPath("$.dueDate", notNullValue()))
        .andExpect(jsonPath("$", hasKey("fields")))
        .andExpect(jsonPath("$.fields", hasSize(1)))
        .andExpect(jsonPath("$.fields[0]", hasKey("displayName")))
        .andExpect(jsonPath("$.fields[0].displayName", is("Test field 1")))
        .andExpect(jsonPath("$.fields[0]", hasKey("fieldName")))
        .andExpect(jsonPath("$.fields[0].fieldName", is("test_field_1")))
        .andExpect(jsonPath("$.fields[0]", hasKey("type")))
        .andExpect(jsonPath("$.fields[0].type", is("STRING")))
        .andExpect(jsonPath("$.fields[0]", hasKey("fieldOrder")))
        .andExpect(jsonPath("$.fields[0].fieldOrder", is(1)))
        .andExpect(jsonPath("$.fields[0]", hasKey("active")))
        .andExpect(jsonPath("$.fields[0].active", is(true)))
        .andExpect(jsonPath("$.fields[0]", hasKey("description")))
        .andExpect(jsonPath("$.fields[0].description", is("This is a test")))
        .andExpect(jsonPath("$.fields[0]", hasKey("required")))
        .andExpect(jsonPath("$.fields[0].required", is(true)))
    ;

    mockMvc
        .perform(get("/api/internal/assay/" + assay.getCode() + "/tasks")
            .with(user(username)).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  public void updateTaskTest() throws Exception {
    Assay assay =
        assayRepository.findByCode("PPB-10001-001").orElseThrow(RecordNotFoundException::new);
    User user = assay.getOwner();

    AssayTask task = assay.getTasks().stream().findFirst().get();
    AssayTaskFormDto dto = new AssayTaskFormDto();
    dto.setId(task.getId());
    dto.setLabel(task.getLabel());
    dto.setOrder(task.getOrder());
    dto.setStatus(TaskStatus.COMPLETE);

    mockMvc
        .perform(
            put("/api/internal/assay/xxxxxxx/tasks")
                .with(user(user.getEmail())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNotFound());

    mockMvc
        .perform(
            put("/api/internal/assay/" + assay.getCode() + "/tasks")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().is3xxRedirection());

    mockMvc
        .perform(
            put("/api/internal/assay/" + assay.getCode() + "/tasks")
                .with(user(user.getEmail())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/api/internal/assay/" + assay.getCode() + "/tasks").with(user(username)).with(csrf()))
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
            delete("/api/internal/assay/xxxxxxx/tasks")
                .with(user(user.getEmail())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mapper.toDetailsDto(task))))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNotFound());

    mockMvc
        .perform(
            delete("/api/internal/assay/" + assay.getCode() + "/tasks")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mapper.toDetailsDto(task))))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().is3xxRedirection());

    mockMvc
        .perform(
            delete("/api/internal/assay/" + assay.getCode() + "/tasks")
                .with(user(user.getEmail())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mapper.toDetailsDto(task))))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/api/internal/assay/" + assay.getCode() + "/tasks").with(user(username)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }
}
