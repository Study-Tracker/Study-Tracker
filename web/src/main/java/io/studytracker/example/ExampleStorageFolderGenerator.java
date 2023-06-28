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

import io.studytracker.model.StorageDriveFolder;
import io.studytracker.repository.StorageDriveFolderRepository;
import io.studytracker.repository.StorageDriveRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class ExampleStorageFolderGenerator implements ExampleDataGenerator<StorageDriveFolder> {

  @Autowired private StorageDriveRepository storageDriveRepository;
  @Autowired private StorageDriveFolderRepository storageDriveFolderRepository;

  @Override
  public List<StorageDriveFolder> generateData(Object... args) throws Exception {
    return null;
  }

  @Override
  public void deleteData() {
    storageDriveFolderRepository.deleteAll();
    storageDriveRepository.deleteAll();
  }
}
