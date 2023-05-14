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

import io.studytracker.model.AssayType;
import io.studytracker.model.AssayTypeField;
import io.studytracker.model.AssayTypeTask;
import io.studytracker.model.CustomEntityFieldType;
import io.studytracker.model.TaskStatus;
import io.studytracker.repository.AssayTypeFieldRepository;
import io.studytracker.repository.AssayTypeRepository;
import io.studytracker.repository.AssayTypeTaskRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class ExampleAssayTypeGenerator implements ExampleDataGenerator<AssayType> {

  public static final int ASSAY_TYPE_COUNT = 2;

  @Autowired
  private AssayTypeRepository assayTypeRepository;

  @Autowired
  private AssayTypeFieldRepository assayTypeFieldRepository;

  @Autowired
  private AssayTypeTaskRepository assayTypeTaskRepository;

  @Override
  public List<AssayType> generateData(Object... args) throws Exception {

    List<AssayType> assayTypes = new ArrayList<>();
    AssayType assayType;

    if (assayTypeRepository.findByName("Generic").isEmpty()) {
      assayType = new AssayType();
      assayType.setName("Generic");
      assayType.setDescription("Generic assay type for all purposes");
      assayType.setActive(true);
      assayTypeRepository.save(assayType);
      assayTypes.add(assayType);
    }

    assayType = new AssayType();
    assayType.setName("Histology");
    assayType.setDescription("Histological analysis assays");
    assayType.setActive(true);
    assayTypeRepository.save(assayType);
    assayTypes.add(assayType);

    List<AssayTypeField> fields =
        Arrays.asList(
            new AssayTypeField(
                assayType, "No. Slides", "number_of_slides", CustomEntityFieldType.INTEGER, 1, true),
            new AssayTypeField(assayType, "Antibodies", "antibodies", CustomEntityFieldType.TEXT, 2),
            new AssayTypeField(
                assayType, "Concentration (ul/mg)", "concentration", CustomEntityFieldType.FLOAT, 3),
            new AssayTypeField(assayType, "Date", "date", CustomEntityFieldType.DATE, 4),
            new AssayTypeField(
                assayType, "External", "external", CustomEntityFieldType.BOOLEAN, 5, true),
            new AssayTypeField(assayType, "Stain", "stain", CustomEntityFieldType.STRING, 6));
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

    return assayTypes;

  }

  @Override
  public void deleteData() {
    assayTypeTaskRepository.deleteAll();
    assayTypeFieldRepository.deleteAll();
    assayTypeRepository.deleteAll();
  }
}
