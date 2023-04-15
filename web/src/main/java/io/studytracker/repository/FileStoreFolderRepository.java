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

package io.studytracker.repository;

import io.studytracker.model.FileStoreFolder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Deprecated
public interface FileStoreFolderRepository extends JpaRepository<FileStoreFolder, Long> {

  @Query("select f from FileStoreFolder  f where f.fileStorageLocation.id = ?1 and f.path = ?2")
  List<FileStoreFolder> findByPath(Long locationId, String path);

  @Query("select f from FileStoreFolder f where f.fileStorageLocation.name = 'PLACEHOLDER_FILE_STORE'")
  List<FileStoreFolder> findFoldersWithPlaceholderLocations();

}
