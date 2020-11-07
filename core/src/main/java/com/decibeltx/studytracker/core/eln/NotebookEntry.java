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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class NotebookEntry {

  private String url;

  private String name;

  private String referenceId;

  private Map<String, Object> attributes = new HashMap<>();

  public void setUrl(URL url) {
    this.url = url.toString();
  }

  public void setUrl(String string) {
    this.url = url;
  }

}
