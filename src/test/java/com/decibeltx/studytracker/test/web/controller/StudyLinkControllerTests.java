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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.decibeltx.studytracker.Application;
import com.decibeltx.studytracker.example.ExampleDataGenerator;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.ExternalLink;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.repository.UserRepository;
import com.decibeltx.studytracker.service.StudyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;
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
public class StudyLinkControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private StudyService studyService;

  private String username;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
    username = userRepository.findAll().get(0).getUsername();
  }

  @Test
  public void fetchStudyLinksTest() throws Exception {
    mockMvc.perform(get("/api/study/CPA-10001/links")
        .with(user(username)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", hasKey("label")))
        .andExpect(jsonPath("$[0].label", is("Google")))
        .andExpect(jsonPath("$[0]", hasKey("url")))
        .andExpect(jsonPath("$[0].url", is("https://google.com")));

    mockMvc.perform(get("/api/study/CPA-XXXX/links")
        .with(user(username)))
        .andExpect(status().isNotFound());

    mockMvc.perform(get("/api/study/PPB-10001/links")
        .with(user(username)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  public void createStudyLinkTest() throws Exception {

    mockMvc.perform(get("/api/study/CPA-10001/links")
        .with(user(username)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));

    User user = userRepository.findByUsername("jsmith")
        .orElseThrow(RecordNotFoundException::new);
    ExternalLink link = new ExternalLink();
    link.setLabel("Twitter");
    link.setUrl(new URL("https://twitter.com"));

    mockMvc.perform(post("/api/study/CPA-10001/links")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(link))
        .with(user(user.getUsername())))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/api/study/CPA-10001/links")
        .with(user(username)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  public void deleteStudyLinkTest() throws Exception {

    mockMvc.perform(get("/api/study/CPA-10001/links")
        .with(user(username)))
        .andExpect(status().isOk())
         .andExpect(jsonPath("$", hasSize(1)));

    User user = userRepository.findByUsername("jsmith")
        .orElseThrow(RecordNotFoundException::new);
    Study study = studyService.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    Assert.assertFalse(study.getExternalLinks().isEmpty());
    ExternalLink link = study.getExternalLinks().stream().findFirst().get();

    mockMvc.perform(delete("/api/study/CPA-10001/links/" + link.getId())
//        .contentType(MediaType.APPLICATION_JSON)
//        .content(objectMapper.writeValueAsBytes(link))
        .with(user(user.getUsername())))
        .andExpect(status().isOk());

    mockMvc.perform(get("/api/study/CPA-10001/links")
        .with(user(username)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

}
