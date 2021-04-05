/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.decibeltx.studytracker.benchling.eln;

import com.decibeltx.studytracker.benchling.eln.entities.BenchlingEntry;
import com.decibeltx.studytracker.benchling.eln.entities.BenchlingEntryRequest;
import com.decibeltx.studytracker.benchling.eln.entities.BenchlingFolder;
import com.decibeltx.studytracker.benchling.eln.entities.BenchlingProject;
import java.util.List;
import java.util.Optional;

public interface BenchlingElnClientOperations {

  //// Projects

  /**
   * Returns a list of all projects as {@link BenchlingProject} objects.
   *
   * @return project list
   */
  List<BenchlingProject> findProjects();

  /**
   * Returns an {@link Optional} project object.
   *
   * @param id project ID
   * @return
   */
  Optional<BenchlingProject> findProjectById(String id);

  //// Folders

  /**
   * Returns all of the {@link BenchlingFolder} at the root of the folder hierarchy.
   *
   * @return
   */
  List<BenchlingFolder> findRootFolders();

  /**
   * Returns all {@link BenchlingFolder} entries within a {@link BenchlingProject} parent folder
   *
   * @param projectId
   * @return
   */
  List<BenchlingFolder> findProjectFolderChildren(String projectId);

  /**
   * Returns all child {@link BenchlingFolder} entries within a parent folder.
   *
   * @param folderId
   * @return
   */
  List<BenchlingFolder> findFolderChildren(String folderId);

  /**
   * Returns a single {@link BenchlingFolder}, identified by its ID
   *
   * @param id
   * @return
   */
  Optional<BenchlingFolder> findFolderById(String id);

  /**
   * Creates a new {@link BenchlingFolder} in the provided parent folder.
   *
   * @param name           new folder name
   * @param parentFolderId parent folder ID
   * @return
   */
  BenchlingFolder createFolder(String name, String parentFolderId);

  // Entries

  /**
   * Returns all {@link BenchlingEntry} records, sorted newest to oldest.
   *
   * @return
   */
  List<BenchlingEntry> findAllEntries();

  /**
   * Returns all {@link BenchlingEntry} records associated with a project.
   *
   * @param projectId
   * @return
   */
  List<BenchlingEntry> findProjectEntries(String projectId);

  /**
   * Gets an entity by it's ID.
   *
   * @param entityId
   * @return
   */
  Optional<BenchlingEntry> findEntryById(String entityId);

  /**
   * Creates a new {@link BenchlingEntry} entry.
   *
   * @param entryRequest
   * @return
   */
  BenchlingEntry createEntry(BenchlingEntryRequest entryRequest);

}
