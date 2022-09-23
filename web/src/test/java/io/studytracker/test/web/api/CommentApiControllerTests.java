/*
 * Copyright 2022 the original author or authors.
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
import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.CommentPayloadDto;
import io.studytracker.mapstruct.mapper.CommentMapper;
import io.studytracker.model.Comment;
import io.studytracker.model.Study;
import io.studytracker.repository.CommentRepository;
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
public class CommentApiControllerTests extends AbstractApiControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private CommentMapper commentMapper;

  @Test
  public void findAllTest() throws Exception {
    mockMvc.perform(get("/api/v1/comment")
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", not(empty())))
        .andExpect(jsonPath("$.content", hasSize(ExampleDataGenerator.COMMENT_COUNT)))
        .andExpect(jsonPath("$", hasKey("last")))
        .andExpect(jsonPath("$.last", is(true)))
        .andExpect(jsonPath("$", hasKey("pageable")))
        .andExpect(jsonPath("$.pageable", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("totalPages")))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$", hasKey("totalElements")))
        .andExpect(jsonPath("$.totalElements", is(ExampleDataGenerator.COMMENT_COUNT)))
        .andExpect(jsonPath("$", hasKey("first")))
        .andExpect(jsonPath("$.first", is(true)))
        .andExpect(jsonPath("$", hasKey("size")))
        .andExpect(jsonPath("$.size", is(100)))
        .andExpect(jsonPath("$", hasKey("number")))
        .andExpect(jsonPath("$.number", is(0)))
        .andExpect(jsonPath("$", hasKey("numberOfElements")))
        .andExpect(jsonPath("$.numberOfElements", is(ExampleDataGenerator.COMMENT_COUNT)))
        .andExpect(jsonPath("$", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("empty")))
        .andExpect(jsonPath("$.empty", is(false)));
    ;

    Study study1 = studyRepository.findByCode("PPB-10001")
        .orElseThrow(RecordNotFoundException::new);
    Study study2 = studyRepository.findAll().stream()
            .filter(study -> !study.getCode().equals("PPB-10001"))
            .findFirst()
            .orElseThrow(RecordNotFoundException::new);

    mockMvc.perform(get("/api/v1/comment?studyId=" + study2.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", empty()));

    mockMvc.perform(get("/api/v1/comment?studyId=" + study1.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", not(empty())))
        .andExpect(jsonPath("$.content", hasSize(1)));

  }

  @Test
  public void findByIdTest() throws Exception {

    Comment comment = commentRepository.findAll().get(0);

    mockMvc.perform(get("/api/v1/comment/" + comment.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("text")))
        .andExpect(jsonPath("$.text", is(comment.getText())))
        .andExpect(jsonPath("$", hasKey("studyId")))
        .andExpect(jsonPath("$.studyId", is(comment.getStudy().getId().intValue())));

  }

  @Test
  public void createCommentTest() throws Exception {
    Study study = studyRepository.findByCode("PPB-10001")
        .orElseThrow(RecordNotFoundException::new);

    CommentPayloadDto comment = new CommentPayloadDto();
    comment.setText("Test comment");
    comment.setStudyId(study.getId());

    mockMvc.perform(post("/api/v1/comment")
            .header("Authorization", "Bearer " + this.getToken())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(comment)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", not(nullValue())))
        .andExpect(jsonPath("$", hasKey("text")))
        .andExpect(jsonPath("$.text", is("Test comment")))
        .andExpect(jsonPath("$", hasKey("studyId")))
        .andExpect(jsonPath("$.studyId", is(study.getId().intValue())));
  }

  @Test
  public void updateCommentTest() throws Exception {
    Comment comment = commentRepository.findAll().get(0);
    CommentPayloadDto dto = new CommentPayloadDto();
    dto.setId(comment.getId());
    dto.setText("Something different");
    dto.setStudyId(comment.getStudy().getId());

    mockMvc.perform(put("/api/v1/comment/" + comment.getId())
            .header("Authorization", "Bearer " + this.getToken())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", not(nullValue())))
        .andExpect(jsonPath("$", hasKey("text")))
        .andExpect(jsonPath("$.text", is(dto.getText())))
        .andExpect(jsonPath("$", hasKey("studyId")))
        .andExpect(jsonPath("$.studyId", is(comment.getStudy().getId().intValue())));
  }

  @Test
  public void deleteCommentTest() throws Exception {
    Comment comment = commentRepository.findAll().get(0);

    mockMvc.perform(delete("/api/v1/comment/" + comment.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());

    Assert.assertEquals(0, commentRepository.findAll().size());
  }

}
