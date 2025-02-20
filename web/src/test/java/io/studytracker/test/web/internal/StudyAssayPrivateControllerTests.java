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
import io.studytracker.example.ExampleAssayGenerator;
import io.studytracker.example.ExampleDataRunner;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.form.AssayFormDto;
import io.studytracker.mapstruct.dto.form.AssayTaskFieldFormDto;
import io.studytracker.mapstruct.dto.form.AssayTaskFormDto;
import io.studytracker.mapstruct.dto.response.AssayTypeDetailsDto;
import io.studytracker.mapstruct.dto.response.UserSlimDto;
import io.studytracker.mapstruct.mapper.AssayMapper;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayType;
import io.studytracker.model.CustomEntityFieldType;
import io.studytracker.model.Status;
import io.studytracker.model.Study;
import io.studytracker.model.TaskStatus;
import io.studytracker.model.User;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.AssayTypeRepository;
import io.studytracker.repository.StudyRepository;
import io.studytracker.repository.UserRepository;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Assert;
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
public class StudyAssayPrivateControllerTests {

  private static final int NUM_ASSAYS = ExampleAssayGenerator.ASSAY_COUNT;

  @Autowired private MockMvc mockMvc;

  @Autowired private ExampleDataRunner exampleDataRunner;

  @Autowired private StudyRepository studyRepository;

  @Autowired private AssayRepository assayRepository;

  @Autowired private AssayTypeRepository assayTypeRepository;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserRepository userRepository;

  @Autowired private AssayMapper assayMapper;

  private String username;

  @Before
  public void doBefore() {
    exampleDataRunner.populateDatabase();
    username = userRepository.findAll().get(0).getEmail();
  }

  @Test
  public void findStudyAssaysTest() throws Exception {
    mockMvc
        .perform(get("/api/internal/study/PPB-10001/assays").with(user(username)).with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]", hasKey("code")))
        .andExpect(jsonPath("$[0].code", Matchers.oneOf("PPB-10001-001", "PPB-10001-00002")));

    mockMvc
        .perform(get("/api/internal/study/CPA-10001/assays").with(user(username)).with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));

    mockMvc
        .perform(get("/api/internal/study/PPB-XXXX/assays").with(user(username)).with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNotFound());
  }

  @Test
  public void findAssayByIdTest() throws Exception {
    mockMvc
        .perform(get("/api/internal/study/PPB-10001/assays/PPB-10001-001").with(user(username)).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is("PPB-10001-001")))
        .andExpect(jsonPath("$", hasKey("assayType")))
        .andExpect(jsonPath("$.assayType", hasKey("name")))
        .andExpect(jsonPath("$.assayType.name", is("Generic")));

    mockMvc
        .perform(get("/api/internal/study/PPB-10001/assay/PPB-10001-XXXXX").with(user(username)).with(csrf()))
        .andExpect(status().isNotFound());
  }

  @Test
  public void createTest() throws Exception {

    AssayType assayType =
        assayTypeRepository.findByName("Histology").orElseThrow(RecordNotFoundException::new);
    AssayTypeDetailsDto assayTypeDto = new AssayTypeDetailsDto();
    assayTypeDto.setId(assayType.getId());

    Assert.assertEquals(NUM_ASSAYS, assayRepository.count());
    Study study = studyRepository.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    User user = study.getOwner();
    UserSlimDto userDto = new UserSlimDto();
    userDto.setId(user.getId());

    AssayFormDto assay = new AssayFormDto();
    assay.setActive(true);
    assay.setName("Test assay");
    assay.setDescription("This is a test");
    assay.setStatus(Status.IN_PLANNING);
    assay.setStartDate(new Date());
    assay.setAssayType(assayTypeDto);
    assay.setOwner(userDto);
    assay.setUsers(Collections.singleton(userDto));
    assay.setCreatedBy(userDto);
    assay.setLastModifiedBy(userDto);
    assay.setUpdatedAt(new Date());
    assay.setAttributes(Collections.singletonMap("key", "value"));
    assay.setUseNotebook(false);

    Map<String, Object> fields = new LinkedHashMap<>();
    fields.put("number_of_slides", 10);
    fields.put("antibodies", "AKT1, AKT2, AKT3");
    fields.put("concentration", 1.2345F);
    fields.put("date", new Date());
    fields.put("external", true);
    fields.put("stain", "DAPI");
    assay.setFields(fields);

    Set<AssayTaskFormDto> tasks = new HashSet<>();
    AssayTaskFormDto task = new AssayTaskFormDto();
    task.setLabel("Step 1");
    task.setStatus(TaskStatus.TODO);
    task.setOrder(0);
    task.setAssignedTo(userDto);
    task.setDueDate(new Date());
    tasks.add(task);
    AssayTaskFieldFormDto taskField = new AssayTaskFieldFormDto();
    taskField.setDisplayName("Text field");
    taskField.setFieldName("text_field");
    taskField.setFieldOrder(0);
    taskField.setActive(true);
    taskField.setRequired(true);
    taskField.setType(CustomEntityFieldType.STRING);
    taskField.setDescription("This is a text field");
    task.setFields(Collections.singleton(taskField));
    assay.setTasks(tasks);

    mockMvc
        .perform(
            post("/api/internal/study/XXXXXX/assays/")
                .with(user(user.getEmail())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(assay)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNotFound());

    mockMvc
        .perform(
            post("/api/internal/study/" + study.getCode() + "/assays/")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(assay)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().is3xxRedirection());

    mockMvc
        .perform(
            post("/api/internal/study/" + study.getCode() + "/assays/")
                .with(user(user.getEmail())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(assay)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$", hasKey("name")))
        .andExpect(jsonPath("$.name", is("Test assay")))
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is("CPA-10001-001")))
    ;

    Assay created = assayRepository.findByStudyId(study.getId()).get(0);

    mockMvc
        .perform(get("/api/internal/assay/" + created.getId()).with(user(username)).with(csrf()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", is(created.getId().intValue())))
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is("CPA-10001-001")))
        .andExpect(jsonPath("$", hasKey("name")))
        .andExpect(jsonPath("$.name", is("Test assay")))
        .andExpect(jsonPath("$", hasKey("status")))
        .andExpect(jsonPath("$.status", is("IN_PLANNING")))
        .andExpect(jsonPath("$", hasKey("active")))
        .andExpect(jsonPath("$.active", is(true)))
        .andExpect(jsonPath("$", hasKey("assayType")))
        .andExpect(jsonPath("$.assayType", hasKey("name")))
        .andExpect(jsonPath("$.assayType.name", is("Histology")))
        .andExpect(jsonPath("$", hasKey("owner")))
        .andExpect(jsonPath("$.owner", hasKey("id")))
        .andExpect(jsonPath("$.owner.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$", hasKey("fields")))
        .andExpect(jsonPath("$.fields", hasKey("number_of_slides")))
        .andExpect(jsonPath("$.fields.number_of_slides", is(10)))
        .andExpect(jsonPath("$.fields", hasKey("antibodies")))
        .andExpect(jsonPath("$.fields.antibodies", is("AKT1, AKT2, AKT3")))
        .andExpect(jsonPath("$.fields", hasKey("concentration")))
        .andExpect(jsonPath("$.fields.concentration", is(1.2345)))
        .andExpect(jsonPath("$.fields", hasKey("date")))
        .andExpect(jsonPath("$.fields.date", notNullValue()))
        .andExpect(jsonPath("$.fields", hasKey("external")))
        .andExpect(jsonPath("$.fields.external", is(true)))
        .andExpect(jsonPath("$.fields", hasKey("stain")))
        .andExpect(jsonPath("$.fields.stain", is("DAPI")))
        .andExpect(jsonPath("$", hasKey("attributes")))
        .andExpect(jsonPath("$.attributes", hasKey("key")))
        .andExpect(jsonPath("$.attributes.key", is("value")))
        .andExpect(jsonPath("$", hasKey("tasks")))
        .andExpect(jsonPath("$.tasks", hasSize(1)))
        .andExpect(jsonPath("$.tasks[0]", hasKey("label")))
        .andExpect(jsonPath("$.tasks[0].label", is("Step 1")))
        .andExpect(jsonPath("$.tasks[0]", hasKey("status")))
        .andExpect(jsonPath("$.tasks[0].status", is("TODO")))
        .andExpect(jsonPath("$.tasks[0]", hasKey("order")))
        .andExpect(jsonPath("$.tasks[0].order", is(0)))
        .andExpect(jsonPath("$.tasks[0]", hasKey("assignedTo")))
        .andExpect(jsonPath("$.tasks[0].assignedTo", hasKey("id")))
        .andExpect(jsonPath("$.tasks[0].assignedTo.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.tasks[0]", hasKey("dueDate")))
        .andExpect(jsonPath("$.tasks[0].dueDate", notNullValue()))
        .andExpect(jsonPath("$.tasks[0]", hasKey("fields")))
        .andExpect(jsonPath("$.tasks[0].fields", hasSize(1)))
        .andExpect(jsonPath("$.tasks[0].fields[0]", hasKey("displayName")))
        .andExpect(jsonPath("$.tasks[0].fields[0].displayName", is("Text field")))
        .andExpect(jsonPath("$.tasks[0].fields[0]", hasKey("fieldName")))
        .andExpect(jsonPath("$.tasks[0].fields[0].fieldName", is("text_field")))
        .andExpect(jsonPath("$.tasks[0].fields[0]", hasKey("fieldOrder")))
        .andExpect(jsonPath("$.tasks[0].fields[0].fieldOrder", is(0)))
        .andExpect(jsonPath("$.tasks[0].fields[0]", hasKey("required")))
        .andExpect(jsonPath("$.tasks[0].fields[0].required", is(true)))
        .andExpect(jsonPath("$.tasks[0].fields[0]", hasKey("active")))
        .andExpect(jsonPath("$.tasks[0].fields[0].active", is(true)))
        .andExpect(jsonPath("$.tasks[0].fields[0]", hasKey("type")))
        .andExpect(jsonPath("$.tasks[0].fields[0].type", is("STRING")))
    ;
  }

  @Test
  public void updateAssayTest() throws Exception {

    mockMvc
        .perform(get("/api/internal/study/PPB-10001/assays/PPB-10001-001").with(user(username)).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("ACTIVE")));

    Assay assay =
        assayRepository.findByCode("PPB-10001-001").orElseThrow(RecordNotFoundException::new);
    assay.setStatus(Status.COMPLETE);

    UserSlimDto userDto = new UserSlimDto();
    userDto.setId(assay.getOwner().getId());

    AssayFormDto dto = new AssayFormDto();
    dto.setId(assay.getId());
    dto.setName(assay.getName());
    dto.setCode(assay.getCode());
    dto.setDescription(assay.getDescription());
    dto.setStatus(Status.COMPLETE);
    dto.setOwner(userDto);
    dto.setStartDate(assay.getStartDate());
    dto.setEndDate(assay.getEndDate());
    dto.setUsers(new HashSet<>(Collections.singletonList(userDto)));
    dto.setFields(assay.getFields());
    dto.setAttributes(assay.getAttributes());

    mockMvc
        .perform(
            put("/api/internal/study/PPB-10001/assays/PPB-10001-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto))
                .with(user(assay.getOwner().getEmail())).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("status")))
        .andExpect(jsonPath("$.status", is("COMPLETE")));
  }

  @Test
  public void deleteAssayTest() throws Exception {

    mockMvc
        .perform(get("/api/internal/study/PPB-10001/assays/PPB-10001-001").with(user(username)).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active", is(true)));

    mockMvc
        .perform(delete("/api/internal/study/PPB-10001/assays/PPB-10001-001").with(user(username)).with(csrf()))
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/api/internal/study/PPB-10001/assays/PPB-10001-001").with(user(username)).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active", is(false)));
  }
}
