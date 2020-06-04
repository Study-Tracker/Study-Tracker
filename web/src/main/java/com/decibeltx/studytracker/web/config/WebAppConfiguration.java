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

package com.decibeltx.studytracker.web.config;

import com.decibeltx.studytracker.core.notebook.NotebookEntry;
import com.decibeltx.studytracker.core.notebook.SimpleNotebookEntry;
import com.decibeltx.studytracker.core.storage.BasicStorageFile;
import com.decibeltx.studytracker.core.storage.BasicStorageFolder;
import com.decibeltx.studytracker.core.storage.StorageFile;
import com.decibeltx.studytracker.core.storage.StorageFolder;
import com.decibeltx.studytracker.egnyte.entity.EgnyteFile;
import com.decibeltx.studytracker.egnyte.entity.EgnyteFolder;
import com.decibeltx.studytracker.idbs.eln.entities.IdbsNotebookEntry;
import com.decibeltx.studytracker.web.service.FileSystemStorageService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(basePackages = {"com.decibeltx.studytracker.web.controller"})
@PropertySource("classpath:web.properties")
public class WebAppConfiguration {

  @Autowired
  private Environment env;

  @Bean
  public WebMvcConfigurer webMvcConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**");
      }
    };
  }

  @Bean
  public FileSystemStorageService fileSystemStorageService() {
    return new FileSystemStorageService(Paths.get(env.getRequiredProperty("storage.temp-dir")));
  }

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new SimpleModule()
        .addAbstractTypeMapping(StorageFile.class, BasicStorageFile.class));
    objectMapper.registerModule(
        new SimpleModule().addAbstractTypeMapping(StorageFile.class, EgnyteFile.class)
    );
    objectMapper.registerModule(new SimpleModule()
        .addAbstractTypeMapping(StorageFolder.class, BasicStorageFolder.class));
    objectMapper.registerModule(
        new SimpleModule().addAbstractTypeMapping(StorageFolder.class, EgnyteFolder.class)
    );
    objectMapper.registerModule(
        new SimpleModule().addAbstractTypeMapping(NotebookEntry.class, SimpleNotebookEntry.class));
    objectMapper.registerModule(
        new SimpleModule().addAbstractTypeMapping(NotebookEntry.class, IdbsNotebookEntry.class));
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper;
  }

}
