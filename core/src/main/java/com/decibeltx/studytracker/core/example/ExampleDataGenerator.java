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

package com.decibeltx.studytracker.core.example;

import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.exception.StudyTrackerException;
import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.AssayType;
import com.decibeltx.studytracker.core.model.Collaborator;
import com.decibeltx.studytracker.core.model.Comment;
import com.decibeltx.studytracker.core.model.Conclusions;
import com.decibeltx.studytracker.core.model.ExternalLink;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.Status;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.model.Task;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.core.notebook.SimpleNotebookEntry;
import com.decibeltx.studytracker.core.repository.ActivityRepository;
import com.decibeltx.studytracker.core.repository.AssayRepository;
import com.decibeltx.studytracker.core.repository.CollaboratorRepository;
import com.decibeltx.studytracker.core.repository.ProgramRepository;
import com.decibeltx.studytracker.core.repository.StudyRepository;
import com.decibeltx.studytracker.core.repository.UserRepository;
import com.decibeltx.studytracker.core.service.StudyCommentService;
import com.decibeltx.studytracker.core.service.StudyConclusionsService;
import com.decibeltx.studytracker.core.service.StudyExternalLinkService;
import com.decibeltx.studytracker.core.service.StudyService;
import com.decibeltx.studytracker.core.storage.StorageFolder;
import com.decibeltx.studytracker.core.storage.StudyStorageService;
import com.decibeltx.studytracker.core.storage.exception.StudyStorageNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ExampleDataGenerator {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExampleDataGenerator.class);

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private CollaboratorRepository collaboratorRepository;

  @Autowired
  private AssayRepository assayRepository;

  @Autowired
  private ActivityRepository activityRepository;

  @Autowired
  private StudyStorageService studyStorageService;

  @Autowired
  private StudyService studyService;

  @Autowired
  private StudyExternalLinkService externalLinkService;

  @Autowired
  private StudyCommentService commentService;

  @Autowired
  private StudyConclusionsService conclusionsService;

  public List<Program> generateExamplePrograms() {
    List<Program> programs = new ArrayList<>();

    Program program = new Program();
    program.setName("Clinical Program A");
    program.setCode("CPA");
    program.setActive(true);
    programs.add(program);

    program = new Program();
    program.setName("Preclinical Project B");
    program.setCode("PPB");
    program.setActive(true);
    programs.add(program);

    program = new Program();
    program.setName("Cancelled Program C");
    program.setCode("CPC");
    program.setActive(false);
    programs.add(program);

    program = new Program();
    program.setName("Target ID Project D");
    program.setCode("TID");
    program.setActive(true);
    programs.add(program);

    program = new Program();
    program.setName("Target ID Project E");
    program.setCode("TID");
    program.setActive(true);
    programs.add(program);

    return programs;
  }

  public void createProgramFolders() {
    try {
      for (Program program : programRepository.findAll()) {
        try {
          studyStorageService.getProgramFolder(program);
        } catch (StudyStorageNotFoundException ex) {
          studyStorageService.createProgramFolder(program);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public List<User> generateExampleUsers() {

    List<User> users = new ArrayList<>();

    User user = new User();
    user.setAccountName("jsmith");
    user.setDisplayName("Joe Smith");
    user.setEmail("jsmith@email.com");
    user.setTitle("Director");
    user.setAdmin(false);
    user.setDepartment("Biology");
    users.add(user);

    user = new User();
    user.setAccountName("ajohnson");
    user.setDisplayName("Ann Johnson");
    user.setEmail("ajohnson@email.com");
    user.setTitle("Sr. Scientist");
    user.setAdmin(false);
    user.setDepartment("Biology");
    users.add(user);

    user = new User();
    user.setAccountName("rblack");
    user.setDisplayName("Rob Black");
    user.setEmail("rblack@email.com");
    user.setTitle("IT Admin");
    user.setAdmin(true);
    user.setDepartment("IT");
    users.add(user);

    user = new User();
    user.setAccountName("demo");
    user.setDisplayName("Demo User");
    user.setEmail("demo@email.com");
    user.setTitle("n/a");
    user.setAdmin(true);
    user.setDepartment("n/a");
    users.add(user);

    return users;

  }

  public List<Collaborator> generateExampleCollaborators() {

    List<Collaborator> collaborators = new ArrayList<>();

    Collaborator collaborator = new Collaborator();
    collaborator.setActive(true);
    collaborator.setLabel("Partner Co - In Vivo");
    collaborator.setOrganizationName("Partner Co");
    collaborator.setOrganizationLocation("China");
    collaborator.setContactPersonName("Joe Person");
    collaborator.setContactEmail("jperson@partnerco.com");
    collaborator.setCode("PC");
    collaborators.add(collaborator);

    collaborator = new Collaborator();
    collaborator.setActive(true);
    collaborator.setLabel("Partner Co - Chemistry");
    collaborator.setOrganizationName("Partner Co");
    collaborator.setOrganizationLocation("China");
    collaborator.setContactPersonName("Alex Person");
    collaborator.setContactEmail("aperson@partnerco.com");
    collaborator.setCode("PC");
    collaborators.add(collaborator);

    collaborator = new Collaborator();
    collaborator.setActive(true);
    collaborator.setLabel("University of Somewhere");
    collaborator.setOrganizationName("University of Somewhere");
    collaborator.setOrganizationLocation("Cambridge, MA");
    collaborator.setContactPersonName("John Scientist");
    collaborator.setContactEmail("jscientist@uos.edu");
    collaborator.setCode("US");
    collaborators.add(collaborator);

    collaborator = new Collaborator();
    collaborator.setActive(false);
    collaborator.setLabel("Inactive CRO");
    collaborator.setOrganizationName("Inactive CRO");
    collaborator.setOrganizationLocation("Cambridge, MA");
    collaborator.setCode("IN");
    collaborators.add(collaborator);

    return collaborators;

  }

  public void generateExampleStudies() throws Exception {

    // Study 1
    Program program = programRepository.findByName("Clinical Program A")
        .orElseThrow(RecordNotFoundException::new);
    User user = userRepository.findByAccountName("jsmith")
        .orElseThrow(RecordNotFoundException::new);
    Collaborator collaborator = collaboratorRepository.findByLabel("University of Somewhere")
        .orElseThrow(RecordNotFoundException::new);
    Study study = new Study();
    study.setStatus(Status.IN_PLANNING);
    study.setName("Example Collaborator Study");
    //study.setCode(program.getCode() + "-10001");
    study.setProgram(program);
    study.setDescription(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
    study.setLegacy(false);
    study.setActive(true);
    study.setCreatedBy(user);
    study.setLastModifiedBy(user);
    study.setStartDate(new Date());
    study.setOwner(user);
    study.setUsers(Collections.singletonList(user));
    study.setCollaborator(collaborator);
    study.setExternalCode(collaborator.getCode() + "-00001");
    SimpleNotebookEntry notebookEntry = new SimpleNotebookEntry();
    notebookEntry.setLabel("IDBS ELN");
    notebookEntry.setUrl(
        "https://decibel.idbs-eworkbook.com:8443/EWorkbookWebApp/#entity/displayEntity?entityId=603e68c0e01411e7acd000000a0000a2&v=y");
    study.setNotebookEntry(notebookEntry);

    studyService.create(study);

    ExternalLink link = new ExternalLink();
    link.setId(UUID.randomUUID().toString());
    link.setLabel("Google");
    link.setUrl(new URL("https://google.com"));
    externalLinkService.addStudyExternalLink(study, link);

    // Study 2
    program = programRepository.findByName("Preclinical Project B")
        .orElseThrow(RecordNotFoundException::new);
    user = userRepository.findByAccountName("ajohnson").orElseThrow(RecordNotFoundException::new);
    study = new Study();
    study.setStatus(Status.IN_PLANNING);
    study.setName("Example Study");
    //study.setCode(program.getCode() + "-10001");
    study.setProgram(program);
    study.setDescription(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
    study.setLegacy(false);
    study.setActive(true);
    study.setCreatedBy(user);
    study.setLastModifiedBy(user);
    study.setStartDate(new Date());
    study.setOwner(user);
    study.setUsers(Collections.singletonList(user));
    notebookEntry = new SimpleNotebookEntry();
    notebookEntry.setLabel("ELN");
    notebookEntry.setUrl(
        "https://google.com");
    study.setNotebookEntry(notebookEntry);

    studyService.create(study);

    studyService.updateStatus(study, Status.ACTIVE);

    Comment comment = new Comment();
    comment.setId(UUID.randomUUID().toString());
    comment.setCreatedAt(new Date());
    comment.setCreatedBy(user);
    comment.setText(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
    commentService.addStudyComment(study, comment);

    Conclusions conclusions = new Conclusions();
    conclusions.setId(UUID.randomUUID().toString());
    conclusions.setCreatedAt(new Date());
    conclusions.setCreatedBy(user);
    conclusions.setContent(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
    conclusionsService.addStudyConclusions(study, conclusions);

    // Study 3
    program = programRepository.findByName("Preclinical Project B")
        .orElseThrow(RecordNotFoundException::new);
    user = userRepository.findByAccountName("ajohnson").orElseThrow(RecordNotFoundException::new);
    study = new Study();
    study.setStatus(Status.IN_PLANNING);
    study.setName("Example Legacy Study");
    study.setCode(program.getCode() + "-00001");
    study.setProgram(program);
    study.setDescription(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
    study.setLegacy(true);
    study.setActive(true);
    study.setCreatedBy(user);
    study.setLastModifiedBy(user);
    study.setStartDate(new Date());
    study.setEndDate(new Date());
    study.setOwner(user);
    study.setUsers(Collections.singletonList(user));
    notebookEntry = new SimpleNotebookEntry();
    notebookEntry.setLabel("ELN");
    notebookEntry.setUrl(
        "https://google.com");
    study.setNotebookEntry(notebookEntry);
    studyService.create(study);
    studyService.updateStatus(study, Status.COMPLETE);

    // Study 4
    program = programRepository.findByName("Clinical Program A")
        .orElseThrow(RecordNotFoundException::new);
    user = userRepository.findByAccountName("jsmith").orElseThrow(RecordNotFoundException::new);
    study = new Study();
    study.setStatus(Status.IN_PLANNING);
    study.setName("Example Inactive Study");
    //study.setCode(program.getCode() + "-10002");
    study.setProgram(program);
    study.setDescription(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
    study.setLegacy(false);
    study.setActive(false);
    study.setCreatedBy(user);
    study.setLastModifiedBy(user);
    study.setStartDate(new Date());
    study.setOwner(user);
    study.setUsers(Collections.singletonList(user));
    studyService.create(study);
    studyService.updateStatus(study, Status.ON_HOLD);

    // Study 5
    program = programRepository.findByName("Target ID Project D")
        .orElseThrow(RecordNotFoundException::new);
    user = userRepository.findByAccountName("rblack").orElseThrow(RecordNotFoundException::new);
    study = new Study();
    study.setStatus(Status.IN_PLANNING);
    study.setName("Example Target ID Study 1");
    //study.setCode(program.getCode() + "-10001");
    study.setProgram(program);
    study.setDescription(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
    study.setLegacy(false);
    study.setActive(true);
    study.setCreatedBy(user);
    study.setLastModifiedBy(user);
    study.setStartDate(new Date());
    study.setEndDate(new Date());
    study.setOwner(user);
    study.setUsers(Collections.singletonList(user));
    studyService.create(study);
    studyService.updateStatus(study, Status.COMPLETE);

    // Study 6
    program = programRepository.findByName("Target ID Project E")
        .orElseThrow(RecordNotFoundException::new);
    user = userRepository.findByAccountName("rblack").orElseThrow(RecordNotFoundException::new);
    study = new Study();
    study.setStatus(Status.IN_PLANNING);
    study.setName("Example Target ID Study 2");
    study.setCode(program.getCode() + "-10002");
    study.setProgram(program);
    study.setDescription(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
    study.setLegacy(false);
    study.setActive(true);
    study.setCreatedBy(user);
    study.setLastModifiedBy(user);
    study.setStartDate(new Date());
    study.setEndDate(new Date());
    study.setOwner(user);
    study.setUsers(Collections.singletonList(user));
    studyService.create(study);

  }

  public void createStudyFolders() {
    for (Study study : studyRepository.findAll()) {
      try {
        StorageFolder folder;
        try {
          folder = studyStorageService.getStudyFolder(study);
        } catch (Exception e) {
          folder = studyStorageService.createStudyFolder(study);
        }
        study.setStorageFolder(folder);
        study.setUpdatedAt(new Date());
        studyRepository.save(study);
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  public List<Assay> generateExampleAssays(List<Study> studies) {

    List<Assay> assays = new ArrayList<>();

    Study study = studies.stream()
        .filter(s -> s.getCode().equals("PPB-10001"))
        .collect(Collectors.toList())
        .get(0);
    User user = study.getOwner();
    Assay assay = new Assay();
    assay.setStudy(study);
    assay.setActive(true);
    assay.setCode(study.getCode() + "-00001");
    assay.setName("Histology assay");
    assay.setDescription(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. ");
    assay.setStatus(Status.ACTIVE);
    assay.setStartDate(new Date());
    assay.setAssayType(AssayType.HISTOLOGY);
    assay.setOwner(user);
    assay.setCreatedBy(user);
    assay.setUsers(Collections.singletonList(user));
    assay.setLastModifiedBy(user);
    assay.setUpdatedAt(new Date());
    assay.setAttributes(Collections.singletonMap("key", "value"));
    assay.setTasks(Collections.singletonList(new Task("My task")));
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
    assay.setAssayType(AssayType.IN_VIVO);
    assay.setOwner(user);
    assay.setCreatedBy(user);
    assay.setUsers(Collections.singletonList(user));
    assay.setLastModifiedBy(user);
    assay.setUpdatedAt(new Date());
    assay.setAttributes(Collections.singletonMap("key", "value"));
    assay.setTasks(Collections.singletonList(new Task("My task")));
    assays.add(assay);

    return assays;

  }

  public void createAssayFolders() {
    for (Assay assay : assayRepository.findAll()) {
      try {
        StorageFolder folder;
        try {
          folder = studyStorageService.getAssayFolder(assay);
        } catch (Exception e) {
          folder = studyStorageService.createAssayFolder(assay);
        }
        assay.setStorageFolder(folder);
        assayRepository.save(assay);
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  public void populateDatabase() {
    try {

      LOGGER.info("Preparing to populate database with example data...");

      LOGGER.info("Wiping collections...");
      programRepository.deleteAll();
      userRepository.deleteAll();
      collaboratorRepository.deleteAll();
      studyRepository.deleteAll();
      assayRepository.deleteAll();
      activityRepository.deleteAll();

      LOGGER.info("Inserting example data...");
      programRepository.insert(generateExamplePrograms());
      createProgramFolders();
      userRepository.insert(generateExampleUsers());
      collaboratorRepository.insert(generateExampleCollaborators());
      //createStudyFolders();
      generateExampleStudies();

      for (Assay assay : generateExampleAssays(studyRepository.findAll())) {
        assayRepository.insert(assay);
        Study study = assay.getStudy();
        study.getAssays().add(assay);
        studyRepository.save(study);
      }
      createAssayFolders();

      LOGGER.info("Done.");

    } catch (Exception e) {
      throw new StudyTrackerException(e);
    }
  }

}
