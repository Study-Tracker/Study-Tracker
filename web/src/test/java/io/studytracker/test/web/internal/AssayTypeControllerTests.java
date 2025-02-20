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
import io.studytracker.example.ExampleDataRunner;
import io.studytracker.mapstruct.dto.form.AssayTypeFieldFormDto;
import io.studytracker.mapstruct.dto.form.AssayTypeFormDto;
import io.studytracker.mapstruct.dto.form.AssayTypeTaskFieldFormDto;
import io.studytracker.mapstruct.dto.form.AssayTypeTaskFormDto;
import io.studytracker.mapstruct.mapper.AssayTaskMapper;
import io.studytracker.model.AssayType;
import io.studytracker.model.CustomEntityFieldType;
import io.studytracker.model.TaskStatus;
import io.studytracker.model.User;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.AssayTypeRepository;
import io.studytracker.repository.AssayTypeTaskFieldRepository;
import io.studytracker.repository.AssayTypeTaskRepository;
import io.studytracker.service.UserService;
import java.util.Collections;
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
public class AssayTypeControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private AssayRepository assayRepository;

  @Autowired private AssayTypeRepository assayTypeRepository;

  @Autowired private AssayTypeTaskRepository assayTypeTaskRepository;

  @Autowired private AssayTypeTaskFieldRepository assayTypeTaskFieldRepository;

  @Autowired private ExampleDataRunner exampleDataRunner;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserService userService;

  @Autowired private AssayTaskMapper mapper;

  private String username;

  @Before
  public void doBefore() {
    exampleDataRunner.populateDatabase();
    username = userService.findAll().get(0).getEmail();
  }

  @Test
  public void findAssayTypesTest() throws Exception {
    mockMvc
        .perform(get("/api/internal/assaytype").with(user(username)).with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
    ;
  }

  @Test
  public void createAssayTypeTest() throws Exception {
    User user = userService.findByUsername(username).orElseThrow();

    AssayTypeFormDto assayTypeFormDto = new AssayTypeFormDto();
    assayTypeFormDto.setActive(true);
    assayTypeFormDto.setName("Test assay type");
    assayTypeFormDto.setDescription("This is a test");

    mockMvc
        .perform(
            post("/api/internal/assaytype")
                .with(user(user.getEmail())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(assayTypeFormDto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$", hasKey("name")))
        .andExpect(jsonPath("$.name", is("Test assay type")))
        .andExpect(jsonPath("$", hasKey("description")))
        .andExpect(jsonPath("$.description", is("This is a test")))
        .andExpect(jsonPath("$", hasKey("fields")))
        .andExpect(jsonPath("$.fields", hasSize(0)))
        .andExpect(jsonPath("$", hasKey("tasks")))
        .andExpect(jsonPath("$.tasks", hasSize(0)))
    ;

  }

  @Test
  public void createTypeWithFieldsTest() throws Exception {

    User user = userService.findByUsername(username).orElseThrow();

    AssayTypeFormDto assayTypeFormDto = new AssayTypeFormDto();
    assayTypeFormDto.setActive(true);
    assayTypeFormDto.setName("Test assay type");
    assayTypeFormDto.setDescription("This is a test");

    Set<AssayTypeFieldFormDto> fields = new HashSet<>();
    AssayTypeFieldFormDto field = new AssayTypeFieldFormDto();
    field.setDisplayName("Test field 1");
    field.setFieldName("test_field_1");
    field.setType(CustomEntityFieldType.STRING);
    field.setFieldOrder(1);
    field.setActive(true);
    field.setDescription("This is a test");
    field.setRequired(true);
    fields.add(field);
    assayTypeFormDto.setFields(fields);

    mockMvc
        .perform(
            post("/api/internal/assaytype")
                .with(user(user.getEmail())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(assayTypeFormDto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$", hasKey("name")))
        .andExpect(jsonPath("$.name", is("Test assay type")))
        .andExpect(jsonPath("$", hasKey("description")))
        .andExpect(jsonPath("$.description", is("This is a test")))
        .andExpect(jsonPath("$", hasKey("tasks")))
        .andExpect(jsonPath("$.tasks", hasSize(0)))
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

  }

  @Test
  public void createTypeWithTasksTest() throws Exception {
    User user = userService.findByUsername(username).orElseThrow();

    AssayTypeFormDto assayTypeFormDto = new AssayTypeFormDto();
    assayTypeFormDto.setActive(true);
    assayTypeFormDto.setName("Test assay type");
    assayTypeFormDto.setDescription("This is a test");

    AssayTypeTaskFormDto task = new AssayTypeTaskFormDto();
    task.setStatus(TaskStatus.TODO);
    task.setLabel("Test task");
    task.setOrder(0);

    Set<AssayTypeTaskFieldFormDto> fields = new HashSet<>();
    AssayTypeTaskFieldFormDto field = new AssayTypeTaskFieldFormDto();
    field.setDisplayName("Test field 1");
    field.setFieldName("test_field_1");
    field.setType(CustomEntityFieldType.STRING);
    field.setFieldOrder(1);
    field.setActive(true);
    field.setDescription("This is a test");
    field.setRequired(true);
    fields.add(field);
    task.setFields(fields);

    assayTypeFormDto.setTasks(Collections.singleton(task));

    mockMvc
        .perform(
            post("/api/internal/assaytype")
                .with(user(user.getEmail())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(assayTypeFormDto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$", hasKey("name")))
        .andExpect(jsonPath("$.name", is("Test assay type")))
        .andExpect(jsonPath("$", hasKey("description")))
        .andExpect(jsonPath("$.description", is("This is a test")))
        .andExpect(jsonPath("$", hasKey("fields")))
        .andExpect(jsonPath("$.fields", hasSize(0)))
        .andExpect(jsonPath("$", hasKey("tasks")))
        .andExpect(jsonPath("$.tasks", hasSize(1)))
        .andExpect(jsonPath("$.tasks[0]", hasKey("label")))
        .andExpect(jsonPath("$.tasks[0].label", is("Test task")))
        .andExpect(jsonPath("$.tasks[0]", hasKey("status")))
        .andExpect(jsonPath("$.tasks[0].status", is("TODO")))
        .andExpect(jsonPath("$.tasks[0]", hasKey("fields")))
        .andExpect(jsonPath("$.tasks[0].fields", hasSize(1)))
        .andExpect(jsonPath("$.tasks[0].fields[0]", hasKey("displayName")))
        .andExpect(jsonPath("$.tasks[0].fields[0].displayName", is("Test field 1")))
        .andExpect(jsonPath("$.tasks[0].fields[0]", hasKey("fieldName")))
        .andExpect(jsonPath("$.tasks[0].fields[0].fieldName", is("test_field_1")))
        .andExpect(jsonPath("$.tasks[0].fields[0]", hasKey("type")))
        .andExpect(jsonPath("$.tasks[0].fields[0].type", is("STRING")))
        .andExpect(jsonPath("$.tasks[0].fields[0]", hasKey("fieldOrder")))
        .andExpect(jsonPath("$.tasks[0].fields[0].fieldOrder", is(1)))
        .andExpect(jsonPath("$.tasks[0].fields[0]", hasKey("active")))
        .andExpect(jsonPath("$.tasks[0].fields[0].active", is(true)))
        .andExpect(jsonPath("$.tasks[0].fields[0]", hasKey("description")))
        .andExpect(jsonPath("$.tasks[0].fields[0].description", is("This is a test")))
        .andExpect(jsonPath("$.tasks[0].fields[0]", hasKey("required")))
        .andExpect(jsonPath("$.tasks[0].fields[0].required", is(true)))
    ;

    AssayType created = assayTypeRepository.findAll().stream()
        .filter(at -> at.getName().equals("Test assay type"))
        .findFirst()
        .orElseThrow();

    mockMvc
        .perform(
            get("/api/internal/assaytype/" + created.getId())
                .with(user(user.getEmail())).with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$", hasKey("name")))
        .andExpect(jsonPath("$.name", is("Test assay type")))
        .andExpect(jsonPath("$", hasKey("description")))
        .andExpect(jsonPath("$.description", is("This is a test")))
        .andExpect(jsonPath("$", hasKey("fields")))
        .andExpect(jsonPath("$.fields", hasSize(0)))
        .andExpect(jsonPath("$", hasKey("tasks")))
        .andExpect(jsonPath("$.tasks", hasSize(1)))
        .andExpect(jsonPath("$.tasks[0]", hasKey("label")))
        .andExpect(jsonPath("$.tasks[0].label", is("Test task")))
        .andExpect(jsonPath("$.tasks[0]", hasKey("status")))
        .andExpect(jsonPath("$.tasks[0].status", is("TODO")))
        .andExpect(jsonPath("$.tasks[0]", hasKey("fields")))
        .andExpect(jsonPath("$.tasks[0].fields", hasSize(1)))
        .andExpect(jsonPath("$.tasks[0].fields[0]", hasKey("displayName")))
        .andExpect(jsonPath("$.tasks[0].fields[0].displayName", is("Test field 1")))
        .andExpect(jsonPath("$.tasks[0].fields[0]", hasKey("fieldName")))
        .andExpect(jsonPath("$.tasks[0].fields[0].fieldName", is("test_field_1")))
        .andExpect(jsonPath("$.tasks[0].fields[0]", hasKey("type")))
        .andExpect(jsonPath("$.tasks[0].fields[0].type", is("STRING")))
        .andExpect(jsonPath("$.tasks[0].fields[0]", hasKey("fieldOrder")))
        .andExpect(jsonPath("$.tasks[0].fields[0].fieldOrder", is(1)))
        .andExpect(jsonPath("$.tasks[0].fields[0]", hasKey("active")))
        .andExpect(jsonPath("$.tasks[0].fields[0].active", is(true)))
        .andExpect(jsonPath("$.tasks[0].fields[0]", hasKey("description")))
        .andExpect(jsonPath("$.tasks[0].fields[0].description", is("This is a test")))
        .andExpect(jsonPath("$.tasks[0].fields[0]", hasKey("required")))
        .andExpect(jsonPath("$.tasks[0].fields[0].required", is(true)))
    ;

  }

  @Test
  public void updateTypeTest() throws Exception {
    AssayType assayType = assayTypeRepository.findAll().stream().findFirst().orElseThrow();
    AssayTypeFormDto dto = new AssayTypeFormDto();
    dto.setId(assayType.getId());
    dto.setName("Updated name");
    dto.setDescription("Updated description");
    dto.setActive(assayType.isActive());

    mockMvc
        .perform(
            put("/api/internal/assaytype/9999999")
                .with(user(username)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNotFound());

    mockMvc
        .perform(
            put("/api/internal/assaytype/" + assayType.getId())
                .with(user(username)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", is(assayType.getId().intValue())))
        .andExpect(jsonPath("$", hasKey("name")))
        .andExpect(jsonPath("$.name", is("Updated name")))
        .andExpect(jsonPath("$", hasKey("description")))
        .andExpect(jsonPath("$.description", is("Updated description")))
    ;

  }

  @Test
  public void deleteAssayTypeTest() throws Exception {
    AssayType assayType = assayTypeRepository.findAll().stream().findFirst().orElseThrow();

    mockMvc
        .perform(get("/api/internal/assaytype/" + assayType.getId())
            .with(user(username)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", is(assayType.getId().intValue())))
        .andExpect(jsonPath("$", hasKey("active")))
        .andExpect(jsonPath("$.active", is(true)))
    ;

    mockMvc
        .perform(
            delete("/api/internal/assaytype/" + assayType.getId())
                .with(user(username)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/api/internal/assaytype/" + assayType.getId())
            .with(user(username)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", is(assayType.getId().intValue())))
        .andExpect(jsonPath("$", hasKey("active")))
        .andExpect(jsonPath("$.active", is(false)));
  }
}
