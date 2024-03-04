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

import io.studytracker.events.util.StudyActivityUtils;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.model.Activity;
import io.studytracker.model.Collaborator;
import io.studytracker.model.Comment;
import io.studytracker.model.ELNFolder;
import io.studytracker.model.ExternalLink;
import io.studytracker.model.Keyword;
import io.studytracker.model.Program;
import io.studytracker.model.Status;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.model.Study;
import io.studytracker.model.StudyConclusions;
import io.studytracker.model.User;
import io.studytracker.repository.ActivityRepository;
import io.studytracker.repository.CollaboratorRepository;
import io.studytracker.repository.CommentRepository;
import io.studytracker.repository.ELNFolderRepository;
import io.studytracker.repository.ExternalLinkRepository;
import io.studytracker.repository.KeywordRepository;
import io.studytracker.repository.ProgramRepository;
import io.studytracker.repository.StorageDriveFolderRepository;
import io.studytracker.repository.StudyConclusionsRepository;
import io.studytracker.repository.StudyNotebookFolderRepository;
import io.studytracker.repository.StudyRelationshipRepository;
import io.studytracker.repository.StudyRepository;
import io.studytracker.repository.UserRepository;
import io.studytracker.storage.StudyStorageService;
import io.studytracker.storage.StudyStorageServiceLookup;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

public class ExampleStudyGenerator implements ExampleDataGenerator<Study> {

  public static final int STUDY_COUNT = 6;
  public static final int COMMENT_COUNT = 1;
  public static final int CONCLUSIONS_COUNT = 1;
  public static final int EXTERNAL_LINK_COUNT = 1;
  public static final int STUDY_RELATIONSHIPS_COUNT = 0;

  @Autowired private KeywordRepository keywordRepository;
  @Autowired private StorageDriveFolderRepository storageDriveFolderRepository;
  @Autowired private StudyStorageServiceLookup studyStorageServiceLookup;
  @Autowired private ProgramRepository programRepository;
  @Autowired private CollaboratorRepository collaboratorRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private StudyRepository studyRepository;
  @Autowired private ActivityRepository activityRepository;
  @Autowired private ExternalLinkRepository externalLinkRepository;
  @Autowired private CommentRepository commentRepository;
  @Autowired private StudyConclusionsRepository studyConclusionsRepository;
  @Autowired private StudyRelationshipRepository studyRelationshipRepository;
  @Autowired private ELNFolderRepository elnFolderRepository;
  @Autowired private StudyNotebookFolderRepository studyNotebookFolderRepository;

  private StorageDriveFolder createStudyFolder(Study study) {
    try {
      StorageDriveFolder programFolder = storageDriveFolderRepository
          .findByProgramId(study.getProgram().getId()).get(0);
      StudyStorageService studyStorageService = studyStorageServiceLookup.lookup(programFolder)
          .orElseThrow(RecordNotFoundException::new);
      return studyStorageService.createStudyFolder(programFolder, study);
    } catch (Exception ex) {
      throw new StudyTrackerException(ex);
    }
  }

  @Override
  public List<Study> generateData(Object... args) throws Exception {

    List<Study> studies = new ArrayList<>();

    Set<Keyword> keywords = new HashSet<>();
    keywords.add(
        keywordRepository
            .findByKeywordAndCategory("AKT1", "Gene")
            .orElseThrow(RecordNotFoundException::new));
    keywords.add(
        keywordRepository
            .findByKeywordAndCategory("MCF7", "Cell Line")
            .orElseThrow(RecordNotFoundException::new));

    // Study 1
    Program program =
        programRepository
            .findByName("Clinical Program A")
            .orElseThrow(RecordNotFoundException::new);
    User user = userRepository.findByEmail("jsmith@email.com").orElseThrow(RecordNotFoundException::new);
    Collaborator collaborator =
        collaboratorRepository
            .findByLabel("University of Somewhere")
            .orElseThrow(RecordNotFoundException::new);
    Study study = new Study();
    study.setStatus(Status.IN_PLANNING);
    study.setName("Example Collaborator Study");
    study.setCode(program.getCode() + "-10001");
    study.setProgram(program);
    study.setDescription(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
    study.setLegacy(false);
    study.setActive(true);
    study.setCreatedBy(user);
    study.setLastModifiedBy(user);
    study.setStartDate(new Date());
    study.setOwner(user);
    study.setUsers(Collections.singleton(user));
    study.setCollaborator(collaborator);
    study.setExternalCode(collaborator.getCode() + "-00001");
    study.setKeywords(keywords);
    study.addStorageFolder(createStudyFolder(study), true);

    ELNFolder elnFolder = new ELNFolder();
    elnFolder.setName("IDBS ELN");
    elnFolder.setUrl(
        "https://example.idbs-eworkbook.com:8443/EWorkbookWebApp/#entity/displayEntity?entityId=603e68c0e01411e7acd000000a0000a2&v=y");
    elnFolder.setReferenceId("12345");
    elnFolderRepository.save(elnFolder);
    study.addNotebookFolder(elnFolder, true);

    studyRepository.save(study);
    studies.add(study);

    activityRepository.save(StudyActivityUtils.fromNewStudy(study, user));

    ExternalLink link = new ExternalLink();
    link.setLabel("Google");
    link.setUrl(new URL("https://google.com"));
    link.setStudy(study);
    study.addExternalLink(link);
    externalLinkRepository.save(link);

    activityRepository.save(StudyActivityUtils.fromNewExternalLink(study, user, link));

    // Study 2
    program =
        programRepository
            .findByName("Preclinical Project B")
            .orElseThrow(RecordNotFoundException::new);
    user = userRepository.findByEmail("ajohnson@email.com").orElseThrow(RecordNotFoundException::new);
    study = new Study();
    study.setStatus(Status.IN_PLANNING);
    study.setName("Example Study");
    study.setCode(program.getCode() + "-10001");
    study.setProgram(program);
    study.setDescription(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
    study.setLegacy(false);
    study.setActive(true);
    study.setCreatedBy(user);
    study.setLastModifiedBy(user);
    study.setStartDate(new Date());
    study.setOwner(user);
    study.setUsers(Collections.singleton(user));
    study.setKeywords(keywords);
    study.addStorageFolder(createStudyFolder(study), true);

    elnFolder = new ELNFolder();
    elnFolder.setName("ELN");
    elnFolder.setUrl("https://google.com");
    elnFolder.setReferenceId("12345");
    elnFolderRepository.save(elnFolder);
    study.addNotebookFolder(elnFolder, true);

    studyRepository.save(study);

    activityRepository.save(StudyActivityUtils.fromNewStudy(study, user));

    study.setStatus(Status.ACTIVE);
    studyRepository.save(study);

    Activity activity =
        StudyActivityUtils.fromStudyStatusChange(study, user, Status.IN_PLANNING, Status.ACTIVE);
    activityRepository.save(activity);

    Comment comment = new Comment();
    comment.setCreatedAt(new Date());
    comment.setCreatedBy(user);
    comment.setText(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
    comment.setStudy(study);
    commentRepository.save(comment);

    activityRepository.save(StudyActivityUtils.fromNewComment(study, user, comment));

    StudyConclusions conclusions = new StudyConclusions();
    conclusions.setCreatedAt(new Date());
    conclusions.setCreatedBy(user);
    conclusions.setContent(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
    conclusions.setStudy(study);
    conclusions.setLastModifiedBy(user);
    studyConclusionsRepository.save(conclusions);

    activityRepository.save(StudyActivityUtils.fromNewConclusions(study, user, conclusions));
    studies.add(study);

    // Study 3
    program =
        programRepository
            .findByName("Preclinical Project B")
            .orElseThrow(RecordNotFoundException::new);
    user = userRepository.findByEmail("ajohnson@email.com").orElseThrow(RecordNotFoundException::new);
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
    study.setUsers(Collections.singleton(user));
    study.setKeywords(keywords);

    elnFolder = new ELNFolder();
    elnFolder.setName("ELN");
    elnFolder.setUrl("https://google.com");
    elnFolderRepository.save(elnFolder);
    study.addNotebookFolder(elnFolder, true);

    study.addStorageFolder(createStudyFolder(study), true);
    studyRepository.save(study);

    activityRepository.save(StudyActivityUtils.fromNewStudy(study, user));

    study.setStatus(Status.COMPLETE);
    studyRepository.save(study);

    activityRepository.save(
        StudyActivityUtils.fromStudyStatusChange(study, user, Status.IN_PLANNING, Status.COMPLETE));
    studies.add(study);


    // Study 4
    program =
        programRepository
            .findByName("Clinical Program A")
            .orElseThrow(RecordNotFoundException::new);
    user = userRepository.findByEmail("jsmith@email.com").orElseThrow(RecordNotFoundException::new);
    study = new Study();
    study.setStatus(Status.IN_PLANNING);
    study.setName("Example Inactive Study");
    study.setCode(program.getCode() + "-10002");
    study.setProgram(program);
    study.setDescription(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
    study.setLegacy(false);
    study.setActive(false);
    study.setCreatedBy(user);
    study.setLastModifiedBy(user);
    study.setStartDate(new Date());
    study.setOwner(user);
    study.setUsers(Collections.singleton(user));
    study.setKeywords(keywords);
    study.addStorageFolder(createStudyFolder(study), true);
    studyRepository.save(study);

    activityRepository.save(StudyActivityUtils.fromNewStudy(study, user));

    study.setStatus(Status.ON_HOLD);
    studyRepository.save(study);

    activityRepository.save(
        StudyActivityUtils.fromStudyStatusChange(study, user, Status.IN_PLANNING, Status.ON_HOLD));
    studies.add(study);

    // Study 5
    program =
        programRepository
            .findByName("Target ID Project D")
            .orElseThrow(RecordNotFoundException::new);
    user = userRepository.findByEmail("rblack@email.com").orElseThrow(RecordNotFoundException::new);
    study = new Study();
    study.setStatus(Status.IN_PLANNING);
    study.setName("Example Target ID Study 1");
    study.setCode(program.getCode() + "-10001");
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
    study.setUsers(Collections.singleton(user));
    study.setKeywords(keywords);
    study.addStorageFolder(createStudyFolder(study), true);
    studyRepository.save(study);
    activityRepository.save(StudyActivityUtils.fromNewStudy(study, user));

    study.setStatus(Status.COMPLETE);
    studyRepository.save(study);
    activityRepository.save(
        StudyActivityUtils.fromStudyStatusChange(study, user, Status.IN_PLANNING, Status.COMPLETE));
    studies.add(study);

    // Study 6
    program =
        programRepository
            .findByName("Target ID Project E")
            .orElseThrow(RecordNotFoundException::new);
    user = userRepository.findByEmail("rblack@email.com").orElseThrow(RecordNotFoundException::new);
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
    study.setUsers(Collections.singleton(user));
    study.setKeywords(keywords);
    study.addStorageFolder(createStudyFolder(study), true);
    studyRepository.save(study);
    activityRepository.save(StudyActivityUtils.fromNewStudy(study, user));
    studies.add(study);

    return studies;
  }

  @Override
  public void deleteData() {
    studyNotebookFolderRepository.deleteAll();
    elnFolderRepository.deleteAll();
    externalLinkRepository.deleteAll();
    studyConclusionsRepository.deleteAll();
    commentRepository.deleteAll();
    studyRelationshipRepository.deleteAll();
    studyRepository.deleteAll();
  }
}
