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

package io.studytracker.msgraph;

import com.microsoft.graph.models.DriveItem;
import io.studytracker.model.Assay;
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OneDriveUtils {

  private static final String REGEXP = "[\\/:*?”<>|#{}%~&]+";

  public static String getProgramFolderName(Program program) {
    return program.getName().replaceAll(REGEXP, " ");
  }

  public static String getStudyFolderName(Study study) {
    String folderName = study.getCode() + " - " + study.getName().replaceAll(REGEXP, " ");
    if (folderName.length() > 100) {
      return folderName.substring(0, 100);
    }
    return folderName;
  }

  public static String getAssayFolderName(Assay assay) {
    String folderName = assay.getCode() + " - " + assay.getName().replaceAll(REGEXP, " ");
    if (folderName.length() > 100) {
      return folderName.substring(0, 100);
    }
    return folderName;
  }

  public static String getPathFromDriveItem(DriveItem item) {
    String path;
    if (item.parentReference == null) {
      return null;
    } else if (item.parentReference.path == null && item.parentReference.name == null) {
      return "/";
    } else {
      String[] bits = item.parentReference.path.split("root:");
      if (bits.length > 1) {
        path = bits[1];
        if (!path.endsWith("/")) {
          path += "/";
        }
        if (!path.startsWith("/")) {
          path = "/" + path;
        }
      } else {
        path = "/";
      }
    }
    return path + item.name;
  }

  public static StorageFolder convertDriveItemFolder(DriveItem driveItem) {
    StorageFolder folder = new StorageFolder();
    folder.setName(driveItem.name);
    folder.setPath(getPathFromDriveItem(driveItem));
    folder.setUrl(driveItem.webUrl);
    folder.setFolderId(driveItem.id);
    folder.setLastModified(new Date(driveItem.lastModifiedDateTime.toInstant().toEpochMilli()));
    folder.setTotalSize(driveItem.size);
    folder.setDownloadable(false);
    return folder;
  }

  public static StorageFile convertDriveItemFile(DriveItem driveItem) {
    StorageFile file = new StorageFile();
    file.setName(driveItem.name);
    file.setPath(getPathFromDriveItem(driveItem));
    file.setUrl(driveItem.webUrl);
    file.setFileId(driveItem.id);
    file.setLastModified(new Date(driveItem.lastModifiedDateTime.toInstant().toEpochMilli()));
    file.setSize(driveItem.size);
    file.setDownloadable(true);
    return file;
  }

  public static StorageFolder convertDriveItemFolderWithChildren(DriveItem driveItem, List<DriveItem> children) {
    StorageFolder folder = convertDriveItemFolder(driveItem);
    List<StorageFile> files = new ArrayList<>();
    List<StorageFolder> folders = new ArrayList<>();
    for (DriveItem child : children) {
      if (child.folder != null) {
        folders.add(convertDriveItemFolder(child));
      } else {
        files.add(convertDriveItemFile(child));
      }
    }
    folder.setFiles(files);
    folder.setSubFolders(folders);
    return folder;
  }

  public static String joinPaths(String path1, String path2) {
    if (path1 == null || path1.isEmpty()) {
      return path2;
    } else if (path2 == null || path2.isEmpty()) {
      return path1;
    } else {
      if (path1.endsWith("/")) {
        path1 = path1.substring(0, path1.length() - 1);
      }
      if (path2.startsWith("/")) {
        path2 = path2.substring(1);
      }
      return path1 + "/" + path2;
    }
  }

}
