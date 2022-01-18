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

package com.decibeltx.studytracker.test.search;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.decibeltx.studytracker.Application;
import com.decibeltx.studytracker.example.ExampleDataGenerator;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.search.elasticsearch.ElasticsearchSearchService;
import com.decibeltx.studytracker.service.StudyService;
import com.decibeltx.studytracker.service.UserService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles({"web-elasticsearch-test", "example"})
public class ElasticsearchSearchControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Autowired
  private StudyService studyService;

  @Autowired
  private UserService userService;

  @Autowired
  private ElasticsearchSearchService searchService;

  private String username;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
    username = userService.findAll().get(0).getUsername();
    for (Study s: studyService.findAll()){
      Study study = studyService.findById(s.getId())
          .orElseThrow(RecordNotFoundException::new);
      searchService.indexStudy(study);
    }
  }

  @Test
  public void searchStudies() throws Exception {

    mockMvc.perform(get("/api/search?keyword=legacy")
        .with(user(username)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("numHits")))
        .andExpect(jsonPath("$.numHits", Matchers.is(3)))
        .andExpect(jsonPath("$", hasKey("maxScore")))
        .andExpect(jsonPath("$.maxScore", greaterThan(0.0)))
        .andExpect(jsonPath("$", hasKey("hits")))
        .andExpect(jsonPath("$.hits", hasSize(3)))
        .andExpect(jsonPath("$.hits[0]", hasKey("score")))
        .andExpect(jsonPath("$.hits[0].score", greaterThan(0.0)))
        .andExpect(jsonPath("$.hits[0]", hasKey("document")))
        .andExpect(jsonPath("$.hits[0].document", hasKey("code")))
        .andExpect(jsonPath("$.hits[0].document.code", Matchers.is("PPB-00001")));

  }

}
