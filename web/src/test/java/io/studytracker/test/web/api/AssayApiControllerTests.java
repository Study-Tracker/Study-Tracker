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
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import antlr.RecognitionException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.studytracker.Application;
import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.AssayPayloadDto;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayType;
import io.studytracker.model.Status;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.AssayTypeRepository;
import io.studytracker.repository.StudyRepository;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
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
public class AssayApiControllerTests extends AbstractApiControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private AssayRepository assayRepository;

  @Autowired private AssayTypeRepository assayTypeRepository;

  @Autowired private StudyRepository studyRepository;

  @Autowired private ObjectMapper objectMapper;

  // Study methods

  @Test
  public void findAllTest() throws Exception {
    mockMvc
        .perform(get("/api/v1/assay")
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", not(empty())))
        .andExpect(jsonPath("$.content", hasSize(ExampleDataGenerator.ASSAY_COUNT)))
        .andExpect(jsonPath("$", hasKey("last")))
        .andExpect(jsonPath("$.last", is(true)))
        .andExpect(jsonPath("$", hasKey("pageable")))
        .andExpect(jsonPath("$.pageable", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("totalPages")))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$", hasKey("totalElements")))
        .andExpect(jsonPath("$.totalElements", is(ExampleDataGenerator.ASSAY_COUNT)))
        .andExpect(jsonPath("$", hasKey("first")))
        .andExpect(jsonPath("$.first", is(true)))
        .andExpect(jsonPath("$", hasKey("size")))
        .andExpect(jsonPath("$.size", is(100)))
        .andExpect(jsonPath("$", hasKey("number")))
        .andExpect(jsonPath("$.number", is(0)))
        .andExpect(jsonPath("$", hasKey("numberOfElements")))
        .andExpect(jsonPath("$.numberOfElements", is(ExampleDataGenerator.ASSAY_COUNT)))
        .andExpect(jsonPath("$", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("empty")))
        .andExpect(jsonPath("$.empty", is(false)));
  }

  @Test
  public void findByIdTest() throws Exception {
    Assay assay = assayRepository.findByCode("PPB-10001-001")
            .orElseThrow(RecognitionException::new);

    mockMvc
        .perform(get("/api/v1/assay/999999999")
            .header("Authorization", "Bearer " + this.getToken()))
        .andExpect(status().isNotFound());

    mockMvc
        .perform(get("/api/v1/assay/" + assay.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is("PPB-10001-001")))
        .andExpect(jsonPath("$", hasKey("status")))
        .andExpect(jsonPath("$.status", is("ACTIVE")))
        .andExpect(jsonPath("$", hasKey("assayTypeId")))
        .andExpect(jsonPath("$.assayTypeId", is(assay.getAssayType().getId().intValue())))
        .andExpect(jsonPath("$", hasKey("studyId")))
        .andExpect(jsonPath("$.studyId", is(assay.getStudy().getId().intValue())));
  }

  @Test
  public void createTest() throws Exception {

    AssayType assayType =
        assayTypeRepository.findByName("Histology").orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(ExampleDataGenerator.ASSAY_COUNT, assayRepository.count());
    Study study = studyRepository.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    User user = study.getOwner();

    AssayPayloadDto assay = new AssayPayloadDto();
    assay.setStudyId(study.getId());
    assay.setActive(true);
    assay.setName("Test assay");
    assay.setDescription("This is a test");
    assay.setStatus(Status.IN_PLANNING);
    assay.setStartDate(new Date());
    assay.setAssayTypeId(assayType.getId());
    assay.setOwner(user.getId());
    assay.setUsers(Collections.singleton(user.getId()));
    assay.setAttributes(Collections.singletonMap("key", "value"));
    Map<String, Object> fields = new LinkedHashMap<>();
    fields.put("number_of_slides", 10);
    fields.put("antibodies", "AKT1, AKT2, AKT3");
    fields.put("concentration", 1.2345F);
    fields.put("date", new Date());
    fields.put("external", true);
    fields.put("stain", "DAPI");
    assay.setFields(fields);

    mockMvc.perform(
        post("/api/v1/assay")
            .header("Authorization", "Bearer " + this.getToken())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(assay)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$", hasKey("name")))
        .andExpect(jsonPath("$.name", is("Test assay")))
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is("CPA-10001-001")));

  }

  @Test
  public void updateAssayTest() throws Exception {

    Assay assay = assayRepository.findByCode("PPB-10001-001")
            .orElseThrow(RecordNotFoundException::new);

    mockMvc
        .perform(get("/api/v1/assay/" + assay.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("ACTIVE")));

    AssayPayloadDto dto = new AssayPayloadDto();
    dto.setId(assay.getId());
    dto.setName(assay.getName());
    dto.setCode(assay.getCode());
    dto.setAssayTypeId(assay.getAssayType().getId());
    dto.setStudyId(assay.getStudy().getId());
    dto.setDescription(assay.getDescription());
    dto.setStatus(Status.COMPLETE);
    dto.setOwner(assay.getOwner().getId());
    dto.setStartDate(assay.getStartDate());
    dto.setEndDate(assay.getEndDate());
    dto.setUsers(assay.getUsers().stream().map(User::getId).collect(Collectors.toSet()));
    dto.setFields(assay.getFields());
    dto.setAttributes(assay.getAttributes());

    mockMvc
        .perform(
            put("/api/v1/assay/" + assay.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto))
                .header("Authorization", "Bearer " + this.getToken()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("status")))
        .andExpect(jsonPath("$.status", is("COMPLETE")));

    mockMvc
        .perform(get("/api/v1/assay/" + assay.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("COMPLETE")));
  }

  @Test
  public void deleteAssayTest() throws Exception {

    Assay assay = assayRepository.findByCode("PPB-10001-001")
        .orElseThrow(RecordNotFoundException::new);

    mockMvc
        .perform(delete("/api/v1/assay/" + assay.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/api/v1/assay/" + assay.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active", is(false)));
  }

}
