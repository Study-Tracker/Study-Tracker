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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.decibeltx.studytracker.core.example.ExampleDataGenerator;
import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.model.StudyRelationship;
import com.decibeltx.studytracker.core.model.StudyRelationship.Type;
import com.decibeltx.studytracker.core.repository.StudyRepository;
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
public class StudyRelationshipControllerTests {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ExampleDataGenerator exampleDataGenerator;
  @Autowired
  private StudyRepository studyRepository;
  @Autowired
  private ObjectMapper objectMapper;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void createStudyRelationshipTest() throws Exception {

    mockMvc.perform(get("/api/study/CPA-10001/relationships"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));

    Study sourceStudy = studyRepository.findByCode("CPA-10001")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(0, sourceStudy.getStudyRelationships().size());
    Study targetStudy = studyRepository.findByCode("PPB-10001")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(0, targetStudy.getStudyRelationships().size());

    StudyRelationship studyRelationship = new StudyRelationship(Type.IS_BLOCKING, targetStudy);
    mockMvc.perform(post("/api/study/CPA-10001/relationships")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(studyRelationship))
        .with(user("jsmith")))
        .andExpect(status().isCreated());

    sourceStudy = studyRepository.findByCode("CPA-10001")
        .orElseThrow(RecordNotFoundException::new);
    targetStudy = studyRepository.findByCode("PPB-10001")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(1, targetStudy.getStudyRelationships().size());
    Assert.assertEquals(Type.IS_BLOCKING, sourceStudy.getStudyRelationships().get(0).getType());
    Assert.assertEquals(targetStudy.getCode(),
        sourceStudy.getStudyRelationships().get(0).getStudy().getId());
    targetStudy = studyRepository.findByCode("PPB-10001")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(Type.IS_BLOCKED_BY, targetStudy.getStudyRelationships().get(0).getType());
    Assert.assertEquals(sourceStudy.getCode(),
        targetStudy.getStudyRelationships().get(0).getStudy().getId());

  }

  @Test
  public void fetchStudyRelationshipsTest() throws Exception {
    this.createStudyRelationshipTest();
    mockMvc.perform(get("/api/study/CPA-10001/relationships"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", hasKey("type")))
        .andExpect(jsonPath("$[0].type", is("IS_BLOCKING")))
        .andExpect(jsonPath("$[0]", hasKey("studyId")))
        .andExpect(jsonPath("$[0].studyId", is("PPB-10001")));
  }

  @Test
  public void deleteStudyRelationshipTest() throws Exception {
    this.createStudyRelationshipTest();
    Study targetStudy = studyRepository.findByCode("PPB-10001")
        .orElseThrow(RecordNotFoundException::new);
    StudyRelationship studyRelationship = new StudyRelationship(Type.IS_BLOCKING, targetStudy);
    mockMvc.perform(delete("/api/study/CPA-10001/relationships")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(studyRelationship))
        .with(user("jsmith")))
        .andExpect(status().isOk());

    Study sourceStudy = studyRepository.findByCode("CPA-10001")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(0, sourceStudy.getStudyRelationships().size());
    targetStudy = studyRepository.findByCode("PPB-10001")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(0, targetStudy.getStudyRelationships().size());
  }

}
