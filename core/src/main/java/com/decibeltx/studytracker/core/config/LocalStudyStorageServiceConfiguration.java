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

package com.decibeltx.studytracker.core.config;

import com.decibeltx.studytracker.core.storage.LocalFileSystemStudyStorageService;
import com.decibeltx.studytracker.core.storage.StudyStorageService;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

@Configuration
@ConditionalOnMissingBean(StudyStorageService.class)
public class LocalStudyStorageServiceConfiguration {

  @Autowired
  private Environment env;

  @Bean
  public StudyStorageService localFileSystemStudyStorageService() {
    Assert.notNull(env.getProperty("storage.local-dir"),
        "Local storage directory is not set. Eg. storage.local-dir=/path/to/storage");
    Path path = Paths.get(env.getRequiredProperty("storage.local-dir"));
    LocalFileSystemStudyStorageService service = new LocalFileSystemStudyStorageService(path);
    if (env.containsProperty("storage.overwrite-existing")) {
      service.setOverwriteExisting(
          env.getRequiredProperty("storage.overwrite-existing", Boolean.class));
    }
    if (env.containsProperty("storage.use-existing")) {
      service.setUseExisting(env.getRequiredProperty("storage.use-existing", Boolean.class));
    }
    if (env.containsProperty("storage.max-folder-read-depth")) {
      service.setMaxDepth(env.getRequiredProperty("storage.max-folder-read-depth", int.class));
    }
    return service;
  }

}
