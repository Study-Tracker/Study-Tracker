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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    storageFolder.setParentFolder(deriveParentFolder(egnyteFolder));
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

  public static StorageFolder convertEgnyteFolderWithContents(EgnyteFolder egnyteFolder,
      String rootFolderPath) {
    StorageFolder storageFolder = convertEgnyteFolderWithContents(egnyteFolder);
    if (comparePaths(rootFolderPath, storageFolder.getPath())) {
      storageFolder.setParentFolder(null);
    }
    return storageFolder;
  }

  public static StorageFolder deriveParentFolder(EgnyteFolder egnyteFolder) {
    StorageFolder parentFolder = new StorageFolder();
    List<String> bits = new ArrayList<>(Arrays.asList(egnyteFolder.getPath().split("/")));
    if (bits.size() > 1) {
      bits.remove(bits.size() - 1);
      String parentPath = String.join("/", bits);
      String parentName = bits.get(bits.size() - 1);
      parentFolder.setPath(parentPath);
      parentFolder.setName(parentName);
    }
    parentFolder.setFolderId(egnyteFolder.getParentId());
    return parentFolder;
  }

  public static String joinPath(String path, String name) {
    if (!path.endsWith("/")) {
      path += "/";
    }
    return path + name;
  }

  public static boolean comparePaths(String path1, String path2) {
    if (!path1.startsWith("/")) path1 = "/" + path1;
    if (!path1.endsWith("/")) path1 += "/";
    if (!path2.startsWith("/")) path2 = "/" + path2;
    if (!path2.endsWith("/")) path2 += "/";
    String slug1 = String.join("/", (Arrays.asList(path1.toLowerCase().split("/"))));
    String slug2 = String.join("/", (Arrays.asList(path2.toLowerCase().split("/"))));
    System.out.println("Comparing " + slug1 + " to " + slug2);
    return slug1.equals(slug2);
  }

}
