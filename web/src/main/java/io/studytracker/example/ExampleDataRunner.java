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

import io.studytracker.config.initialization.DefaultDataInitializer;
import io.studytracker.config.initialization.IntegrationInitializer;
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import io.studytracker.repository.ActivityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ExampleDataRunner {

  public static final int ACTIVITY_COUNT = 13;
  public static final int STORAGE_FOLDER_COUNT = 13;
  public static final int NOTEBOOK_FOLDER_COUNT = 3;
  public static final int STUDY_ROOT_FOLDER_COUNT = 1;
  public static final int BROWSER_ROOT_FOLDER_COUNT = 1;
  public static final int STORAGE_DRIVE_COUNT = 1;
  public static final int STORAGE_DRIVE_FOLDER_COUNT = ExampleStudyGenerator.STUDY_COUNT
      + ExampleProgramGenerator.PROGRAM_COUNT
      + ExampleAssayGenerator.ASSAY_COUNT
      + STUDY_ROOT_FOLDER_COUNT;

  private static final Logger LOGGER = LoggerFactory.getLogger(ExampleDataRunner.class);

  @Autowired private DefaultDataInitializer defaultDataInitializer;
  @Autowired private IntegrationInitializer integrationInitializer;
  @Autowired private ExampleUserGenerator userGenerator;
  @Autowired private ExampleProgramGenerator programGenerator;
  @Autowired private ExampleStudyGenerator studyGenerator;
  @Autowired private ExampleCollaboratorGenerator collaboratorGenerator;
  @Autowired private ExampleAssayTypeGenerator assayTypeGenerator;
  @Autowired private ExampleAssayGenerator assayGenerator;
  @Autowired private ExampleStudyCollectionGenerator studyCollectionGenerator;
  @Autowired private ActivityRepository activityRepository;
  @Autowired private ExampleKeywordGenerator keywordGenerator;
  @Autowired private ExampleStorageFolderGenerator storageFolderGenerator;
  @Autowired private ExampleIntegrationGenerator integrationGenerator;
  @Autowired private ExampleGitRepositoryGenerator gitRepositoryGenerator;

  public void clearDatabase() {
    LOGGER.info("Wiping database...");
    activityRepository.deleteAll();
    studyCollectionGenerator.deleteData();
    assayGenerator.deleteData();
    assayTypeGenerator.deleteData();
    studyGenerator.deleteData();
    collaboratorGenerator.deleteData();
    keywordGenerator.deleteData();
    programGenerator.deleteData();
    userGenerator.deleteData();
    storageFolderGenerator.deleteData();
    gitRepositoryGenerator.deleteData();
    integrationGenerator.deleteData();
  }

  public void populateDatabase() {
    try {

      LOGGER.info("Preparing to populate database with example data...");
      this.clearDatabase();

      LOGGER.info("Reinitializing integrations & drives...");
      integrationInitializer.run(null);

      LOGGER.info("Inserting example data...");

      // Users
      List<User> users = userGenerator.generateData();

      // Programs
      programGenerator.generateData(users);

      // Assay types
      assayTypeGenerator.generateData();

      // Keywords
      keywordGenerator.generateData();

      // Collaborators
      collaboratorGenerator.generateData();

      // Studies
      List<Study> studies = studyGenerator.generateData();

      // Assays
      assayGenerator.generateData(studies);

      // Study collections
      studyCollectionGenerator.generateData(studies);

      LOGGER.info("Done.");

    } catch (Exception e) {
      throw new StudyTrackerException(e);
    }
  }
}
