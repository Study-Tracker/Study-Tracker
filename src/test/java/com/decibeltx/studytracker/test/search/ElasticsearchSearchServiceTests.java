package com.decibeltx.studytracker.test.search;

import com.decibeltx.studytracker.Application;
import com.decibeltx.studytracker.example.ExampleDataGenerator;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.mapstruct.dto.elasticsearch.ElasticsearchStudyDocument;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.repository.StudyRepository;
import com.decibeltx.studytracker.search.StudySearchHit;
import com.decibeltx.studytracker.search.StudySearchHits;
import com.decibeltx.studytracker.search.elasticsearch.ElasticsearchSearchService;
import com.decibeltx.studytracker.search.elasticsearch.StudyIndexRepository;
import java.util.ArrayList;
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
@ActiveProfiles({"test", "example", "elasticsearch-test"})
public class ElasticsearchSearchServiceTests {

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private ElasticsearchSearchService elasticsearchSearchService;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private StudyIndexRepository studyIndexRepository;

  @Before
  public void before(){
    exampleDataGenerator.populateDatabase();
    studyIndexRepository.deleteAll();
  }

  @Test
  public void indexStudiesTest() {
    Assert.assertEquals(0, studyIndexRepository.count());
    List<Study> studyList = new ArrayList<>();
    for (Study s: studyRepository.findAll()){
      Study study = studyRepository.findById(s.getId()).orElseThrow(RecordNotFoundException::new);
      studyList.add(study);
    }
    elasticsearchSearchService.indexStudies(studyList);
    Assert.assertEquals(ExampleDataGenerator.STUDY_COUNT, studyIndexRepository.count());
  }

  @Test
  public void searchIndexTest() {
    this.indexStudiesTest();
    StudySearchHits<ElasticsearchStudyDocument> hits = elasticsearchSearchService.search("legacy");
    System.out.println(hits.toString());
    Assert.assertNotNull(hits);
    Assert.assertEquals(1, hits.getNumHits().longValue());
    StudySearchHit<ElasticsearchStudyDocument> hit = hits.getHits().get(0);
    Assert.assertEquals("PPB-00001", hit.getDocument().getCode());
  }

}
