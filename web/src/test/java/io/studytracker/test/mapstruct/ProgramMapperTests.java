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

package io.studytracker.test.mapstruct;

import io.studytracker.Application;
import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.response.ProgramDetailsDto;
import io.studytracker.mapstruct.dto.response.ProgramSummaryDto;
import io.studytracker.mapstruct.mapper.ProgramMapper;
import io.studytracker.model.Program;
import io.studytracker.repository.ProgramRepository;
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
public class ProgramMapperTests {

  @Autowired private ExampleDataGenerator exampleDataGenerator;

  @Autowired private ProgramRepository programRepository;

  @Autowired private ProgramMapper programMapper;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void programSummaryMappingTest() {
    List<Program> programs = programRepository.findAll();
    Assert.assertFalse(programs.isEmpty());
    List<ProgramSummaryDto> dtos = programMapper.toProgramSummaryList(programs);
    Assert.assertNotNull(dtos);
    Assert.assertFalse(dtos.isEmpty());
    System.out.println(dtos);
  }

  @Test
  public void programDetailsMappingTest() {
    Program program =
        programRepository
            .findByName("Clinical Program A")
            .orElseThrow(RecordNotFoundException::new);
    ProgramDetailsDto dto = programMapper.toProgramDetails(program);
    Assert.assertNotNull(dto);
    Assert.assertEquals("Clinical Program A", dto.getName());
    Assert.assertNotNull(program.getCreatedBy().getDisplayName());
    System.out.println(dto);
    Program created = programMapper.fromProgramDetails(dto);
    Assert.assertNotNull(created);
    Assert.assertNotNull(created.getCreatedBy().getDisplayName());
  }
}
