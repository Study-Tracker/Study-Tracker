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

package io.studytracker.test.web.internal;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.studytracker.Application;
import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.Keyword;
import io.studytracker.model.KeywordCategory;
import io.studytracker.model.User;
import io.studytracker.repository.KeywordCategoryRepository;
import io.studytracker.repository.UserRepository;
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
public class KeywordPrivateControllerTests {

  private static final int KEYWORD_COUNT = 7;

  private static final int CATEGORY_COUNT = 2;

  @Autowired private MockMvc mockMvc;

  @Autowired private ExampleDataGenerator exampleDataGenerator;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserRepository userRepository;

  @Autowired private KeywordCategoryRepository keywordCategoryRepository;

  private String username;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
    username = userRepository.findAll().get(0).getEmail();
  }

  @Test
  public void getAllKeywordsTest() throws Exception {
    mockMvc
        .perform(get("/api/internal/keyword").with(user(username)).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", not(empty())))
        .andExpect(jsonPath("$", hasSize(KEYWORD_COUNT)));
  }

  @Test
  public void getAllKeywordCategoryTest() throws Exception {
    mockMvc
        .perform(get("/api/internal/keyword-category").with(user(username)).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", not(empty())))
        .andExpect(jsonPath("$", hasSize(CATEGORY_COUNT)));
  }

  @Test
  public void keywordSearchTest() throws Exception {
    mockMvc
        .perform(get("/api/internal/keyword?q=akt").with(user(username)).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", not(empty())))
        .andExpect(jsonPath("$", hasSize(3)));

    KeywordCategory category = keywordCategoryRepository.findByName("Gene")
        .orElseThrow(RecordNotFoundException::new);
    mockMvc
        .perform(get("/api/internal/keyword?q=akt&categoryId=" + category.getId()).with(user(username)).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", not(empty())))
        .andExpect(jsonPath("$", hasSize(3)));

    category = keywordCategoryRepository.findByName("Cell Line")
        .orElseThrow(RecordNotFoundException::new);
    mockMvc
        .perform(get("/api/internal/keyword?q=akt&categoryId=" + category.getId()).with(user(username)).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", empty()));
  }

  @Test
  public void createKeywordTest() throws Exception {
    User user = userRepository.findByEmail("jsmith@email.com").orElseThrow(RecordNotFoundException::new);
    KeywordCategory category = keywordCategoryRepository.findByName("Gene")
        .orElseThrow(RecordNotFoundException::new);

    Keyword keyword = new Keyword(category, "TTN");
    mockMvc
        .perform(
            post("/api/internal/keyword")
                .with(user(user.getEmail())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(keyword)))
        .andExpect(status().isCreated());
  }

  @Test
  public void duplicateKeywordTest() throws Exception {
    User user = userRepository.findByEmail("jsmith@email.com").orElseThrow(RecordNotFoundException::new);
    KeywordCategory category = keywordCategoryRepository.findByName("Gene")
        .orElseThrow(RecordNotFoundException::new);
    Keyword keyword = new Keyword(category, "AKT1");
    mockMvc
        .perform(
            post("/api/internal/keyword")
                .with(user(user.getEmail())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(keyword)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isBadRequest());
  }
}
