/*
 * Copyright 2022 the original author or authors.
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

package io.studytracker.egnyte;

import io.studytracker.egnyte.entity.EgnyteFile;
import io.studytracker.egnyte.entity.EgnyteFolder;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;

public class EgnyteUtils {

  public static StorageFile convertEgnyteFile(EgnyteFile egnyteFile) {
    StorageFile storageFile = new StorageFile();
    storageFile.setUrl(egnyteFile.getUrl());
    storageFile.setPath(egnyteFile.getPath());
    storageFile.setName(egnyteFile.getName());
    storageFile.setLastModified(egnyteFile.getLastModified());
    storageFile.setSize(egnyteFile.getSize());
    storageFile.setFileId(egnyteFile.getEntryId());
    return storageFile;
  }

  public static StorageFolder convertEgnyteFolder(EgnyteFolder egnyteFolder) {
    StorageFolder storageFolder = new StorageFolder();
    storageFolder.setUrl(egnyteFolder.getUrl());
    storageFolder.setPath(egnyteFolder.getPath());
    storageFolder.setName(egnyteFolder.getName());
    storageFolder.setLastModified(egnyteFolder.getLastModified());
    storageFolder.setFolderId(egnyteFolder.getFolderId());
    return storageFolder;
  }

  public static StorageFolder convertEgnyteFolderWithContents(EgnyteFolder egnyteFolder) {
    StorageFolder storageFolder = convertEgnyteFolder(egnyteFolder);
    egnyteFolder.getFiles()
        .forEach(egnyteFile -> storageFolder.addFile(convertEgnyteFile(egnyteFile)));
    egnyteFolder.getSubFolders()
        .forEach(egnyteSubFolder -> storageFolder.addSubfolder(convertEgnyteFolder(egnyteSubFolder)));
    return storageFolder;
  }

  public static String joinPath(String path, String name) {
    if (!path.endsWith("/")) {
      path += "/";
    }
    return path + name;
  }

}
