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

package com.decibeltx.studytracker.core.eln;


import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.Study;

public class NotebookUtils {

  public static String getProgramFolderName(Program program) {
    return program.getName();
  }

  public static String getStudyFolderName(Study study) {
    return study.getName() + " (" + study.getCode() + ")";
  }

  public static String getAssayFolderName(Assay assay) {
    return assay.getName() + " (" + assay.getCode() + ")";
  }

}
