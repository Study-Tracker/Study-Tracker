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

import com.decibeltx.studytracker.core.service.NamingOptions;
import com.decibeltx.studytracker.core.service.NamingService;
import com.decibeltx.studytracker.core.service.impl.NamingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan(basePackages = {"com.decibeltx.studytracker.core.service"})
public class ServiceConfiguration {

  @Autowired
  private Environment env;

  @Bean
  @Primary
  public NamingService namingService() {
    NamingOptions namingOptions = new NamingOptions();
    if (env.containsProperty("study.study-code-counter-start")) {
      namingOptions.setStudyCodeCounterStart(
          env.getRequiredProperty("study.study-code-counter-start", Integer.class));
    }
    if (env.containsProperty("study.study-code-min-digits")) {
      namingOptions.setStudyCodeMinimumDigits(
          env.getRequiredProperty("study.study-code-min-digits", Integer.class));
    }
    if (env.containsProperty("study.external-code-counter-start")) {
      namingOptions.setExternalStudyCodeCounterStart(
          env.getRequiredProperty("study.external-code-counter-start", Integer.class));
    }
    if (env.containsProperty("study.external-code-min-digits")) {
      namingOptions.setExternalStudyCodeMinimumDigits(
          env.getRequiredProperty("study.external-code-min-digits", Integer.class));
    }
    if (env.containsProperty("study.assay-code-counter-start")) {
      namingOptions.setAssayCodeCounterStart(
          env.getRequiredProperty("study.assay-code-counter-start", Integer.class));
    }
    if (env.containsProperty("study.assay-code-min-digits")) {
      namingOptions.setAssayCodeMinimumDigits(
          env.getRequiredProperty("study.assay-code-min-digits", Integer.class));
    }
    return new NamingServiceImpl(namingOptions);
  }

}
