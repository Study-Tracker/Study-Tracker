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

package io.studytracker.test.web.api;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.studytracker.Application;
import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.StudyRelationshipPayloadDto;
import io.studytracker.model.RelationshipType;
import io.studytracker.model.Study;
import io.studytracker.model.StudyRelationship;
import io.studytracker.repository.StudyRepository;
import io.studytracker.repository.UserRepository;
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
public class StudyRelationshipApiControllerTests extends AbstractApiControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private StudyRepository studyRepository;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserRepository userRepository;

  @Test
  public void createStudyRelationshipTest() throws Exception {

    Study sourceStudy =
        studyRepository.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(0, sourceStudy.getStudyRelationships().size());
    Study targetStudy =
        studyRepository.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(0, targetStudy.getStudyRelationships().size());

    StudyRelationshipPayloadDto dto = new StudyRelationshipPayloadDto();
    dto.setSourceStudyId(sourceStudy.getId());
    dto.setTargetStudyId(targetStudy.getId());
    dto.setType(RelationshipType.IS_BLOCKING);

    mockMvc
        .perform(
            post("/api/v1/study-relationship")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto))
                .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", not(empty())))
        .andExpect(jsonPath("$", hasKey("sourceStudyId")))
        .andExpect(jsonPath("$.sourceStudyId", is(sourceStudy.getId().intValue())))
        .andExpect(jsonPath("$", hasKey("targetStudyId")))
        .andExpect(jsonPath("$.targetStudyId", is(targetStudy.getId().intValue())))
        .andExpect(jsonPath("$", hasKey("type")))
        .andExpect(jsonPath("$.type", is(RelationshipType.IS_BLOCKING.toString())))
    ;

    sourceStudy = studyRepository.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    targetStudy = studyRepository.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(1, targetStudy.getStudyRelationships().size());
    Assert.assertEquals(
        RelationshipType.IS_BLOCKING,
        sourceStudy.getStudyRelationships().stream().findFirst().get().getType());
    Assert.assertEquals(
        targetStudy.getId(),
        sourceStudy.getStudyRelationships().stream().findFirst().get().getTargetStudy().getId());
    targetStudy = studyRepository.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(
        RelationshipType.IS_BLOCKED_BY,
        targetStudy.getStudyRelationships().stream().findFirst().get().getType());
    Assert.assertEquals(
        sourceStudy.getId(),
        targetStudy.getStudyRelationships().stream().findFirst().get().getTargetStudy().getId());
  }

  @Test
  public void fetchStudyRelationshipsTest() throws Exception {
    this.createStudyRelationshipTest();
    mockMvc
        .perform(get("/api/v1/study-relationship")
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", not(empty())))
        .andExpect(jsonPath("$.content", hasSize(ExampleDataGenerator.STUDY_RELATIONSHIPS_COUNT + 2)))
        .andExpect(jsonPath("$", hasKey("last")))
        .andExpect(jsonPath("$.last", is(true)))
        .andExpect(jsonPath("$", hasKey("pageable")))
        .andExpect(jsonPath("$.pageable", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("totalPages")))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$", hasKey("totalElements")))
        .andExpect(jsonPath("$.totalElements", is(ExampleDataGenerator.STUDY_RELATIONSHIPS_COUNT + 2)))
        .andExpect(jsonPath("$", hasKey("first")))
        .andExpect(jsonPath("$.first", is(true)))
        .andExpect(jsonPath("$", hasKey("size")))
        .andExpect(jsonPath("$.size", is(100)))
        .andExpect(jsonPath("$", hasKey("number")))
        .andExpect(jsonPath("$.number", is(0)))
        .andExpect(jsonPath("$", hasKey("numberOfElements")))
        .andExpect(jsonPath("$.numberOfElements", is(ExampleDataGenerator.STUDY_RELATIONSHIPS_COUNT + 2)))
        .andExpect(jsonPath("$", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("empty")))
        .andExpect(jsonPath("$.empty", is(false)));
  }

  @Test
  public void deleteStudyRelationshipTest() throws Exception {
    this.createStudyRelationshipTest();
    Study targetStudy =
        studyRepository.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);
    Study sourceStudy =
        studyRepository.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(1, sourceStudy.getStudyRelationships().size());
    Assert.assertEquals(1, targetStudy.getStudyRelationships().size());

    StudyRelationship relationship =
        sourceStudy.getStudyRelationships().stream().findFirst().orElseThrow();

    mockMvc
        .perform(
            delete("/api/v1/study-relationship/" + relationship.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.getToken()))
        .andExpect(status().isOk());

    Study targetStudy2 =
        studyRepository.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);
    Study sourceStudy2 =
        studyRepository.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);

    Assert.assertEquals(0, sourceStudy2.getStudyRelationships().size());
    Assert.assertEquals(0, targetStudy2.getStudyRelationships().size());
  }
}
