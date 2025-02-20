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

package io.studytracker.controller.api.internal;

import io.studytracker.eln.NotebookFolder;
import io.studytracker.eln.NotebookFolderService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/eln")
public class NotebookPrivateController {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotebookPrivateController.class);

  @Autowired
  private NotebookFolderService notebookFolderService;

  @GetMapping("/project-folders")
  public List<NotebookFolder> findNotebookProjects() {
    LOGGER.debug("Find notebook project folders");
    return notebookFolderService.listProjectFolders();
  }
  
  @GetMapping("/folder/{folderId}")
  public NotebookFolder findFolder(@PathVariable("folderId") String folderId,
          @RequestParam(value = "loadContents", required = false, defaultValue = "false") boolean loadContents) {
    LOGGER.debug("Find notebook folder: {}", folderId);
    NotebookFolder notebookFolder = notebookFolderService.findFolderById(folderId);
    if (loadContents) {
      notebookFolder = notebookFolderService.loadFolderContents(notebookFolder);
      LOGGER.debug("Loaded {} subfolders and {} entries", notebookFolder.getSubFolders().size(),
              notebookFolder.getEntries().size());
    }
    return notebookFolder;
  }

}
