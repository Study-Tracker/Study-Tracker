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

package io.studytracker.example;

import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.model.Organization;
import io.studytracker.model.Program;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.model.User;
import io.studytracker.repository.ProgramRepository;
import io.studytracker.storage.StorageDriveFolderService;
import io.studytracker.storage.StudyStorageService;
import io.studytracker.storage.StudyStorageServiceLookup;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ExampleProgramGenerator implements ExampleDataGenerator<Program> {

  public static final int PROGRAM_COUNT = 5;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private StudyStorageServiceLookup studyStorageServiceLookup;

  @Autowired
  private StorageDriveFolderService storageDriveFolderService;

  private StorageDriveFolder createProgramFolder(Program program) {
    try {
      StorageDriveFolder rootFolder = storageDriveFolderService.findStudyRootFolders()
          .stream()
          .min(Comparator.comparing(StorageDriveFolder::getId))
          .orElseThrow(RecordNotFoundException::new);
      StudyStorageService studyStorageService = studyStorageServiceLookup.lookup(rootFolder)
          .orElseThrow(RecordNotFoundException::new);
      return studyStorageService.createProgramFolder(rootFolder, program);
    } catch (Exception ex) {
      throw new StudyTrackerException(ex);
    }
  }

  @Override
  public List<Program> generateData(Object... args) {

    Organization organization = (Organization) args[0];
    List<User> users = (List<User>) args[1];

    User user = users.get(0);
    List<Program> programs = new ArrayList<>();

    Program program = new Program();
    program.setOrganization(organization);
    program.setName("Clinical Program A");
    program.setCode("CPA");
    program.setActive(true);
    program.setCreatedBy(user);
    program.setLastModifiedBy(user);
    program.setCreatedAt(new Date());
    program.addStorageFolder(createProgramFolder(program), true);
    programs.add(program);

    program = new Program();
    program.setOrganization(organization);
    program.setName("Preclinical Project B");
    program.setCode("PPB");
    program.setActive(true);
    program.setCreatedBy(user);
    program.setLastModifiedBy(user);
    program.setCreatedAt(new Date());
    program.addStorageFolder(createProgramFolder(program), true);
    programs.add(program);

    program = new Program();
    program.setOrganization(organization);
    program.setName("Cancelled Program C");
    program.setCode("CPC");
    program.setActive(false);
    program.setCreatedBy(user);
    program.setLastModifiedBy(user);
    program.setCreatedAt(new Date());
    program.addStorageFolder(createProgramFolder(program), true);
    programs.add(program);

    program = new Program();
    program.setOrganization(organization);
    program.setName("Target ID Project D");
    program.setCode("TID");
    program.setActive(true);
    program.setCreatedBy(user);
    program.setLastModifiedBy(user);
    program.setCreatedAt(new Date());
    program.addStorageFolder(createProgramFolder(program), true);
    programs.add(program);

    program = new Program();
    program.setOrganization(organization);
    program.setName("Target ID Project E");
    program.setCode("TID");
    program.setActive(true);
    program.setCreatedBy(user);
    program.setLastModifiedBy(user);
    program.setCreatedAt(new Date());
    program.addStorageFolder(createProgramFolder(program), true);
    programs.add(program);

    return programRepository.saveAll(programs);
  }

  @Override
  public void deleteData() {
    programRepository.deleteAll();
  }
}
