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

import io.studytracker.model.ELNFolder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ELNFolderRepository extends JpaRepository<ELNFolder, Long> {

  @Query("select f from Program p join p.notebookFolders nf join nf.elnFolder f where p.id = ?1")
  List<ELNFolder> findByProgramId(Long programId);

  @Query("select f from Program p join p.notebookFolders nf join nf.elnFolder f where p.id = ?1 and nf.primary = true")
  Optional<ELNFolder> findPrimaryByProgramId(Long programId);

  @Query("select f from Study s join s.notebookFolders nf join nf.elnFolder f where s.id = ?1")
  List<ELNFolder> findByStudyId(Long studyId);

  @Query("select f from Study s join s.notebookFolders nf join nf.elnFolder f where s.id = ?1 and nf.primary = true")
  Optional<ELNFolder> findPrimaryByStudyId(Long studyId);

  @Query("select f from Assay a join a.notebookFolders nf join nf.elnFolder f where a.id = ?1")
  List<ELNFolder> findByAssayId(Long assayId);

  @Query("select f from Assay a join a.notebookFolders nf join nf.elnFolder f where a.id = ?1 and nf.primary = true")
  Optional<ELNFolder> findPrimaryByAssayId(Long assayId);
}
