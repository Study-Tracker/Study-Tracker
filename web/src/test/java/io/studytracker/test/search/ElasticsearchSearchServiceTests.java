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

package io.studytracker.test.search;

import io.studytracker.Application;
import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchAssayDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchPowerSearchDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchStudyDocument;
import io.studytracker.model.Assay;
import io.studytracker.model.Study;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.StudyRepository;
import io.studytracker.search.DocumentType;
import io.studytracker.search.GenericSearchHit;
import io.studytracker.search.GenericSearchHits;
import io.studytracker.search.elasticsearch.AssayIndexRepository;
import io.studytracker.search.elasticsearch.ElasticsearchSearchService;
import io.studytracker.search.elasticsearch.StudyIndexRepository;
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

  @Autowired private ExampleDataGenerator exampleDataGenerator;

  @Autowired private StudyRepository studyRepository;

  @Autowired private StudyIndexRepository studyIndexRepository;

  @Autowired
  private AssayIndexRepository assayIndexRepository;

  @Autowired
  private AssayRepository assayRepository;

  @Before
  public void before() {
    exampleDataGenerator.populateDatabase();
    studyIndexRepository.deleteAll();
    assayIndexRepository.deleteAll();
  }

  @Test
  public void indexStudiesTest() {
    Assert.assertEquals(0, studyIndexRepository.count());
    List<Study> studyList = new ArrayList<>();
    for (Study s : studyRepository.findAll()) {
      Study study = studyRepository.findById(s.getId()).orElseThrow(RecordNotFoundException::new);
      studyList.add(study);
    }
    elasticsearchSearchService.indexStudies(studyList);
    Assert.assertEquals(ExampleDataGenerator.STUDY_COUNT, studyIndexRepository.count());
  }
  @Test
  public void searchStudyIndexTest() {
    this.indexStudiesTest();
    GenericSearchHits<ElasticsearchStudyDocument> hits = elasticsearchSearchService.searchStudies("legacy");
    System.out.println(hits.toString());
    Assert.assertNotNull(hits);
    Assert.assertEquals(1, hits.getNumHits().longValue());
    GenericSearchHit<ElasticsearchStudyDocument> hit = hits.getHits().get(0);
    Assert.assertEquals("PPB-00001", hit.getDocument().getCode());
  }

  @Test
  public void indexAssaysTest() {
    Assert.assertEquals(0, assayIndexRepository.count());
    List<Assay> assayList = new ArrayList<>();
    assayRepository.findAll().forEach(a -> {
      Assay assay = assayRepository.findById(a.getId()).orElseThrow(RecordNotFoundException::new);
      assayList.add(assay);
    });
    elasticsearchSearchService.indexAssays(assayList);
    Assert.assertEquals(ExampleDataGenerator.ASSAY_COUNT, assayIndexRepository.count());
  }

  @Test
  public void searchAssayIndexTest() {
    this.indexAssaysTest();
    GenericSearchHits<ElasticsearchAssayDocument> hits = elasticsearchSearchService.searchAssays("histology");
    System.out.println(hits.toString());
    Assert.assertNotNull(hits);
    Assert.assertEquals(1, hits.getNumHits().longValue());
    GenericSearchHit<ElasticsearchAssayDocument> hit = hits.getHits().get(0);
    Assert.assertEquals("PPB-10001-001", hit.getDocument().getCode());
  }

  @Test
  public void searchAllIndexesTest() {
    this.indexStudiesTest();
    this.indexAssaysTest();

    GenericSearchHits<ElasticsearchPowerSearchDocument> hits = elasticsearchSearchService.search("Lorem ipsum");
    System.out.println(hits.toString());
    Assert.assertNotNull(hits);
    Assert.assertEquals(ExampleDataGenerator.STUDY_COUNT + ExampleDataGenerator.ASSAY_COUNT, hits.getNumHits().intValue());

    hits = elasticsearchSearchService.search("histology");
    System.out.println(hits.toString());
    Assert.assertNotNull(hits);
    Assert.assertEquals(1, hits.getNumHits().intValue());
    Assert.assertEquals(DocumentType.ASSAY, hits.getHits().get(0).getDocument().getType());

    hits = elasticsearchSearchService.search("legacy");
    System.out.println(hits.toString());
    Assert.assertNotNull(hits);
    Assert.assertEquals(1, hits.getNumHits().intValue());
    Assert.assertEquals(DocumentType.STUDY, hits.getHits().get(0).getDocument().getType());

  }

}
