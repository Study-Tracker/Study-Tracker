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

package io.studytracker.benchling.api;

import io.studytracker.benchling.BenchlingClientFactory;
import io.studytracker.benchling.api.entities.BenchlingEntry;
import io.studytracker.benchling.api.entities.BenchlingFolder;
import io.studytracker.eln.NotebookEntry;
import io.studytracker.eln.NotebookFolder;
import io.studytracker.model.BenchlingIntegration;
import io.studytracker.repository.BenchlingIntegrationRepository;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractBenchlingApiService {

  @Autowired
  private BenchlingClientFactory clientFactory;
  
  @Autowired
  private BenchlingIntegrationRepository integrationRepository;

  /**
   * Converts a {@link BenchlingEntry} object into a {@link NotebookEntry} object.
   *
   * @param benchlingEntry
   * @return
   */
  protected NotebookEntry convertBenchlingEntry(BenchlingEntry benchlingEntry) {
    NotebookEntry notebookEntry = new NotebookEntry();
    notebookEntry.setName(benchlingEntry.getName());
    notebookEntry.setReferenceId(benchlingEntry.getId());
    notebookEntry.setUrl(benchlingEntry.getWebURL());
    notebookEntry.getAttributes().put("folderId", benchlingEntry.getFolderId());
    return notebookEntry;
  }
  
  /**
   * Converts a {@link BenchlingFolder} object into a {@link NotebookFolder} object.
   *
   * @param benchlingFolder
   * @return
   */
  protected NotebookFolder convertBenchlingFolder(BenchlingFolder benchlingFolder) {
    NotebookFolder notebookFolder = new NotebookFolder();
    notebookFolder.setName(benchlingFolder.getName());
    notebookFolder.setUrl(benchlingFolder.getUrl());
    notebookFolder.setReferenceId(benchlingFolder.getId());
    notebookFolder.getAttributes().put("projectId", benchlingFolder.getProjectId());
    return notebookFolder;
  }
  
  public BenchlingElnRestClient getClient() {
    BenchlingIntegration integration = integrationRepository.findAll().stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No Benchling integration found"));
    return clientFactory.createBenchlingClient(integration);
  }
  
}
