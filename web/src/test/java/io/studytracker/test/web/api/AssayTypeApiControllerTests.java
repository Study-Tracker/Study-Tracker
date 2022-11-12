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

package io.studytracker.test.web.api;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
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
import io.studytracker.mapstruct.dto.api.AssayTypeFieldPayloadDto;
import io.studytracker.mapstruct.dto.api.AssayTypePayloadDto;
import io.studytracker.mapstruct.dto.api.AssayTypeTaskPayloadDto;
import io.studytracker.model.AssayType;
import io.studytracker.model.CustomEntityFieldType;
import io.studytracker.model.TaskStatus;
import io.studytracker.repository.AssayTypeRepository;
import java.util.Collections;
import java.util.Set;
import org.junit.Assert;
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
public class AssayTypeApiControllerTests extends AbstractApiControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AssayTypeRepository assayTypeRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void findAllTest() throws Exception {
    mockMvc.perform(get("/api/v1/assay-type")
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", not(empty())))
        .andExpect(jsonPath("$.content", hasSize(ExampleDataGenerator.ASSAY_TYPE_COUNT)))
        .andExpect(jsonPath("$", hasKey("last")))
        .andExpect(jsonPath("$.last", is(true)))
        .andExpect(jsonPath("$", hasKey("pageable")))
        .andExpect(jsonPath("$.pageable", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("totalPages")))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$", hasKey("totalElements")))
        .andExpect(jsonPath("$.totalElements", is(ExampleDataGenerator.ASSAY_TYPE_COUNT)))
        .andExpect(jsonPath("$", hasKey("first")))
        .andExpect(jsonPath("$.first", is(true)))
        .andExpect(jsonPath("$", hasKey("size")))
        .andExpect(jsonPath("$.size", is(100)))
        .andExpect(jsonPath("$", hasKey("number")))
        .andExpect(jsonPath("$.number", is(0)))
        .andExpect(jsonPath("$", hasKey("numberOfElements")))
        .andExpect(jsonPath("$.numberOfElements", is(ExampleDataGenerator.ASSAY_TYPE_COUNT)))
        .andExpect(jsonPath("$", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("empty")))
        .andExpect(jsonPath("$.empty", is(false)));
  }

  @Test
  public void findByIdTest() throws Exception {

    AssayType assayType = assayTypeRepository.findByName("Histology")
        .orElseThrow(RecordNotFoundException::new);

    mockMvc.perform(get("/api/v1/assay-type/" + assayType.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("name")))
        .andExpect(jsonPath("$.name", is(assayType.getName())))
        .andExpect(jsonPath("$", hasKey("description")))
        .andExpect(jsonPath("$.description", is(assayType.getDescription())))
        .andExpect(jsonPath("$", hasKey("fields")))
        .andExpect(jsonPath("$.fields", hasSize(assayType.getFields().size())))
        .andExpect(jsonPath("$", hasKey("tasks")))
        .andExpect(jsonPath("$.tasks", hasSize(assayType.getTasks().size())));

  }

  @Test
  public void createTest() throws Exception {

    AssayTypePayloadDto assayType = new AssayTypePayloadDto();
    assayType.setName("Test Assay Type");
    assayType.setDescription("This is a test");
    assayType.setActive(true);
    assayType.setAttributes(Collections.singletonMap("key", "value"));
    AssayTypeFieldPayloadDto field = new AssayTypeFieldPayloadDto();
    field.setFieldName("test_field");
    field.setDisplayName("Test Field");
    field.setType(CustomEntityFieldType.FLOAT);
    field.setDescription("This is a test");
    field.setRequired(true);
    field.setFieldOrder(1);
    assayType.setFields(Set.of(field));
    AssayTypeTaskPayloadDto task = new AssayTypeTaskPayloadDto();
    task.setLabel("Test task");
    task.setOrder(0);
    task.setStatus(TaskStatus.TODO);
    assayType.setTasks(Set.of(task));

    mockMvc.perform(post("/api/v1/assay-type")
            .header("Authorization", "Bearer " + this.getToken())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(assayType)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", not(nullValue())))
        .andExpect(jsonPath("$", hasKey("name")))
        .andExpect(jsonPath("$.name", is(assayType.getName())))
        .andExpect(jsonPath("$", hasKey("description")))
        .andExpect(jsonPath("$.description", is(assayType.getDescription())))
        .andExpect(jsonPath("$", hasKey("attributes")))
        .andExpect(jsonPath("$.attributes", hasKey("key")))
        .andExpect(jsonPath("$.attributes.key", is("value")))
        .andExpect(jsonPath("$", hasKey("fields")))
        .andExpect(jsonPath("$.fields", hasSize(1)))
        .andExpect(jsonPath("$.fields[0]", hasKey("fieldName")))
        .andExpect(jsonPath("$.fields[0].fieldName", is("test_field")))
        .andExpect(jsonPath("$.fields[0]", hasKey("displayName")))
        .andExpect(jsonPath("$.fields[0].displayName", is("Test Field")))
        .andExpect(jsonPath("$.fields[0]", hasKey("description")))
        .andExpect(jsonPath("$.fields[0].description", is("This is a test")))
        .andExpect(jsonPath("$.fields[0]", hasKey("required")))
        .andExpect(jsonPath("$.fields[0].required", is(true)))
        .andExpect(jsonPath("$.fields[0]", hasKey("type")))
        .andExpect(jsonPath("$.fields[0].type", is("FLOAT")))
        .andExpect(jsonPath("$", hasKey("tasks")))
        .andExpect(jsonPath("$.tasks", hasSize(1)))
        .andExpect(jsonPath("$.tasks[0]", hasKey("label")))
        .andExpect(jsonPath("$.tasks[0].label", is("Test task")))
        .andExpect(jsonPath("$.tasks[0]", hasKey("order")))
        .andExpect(jsonPath("$.tasks[0].order", is(0)))
        .andExpect(jsonPath("$.tasks[0]", hasKey("status")))
        .andExpect(jsonPath("$.tasks[0].status", is("TODO")))
    ;
  }

  @Test
  public void updateTest() throws Exception {

    createTest();

    AssayType existing = assayTypeRepository.findByName("Test Assay Type")
        .orElseThrow(RecordNotFoundException::new);

    AssayTypePayloadDto assayType = new AssayTypePayloadDto();
    assayType.setId(existing.getId());
    assayType.setName("Test Assay Type");
    assayType.setDescription("Something new");
    assayType.setActive(true);
    assayType.setAttributes(Collections.singletonMap("key", "value"));
    AssayTypeFieldPayloadDto field = new AssayTypeFieldPayloadDto();
    field.setFieldName("test_field");
    field.setDisplayName("Test Field");
    field.setType(CustomEntityFieldType.FLOAT);
    field.setDescription("This is a test");
    field.setRequired(true);
    field.setFieldOrder(1);
    assayType.setFields(Set.of(field));
    AssayTypeTaskPayloadDto task = new AssayTypeTaskPayloadDto();
    task.setLabel("Test task");
    task.setOrder(0);
    task.setStatus(TaskStatus.TODO);
    assayType.setTasks(Set.of(task));

    mockMvc.perform(put("/api/v1/assay-type/" + assayType.getId())
            .header("Authorization", "Bearer " + this.getToken())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(assayType)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", not(nullValue())))
        .andExpect(jsonPath("$", hasKey("description")))
        .andExpect(jsonPath("$.description", is(assayType.getDescription())));

  }

  @Test
  public void deleteTest() throws Exception {

    AssayType assayType = assayTypeRepository.findByName("Histology")
            .orElseThrow(RecordNotFoundException::new);
    Assert.assertTrue(assayType.isActive());

    mockMvc.perform(delete("/api/v1/assay-type/" + assayType.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());

    assayType = assayTypeRepository.findByName("Histology")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertFalse(assayType.isActive());

  }

}
