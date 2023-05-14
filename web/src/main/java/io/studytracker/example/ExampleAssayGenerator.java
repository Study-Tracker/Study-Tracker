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
import io.studytracker.model.Assay;
import io.studytracker.model.AssayTask;
import io.studytracker.model.AssayType;
import io.studytracker.model.Status;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.model.Study;
import io.studytracker.model.TaskStatus;
import io.studytracker.model.User;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.AssayTaskRepository;
import io.studytracker.repository.AssayTypeRepository;
import io.studytracker.repository.StorageDriveFolderRepository;
import io.studytracker.repository.StudyRepository;
import io.studytracker.repository.UserRepository;
import io.studytracker.storage.StudyStorageService;
import io.studytracker.storage.StudyStorageServiceLookup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

public class ExampleAssayGenerator implements ExampleDataGenerator<Assay> {

  public static final int ASSAY_COUNT = 2;
  public static final int ASSAY_TASK_COUNT = 2;

  @Autowired private AssayRepository assayRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private StudyRepository studyRepository;
  @Autowired private AssayTypeRepository assayTypeRepository;
  @Autowired private StorageDriveFolderRepository storageDriveFolderRepository;
  @Autowired private StudyStorageServiceLookup storageServiceLookup;
  @Autowired private AssayTaskRepository assayTaskRepository;

  private StorageDriveFolder createAssayFolder(Assay assay) {
    try {
      StorageDriveFolder studyFolder = storageDriveFolderRepository
          .findByStudyId(assay.getStudy().getId()).get(0);
      StudyStorageService studyStorageService = storageServiceLookup.lookup(studyFolder)
          .orElseThrow(RecordNotFoundException::new);
      return studyStorageService.createAssayFolder(studyFolder, assay);
    } catch (Exception ex) {
      throw new StudyTrackerException(ex);
    }
  }

  @Override
  public List<Assay> generateData(Object... args) throws Exception {

    List<Study> studies = (List<Study>) args[0];
    List<Assay> assays = new ArrayList<>();

    AssayType assayType =
        assayTypeRepository.findByName("Generic").orElseThrow(RecordNotFoundException::new);

    Study study =
        studies.stream()
            .filter(s -> s.getCode().equals("PPB-10001"))
            .collect(Collectors.toList())
            .get(0);
    User user = study.getOwner();
    Assay assay = new Assay();
    assay.setStudy(study);
    assay.setActive(true);
    assay.setCode(study.getCode() + "-001");
    assay.setName("Histology assay");
    assay.setDescription(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. ");
    assay.setStatus(Status.ACTIVE);
    assay.setStartDate(new Date());
    assay.setAssayType(assayType);
    assay.setOwner(user);
    assay.setCreatedBy(user);
    assay.setUsers(Collections.singleton(user));
    assay.setLastModifiedBy(user);
    assay.setUpdatedAt(new Date());
    assay.setAttributes(Collections.singletonMap("key", "value"));
    assay.addStorageFolder(createAssayFolder(assay), true);

    AssayTask task = new AssayTask();
    task.setLabel("My task");
    task.setOrder(0);
    task.setStatus(TaskStatus.TODO);
    task.setCreatedBy(user);
    task.setLastModifiedBy(user);
    assay.addTask(task);

    assayRepository.save(assay);
    assays.add(assay);

    assay = new Assay();
    assay.setStudy(study);
    assay.setActive(true);
    assay.setCode(study.getCode() + "-00002");
    assay.setName("In vivo assay");
    assay.setDescription(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. ");
    assay.setStatus(Status.COMPLETE);
    assay.setStartDate(new Date());
    assay.setEndDate(new Date());
    assay.setAssayType(assayType);
    assay.setOwner(user);
    assay.setCreatedBy(user);
    assay.setUsers(Collections.singleton(user));
    assay.setLastModifiedBy(user);
    assay.setUpdatedAt(new Date());
    assay.setAttributes(Collections.singletonMap("key", "value"));
    assay.addStorageFolder(createAssayFolder(assay), true);

    task = new AssayTask();
    task.setLabel("My task");
    task.setOrder(0);
    task.setStatus(TaskStatus.TODO);
    task.setCreatedBy(user);
    task.setLastModifiedBy(user);
    assay.addTask(task);

    assayRepository.save(assay);
    assays.add(assay);

    return assays;

  }

  @Override
  public void deleteData() {
    assayTaskRepository.deleteAll();
    assayRepository.deleteAll();
  }
}
