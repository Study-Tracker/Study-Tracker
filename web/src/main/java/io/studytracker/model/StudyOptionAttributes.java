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

package io.studytracker.model;

public class StudyOptionAttributes {

  public static final String USE_NOTEBOOK = "_options.useNotebook";
  public static final String NOTEBOOK_TEMPLATE_ID = "_options.notebookTemplateId";
  public static final String USE_GIT = "_options.useGit";
  public static final String USE_STORAGE = "_options.useStorage";

  public static void setStudyOptionAttributes(Study study, StudyOptions options) {
    study.setAttribute(USE_NOTEBOOK, Boolean.toString(options.isUseNotebook()));
    study.setAttribute(NOTEBOOK_TEMPLATE_ID, options.getNotebookTemplateId());
    study.setAttribute(USE_GIT, Boolean.toString(options.isUseGit()));
    study.setAttribute(USE_STORAGE, Boolean.toString(options.isUseStorage()));
  }

}
