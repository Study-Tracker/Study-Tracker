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

import io.studytracker.model.Collaborator;
import io.studytracker.repository.CollaboratorRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ExampleCollaboratorGenerator implements ExampleDataGenerator<Collaborator> {

  public static final int COLLABORATOR_COUNT = 4;

  @Autowired
  private CollaboratorRepository collaboratorRepository;

  @Override
  public List<Collaborator> generateData(Object... args) {
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

    return collaboratorRepository.saveAll(collaborators);
  }

  @Override
  public void deleteData() {
    collaboratorRepository.deleteAll();
  }
}
