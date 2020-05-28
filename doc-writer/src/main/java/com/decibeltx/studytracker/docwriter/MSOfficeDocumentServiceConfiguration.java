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

package com.decibeltx.studytracker.docwriter;

import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

@Configuration
public class MSOfficeDocumentServiceConfiguration {

  @Autowired
  private Environment env;

  @Bean
  public DocumentServiceOptions documentServiceOptions() {
    Assert.notNull(env.getProperty("storage.temp-dir"),
        "Temporary file directory must not be null. Eg.: storage.temp-dir=/tmp");
    DocumentServiceOptions options = new DocumentServiceOptions();
    options.setTempDir(Paths.get(env.getRequiredProperty("storage.temp-dir")));
    if (env.containsProperty("documents.slideshow.template")) {
      Resource slideTemplate = new ClassPathResource(
          env.getRequiredProperty("documents.slideshow.template"));
      Assert.isTrue(slideTemplate.exists(), "Slide show template file does not exist: "
          + env.getRequiredProperty("documents.slideshow.template"));
      options.setSlideShowTemplate(slideTemplate);
    }
    return options;
  }

  @Bean
  public StudySlideShowWriter studySlideShowWriter(DocumentServiceOptions options) {
    return new StudySlideShowWriter(options.getTempDir(), options.getSlideShowTemplate());
  }

  @Bean
  public MSOfficeDocumentService msOfficeStudyDocumentService() {
    return new MSOfficeDocumentService();
  }

}
