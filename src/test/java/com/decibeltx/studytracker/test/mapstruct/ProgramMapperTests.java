package com.decibeltx.studytracker.test.mapstruct;

import com.decibeltx.studytracker.Application;
import com.decibeltx.studytracker.example.ExampleDataGenerator;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.mapstruct.dto.ProgramDetailsDto;
import com.decibeltx.studytracker.mapstruct.dto.ProgramSummaryDto;
import com.decibeltx.studytracker.mapstruct.mapper.ProgramMapper;
import com.decibeltx.studytracker.model.Program;
import com.decibeltx.studytracker.repository.ProgramRepository;
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

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private ProgramMapper programMapper;

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
    Program program = programRepository.findByName("Clinical Program A")
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
