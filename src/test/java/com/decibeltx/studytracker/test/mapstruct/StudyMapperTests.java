package com.decibeltx.studytracker.test.mapstruct;

import com.decibeltx.studytracker.Application;
import com.decibeltx.studytracker.example.ExampleDataGenerator;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.mapstruct.dto.StudyDetailsDto;
import com.decibeltx.studytracker.mapstruct.dto.StudySlimDto;
import com.decibeltx.studytracker.mapstruct.dto.StudySummaryDto;
import com.decibeltx.studytracker.mapstruct.mapper.StudyMapper;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.repository.StudyRepository;
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

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private StudyMapper studyMapper;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
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
    Study study = studyRepository.findByCode("PPB-10001")
        .orElseThrow(RecordNotFoundException::new);
    StudyDetailsDto dto = studyMapper.toStudyDetails(study);
    Assert.assertEquals("PPB-10001", dto.getCode());
    Assert.assertFalse(dto.getComments().isEmpty());
    Assert.assertNotNull(dto.getConclusions().getContent());
    System.out.println(dto);
  }

}
