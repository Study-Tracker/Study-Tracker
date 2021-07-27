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

package com.decibeltx.studytracker.test.service;

import com.decibeltx.studytracker.Application;
import com.decibeltx.studytracker.example.ExampleDataGenerator;
import com.decibeltx.studytracker.model.AssayType;
import com.decibeltx.studytracker.model.AssayTypeField;
import com.decibeltx.studytracker.model.CustomEntityFieldType;
import com.decibeltx.studytracker.service.AssayTypeService;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
public class AssayTypeServiceTests {

  private static final int ASSAY_TYPE_COUNT = ExampleDataGenerator.ASSAY_TYPE_COUNT;

  @Autowired
  private AssayTypeService assayTypeService;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void findAllTest() {
    List<AssayType> assayTypes = assayTypeService.findAll();
    Assert.assertNotNull(assayTypes);
    Assert.assertTrue(!assayTypes.isEmpty());
    Assert.assertEquals(ASSAY_TYPE_COUNT, assayTypes.size());
  }

  @Test
  public void findByNameTest() {
    Optional<AssayType> optional = assayTypeService.findByName("Generic");
    Assert.assertTrue(optional.isPresent());
    Assert.assertEquals("Generic", optional.get().getName());

    optional = assayTypeService.findByName("Bad");
    Assert.assertFalse(optional.isPresent());
  }

  @Test
  public void createAssayTypeTest() {
    Assert.assertEquals(ASSAY_TYPE_COUNT, assayTypeService.count());
    AssayType assayType = new AssayType();
    assayType.setActive(true);
    assayType.setName("Test");
    assayType.setDescription("This is a test");
    AssayTypeField field = new AssayTypeField();
    field.setDisplayName("Name");
    field.setFieldName("name");
    field.setRequired(true);
    field.setType(CustomEntityFieldType.STRING);
    assayType.addField(field);
    assayTypeService.create(assayType);
    Assert.assertEquals(ASSAY_TYPE_COUNT + 1, assayTypeService.count());

    Optional<AssayType> optional = assayTypeService.findByName("Test");
    Assert.assertTrue(optional.isPresent());
    Assert.assertEquals("Test", optional.get().getName());
    Assert.assertFalse(optional.get().getFields().isEmpty());

  }

  @Test
  public void updateAssayTypeTest() {
    Assert.assertEquals(ASSAY_TYPE_COUNT, assayTypeService.count());
    AssayType assayType = new AssayType();
    assayType.setActive(true);
    assayType.setName("Test");
    assayType.setDescription("This is a test");
    AssayTypeField field = new AssayTypeField();
    field.setDisplayName("Name");
    field.setFieldName("name");
    field.setRequired(true);
    field.setType(CustomEntityFieldType.STRING);
    assayType.addField(field);
    assayTypeService.create(assayType);
    Assert.assertEquals(ASSAY_TYPE_COUNT + 1, assayTypeService.count());

    Optional<AssayType> optional = assayTypeService.findByName("Test");
    Assert.assertTrue(optional.isPresent());
    Assert.assertEquals("Test", optional.get().getName());
    Assert.assertFalse(optional.get().getFields().isEmpty());
    Assert.assertEquals(1, optional.get().getFields().size());

    assayType = optional.get();
    field = new AssayTypeField();
    field.setDisplayName("Description");
    field.setFieldName("description");
    field.setRequired(false);
    field.setType(CustomEntityFieldType.TEXT);
    assayType.getFields().add(field);
    assayTypeService.update(assayType);

    optional = assayTypeService.findByName("Test");
    Assert.assertTrue(optional.isPresent());
    Assert.assertFalse(optional.get().getFields().isEmpty());
    Assert.assertEquals(2, optional.get().getFields().size());

  }

  @Test
  public void deleteAssayTypeTest() {
    Assert.assertEquals(ASSAY_TYPE_COUNT, assayTypeService.count());
    AssayType assayType = new AssayType();
    assayType.setActive(true);
    assayType.setName("Test");
    assayType.setDescription("This is a test");
    AssayTypeField field = new AssayTypeField();
    field.setDisplayName("Name");
    field.setFieldName("name");
    field.setRequired(true);
    field.setType(CustomEntityFieldType.STRING);
    assayType.setFields(new HashSet<>(Arrays.asList(field)));
    assayTypeService.create(assayType);
    Assert.assertEquals(ASSAY_TYPE_COUNT + 1, assayTypeService.count());

    Optional<AssayType> optional = assayTypeService.findByName("Test");
    Assert.assertTrue(optional.isPresent());
    Assert.assertEquals("Test", optional.get().getName());
    assayType = optional.get();
    assayTypeService.delete(assayType);

    optional = assayTypeService.findByName("Test");
    Assert.assertTrue(optional.isPresent());
    Assert.assertFalse(optional.get().isActive());

  }


}
