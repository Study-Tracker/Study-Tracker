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

package com.decibeltx.studytracker.core.notebook;

import java.net.URL;

public interface NotebookEntry {

  /**
   * Returns a URL that allows linking of the entry.
   *
   * @return
   */
  String getUrl();

  void setUrl(String url);

  void setUrl(URL url);

  /**
   * Returns the user-facing display label for the notebook entry.
   *
   * @return
   */
  String getLabel();

  void setLabel(String label);

}
