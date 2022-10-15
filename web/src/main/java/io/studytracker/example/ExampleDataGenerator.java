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

package io.studytracker.example;

import io.studytracker.events.util.StudyActivityUtils;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.model.Activity;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayTask;
import io.studytracker.model.AssayType;
import io.studytracker.model.AssayTypeField;
import io.studytracker.model.AssayTypeTask;
import io.studytracker.model.Collaborator;
import io.studytracker.model.Comment;
import io.studytracker.model.CustomEntityFieldType;
import io.studytracker.model.ELNFolder;
import io.studytracker.model.ExternalLink;
import io.studytracker.model.FileStoreFolder;
import io.studytracker.model.Keyword;
import io.studytracker.model.KeywordCategory;
import io.studytracker.model.NotebookEntryTemplate;
import io.studytracker.model.NotebookEntryTemplate.Category;
import io.studytracker.model.Program;
import io.studytracker.model.Status;
import io.studytracker.model.Study;
import io.studytracker.model.StudyCollection;
import io.studytracker.model.StudyConclusions;
import io.studytracker.model.TaskStatus;
import io.studytracker.model.User;
import io.studytracker.model.UserType;
import io.studytracker.repository.ActivityRepository;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.AssayTaskRepository;
import io.studytracker.repository.AssayTypeFieldRepository;
import io.studytracker.repository.AssayTypeRepository;
import io.studytracker.repository.AssayTypeTaskRepository;
import io.studytracker.repository.CollaboratorRepository;
import io.studytracker.repository.CommentRepository;
import io.studytracker.repository.ExternalLinkRepository;
import io.studytracker.repository.KeywordCategoryRepository;
import io.studytracker.repository.KeywordRepository;
import io.studytracker.repository.NotebookEntryTemplateRepository;
import io.studytracker.repository.PasswordResetTokenRepository;
import io.studytracker.repository.ProgramRepository;
import io.studytracker.repository.StudyCollectionRepository;
import io.studytracker.repository.StudyConclusionsRepository;
import io.studytracker.repository.StudyRelationshipRepository;
import io.studytracker.repository.StudyRepository;
import io.studytracker.repository.UserRepository;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StudyStorageService;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.Assert;

public class ExampleDataGenerator {

  public static final int PROGRAM_COUNT = 5;
  public static final int USER_COUNT = 3;
  public static final int COLLABORATOR_COUNT = 4;
  public static final int KEYWORD_CATEGORY_COUNT = 2;
  public static final int KEYWORD_COUNT = 7;
  public static final int ASSAY_TYPE_COUNT = 2;
  public static final int ASSAY_COUNT = 2;
  public static final int NOTEBOOK_ENTRY_TEMPLATE_COUNT = 5;
  public static final int STUDY_COUNT = 6;
  public static final int ACTIVITY_COUNT = 13;
  public static final int STORAGE_FOLDER_COUNT = 13;
  public static final int NOTEBOOK_FOLDER_COUNT = 3;
  public static final int COMMENT_COUNT = 1;
  public static final int STUDY_RELATIONSHIPS_COUNT = 0;
  public static final int ASSAY_TASK_COUNT = 2;
  public static final int CONCLUSIONS_COUNT = 1;
  public static final int EXTERNAL_LINK_COUNT = 1;
  public static final int STUDY_COLLECTION_COUNT = 2;

  private static final Logger LOGGER = LoggerFactory.getLogger(ExampleDataGenerator.class);

  @Autowired private ProgramRepository programRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private StudyRepository studyRepository;

  @Autowired private CollaboratorRepository collaboratorRepository;

  @Autowired private AssayRepository assayRepository;

  @Autowired private ActivityRepository activityRepository;

  @Autowired private StudyStorageService studyStorageService;

  @Autowired private AssayTypeRepository assayTypeRepository;

  @Autowired private AssayTypeFieldRepository assayTypeFieldRepository;

  @Autowired private AssayTypeTaskRepository assayTypeTaskRepository;

  @Autowired private KeywordRepository keywordRepository;

  @Autowired private CommentRepository commentRepository;

  @Autowired private StudyConclusionsRepository studyConclusionsRepository;

  @Autowired private NotebookEntryTemplateRepository notebookEntryTemplateRepository;

  @Autowired private AssayTaskRepository assayTaskRepository;

  @Autowired private StudyRelationshipRepository studyRelationshipRepository;

  @Autowired private ExternalLinkRepository externalLinkRepository;

  @Autowired private PasswordResetTokenRepository passwordResetTokenRepository;

  @Autowired private KeywordCategoryRepository keywordCategoryRepository;

  @Autowired private StudyCollectionRepository studyCollectionRepository;

  public List<NotebookEntryTemplate> generateExampleEntryTemplates() {
    User user = userRepository.findAll().get(0);
    List<NotebookEntryTemplate> templates = new ArrayList<>();

    NotebookEntryTemplate template = new NotebookEntryTemplate();
    template.setName("Default Study Template");
    template.setTemplateId("id1");
    template.setActive(true);
    template.setDefault(true);
    template.setCategory(Category.STUDY);
    template.setCreatedBy(user);
    template.setLastModifiedBy(user);
    template.setCreatedAt(new Date());
    template.setUpdatedAt(new Date());
    templates.add(template);

    template = new NotebookEntryTemplate();
    template.setName("Active Study Template");
    template.setTemplateId("id2");
    template.setActive(true);
    template.setDefault(false);
    template.setCategory(Category.STUDY);
    template.setCreatedBy(user);
    template.setLastModifiedBy(user);
    template.setCreatedAt(new Date());
    template.setUpdatedAt(new Date());
    templates.add(template);

    template = new NotebookEntryTemplate();
    template.setName("Inactive Study Template");
    template.setTemplateId("id3");
    template.setActive(false);
    template.setDefault(false);
    template.setCategory(Category.STUDY);
    template.setCreatedBy(user);
    template.setLastModifiedBy(user);
    template.setCreatedAt(new Date());
    template.setUpdatedAt(new Date());
    templates.add(template);

    template = new NotebookEntryTemplate();
    template.setName("Default Assay Template");
    template.setTemplateId("id4");
    template.setActive(true);
    template.setDefault(true);
    template.setCategory(Category.ASSAY);
    template.setCreatedBy(user);
    template.setLastModifiedBy(user);
    template.setCreatedAt(new Date());
    template.setUpdatedAt(new Date());
    templates.add(template);

    template = new NotebookEntryTemplate();
    template.setName("Active Assay Template");
    template.setTemplateId("id5");
    template.setActive(true);
    template.setDefault(false);
    template.setCategory(Category.ASSAY);
    template.setCreatedBy(user);
    template.setLastModifiedBy(user);
    template.setCreatedAt(new Date());
    template.setUpdatedAt(new Date());
    templates.add(template);

    return templates;
  }

  public List<Program> generateExamplePrograms(List<User> users) {
    User user = users.get(0);
    List<Program> programs = new ArrayList<>();

    Program program = new Program();
    program.setName("Clinical Program A");
    program.setCode("CPA");
    program.setActive(true);
    program.setCreatedBy(user);
    program.setLastModifiedBy(user);
    program.setCreatedAt(new Date());
    program.setStorageFolder(createProgramFolder(program));
    programs.add(program);

    program = new Program();
    program.setName("Preclinical Project B");
    program.setCode("PPB");
    program.setActive(true);
    program.setCreatedBy(user);
    program.setLastModifiedBy(user);
    program.setCreatedAt(new Date());
    program.setStorageFolder(createProgramFolder(program));
    programs.add(program);

    program = new Program();
    program.setName("Cancelled Program C");
    program.setCode("CPC");
    program.setActive(false);
    program.setCreatedBy(user);
    program.setLastModifiedBy(user);
    program.setCreatedAt(new Date());
    program.setStorageFolder(createProgramFolder(program));
    programs.add(program);

    program = new Program();
    program.setName("Target ID Project D");
    program.setCode("TID");
    program.setActive(true);
    program.setCreatedBy(user);
    program.setLastModifiedBy(user);
    program.setCreatedAt(new Date());
    program.setStorageFolder(createProgramFolder(program));
    programs.add(program);

    program = new Program();
    program.setName("Target ID Project E");
    program.setCode("TID");
    program.setActive(true);
    program.setCreatedBy(user);
    program.setLastModifiedBy(user);
    program.setCreatedAt(new Date());
    program.setStorageFolder(createProgramFolder(program));
    programs.add(program);

    return programs;
  }

  public FileStoreFolder createProgramFolder(Program program) {
    try {
      StorageFolder folder;
      try {
        folder = studyStorageService.getProgramFolder(program);
      } catch (Exception e) {
        folder = studyStorageService.createProgramFolder(program);
      }
      Assert.notNull(folder, "Program folder must not be null");
      return FileStoreFolder.from(folder);
    } catch (Exception ex) {
      throw new StudyTrackerException(ex);
    }
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
      throw new StudyTrackerException(e);
    }
  }

  public List<User> generateExampleUsers() {

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    List<User> users = new ArrayList<>();

    User user = new User();
    user.setPassword(encoder.encode("password"));
    user.setDisplayName("Joe Smith");
    user.setEmail("jsmith@email.com");
    user.setUsername(user.getEmail());
    user.setType(UserType.STANDARD_USER);
    user.setTitle("Director");
    user.setAdmin(false);
    user.setDepartment("Biology");
    users.add(user);

    user = new User();
    user.setPassword(encoder.encode("password"));
    user.setDisplayName("Ann Johnson");
    user.setEmail("ajohnson@email.com");
    user.setUsername(user.getEmail());
    user.setType(UserType.STANDARD_USER);
    user.setTitle("Sr. Scientist");
    user.setAdmin(false);
    user.setDepartment("Biology");
    users.add(user);

    user = new User();
    user.setPassword(encoder.encode("password"));
    user.setDisplayName("Rob Black");
    user.setEmail("rblack@email.com");
    user.setUsername(user.getEmail());
    user.setType(UserType.STANDARD_USER);
    user.setTitle("IT Admin");
    user.setAdmin(true);
    user.setDepartment("IT");
    users.add(user);

    return users;
  }

  public List<KeywordCategory> generateExampleKeywordCategories() {
    List<KeywordCategory> categories = new ArrayList<>();
    categories.add(new KeywordCategory("Cell Line"));
    categories.add(new KeywordCategory("Gene"));
    return categories;
  }

  public List<Keyword> generateExampleKeywords(List<KeywordCategory> categories) {

    KeywordCategory category = categories.get(0);
    KeywordCategory category2 = categories.get(1);

    List<Keyword> keywords = new ArrayList<>();
    keywords.add(new Keyword(category, "MCF7"));
    keywords.add(new Keyword(category, "HELA"));
    keywords.add(new Keyword(category, "A375"));
    keywords.add(new Keyword(category2, "AKT1"));
    keywords.add(new Keyword(category2, "AKT2"));
    keywords.add(new Keyword(category2, "AKT3"));
    keywords.add(new Keyword(category2, "PTEN"));
    return keywords;
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
    study.setStorageFolder(createStudyFolder(study));

    ELNFolder notebookEntry = new ELNFolder();
    notebookEntry.setName("IDBS ELN");
    notebookEntry.setUrl(
        "https://example.idbs-eworkbook.com:8443/EWorkbookWebApp/#entity/displayEntity?entityId=603e68c0e01411e7acd000000a0000a2&v=y");
    notebookEntry.setReferenceId("12345");
    study.setNotebookFolder(notebookEntry);

    studyRepository.save(study);

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
    study.setStorageFolder(createStudyFolder(study));

    notebookEntry = new ELNFolder();
    notebookEntry.setName("ELN");
    notebookEntry.setUrl("https://google.com");
    notebookEntry.setReferenceId("12345");
    study.setNotebookFolder(notebookEntry);

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
    notebookEntry = new ELNFolder();
    notebookEntry.setName("ELN");
    notebookEntry.setUrl("https://google.com");
    study.setNotebookFolder(notebookEntry);
    study.setStorageFolder(createStudyFolder(study));
    studyRepository.save(study);

    activityRepository.save(StudyActivityUtils.fromNewStudy(study, user));

    study.setStatus(Status.COMPLETE);
    studyRepository.save(study);

    activityRepository.save(
        StudyActivityUtils.fromStudyStatusChange(study, user, Status.IN_PLANNING, Status.COMPLETE));

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
    study.setStorageFolder(createStudyFolder(study));
    studyRepository.save(study);

    activityRepository.save(StudyActivityUtils.fromNewStudy(study, user));

    study.setStatus(Status.ON_HOLD);
    studyRepository.save(study);

    activityRepository.save(
        StudyActivityUtils.fromStudyStatusChange(study, user, Status.IN_PLANNING, Status.ON_HOLD));

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
    study.setStorageFolder(createStudyFolder(study));
    studyRepository.save(study);
    activityRepository.save(StudyActivityUtils.fromNewStudy(study, user));

    study.setStatus(Status.COMPLETE);
    studyRepository.save(study);
    activityRepository.save(
        StudyActivityUtils.fromStudyStatusChange(study, user, Status.IN_PLANNING, Status.COMPLETE));

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
    study.setStorageFolder(createStudyFolder(study));
    studyRepository.save(study);
    activityRepository.save(StudyActivityUtils.fromNewStudy(study, user));
  }

  public FileStoreFolder createStudyFolder(Study study) {
    try {
      StorageFolder folder;
      try {
        folder = studyStorageService.getStudyFolder(study);
      } catch (Exception e) {
        folder = studyStorageService.createStudyFolder(study);
      }
      Assert.notNull(folder, "Study folder must not be null");
      return FileStoreFolder.from(folder);
    } catch (Exception ex) {
      throw new StudyTrackerException(ex);
    }
  }

  //  public void createStudyFolders() {
  //    for (Study study : studyRepository.findAll()) {
  //      try {
  //        StorageFolder folder;
  //        try {
  //          folder = studyStorageService.getStudyFolder(study);
  //        } catch (Exception e) {
  //          folder = studyStorageService.createStudyFolder(study);
  //        }
  //        study.setStorageFolder(FileStoreFolder.from(folder));
  //        studyRepository.save(study);
  //      } catch (Exception ex) {
  //        throw new RuntimeException(ex);
  //      }
  //    }
  //  }

  public void generateExampleAssayTypes() {

    List<AssayType> assayTypes = new ArrayList<>();

    AssayType assayType = new AssayType();
    assayType.setName("Generic");
    assayType.setDescription("Generic assay type for all purposes");
    assayType.setActive(true);
    assayTypes.add(assayType);

    assayTypeRepository.save(assayType);

    assayType = new AssayType();
    assayType.setName("Histology");
    assayType.setDescription("Histological analysis assays");
    assayType.setActive(true);

    assayTypeRepository.save(assayType);

    List<AssayTypeField> fields =
        Arrays.asList(
            new AssayTypeField(
                assayType, "No. Slides", "number_of_slides", CustomEntityFieldType.INTEGER, true),
            new AssayTypeField(assayType, "Antibodies", "antibodies", CustomEntityFieldType.TEXT),
            new AssayTypeField(
                assayType, "Concentration (ul/mg)", "concentration", CustomEntityFieldType.FLOAT),
            new AssayTypeField(assayType, "Date", "date", CustomEntityFieldType.DATE),
            new AssayTypeField(
                assayType, "External", "external", CustomEntityFieldType.BOOLEAN, true),
            new AssayTypeField(assayType, "Stain", "stain", CustomEntityFieldType.STRING));
    assayTypeFieldRepository.saveAll(fields);

    AssayTypeTask task1 = new AssayTypeTask();
    task1.setLabel("Embed tissue");
    task1.setStatus(TaskStatus.TODO);
    task1.setOrder(0);
    task1.setAssayType(assayType);
    assayTypeTaskRepository.save(task1);

    AssayTypeTask task2 = new AssayTypeTask();
    task2.setLabel("Cut slides");
    task2.setStatus(TaskStatus.TODO);
    task2.setOrder(1);
    task2.setAssayType(assayType);
    assayTypeTaskRepository.save(task2);

    AssayTypeTask task3 = new AssayTypeTask();
    task3.setLabel("Stain slides");
    task3.setStatus(TaskStatus.TODO);
    task3.setOrder(2);
    task3.setAssayType(assayType);
    assayTypeTaskRepository.save(task3);
  }

  public void generateExampleAssays(List<Study> studies) {

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
    assay.setStorageFolder(createAssayFolder(assay));

    AssayTask task = new AssayTask();
    task.setLabel("My task");
    task.setOrder(0);
    task.setStatus(TaskStatus.TODO);
    task.setCreatedBy(user);
    task.setLastModifiedBy(user);
    assay.addTask(task);

    assayRepository.save(assay);

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
    assay.setStorageFolder(createAssayFolder(assay));

    task = new AssayTask();
    task.setLabel("My task");
    task.setOrder(0);
    task.setStatus(TaskStatus.TODO);
    task.setCreatedBy(user);
    task.setLastModifiedBy(user);
    assay.addTask(task);

    assayRepository.save(assay);
  }

  public FileStoreFolder createAssayFolder(Assay assay) {
    try {
      StorageFolder folder;
      try {
        folder = studyStorageService.getAssayFolder(assay);
      } catch (Exception e) {
        folder = studyStorageService.createAssayFolder(assay);
      }
      Assert.notNull(folder, "Assay folder must not be null");
      return FileStoreFolder.from(folder);
    } catch (Exception ex) {
      throw new StudyTrackerException(ex);
    }
  }

  public List<StudyCollection> generateStudyCollections(List<Study> studies) {
    List<StudyCollection> collections = new ArrayList<>();
    User user = userRepository.findByEmail("rblack@email.com").orElseThrow(RecordNotFoundException::new);

    StudyCollection collection = new StudyCollection();
    collection.setName("Example public collection");
    collection.setDescription("This is a test");
    collection.setShared(true);
    collection.setCreatedBy(user);
    collection.setLastModifiedBy(user);
    collection.setCreatedAt(new Date());
    collection.setUpdatedAt(new Date());
    Set<Study> studySet = new HashSet<>();
    studySet.add(studies.stream().filter(s -> s.getCode().equals("CPA-10001")).findFirst().get());
    studySet.add(studies.stream().filter(s -> s.getCode().equals("PPB-10001")).findFirst().get());
    collection.setStudies(studySet);
    collections.add(collection);

    collection = new StudyCollection();
    collection.setName("Example private collection");
    collection.setDescription("This is also a test");
    collection.setShared(false);
    collection.setCreatedBy(user);
    collection.setLastModifiedBy(user);
    collection.setCreatedAt(new Date());
    collection.setUpdatedAt(new Date());
    studySet = new HashSet<>();
    studySet.add(studies.stream().filter(s -> s.getCode().equals("CPA-10002")).findFirst().get());
    collection.setStudies(studySet);
    collections.add(collection);

    return collections;

  }

  public void clearDatabase() {
    LOGGER.info("Wiping collections...");
    studyCollectionRepository.deleteAll();
    passwordResetTokenRepository.deleteAll();
    externalLinkRepository.deleteAll();
    commentRepository.deleteAll();
    assayTaskRepository.deleteAll();
    studyConclusionsRepository.deleteAll();
    activityRepository.deleteAll();
    assayRepository.deleteAll();
    studyRelationshipRepository.deleteAll();
    studyRepository.deleteAll();
    notebookEntryTemplateRepository.deleteAll();
    assayTypeTaskRepository.deleteAll();
    assayTypeFieldRepository.deleteAll();
    assayTypeRepository.deleteAll();
    collaboratorRepository.deleteAll();
    keywordRepository.deleteAll();
    keywordCategoryRepository.deleteAll();
    programRepository.deleteAll();
    userRepository.deleteAll();
  }

  public void populateDatabase() {
    try {

      LOGGER.info("Preparing to populate database with example data...");
      this.clearDatabase();
      LOGGER.info("Inserting example data...");
      userRepository.saveAll(generateExampleUsers());
      programRepository.saveAll(generateExamplePrograms(userRepository.findAll()));
      generateExampleAssayTypes();

      List<KeywordCategory> categories = generateExampleKeywordCategories();
      keywordCategoryRepository.saveAll(categories);
      keywordRepository.saveAll(generateExampleKeywords(categories));

      collaboratorRepository.saveAll(generateExampleCollaborators());
      notebookEntryTemplateRepository.saveAll(generateExampleEntryTemplates());
      generateExampleStudies();
      generateExampleAssays(studyRepository.findAll());
      studyCollectionRepository.saveAll(generateStudyCollections(studyRepository.findAll()));
      LOGGER.info("Done.");

    } catch (Exception e) {
      throw new StudyTrackerException(e);
    }
  }
}
