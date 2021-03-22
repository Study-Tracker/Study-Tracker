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

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.decibeltx.studytracker.core.example.ExampleDataGenerator;
import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.Keyword;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.core.repository.UserRepository;
import com.decibeltx.studytracker.web.test.TestApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@SpringBootTest(classes = TestApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles({"test", "example"})
public class KeywordControllerTests {

  private static final int KEYWORD_COUNT = 7;

  private static final int CATEGORY_COUNT = 2;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void getAllKeywordsTest() throws Exception {
    mockMvc.perform(get("/api/keyword"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", not(empty())))
        .andExpect(jsonPath("$", hasSize(KEYWORD_COUNT)));
  }

  @Test
  public void getAllKeywordCategoryTest() throws Exception {
    mockMvc.perform(get("/api/keyword/categories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", not(empty())))
        .andExpect(jsonPath("$", hasSize(CATEGORY_COUNT)));
  }

  @Test
  public void keywordSearchTest() throws Exception {
    mockMvc.perform(get("/api/keyword?q=akt"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", not(empty())))
        .andExpect(jsonPath("$", hasSize(3)));

    mockMvc.perform(get("/api/keyword?q=akt&category=Gene"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", not(empty())))
        .andExpect(jsonPath("$", hasSize(3)));

    mockMvc.perform(get("/api/keyword?q=akt&category=Cell Line"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", empty()));
  }

  @Test
  public void createKeywordTest() throws Exception {
    User user = userRepository.findByUsername("jsmith")
        .orElseThrow(RecordNotFoundException::new);
    Keyword keyword = new Keyword("TTN", "Gene");
    mockMvc.perform(post("/api/keyword")
        .with(user(user.getUsername()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(keyword)))
        .andExpect(status().isCreated());
  }

  @Test
  public void duplicateKeywordTest() throws Exception {
    User user = userRepository.findByUsername("jsmith")
        .orElseThrow(RecordNotFoundException::new);
    Keyword keyword = new Keyword("AKT1", "Gene");
    mockMvc.perform(post("/api/keyword")
        .with(user(user.getUsername()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(keyword)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isBadRequest());
  }


}
