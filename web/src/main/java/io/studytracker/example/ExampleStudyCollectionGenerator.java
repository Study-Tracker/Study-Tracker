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
import io.studytracker.model.Study;
import io.studytracker.model.StudyCollection;
import io.studytracker.model.User;
import io.studytracker.repository.StudyCollectionRepository;
import io.studytracker.repository.UserRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

public class ExampleStudyCollectionGenerator implements ExampleDataGenerator<StudyCollection> {

  public static final int STUDY_COLLECTION_COUNT = 2;

  @Autowired private StudyCollectionRepository studyCollectionRepository;
  @Autowired private UserRepository userRepository;

  @Override
  public List<StudyCollection> generateData(Object... args) throws Exception {

    List<Study> studies = (List<Study>) args[0];
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

    return studyCollectionRepository.saveAll(collections);
  }

  @Override
  public void deleteData() {
    studyCollectionRepository.deleteAll();
  }
}
