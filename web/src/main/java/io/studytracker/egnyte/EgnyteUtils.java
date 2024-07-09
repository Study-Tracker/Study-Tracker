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

package io.studytracker.egnyte;

import io.studytracker.egnyte.entity.EgnyteFile;
import io.studytracker.egnyte.entity.EgnyteFolder;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StorageUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EgnyteUtils {

  /**
   * Converts a {@link EgnyteFile} object into a generic {@link StorageFile} object.
   *
   * @param egnyteFile the Egnyte file to convert
   * @param rootUrl the root URL of the Egnyte tenant
   * @return the converted file
   */
  public static StorageFile convertEgnyteFile(EgnyteFile egnyteFile, String rootUrl) {
    StorageFile storageFile = new StorageFile();
    if (egnyteFile.getUrl() == null && rootUrl != null) {
      storageFile.setUrl(buildFileUrl(rootUrl, egnyteFile));
    } else {
      storageFile.setUrl(egnyteFile.getUrl());
    }
    storageFile.setPath(egnyteFile.getPath());
    if (egnyteFile.getName() == null) {
      storageFile.setName(new File(egnyteFile.getPath()).getName());
    } else {
      storageFile.setName(egnyteFile.getName());
    }
    storageFile.setLastModified(egnyteFile.getLastModified());
    storageFile.setSize(egnyteFile.getSize());
    storageFile.setFileId(egnyteFile.getEntryId());
    return storageFile;
  }

  /**
   * Converts a {@link EgnyteFolder} object into a generic {@link StorageFolder} object.
   *
   * @param egnyteFolder the Egnyte folder to convert
   * @param rootUrl the root URL of the Egnyte tenant
   * @return the converted folder
   */
  public static StorageFolder convertEgnyteFolder(EgnyteFolder egnyteFolder, String rootUrl) {
    StorageFolder storageFolder = new StorageFolder();
    if (egnyteFolder.getUrl() == null && rootUrl != null) {
      storageFolder.setUrl(buildFolderUrl(rootUrl, egnyteFolder.getFolderId()));
    } else {
      storageFolder.setUrl(egnyteFolder.getUrl());
    }
    storageFolder.setPath(egnyteFolder.getPath());
    if (egnyteFolder.getName() == null) {
      String name = getNameFromPath(egnyteFolder.getPath());
      storageFolder.setName(name);
    } else {
      storageFolder.setName(egnyteFolder.getName());
    }
    storageFolder.setLastModified(egnyteFolder.getLastModified());
    storageFolder.setFolderId(egnyteFolder.getFolderId());
    storageFolder.setParentFolder(deriveParentFolder(egnyteFolder));
    return storageFolder;
  }

  /**
   * Constructs a URL for the given Egnyte path.
   *
   * @param rootUrl the root URL of the Egnyte tenant
   * @param egnyteFile the Egnyte file object
   * @return the URL
   */
  private static String buildFileUrl(String rootUrl, EgnyteFile egnyteFile) {
    String url;
    if (rootUrl.endsWith("/")) {
      url = rootUrl.substring(0, rootUrl.length() - 1);
    } else {
      url = rootUrl;
    }
    return url + "/navigate/file/" + egnyteFile.getGroupId();
  }

  private static String buildFolderUrl(String rootUrl, String folderId) {
    String url = rootUrl;
    if (url.endsWith("/")) {
      url = url.substring(0, url.length() - 1);
    }
    url = url + "/app/index.do#storage/folder/" + folderId;
    return url;
  }

  /**
   * Converts an {@link EgnyteFolder} folder into a generic {@link StorageFolder} object, including
   *  any files or subfolder objects.
   *
   * @param egnyteFolder the Egnyte folder to convert
   * @param rootUrl the root URL of the Egnyte tenant
   * @return the converted folder
   */
  public static StorageFolder convertEgnyteFolderWithContents(EgnyteFolder egnyteFolder, String rootUrl) {
    StorageFolder storageFolder = convertEgnyteFolder(egnyteFolder, rootUrl);
    egnyteFolder.getFiles()
        .forEach(egnyteFile -> storageFolder.addFile(convertEgnyteFile(egnyteFile, rootUrl)));
    egnyteFolder.getSubFolders()
        .forEach(egnyteSubFolder -> storageFolder.addSubfolder(convertEgnyteFolder(egnyteSubFolder, rootUrl)));
    return storageFolder;
  }

  /**
   * Converts an {@link EgnyteFolder} folder into a generic {@link StorageFolder} object, including
   *  any files or subfolder objects.
   *
   * @param egnyteFolder the Egnyte folder to convert
   * @param rootUrl the root URL of the Egnyte tenant
   * @param rootFolderPath the root folder path
   * @return
   */
  public static StorageFolder convertEgnyteFolderWithContents(EgnyteFolder egnyteFolder,
      String rootUrl, String rootFolderPath) {
    StorageFolder storageFolder = convertEgnyteFolderWithContents(egnyteFolder, rootUrl);
    if (StorageUtils.comparePaths(rootFolderPath, storageFolder.getPath())) {
      storageFolder.setParentFolder(null);
    }
    return storageFolder;
  }

  /**
   * Returns a {@link StorageFolder} object representing the parent folder of the given
   *   {@link EgnyteFolder} object.
   *
   * @param egnyteFolder the Egnyte folder to derive the parent folder from
   * @return the parent folder
   */
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

  /**
   * Returns the name of the file or folder from the given path.
   *
   * @param path the path to the file or folder
   * @return the name of the file or folder
   */
  public static String getNameFromPath(String path) {
    if (path.endsWith("/")) {
      path = path.substring(0, path.length() - 1);
    }
    List<String> bits = new ArrayList<>(Arrays.asList(path.split("/")));
    return bits.get(bits.size() - 1);
  }

  /**
   * Return true if the {@code childPath} is a subfolder or the same folder as the {@code parentPath}.
   *
   * @param parentPath parent directory
   * @param childPath child directory
   * @return true if child is a subfolder of parent
   */
  public static boolean directoryIsSubfolderOf(String parentPath, String childPath) {
    if (parentPath == null || childPath == null) {
      return false;
    }
    if (parentPath.equals(childPath)) {
      return true;
    }
    if (parentPath.endsWith("/")) {
      parentPath = parentPath.substring(0, parentPath.length() - 1);
    }
    if (childPath.endsWith("/")) {
      childPath = childPath.substring(0, childPath.length() - 1);
    }
    return childPath.startsWith(parentPath);
  }

  public static String cleanInputObjectName(String input) {
    return input.replaceAll("_", " ")
        .replaceAll("\\s+", " ")
        .trim();
  }

}
