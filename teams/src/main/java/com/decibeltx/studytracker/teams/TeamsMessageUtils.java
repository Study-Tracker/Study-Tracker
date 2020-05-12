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

package com.decibeltx.studytracker.teams;

import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.Study;
import java.net.URL;

public class TeamsMessageUtils {

  public static final String NEW_STUDY_LINK_LABEL = "Teams Post";

  public static String newStudyMessage(Study study, URL fileUrl) {
    Program program = study.getProgram();
    String message = String.format(
        "<h5>New %s Study</h5><h1><a href='/study/%s'>%s: %s</a></h1><p>%s</p><p>created by %s</p>",
        program.getName(), study.getCode(), study.getCode(), study.getName(),
        study.getDescription(), study.getCreatedBy().getDisplayName());
    if (fileUrl != null) {
      message = message + "<p>Summary file: <a href='" + fileUrl + "'>" + fileUrl + "</a></p>";
    }
    return message;
  }

  public static String newStudyMessage(Study study) {
    return newStudyMessage(study, null);
  }

}
