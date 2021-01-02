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
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.decibeltx.studytracker.core.example.ExampleDataGenerator;
import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.AssayType;
import com.decibeltx.studytracker.core.model.Status;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.core.repository.AssayRepository;
import com.decibeltx.studytracker.core.repository.AssayTypeRepository;
import com.decibeltx.studytracker.core.repository.StudyRepository;
import com.decibeltx.studytracker.web.test.TestApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
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

@SpringBootTest(classes = TestApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles({"test", "example"})
public class StudyAssayControllerTests {

  private static final int NUM_ASSAYS = ExampleDataGenerator.ASSAY_COUNT;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private AssayRepository assayRepository;

  @Autowired
  private AssayTypeRepository assayTypeRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void findStudyAssaysTest() throws Exception {
    mockMvc.perform(get("/api/study/PPB-10001/assays"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]", hasKey("code")))
        .andExpect(jsonPath("$[0].code", is("PPB-10001-001")));

    mockMvc.perform(get("/api/study/CPA-10001/assays"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));

    mockMvc.perform(get("/api/study/PPB-XXXX/assays"))
        .andExpect(status().isNotFound());
  }

  @Test
  public void findAssayByIdTest() throws Exception {
    mockMvc.perform(get("/api/study/PPB-10001/assays/PPB-10001-001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is("PPB-10001-001")))
        .andExpect(jsonPath("$", hasKey("assayType")))
        .andExpect(jsonPath("$.assayType", hasKey("name")))
        .andExpect(jsonPath("$.assayType.name", is("Generic")));

    mockMvc.perform(get("/api/study/PPB-10001/assay/PPB-10001-XXXXX"))
        .andExpect(status().isNotFound());
  }

  @Test
  public void createTest() throws Exception {

    AssayType assayType = assayTypeRepository.findByName("Histology")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(NUM_ASSAYS, assayRepository.count());
    Study study = studyRepository.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    User user = study.getOwner();

    Assay assay = new Assay();
    assay.setStudy(study);
    assay.setActive(true);
    assay.setName("Test assay");
    assay.setDescription("This is a test");
    assay.setStatus(Status.IN_PLANNING);
    assay.setStartDate(new Date());
    assay.setAssayType(assayType);
    assay.setOwner(user);
    assay.setUsers(Collections.singletonList(user));
    assay.setCreatedBy(user);
    assay.setLastModifiedBy(user);
    assay.setUpdatedAt(new Date());
    assay.setAttributes(Collections.singletonMap("key", "value"));
    Map<String, Object> fields = new LinkedHashMap<>();
    fields.put("number_of_slides", 10);
    fields.put("antibodies", "AKT1, AKT2, AKT3");
    fields.put("concentration", 1.2345F);
    fields.put("date", new Date());
    fields.put("external", true);
    fields.put("stain", "DAPI");
    assay.setFields(fields);

    mockMvc.perform(post("/api/study/XXXXXX/assays/")
        .with(user(user.getUsername()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(assay)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNotFound());

    mockMvc.perform(post("/api/study/" + study.getCode() + "/assays/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(assay)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isUnauthorized());

    mockMvc.perform(post("/api/study/" + study.getCode() + "/assays/")
        .with(user(user.getUsername()))
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

    mockMvc.perform(get("/api/study/CPA-10001/assays"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", hasKey("code")))
        .andExpect(jsonPath("$[0].code", is("CPA-10001-001")));

  }

  @Test
  public void updateAssayTest() throws Exception {

    mockMvc.perform(get("/api/study/PPB-10001/assays/PPB-10001-001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("ACTIVE")));

    Assay assay = assayRepository.findByCode("PPB-10001-001")
        .orElseThrow(RecordNotFoundException::new);
    assay.setStatus(Status.COMPLETE);

    mockMvc.perform(put("/api/study/PPB-10001/assays/PPB-10001-001")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(assay))
        .with(user(assay.getOwner().getUsername())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("status")))
        .andExpect(jsonPath("$.status", is("COMPLETE")));


  }

  @Test
  public void deleteAssayTest() throws Exception {

    mockMvc.perform(get("/api/study/PPB-10001/assays/PPB-10001-001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active", is(true)));

    mockMvc.perform(delete("/api/study/PPB-10001/assays/PPB-10001-001")
        .with(user("jsmith")))
        .andExpect(status().isOk());

    mockMvc.perform(get("/api/study/PPB-10001/assays/PPB-10001-001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active", is(false)));

  }

}
