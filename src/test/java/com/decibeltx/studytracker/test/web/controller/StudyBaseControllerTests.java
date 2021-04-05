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

package com.decibeltx.studytracker.test.web.controller;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.decibeltx.studytracker.Application;
import com.decibeltx.studytracker.example.ExampleDataGenerator;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.Program;
import com.decibeltx.studytracker.model.Status;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.repository.ProgramRepository;
import com.decibeltx.studytracker.repository.StudyRepository;
import com.decibeltx.studytracker.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles({"web-test", "example"})
public class StudyBaseControllerTests {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ExampleDataGenerator exampleDataGenerator;
  @Autowired
  private StudyRepository studyRepository;
  @Autowired
  private ProgramRepository programRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ObjectMapper objectMapper;

  private static final int NUM_STUDIES = 6;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  // Study methods

  @Test
  public void allStudiesTest() throws Exception {
    mockMvc.perform(get("/api/study"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(NUM_STUDIES)))
        .andExpect(jsonPath("$[0]", hasKey("id")))
        .andExpect(jsonPath("$[0]", hasKey("name")))
        .andExpect(jsonPath("$[0]", hasKey("description")))
    ;
  }

  @Test
  public void findStudyByIdTest() throws Exception {
    mockMvc.perform(get("/api/study/CPA-10001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is("CPA-10001")))
        .andExpect(jsonPath("$", hasKey("status")))
        .andExpect(jsonPath("$.status", is("IN_PLANNING")))
        .andExpect(jsonPath("$", hasKey("program")))
        .andExpect(jsonPath("$.program", hasKey("name")))
        .andExpect(jsonPath("$.program.name", is("Clinical Program A")));
  }

  @Test
  public void findNonExistantStudyTest() throws Exception {
    mockMvc.perform(get("/api/study/CPA-XXXX"))
        .andExpect(status().isNotFound());
  }

  @Test
  public void createStudyTest() throws Exception {

    Program program = programRepository.findByName("Clinical Program A")
        .orElseThrow(RecordNotFoundException::new);
    User user = userRepository.findByUsername("jsmith")
        .orElseThrow(RecordNotFoundException::new);

    Study study = new Study();
    study.setStatus(Status.ACTIVE);
    study.setName("New Study X");
    study.setProgram(program);
    study.setDescription("This is a test");
    study.setLegacy(false);
    study.setCreatedBy(user);
    study.setLastModifiedBy(user);
    study.setStartDate(new Date());
    study.setOwner(user);
    study.setUsers(Collections.singletonList(user));

    mockMvc.perform(post("/api/study/")
        .with(user(user.getUsername()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(study)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is("CPA-10003")));

  }

  @Test
  public void createStudyWithInvalidAttributes() throws Exception {

    Program program = programRepository.findByName("Clinical Program A")
        .orElseThrow(RecordNotFoundException::new);
    User user = userRepository.findByUsername("jsmith")
        .orElseThrow(RecordNotFoundException::new);

    Study study = new Study();
    study.setStatus(Status.ACTIVE);
    study.setProgram(program);
    study.setDescription("This is a test");
    study.setLegacy(false);
    study.setCreatedBy(user);
    study.setLastModifiedBy(user);
    study.setStartDate(new Date());
    study.setOwner(user);
    study.setUsers(Collections.singletonList(user));

    mockMvc.perform(post("/api/study/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(study))
        .with(user(user.getUsername())))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void createStudyWithoutAuthorizationTest() throws Exception {
    mockMvc.perform(post("/api/study/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(new Study())))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void updateStudyTest() throws Exception {

    mockMvc.perform(get("/api/study/CPA-10001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("status")))
        .andExpect(jsonPath("$.status", is("IN_PLANNING")));

    Study study = studyRepository.findByCode("CPA-10001")
        .orElseThrow(RecordNotFoundException::new);
    study.setStatus(Status.ON_HOLD);

    mockMvc.perform(put("/api/study/CPA-XXXXX")
        .with(user(study.getOwner().getUsername()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(study)))
        .andExpect(status().isOk());
  }

  @Test
  public void updateWithoutAutheotizationTest() throws Exception {
    Study study = studyRepository.findByCode("CPA-10001")
        .orElseThrow(RecordNotFoundException::new);
    study.setStatus(Status.ON_HOLD);

    mockMvc.perform(put("/api/study/CPA-10001")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(study)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void deleteStudyTest() throws Exception {
    mockMvc.perform(delete("/api/study/CPA-10001")
        .with(user("jsmith")))
        .andExpect(status().isOk());
    Study study = studyRepository.findByCode("CPA-10001")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertFalse(study.isActive());
  }

  @Test
  public void deleteWithoutAuthorizationTest() throws Exception {
    mockMvc.perform(delete("/api/study/CPA-10001"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void updateStatusTest() throws Exception {
    User user = userRepository.findByUsername("jsmith")
        .orElseThrow(RecordNotFoundException::new);

    mockMvc.perform(get("/api/study/CPA-10001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("status")))
        .andExpect(jsonPath("$.status", is("IN_PLANNING")));

    Map<String, String> params = new HashMap<>();
    params.put("status", "ON_HOLD");

    mockMvc.perform(post("/api/study/CPA-10001/status")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(params))
        .with(user(user.getUsername())))
        .andExpect(status().isOk());

    mockMvc.perform(get("/api/study/CPA-10001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("status")))
        .andExpect(jsonPath("$.status", is("ON_HOLD")));
  }

  @Test
  public void updateStatusWithoutAuthorizationTest() throws Exception {
    Map<String, String> params = new HashMap<>();
    params.put("status", "ON_HOLD");

    mockMvc.perform(post("/api/study/CPA-10001/status")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(params)))
        .andExpect(status().isUnauthorized());
  }

}
