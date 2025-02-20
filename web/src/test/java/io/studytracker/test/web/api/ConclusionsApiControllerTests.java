/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.studytracker.Application;
import io.studytracker.example.ExampleStudyGenerator;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.StudyConclusionsPayloadDto;
import io.studytracker.model.Study;
import io.studytracker.model.StudyConclusions;
import io.studytracker.repository.StudyConclusionsRepository;
import io.studytracker.repository.StudyRepository;
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
public class ConclusionsApiControllerTests extends AbstractApiControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private StudyConclusionsRepository studyConclusionsRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void findAllTest() throws Exception {
    mockMvc.perform(get("/api/v1/conclusions")
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", not(empty())))
        .andExpect(jsonPath("$.content", hasSize(ExampleStudyGenerator.CONCLUSIONS_COUNT)))
        .andExpect(jsonPath("$", hasKey("last")))
        .andExpect(jsonPath("$.last", is(true)))
        .andExpect(jsonPath("$", hasKey("pageable")))
        .andExpect(jsonPath("$.pageable", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("totalPages")))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$", hasKey("totalElements")))
        .andExpect(jsonPath("$.totalElements", is(ExampleStudyGenerator.CONCLUSIONS_COUNT)))
        .andExpect(jsonPath("$", hasKey("first")))
        .andExpect(jsonPath("$.first", is(true)))
        .andExpect(jsonPath("$", hasKey("size")))
        .andExpect(jsonPath("$.size", is(100)))
        .andExpect(jsonPath("$", hasKey("number")))
        .andExpect(jsonPath("$.number", is(0)))
        .andExpect(jsonPath("$", hasKey("numberOfElements")))
        .andExpect(jsonPath("$.numberOfElements", is(ExampleStudyGenerator.CONCLUSIONS_COUNT)))
        .andExpect(jsonPath("$", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("empty")))
        .andExpect(jsonPath("$.empty", is(false)));

  }

  @Test
  public void findByIdTest() throws Exception {

    StudyConclusions conclusions = studyConclusionsRepository.findAll().get(0);

    mockMvc.perform(get("/api/v1/conclusions/" + conclusions.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", is(conclusions.getContent())))
        .andExpect(jsonPath("$", hasKey("studyId")))
        .andExpect(jsonPath("$.studyId", is(conclusions.getStudy().getId().intValue())));

  }

  @Test
  public void createTest() throws Exception {
    Study study = studyRepository.findByCode("CPA-10001")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertNull(study.getConclusions());

    StudyConclusionsPayloadDto comment = new StudyConclusionsPayloadDto();
    comment.setContent("Test comment");
    comment.setStudyId(study.getId());

    mockMvc.perform(post("/api/v1/conclusions")
            .header("Authorization", "Bearer " + this.getToken())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(comment)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", not(nullValue())))
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", is("Test comment")))
        .andExpect(jsonPath("$", hasKey("studyId")))
        .andExpect(jsonPath("$.studyId", is(study.getId().intValue())));

    study = studyRepository.findByCode("CPA-10001")
        .orElseThrow(RecordNotFoundException::new);
    StudyConclusions conclusions = study.getConclusions();
    Assert.assertNotNull(conclusions);
    Assert.assertNotNull(conclusions.getId());
    Assert.assertEquals(conclusions.getContent(), "Test comment");

  }

  @Test
  public void updateCommentTest() throws Exception {
    StudyConclusions conclusions = studyConclusionsRepository.findAll().get(0);
    StudyConclusionsPayloadDto dto = new StudyConclusionsPayloadDto();
    dto.setId(conclusions.getId());
    dto.setContent("Something different");
    dto.setStudyId(conclusions.getStudy().getId());

    mockMvc.perform(put("/api/v1/conclusions/" + conclusions.getId())
            .header("Authorization", "Bearer " + this.getToken())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", not(nullValue())))
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", is(dto.getContent())))
        .andExpect(jsonPath("$", hasKey("studyId")))
        .andExpect(jsonPath("$.studyId", is(conclusions.getStudy().getId().intValue())));

    Study study = studyRepository.findById(conclusions.getStudy().getId())
        .orElseThrow(RecordNotFoundException::new);
    conclusions = study.getConclusions();
    Assert.assertNotNull(conclusions);
    Assert.assertNotNull(conclusions.getId());
    Assert.assertEquals(conclusions.getContent(), "Something different");

  }

  @Test
  public void deleteCommentTest() throws Exception {
    StudyConclusions conclusions = studyConclusionsRepository.findAll().get(0);

    mockMvc.perform(delete("/api/v1/conclusions/" + conclusions.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());

    Assert.assertEquals(0, studyConclusionsRepository.findAll().size());
  }

}
