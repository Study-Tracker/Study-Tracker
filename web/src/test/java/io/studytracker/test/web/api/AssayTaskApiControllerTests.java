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
import io.studytracker.mapstruct.dto.api.AssayTaskPayloadDto;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayTask;
import io.studytracker.model.TaskStatus;
import io.studytracker.repository.AssayRepository;
import io.studytracker.service.AssayTaskService;
import java.util.List;
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
public class AssayTaskApiControllerTests extends AbstractApiControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private AssayRepository assayRepository;

  @Autowired private AssayTaskService assayTaskService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  public void findAssayTasksTest() throws Exception {
    mockMvc
        .perform(get("/api/v1/assay-task")
            .header("Authorization", "Bearer " + getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", not(empty())))
        .andExpect(jsonPath("$.content", hasSize(ExampleDataGenerator.ASSAY_TASK_COUNT)))
        .andExpect(jsonPath("$", hasKey("last")))
        .andExpect(jsonPath("$.last", is(true)))
        .andExpect(jsonPath("$", hasKey("pageable")))
        .andExpect(jsonPath("$.pageable", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("totalPages")))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$", hasKey("totalElements")))
        .andExpect(jsonPath("$.totalElements", is(ExampleDataGenerator.ASSAY_TASK_COUNT)))
        .andExpect(jsonPath("$", hasKey("first")))
        .andExpect(jsonPath("$.first", is(true)))
        .andExpect(jsonPath("$", hasKey("size")))
        .andExpect(jsonPath("$.size", is(100)))
        .andExpect(jsonPath("$", hasKey("number")))
        .andExpect(jsonPath("$.number", is(0)))
        .andExpect(jsonPath("$", hasKey("numberOfElements")))
        .andExpect(jsonPath("$.numberOfElements", is(ExampleDataGenerator.ASSAY_TASK_COUNT)))
        .andExpect(jsonPath("$", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("empty")))
        .andExpect(jsonPath("$.empty", is(false)));
  }

  @Test
  public void createTaskTest() throws Exception {
    Assay assay =
        assayRepository.findByCode("PPB-10001-001").orElseThrow(RecordNotFoundException::new);

    AssayTaskPayloadDto task = new AssayTaskPayloadDto();
    task.setLabel("New task");
    task.setStatus(TaskStatus.TODO);
    task.setAssayId(assay.getId());

    mockMvc
        .perform(
            post("/api/v1/assay-task")
                .header("Authorization", "Bearer " + getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(task)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", not(nullValue())))
        .andExpect(jsonPath("$", hasKey("label")))
        .andExpect(jsonPath("$.label", is(task.getLabel())))
        .andExpect(jsonPath("$", hasKey("status")))
        .andExpect(jsonPath("$.status", is(task.getStatus().toString())))
        .andExpect(jsonPath("$", hasKey("assayId")))
        .andExpect(jsonPath("$.assayId", is(task.getAssayId().intValue())))
        ;

  }

  @Test
  public void updateTaskTest() throws Exception {
    Assay assay =
        assayRepository.findByCode("PPB-10001-001").orElseThrow(RecordNotFoundException::new);

    AssayTask task = assay.getTasks().stream().findFirst().get();
    AssayTaskPayloadDto dto = new AssayTaskPayloadDto();
    dto.setId(task.getId());
    dto.setLabel(task.getLabel());
    dto.setOrder(task.getOrder());
    dto.setAssayId(task.getAssay().getId());
    dto.setStatus(TaskStatus.COMPLETE);

    mockMvc
        .perform(
            put("/api/v1/assay-task/" + task.getId())
                .header("Authorization", "Bearer " + getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", not(nullValue())))
        .andExpect(jsonPath("$", hasKey("label")))
        .andExpect(jsonPath("$.label", is(task.getLabel())))
        .andExpect(jsonPath("$", hasKey("status")))
        .andExpect(jsonPath("$.status", is(TaskStatus.COMPLETE.toString())))
        .andExpect(jsonPath("$", hasKey("assayId")))
        .andExpect(jsonPath("$.assayId", is(assay.getId().intValue())));

    mockMvc
        .perform(get("/api/v1/assay-task/" + task.getId())
            .header("Authorization", "Bearer " + getToken()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("status")))
        .andExpect(jsonPath("$.status", is("COMPLETE")));
  }

  @Test
  public void deleteTaskTest() throws Exception {
    Assay assay =
        assayRepository.findByCode("PPB-10001-001").orElseThrow(RecordNotFoundException::new);
    List<AssayTask> tasks = assayTaskService.findAssayTasks(assay);
    Assert.assertFalse(tasks.isEmpty());
    AssayTask task = tasks.get(0);

    mockMvc
        .perform(
            delete("/api/v1/assay-task/9999999")
                .header("Authorization", "Bearer " + getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNotFound());

    mockMvc
        .perform(
            delete("/api/v1/assay-task/" + task.getId())
                .header("Authorization", "Bearer " + getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/api/v1/assay-task/" + task.getId())
            .header("Authorization", "Bearer " + getToken()))
        .andExpect(status().isNotFound());
  }
}
