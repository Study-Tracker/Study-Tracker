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

package io.studytracker.controller.api.internal;

import io.studytracker.controller.api.AbstractApiController;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.storage.DataFileStorageService;
import io.studytracker.storage.StorageFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/internal/data-files")
@RestController
public class DataFileStoragePrivateController extends AbstractApiController {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataFileStoragePrivateController.class);

  @Autowired
  private DataFileStorageService dataFileStorageService;

  @Autowired
  private Environment environment;

  @GetMapping("")
  private StorageFolder getDataStorageFolder(
      @RequestParam(name = "path", required = false) String path,
      @RequestParam(name = "folderId", required = false) String folderId
  ) {
    LOGGER.debug("Getting data storage folder");
    String rootPath = environment.getRequiredProperty("egnyte.root-path");
    if (path == null) path = rootPath;
    try {
      StorageFolder folder = dataFileStorageService.findFolderByPath(path);
      return folder;
    } catch (Exception e) {
      throw new RecordNotFoundException("Data storage folder not found: " + path);
    }
  }


}
