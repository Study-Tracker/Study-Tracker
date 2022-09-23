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
import io.studytracker.mapstruct.dto.api.StudyPayloadDto;
import io.studytracker.model.Program;
import io.studytracker.model.Status;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import io.studytracker.repository.ProgramRepository;
import io.studytracker.repository.StudyRepository;
import io.studytracker.repository.UserRepository;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
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
public class StudyApiControllerTests extends AbstractApiControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void findAllTest() throws Exception {
    mockMvc.perform(get("/api/v1/study")
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", not(empty())))
        .andExpect(jsonPath("$.content", hasSize(ExampleDataGenerator.STUDY_COUNT)))
        .andExpect(jsonPath("$", hasKey("last")))
        .andExpect(jsonPath("$.last", is(true)))
        .andExpect(jsonPath("$", hasKey("pageable")))
        .andExpect(jsonPath("$.pageable", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("totalPages")))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$", hasKey("totalElements")))
        .andExpect(jsonPath("$.totalElements", is(ExampleDataGenerator.STUDY_COUNT)))
        .andExpect(jsonPath("$", hasKey("first")))
        .andExpect(jsonPath("$.first", is(true)))
        .andExpect(jsonPath("$", hasKey("size")))
        .andExpect(jsonPath("$.size", is(100)))
        .andExpect(jsonPath("$", hasKey("number")))
        .andExpect(jsonPath("$.number", is(0)))
        .andExpect(jsonPath("$", hasKey("numberOfElements")))
        .andExpect(jsonPath("$.numberOfElements", is(ExampleDataGenerator.STUDY_COUNT)))
        .andExpect(jsonPath("$", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("empty")))
        .andExpect(jsonPath("$.empty", is(false)));
  }

  @Test
  public void findByIdTest() throws Exception {

    Study study = studyRepository.findAll().stream()
        .findFirst()
        .orElseThrow();

    mockMvc.perform(get("/api/v1/study/" + study.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("name")))
        .andExpect(jsonPath("$.name", is(study.getName())))
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is(study.getCode())));

  }

  @Test
  public void createStudyTest() throws Exception {

    Program program =
        programRepository
            .findByName("Clinical Program A")
            .orElseThrow(RecordNotFoundException::new);
    User user = userRepository.findByEmail("jsmith@email.com").orElseThrow(RecordNotFoundException::new);

    StudyPayloadDto study = new StudyPayloadDto();
    study.setStatus(Status.ACTIVE);
    study.setName("New Study X");
    study.setProgramId(program.getId());
    study.setDescription("This is a test");
    study.setLegacy(false);
    study.setStartDate(new Date());
    study.setOwner(user.getId());
    study.setUsers(Collections.singleton(user.getId()));

    mockMvc
        .perform(
            post("/api/v1/study/")
                .header("Authorization", "Bearer " + this.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(study)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is("CPA-10003")));
  }

  @Test
  public void createStudyWithInvalidAttributes() throws Exception {

    Program program =
        programRepository
            .findByName("Clinical Program A")
            .orElseThrow(RecordNotFoundException::new);
    User user = userRepository.findByEmail("jsmith@email.com").orElseThrow(RecordNotFoundException::new);

    StudyPayloadDto study = new StudyPayloadDto();
    study.setStatus(Status.ACTIVE);
    study.setProgramId(program.getId());
    study.setDescription("This is a test");
    study.setLegacy(false);
    study.setStartDate(new Date());
    study.setOwner(user.getId());
    study.setUsers(Collections.singleton(user.getId()));

    mockMvc
        .perform(
            post("/api/v1/study/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(study))
                .with(user(user.getEmail())).with(csrf()))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void updateStudyTest() throws Exception {

    Study study = studyRepository.findByCode("CPA-10001")
        .orElseThrow(RecordNotFoundException::new);
    StudyPayloadDto dto = new StudyPayloadDto();
    dto.setId(study.getId());
    dto.setName(study.getName());
    dto.setCode(study.getCode());
    dto.setProgramId(study.getProgram().getId());
    dto.setDescription(study.getDescription());
    dto.setStatus(study.getStatus());
    dto.setStartDate(study.getStartDate());
    dto.setEndDate(study.getEndDate());
    dto.setActive(study.isActive());
    dto.setLegacy(study.isLegacy());
    dto.setOwner(study.getOwner().getId());
    dto.setUsers(study.getUsers().stream().map(User::getId).collect(Collectors.toSet()));
    dto.setCollaboratorId(study.getCollaborator().getId());
    dto.setExternalCode(study.getExternalCode());


    mockMvc
        .perform(get("/api/v1/study/" + study.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("status")))
        .andExpect(jsonPath("$.status", is("IN_PLANNING")));

    dto.setStatus(Status.ON_HOLD);

    mockMvc
        .perform(
            put("/api/v1/study/99999999")
                .header("Authorization", "Bearer " + this.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)))
        .andExpect(status().isNotFound());

    mockMvc
        .perform(
            put("/api/v1/study/" + study.getId())
                .header("Authorization", "Bearer " + this.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("status")))
        .andExpect(jsonPath("$.status", is("ON_HOLD")));
  }

  @Test
  public void deleteStudyTest() throws Exception {
    Study study = studyRepository.findByCode("CPA-10001")
        .orElseThrow(RecordNotFoundException::new);

    mockMvc.perform(delete("/api/v1/study/" + study.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andExpect(status().isOk());
    study = studyRepository.findByCode("CPA-10001")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertFalse(study.isActive());
  }

  @Test
  public void updateStatusTest() throws Exception {
    Study study = studyRepository.findByCode("CPA-10001")
        .orElseThrow(RecordNotFoundException::new);

    mockMvc
        .perform(get("/api/v1/study/" + study.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("status")))
        .andExpect(jsonPath("$.status", is("IN_PLANNING")));

    Map<String, String> params = new HashMap<>();
    params.put("status", "ON_HOLD");

    mockMvc
        .perform(
            post("/api/v1/study/" + study.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(params))
                .header("Authorization", "Bearer " + this.getToken()))
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/api/v1/study/" + study.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("status")))
        .andExpect(jsonPath("$.status", is("ON_HOLD")));
  }

}
