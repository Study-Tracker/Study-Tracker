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
import io.studytracker.mapstruct.dto.api.ExternalLinkPayloadDto;
import io.studytracker.model.ExternalLink;
import io.studytracker.model.Study;
import io.studytracker.repository.ExternalLinkRepository;
import io.studytracker.repository.StudyRepository;
import java.net.URL;
import java.util.Set;
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
public class ExternalLinksApiControllerTests extends AbstractApiControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private ExternalLinkRepository externalLinkRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void findAllTest() throws Exception {
    mockMvc.perform(get("/api/v1/external-link")
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", not(empty())))
        .andExpect(jsonPath("$.content", hasSize(ExampleStudyGenerator.EXTERNAL_LINK_COUNT)))
        .andExpect(jsonPath("$", hasKey("last")))
        .andExpect(jsonPath("$.last", is(true)))
        .andExpect(jsonPath("$", hasKey("pageable")))
        .andExpect(jsonPath("$.pageable", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("totalPages")))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$", hasKey("totalElements")))
        .andExpect(jsonPath("$.totalElements", is(ExampleStudyGenerator.EXTERNAL_LINK_COUNT)))
        .andExpect(jsonPath("$", hasKey("first")))
        .andExpect(jsonPath("$.first", is(true)))
        .andExpect(jsonPath("$", hasKey("size")))
        .andExpect(jsonPath("$.size", is(100)))
        .andExpect(jsonPath("$", hasKey("number")))
        .andExpect(jsonPath("$.number", is(0)))
        .andExpect(jsonPath("$", hasKey("numberOfElements")))
        .andExpect(jsonPath("$.numberOfElements", is(ExampleStudyGenerator.EXTERNAL_LINK_COUNT)))
        .andExpect(jsonPath("$", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("empty")))
        .andExpect(jsonPath("$.empty", is(false)));
  }

  @Test
  public void findByStudyTest() throws Exception {

    Study study1 = studyRepository.findByCode("CPA-10001")
        .orElseThrow(RecordNotFoundException::new);
    Study study2 = studyRepository.findByCode("PPB-10001")
        .orElseThrow(RecordNotFoundException::new);

    mockMvc.perform(get("/api/v1/external-link?studyId=" + study1.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", not(empty())))
        .andExpect(jsonPath("$.content", hasSize(ExampleStudyGenerator.EXTERNAL_LINK_COUNT)))
        .andExpect(jsonPath("$", hasKey("last")))
        .andExpect(jsonPath("$.last", is(true)))
        .andExpect(jsonPath("$", hasKey("pageable")))
        .andExpect(jsonPath("$.pageable", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("totalPages")))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$", hasKey("totalElements")))
        .andExpect(jsonPath("$.totalElements", is(ExampleStudyGenerator.EXTERNAL_LINK_COUNT)))
        .andExpect(jsonPath("$", hasKey("first")))
        .andExpect(jsonPath("$.first", is(true)))
        .andExpect(jsonPath("$", hasKey("size")))
        .andExpect(jsonPath("$.size", is(100)))
        .andExpect(jsonPath("$", hasKey("number")))
        .andExpect(jsonPath("$.number", is(0)))
        .andExpect(jsonPath("$", hasKey("numberOfElements")))
        .andExpect(jsonPath("$.numberOfElements", is(ExampleStudyGenerator.EXTERNAL_LINK_COUNT)))
        .andExpect(jsonPath("$", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("empty")))
        .andExpect(jsonPath("$.empty", is(false)));

    mockMvc.perform(get("/api/v1/external-link?studyId=" + study2.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", empty()))
        .andExpect(jsonPath("$.empty", is(true)));
  }

  @Test
  public void findByIdTest() throws Exception {

    ExternalLink externalLink = externalLinkRepository.findAll().get(0);

    mockMvc.perform(get("/api/v1/external-link/" + externalLink.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("label")))
        .andExpect(jsonPath("$.label", is(externalLink.getLabel())))
        .andExpect(jsonPath("$", hasKey("url")))
        .andExpect(jsonPath("$.url", is(externalLink.getUrl().toString())))
        .andExpect(jsonPath("$", hasKey("studyId")))
        .andExpect(jsonPath("$.studyId", is(externalLink.getStudy().getId().intValue())));

  }

  @Test
  public void createTest() throws Exception {
    Study study = studyRepository.findByCode("PPB-10001")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertTrue(study.getExternalLinks().isEmpty());

    ExternalLinkPayloadDto dto = new ExternalLinkPayloadDto();
    dto.setUrl(new URL("https://google.com"));
    dto.setLabel("Google");
    dto.setStudyId(study.getId());

    mockMvc.perform(post("/api/v1/external-link")
            .header("Authorization", "Bearer " + this.getToken())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", not(nullValue())))
        .andExpect(jsonPath("$", hasKey("label")))
        .andExpect(jsonPath("$.label", is(dto.getLabel())))
        .andExpect(jsonPath("$", hasKey("url")))
        .andExpect(jsonPath("$.url", is(dto.getUrl().toString())))
        .andExpect(jsonPath("$", hasKey("studyId")))
        .andExpect(jsonPath("$.studyId", is(study.getId().intValue())));

    study = studyRepository.findByCode("PPB-10001")
        .orElseThrow(RecordNotFoundException::new);
    Set<ExternalLink> links = study.getExternalLinks();
    Assert.assertFalse(links.isEmpty());
    ExternalLink link = links.stream().findFirst().get();
    Assert.assertEquals(link.getLabel(), dto.getLabel());

  }

  @Test
  public void updateExternalLinkTest() throws Exception {
    ExternalLink externalLink = externalLinkRepository.findAll().get(0);
    ExternalLinkPayloadDto dto = new ExternalLinkPayloadDto();
    dto.setId(externalLink.getId());
    dto.setLabel("Something different");
    dto.setUrl(externalLink.getUrl());
    dto.setStudyId(externalLink.getStudy().getId());

    mockMvc.perform(put("/api/v1/external-link/" + externalLink.getId())
            .header("Authorization", "Bearer " + this.getToken())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", not(nullValue())))
        .andExpect(jsonPath("$", hasKey("label")))
        .andExpect(jsonPath("$.label", is(dto.getLabel())))
        .andExpect(jsonPath("$", hasKey("studyId")))
        .andExpect(jsonPath("$.studyId", is(externalLink.getStudy().getId().intValue())));

    Study study = studyRepository.findById(externalLink.getStudy().getId())
        .orElseThrow(RecordNotFoundException::new);
    externalLink = study.getExternalLinks().stream().findFirst().get();
    Assert.assertNotNull(externalLink);
    Assert.assertNotNull(externalLink.getId());
    Assert.assertEquals(externalLink.getLabel(), "Something different");

  }

  @Test
  public void deleteExternalLinkTest() throws Exception {
    ExternalLink externalLink = externalLinkRepository.findAll().get(0);

    mockMvc.perform(delete("/api/v1/external-link/" + externalLink.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());

    Assert.assertEquals(0, externalLinkRepository.findAll().size());
  }

}
