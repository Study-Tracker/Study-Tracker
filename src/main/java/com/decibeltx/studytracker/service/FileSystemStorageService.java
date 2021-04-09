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

package com.decibeltx.studytracker.service;

import com.decibeltx.studytracker.exception.FileStorageException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public final class FileSystemStorageService {

  private final Path tempDir;

  public FileSystemStorageService(Path tempDir) {
    this.tempDir = tempDir;
  }

  /**
   * Stores an uploaded file in the application file store. Returns reference to the file.
   *
   * @param file
   * @return
   */
  public Path store(MultipartFile file) throws FileStorageException {
    String filename = StringUtils.cleanPath(file.getOriginalFilename());
    if (file.isEmpty()) {
      throw new FileStorageException("File is empty.");
    }
    if (filename.contains("..")) {
      throw new FileStorageException("Cannot store file with relative path.");
    }
    try (InputStream inputStream = file.getInputStream()) {
      Files.copy(inputStream, tempDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      e.printStackTrace();
      throw new FileStorageException(e);
    }
    return load(filename);
  }

  /**
   * Returns reference to the requested file by name.
   *
   * @param filename
   * @return
   */
  public Path load(String filename) {
    return tempDir.resolve(filename);
  }

  /**
   * Returns the requested file as a {@link Resource}.
   *
   * @param filename
   * @return
   */
  public Resource loadAsResource(String filename) throws FileStorageException {
    try {
      Path path = load(filename);
      Resource resource = new UrlResource(path.toUri());
      if (resource.exists() && resource.isFile() && resource.isReadable()) {
        return resource;
      } else {
        throw new FileStorageException("File not found: " + filename);
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new FileStorageException(e);
    }
  }
}
