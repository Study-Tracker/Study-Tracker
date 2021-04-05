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

package com.decibeltx.studytracker.storage;

import java.net.URL;
import java.nio.file.Path;

public interface StorageObject {

  /**
   * Returns a URL that allows linking of the object.
   *
   * @return
   */
  String getUrl();

  void setUrl(String url);

  void setUrl(URL url);

  /**
   * Returns the relative path of the object within the storage file system.
   *
   * @return
   */
  String getPath();

  void setPath(String string);

  void setPath(Path path);

  /**
   * Returns the object name.
   *
   * @return
   */
  String getName();

  void setName(String name);

}
