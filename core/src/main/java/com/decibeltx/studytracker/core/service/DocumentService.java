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

package com.decibeltx.studytracker.core.service;

import com.decibeltx.studytracker.core.model.Study;
import java.io.File;

public interface DocumentService {

  String SUMMARY_DOCUMENT_LINK_LABEL = "Summary Document (PPTX)";

  /**
   * Generates a summary slide show (eg Powerpoint) from the given {@link Study} object.
   *
   * @param study
   * @return
   */
  File createStudySummarySlideShow(Study study);

}
