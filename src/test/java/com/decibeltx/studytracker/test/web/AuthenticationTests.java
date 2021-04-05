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

package com.decibeltx.studytracker.test.web;

import com.decibeltx.studytracker.Application;
import com.decibeltx.studytracker.example.ExampleDataGenerator;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.Program;
import com.decibeltx.studytracker.model.Status;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.repository.ProgramRepository;
import com.decibeltx.studytracker.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles({"web-test", "example"})
public class AuthenticationTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private Environment env;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
    String username = env.getRequiredProperty("security.example.user");
    Optional<User> optional = userRepository.findByUsername(username);
    if (!optional.isPresent()) {
      User user = new User();
      user.setUsername(username);
      user.setPassword(
          passwordEncoder.encode(env.getRequiredProperty("security.example.password")));
      user.setDisplayName(username);
      user.setActive(true);
      user.setEmail(username + "@test.com");
      userRepository.save(user);
    }
  }

  @Test
  public void accessOpenEndpointTest() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/study/"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void postWithoutAuthenticationTest() throws Exception {
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

    mockMvc.perform(MockMvcRequestBuilders.post("/api/study")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(study)))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @Test
  public void postWithAuthenticationTest() throws Exception {

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

    mockMvc.perform(MockMvcRequestBuilders.post("/api/study")
        .with(SecurityMockMvcRequestPostProcessors.httpBasic(
            env.getRequiredProperty("security.example.user"),
            env.getRequiredProperty("security.example.password")))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(study)))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

}
