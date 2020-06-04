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

package com.decibeltx.studytracker.web.service;

import com.decibeltx.studytracker.core.exception.FileStorageException;
import java.nio.file.Path;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

  /**
   * Stores an uploaded file in the application file store. Returns reference to the file.
   *
   * @param file
   * @return
   */
  Path store(MultipartFile file) throws FileStorageException;

  /**
   * Returns reference to the requested file by name.
   *
   * @param filename
   * @return
   */
  Path load(String filename) throws FileStorageException;

  /**
   * Returns the requested file as a {@link Resource}.
   *
   * @param filename
   * @return
   */
  Resource loadAsResource(String filename) throws FileStorageException;

}
