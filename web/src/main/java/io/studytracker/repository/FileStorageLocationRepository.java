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

package io.studytracker.repository;

import io.studytracker.model.FileStorageLocation;
import io.studytracker.storage.StorageLocationType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FileStorageLocationRepository extends JpaRepository<FileStorageLocation, Long> {

  List<FileStorageLocation> findByType(StorageLocationType type);

  @Query("select f from FileStorageLocation  f where f.defaultStudyLocation = true")
  List<FileStorageLocation> findByDefaultStudyLocation();

  @Query("select f from FileStorageLocation  f where f.defaultStudyLocation = true and f.type = ?1")
  List<FileStorageLocation> findByDefaultStudyLocationByType(StorageLocationType type);

  @Query("select f from FileStorageLocation  f where f.defaultDataLocation = true")
  List<FileStorageLocation> findByDefaultDataLocation();

  @Query("select f from FileStorageLocation f where f.integrationInstance.id = ?1")
  List<FileStorageLocation> findByIntegrationInstance(Long integrationInstanceId);

  @Query("select l from FileStorageLocation l join FileStoreFolder f on l.id = f.fileStorageLocation.id where f.id = ?1")
  FileStorageLocation findByFileStoreFolderId(Long fileStoreFolderId);

}
