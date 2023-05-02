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

package io.studytracker.test.mapstruct;

import io.studytracker.Application;
import io.studytracker.example.ExampleDataRunner;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.response.StudyDetailsDto;
import io.studytracker.mapstruct.dto.response.StudySlimDto;
import io.studytracker.mapstruct.dto.response.StudySummaryDto;
import io.studytracker.mapstruct.mapper.StudyMapper;
import io.studytracker.model.Study;
import io.studytracker.repository.StudyRepository;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "example"})
public class StudyMapperTests {

  @Autowired private ExampleDataRunner exampleDataRunner;

  @Autowired private StudyRepository studyRepository;

  @Autowired private StudyMapper studyMapper;

  @Before
  public void doBefore() {
    exampleDataRunner.populateDatabase();
  }

  @Test
  public void studySlimMappingTest() {
    List<Study> studies = studyRepository.findAll();
    Assert.assertFalse(studies.isEmpty());
    List<StudySlimDto> dtos = studyMapper.toStudySlimList(studies);
    Assert.assertFalse(dtos.isEmpty());
    StudySlimDto dto = dtos.get(0);
    System.out.println(dto);
  }

  @Test
  public void studySummaryMappingTest() {
    List<Study> studies = studyRepository.findAll();
    Assert.assertFalse(studies.isEmpty());
    List<StudySummaryDto> dtos = studyMapper.toStudySummaryList(studies);
    Assert.assertFalse(dtos.isEmpty());
    StudySummaryDto dto = dtos.get(0);
    System.out.println(dto);
  }

  @Test
  public void studyDetailsMappingTest() {
    Study study = studyRepository.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);
    StudyDetailsDto dto = studyMapper.toStudyDetails(study);
    Assert.assertEquals("PPB-10001", dto.getCode());
    Assert.assertFalse(dto.getComments().isEmpty());
    Assert.assertNotNull(dto.getConclusions().getContent());
    System.out.println(dto);
  }
}
