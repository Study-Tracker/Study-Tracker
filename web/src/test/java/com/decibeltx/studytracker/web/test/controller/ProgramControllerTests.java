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

package com.decibeltx.studytracker.web.test.controller;

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

import com.decibeltx.studytracker.core.example.ExampleDataGenerator;
import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.core.repository.ProgramRepository;
import com.decibeltx.studytracker.core.repository.UserRepository;
import com.decibeltx.studytracker.web.test.TestApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@SpringBootTest(classes = TestApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles({"test", "example"})
public class ProgramControllerTests {

  private static final int NUM_PROGRAMS = 5;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  // Study methods

  @Test
  public void allProgramsTest() throws Exception {
    mockMvc.perform(get("/api/program"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(NUM_PROGRAMS)))
        .andExpect(jsonPath("$[0]", hasKey("id")))
        .andExpect(jsonPath("$[0]", hasKey("name")))
        .andExpect(jsonPath("$[0]", hasKey("code")))
    ;
  }

  @Test
  public void findProgramById() throws Exception {

    Program program = programRepository.findByName("Clinical Program A")
        .orElseThrow(RecordNotFoundException::new);

    mockMvc.perform(get("/api/program/" + program.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is("CPA")))
        .andExpect(jsonPath("$", hasKey("active")))
        .andExpect(jsonPath("$.active", is(true)))
        .andExpect(jsonPath("$", hasKey("name")))
        .andExpect(jsonPath("$.name", is("Clinical Program A")));
  }

  @Test
  public void findNonExistantProgramTest() throws Exception {
    mockMvc.perform(get("/api/program/XXXX"))
        .andExpect(status().isNotFound());
  }

  @Test
  public void createProgramTest() throws Exception {

    User user = userRepository.findByAccountName("jsmith")
        .orElseThrow(RecordNotFoundException::new);

    Program program = new Program();
    program.setName("Program X");
    program.setCode("PX");
    program.setActive(true);

    mockMvc.perform(post("/api/program/")
        .with(user(user.getAccountName()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(program)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$", hasKey("name")))
        .andExpect(jsonPath("$.name", is("Program X")));

  }

  @Test
  public void createProgramWithoutAuthorizationTest() throws Exception {
    mockMvc.perform(post("/api/program/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(new Program())))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void updateProgramTest() throws Exception {

    Program program = programRepository.findByName("Clinical Program A")
        .orElseThrow(RecordNotFoundException::new);
    User user = userRepository.findByAccountName("jsmith")
        .orElseThrow(RecordNotFoundException::new);

    mockMvc.perform(get("/api/program/" + program.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("active")))
        .andExpect(jsonPath("$.active", is(true)));

    program.setActive(false);

    mockMvc.perform(put("/api/program/" + program.getId())
        .with(user(user.getAccountName()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(program)))
        .andExpect(status().isCreated());
  }

  @Test
  public void updateWithoutAutheotizationTest() throws Exception {
    Program program = programRepository.findByName("Clinical Program A")
        .orElseThrow(RecordNotFoundException::new);

    mockMvc.perform(put("/api/program/" + program.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(program)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void deleteProgramTest() throws Exception {
    Program program = programRepository.findByName("Clinical Program A")
        .orElseThrow(RecordNotFoundException::new);
    User user = userRepository.findByAccountName("jsmith")
        .orElseThrow(RecordNotFoundException::new);
    mockMvc.perform(delete("/api/program/" + program.getId())
        .with(user(user.getAccountName())))
        .andExpect(status().isOk());
    program = programRepository.findByName("Clinical Program A")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertFalse(program.isActive());
  }

  @Test
  public void deleteWithoutAuthorizationTest() throws Exception {
    Program program = programRepository.findByName("Clinical Program A")
        .orElseThrow(RecordNotFoundException::new);
    mockMvc.perform(delete("/api/program/" + program.getId()))
        .andExpect(status().isUnauthorized());
  }

}
