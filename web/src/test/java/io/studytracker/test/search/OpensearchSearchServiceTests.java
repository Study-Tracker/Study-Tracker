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

package io.studytracker.test.search;

import io.studytracker.Application;
import io.studytracker.example.ExampleAssayGenerator;
import io.studytracker.example.ExampleDataRunner;
import io.studytracker.example.ExampleStudyGenerator;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.opensearch.OpensearchAssayDocument;
import io.studytracker.mapstruct.dto.opensearch.OpensearchPowerSearchDocument;
import io.studytracker.mapstruct.dto.opensearch.OpensearchStudyDocument;
import io.studytracker.model.Assay;
import io.studytracker.model.Study;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.StudyRepository;
import io.studytracker.search.DocumentType;
import io.studytracker.search.GenericSearchHit;
import io.studytracker.search.GenericSearchHits;
import io.studytracker.search.opensearch.AssayIndexRepository;
import io.studytracker.search.opensearch.OpensearchSearchService;
import io.studytracker.search.opensearch.StudyIndexRepository;
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
@ActiveProfiles({"test", "example", "opensearch-test"})
public class OpensearchSearchServiceTests {

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private OpensearchSearchService opensearchSearchService;

  @Autowired private ExampleDataRunner exampleDataRunner;

  @Autowired private StudyRepository studyRepository;

  @Autowired private StudyIndexRepository studyIndexRepository;

  @Autowired
  private AssayIndexRepository assayIndexRepository;

  @Autowired
  private AssayRepository assayRepository;

  @Before
  public void before() {
    exampleDataRunner.populateDatabase();
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
    opensearchSearchService.indexStudies(studyList);
    Assert.assertEquals(ExampleStudyGenerator.STUDY_COUNT, studyIndexRepository.count());
  }
  @Test
  public void searchStudyIndexTest() {
    this.indexStudiesTest();
    GenericSearchHits<OpensearchStudyDocument> hits = opensearchSearchService.searchStudies("legacy");
    System.out.println(hits.toString());
    Assert.assertNotNull(hits);
    Assert.assertEquals(1, hits.getNumHits().longValue());
    GenericSearchHit<OpensearchStudyDocument> hit = hits.getHits().get(0);
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
    opensearchSearchService.indexAssays(assayList);
    Assert.assertEquals(ExampleAssayGenerator.ASSAY_COUNT, assayIndexRepository.count());
  }

  @Test
  public void searchAssayIndexTest() {
    this.indexAssaysTest();
    GenericSearchHits<OpensearchAssayDocument> hits = opensearchSearchService.searchAssays("histology");
    System.out.println(hits.toString());
    Assert.assertNotNull(hits);
    Assert.assertEquals(1, hits.getNumHits().longValue());
    GenericSearchHit<OpensearchAssayDocument> hit = hits.getHits().get(0);
    Assert.assertEquals("PPB-10001-001", hit.getDocument().getCode());
  }

  @Test
  public void searchAllIndexesTest() {
    this.indexStudiesTest();
    this.indexAssaysTest();

    GenericSearchHits<OpensearchPowerSearchDocument> hits = opensearchSearchService.search("Lorem ipsum");
    System.out.println(hits.toString());
    Assert.assertNotNull(hits);
    Assert.assertEquals(ExampleStudyGenerator.STUDY_COUNT + ExampleAssayGenerator.ASSAY_COUNT, hits.getNumHits().intValue());

    hits = opensearchSearchService.search("histology");
    System.out.println(hits.toString());
    Assert.assertNotNull(hits);
    Assert.assertEquals(1, hits.getNumHits().intValue());
    Assert.assertEquals(DocumentType.ASSAY, hits.getHits().get(0).getDocument().getType());

    hits = opensearchSearchService.search("legacy");
    System.out.println(hits.toString());
    Assert.assertNotNull(hits);
    Assert.assertEquals(1, hits.getNumHits().intValue());
    Assert.assertEquals(DocumentType.STUDY, hits.getHits().get(0).getDocument().getType());

  }

}
