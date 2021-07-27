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
import com.decibeltx.studytracker.mapstruct.dto.StudyRelationshipSlimDto;
import com.decibeltx.studytracker.model.RelationshipType;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.StudyRelationship;
import com.decibeltx.studytracker.repository.StudyRepository;
import com.decibeltx.studytracker.repository.UserRepository;
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

@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles({"web-test", "example"})
public class StudyRelationshipControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  private String username;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
    username = userRepository.findAll().get(0).getUsername();
  }

  @Test
  public void createStudyRelationshipTest() throws Exception {

    mockMvc.perform(get("/api/study/CPA-10001/relationships")
        .with(user(username)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));

    Study sourceStudy = studyRepository.findByCode("CPA-10001")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(0, sourceStudy.getStudyRelationships().size());
    Study targetStudy = studyRepository.findByCode("PPB-10001")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(0, targetStudy.getStudyRelationships().size());

    StudyRelationshipSlimDto dto = new StudyRelationshipSlimDto();
    dto.setSourceStudyId(sourceStudy.getId());
    dto.setTargetStudyId(targetStudy.getId());
    dto.setType(RelationshipType.IS_BLOCKING);

    mockMvc.perform(post("/api/study/CPA-10001/relationships")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(dto))
        .with(user("jsmith")))
        .andExpect(status().isCreated());

    sourceStudy = studyRepository.findByCode("CPA-10001")
        .orElseThrow(RecordNotFoundException::new);
    targetStudy = studyRepository.findByCode("PPB-10001")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(1, targetStudy.getStudyRelationships().size());
    Assert.assertEquals(
        RelationshipType.IS_BLOCKING, sourceStudy.getStudyRelationships().stream().findFirst().get().getType());
    Assert.assertEquals(targetStudy.getId(),
        sourceStudy.getStudyRelationships().stream().findFirst().get().getTargetStudy().getId());
    targetStudy = studyRepository.findByCode("PPB-10001")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(
        RelationshipType.IS_BLOCKED_BY, targetStudy.getStudyRelationships().stream().findFirst().get().getType());
    Assert.assertEquals(sourceStudy.getId(),
        targetStudy.getStudyRelationships().stream().findFirst().get().getTargetStudy().getId());

  }

  @Test
  public void fetchStudyRelationshipsTest() throws Exception {
    this.createStudyRelationshipTest();
    mockMvc.perform(get("/api/study/CPA-10001/relationships")
        .with(user(username)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", hasKey("type")))
        .andExpect(jsonPath("$[0].type", is("IS_BLOCKING")))
        .andExpect(jsonPath("$[0]", hasKey("sourceStudy")))
        .andExpect(jsonPath("$[0].sourceStudy", hasKey("code")))
        .andExpect(jsonPath("$[0].sourceStudy.code", is("CPA-10001")))
        .andExpect(jsonPath("$[0]", hasKey("targetStudy")))
        .andExpect(jsonPath("$[0].targetStudy", hasKey("code")))
        .andExpect(jsonPath("$[0].targetStudy.code", is("PPB-10001")));
  }

  @Test
  public void deleteStudyRelationshipTest() throws Exception {
    this.createStudyRelationshipTest();
    Study targetStudy = studyRepository.findByCode("PPB-10001")
        .orElseThrow(RecordNotFoundException::new);
    Study sourceStudy = studyRepository.findByCode("CPA-10001")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(1, sourceStudy.getStudyRelationships().size());
    Assert.assertEquals(1, targetStudy.getStudyRelationships().size());

    StudyRelationship relationship = sourceStudy.getStudyRelationships().stream()
        .findFirst()
        .orElseThrow();

    mockMvc.perform(delete("/api/study/CPA-10001/relationships/" + relationship.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .with(user("jsmith")))
        .andExpect(status().isOk());

    Study targetStudy2 = studyRepository.findByCode("PPB-10001")
        .orElseThrow(RecordNotFoundException::new);
    Study sourceStudy2 = studyRepository.findByCode("CPA-10001")
        .orElseThrow(RecordNotFoundException::new);

    Assert.assertEquals(0, sourceStudy2.getStudyRelationships().size());
    Assert.assertEquals(0, targetStudy2.getStudyRelationships().size());
  }

}
