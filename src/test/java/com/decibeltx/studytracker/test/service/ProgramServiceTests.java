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
import com.decibeltx.studytracker.model.Program;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.repository.ProgramRepository;
import com.decibeltx.studytracker.repository.UserRepository;
import com.decibeltx.studytracker.service.ProgramService;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "example"})
public class ProgramServiceTests {

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private ProgramService programService;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Autowired
  private UserRepository userRepository;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void configTest() {
    Assert.assertNotNull(programRepository);
    Assert.assertNotNull(programService);
  }

  @Test
  public void findAllTest() {
    List<Program> programs = programService.findAll();
    Assert.assertNotNull(programs);
    Assert.assertTrue(!programs.isEmpty());
    Assert.assertEquals(5, programs.size());
    System.out.println(programs.toString());
  }

  @Test
  public void findByNameTest() {
    Assert.assertEquals(5, programRepository.count());
    Optional<Program> program = programService.findByName("Clinical Program A");
    Assert.assertTrue(program.isPresent());
    program = programService.findByName("Program X");
    Assert.assertFalse(program.isPresent());
  }

  @Test
  public void findByCodeTest() {
    List<Program> programs = programService.findByCode("TID");
    Assert.assertNotNull(programs);
    Assert.assertTrue(!programs.isEmpty());
    Assert.assertEquals(2, programs.size());
    Assert.assertEquals("TID", programs.get(0).getCode());
    programs = programService.findByCode("XYZ");
    Assert.assertTrue(programs.isEmpty());
  }

  @Test
  public void createProgramTest() {
    Assert.assertEquals(5, programRepository.count());
    User user = userRepository.findAll().get(0);
    Program program = new Program();
    program.setActive(true);
    program.setCode("TST");
    program.setName("Test Program");
    program.setCreatedBy(user);
    program.setLastModifiedBy(user);
    programRepository.save(program);
    Assert.assertEquals(6, programRepository.count());
    Assert.assertNotNull(program.getId());
    System.out.println(program.toString());
  }

  @Test
  public void cannotDuplicateProgramTest() {
    Assert.assertEquals(5, programRepository.count());
    User user = userRepository.findAll().get(0);
    Program program = new Program();
    program.setName("Clinical Program A");
    program.setCode("CPA");
    program.setCreatedBy(user);
    program.setLastModifiedBy(user);
    Exception exception = null;
    try {
      programRepository.save(program);
    } catch (Exception e) {
      exception = e;
      e.printStackTrace();
    }
    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof DataIntegrityViolationException);
  }

  @Test
  public void fieldValidationTest() {
    Exception exception = null;
    Program program = new Program();
    try {
      programRepository.save(program);
    } catch (Exception e) {
      exception = e;
      e.printStackTrace();
    }
    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof DataIntegrityViolationException);
  }

}
