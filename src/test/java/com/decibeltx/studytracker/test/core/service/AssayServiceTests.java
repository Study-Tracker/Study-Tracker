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

package com.decibeltx.studytracker.test.core.service;

import com.decibeltx.studytracker.Application;
import com.decibeltx.studytracker.example.ExampleDataGenerator;
import com.decibeltx.studytracker.exception.InvalidConstraintException;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.Assay;
import com.decibeltx.studytracker.model.AssayType;
import com.decibeltx.studytracker.model.Status;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.Task;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.repository.AssayRepository;
import com.decibeltx.studytracker.repository.AssayTypeRepository;
import com.decibeltx.studytracker.service.AssayService;
import com.decibeltx.studytracker.service.NamingService;
import com.decibeltx.studytracker.service.StudyService;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
public class AssayServiceTests {

  @Autowired
  private AssayService assayService;

  @Autowired
  private AssayRepository assayRepository;

  @Autowired
  private AssayTypeRepository assayTypeRepository;

  @Autowired
  private StudyService studyService;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Autowired
  private NamingService namingService;

  private static final int ASSAY_COUNT = 2;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void findAllAssaysTest() {
    List<Assay> assays = assayService.findAll();
    Assert.assertNotNull(assays);
    Assert.assertTrue(!assays.isEmpty());
    Assert.assertEquals(ASSAY_COUNT, assays.size());
  }

  @Test
  public void createAssayTest() {
    AssayType assayType = assayTypeRepository.findByName("Generic")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(ASSAY_COUNT, assayRepository.count());
    Study study = studyService.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
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
    assay.setTasks(Collections.singletonList(new Task("My task")));
    assayService.create(assay);
    Assert.assertEquals(ASSAY_COUNT + 1, assayRepository.count());
    Assert.assertNotNull(assay.getId());
    Assert.assertNotNull(assay.getCode());
    Assert.assertEquals(study.getCode() + "-001", assay.getCode());
    study.getAssays().add(assay);
    studyService.update(study);
    Study updated = studyService.findByCode(study.getCode())
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertTrue(!updated.getAssays().isEmpty());
    Assert.assertEquals(updated.getAssays().get(0).getCode(), assay.getCode());
  }

  @Test
  public void createAssayWithFieldDataTest() {
    AssayType assayType = assayTypeRepository.findByName("Histology")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(ASSAY_COUNT, assayRepository.count());
    Study study = studyService.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
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
    fields.put("concentration", 1.2345);
    fields.put("date", new Date());
    fields.put("external", true);
    fields.put("stain", "DAPI");
    assay.setFields(fields);

    assayService.create(assay);
    Assert.assertEquals(ASSAY_COUNT + 1, assayRepository.count());
    Assert.assertNotNull(assay.getId());
    Assert.assertNotNull(assay.getCode());
    Assert.assertEquals(study.getCode() + "-001", assay.getCode());
    study.getAssays().add(assay);
    studyService.update(study);
    Study updated = studyService.findByCode(study.getCode())
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertTrue(!updated.getAssays().isEmpty());
    Assert.assertEquals(updated.getAssays().get(0).getCode(), assay.getCode());
  }

  @Test
  public void createAssayWithInvalidFieldDataTest() {
    AssayType assayType = assayTypeRepository.findByName("Histology")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(ASSAY_COUNT, assayRepository.count());
    Study study = studyService.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
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
    fields.put("antibodies", "AKT1, AKT2, AKT3");
    fields.put("concentration", 1.2345F);
    fields.put("date", new Date());
    fields.put("external", true);
    fields.put("stain", "DAPI");
    assay.setFields(fields);

    Exception exception = null;
    try {
      assayService.create(assay);
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof InvalidConstraintException);
  }

  @Test
  public void assayUpdateTest() {
    String code = "PPB-10001-001";
    Date now = new Date();
    Assay assay = assayService.findByCode(code).orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(code, assay.getCode());
    Assert.assertNull(assay.getEndDate());
    assay.setEndDate(now);
    assayService.update(assay);
    Assay updated = assayService.findByCode(code).orElseThrow(RecordNotFoundException::new);
    Assert.assertNotNull(updated.getEndDate());
    Assert.assertEquals(now, updated.getEndDate());
  }

  @Test
  public void createAssayCodeTest() {
    Assert.assertEquals(ASSAY_COUNT, assayRepository.count());
    Study study = studyService.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    Assay assay = new Assay();
    assay.setStudy(study);
    String code = namingService.generateAssayCode(assay);
    Assert.assertNotNull(code);
    Assert.assertEquals(study.getCode() + "-001", code);
  }

  @Test
  public void inactivateAssayTest() {
    Assay assay = assayService.findByCode("PPB-10001-001")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertTrue(assay.isActive());
    assayService.delete(assay);
    Assay updated = assayService.findByCode("PPB-10001-001")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertFalse(updated.isActive());
  }

  @Test
  public void updateAssayStatusTest() {
    Assay assay = assayService.findByCode("PPB-10001-001")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(Status.ACTIVE, assay.getStatus());
    assayService.updateStatus(assay, Status.COMPLETE);
    Assay updated = assayService.findByCode("PPB-10001-001")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(Status.COMPLETE, updated.getStatus());
  }

}
