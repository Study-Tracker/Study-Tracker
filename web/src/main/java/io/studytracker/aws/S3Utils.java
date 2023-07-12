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

package io.studytracker.aws;

import io.studytracker.model.Assay;
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import io.studytracker.service.NamingService;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import software.amazon.awssdk.services.s3.model.CommonPrefix;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.*;

public class S3Utils {

  public static StorageFile convertS3ObjectToStorageFile(S3Object s3Object) {
    String fileName = s3Object.key().split("/")[s3Object.key().split("/").length - 1];
    StorageFile storageFile = new StorageFile();
    storageFile.setFileId(s3Object.eTag());
    storageFile.setPath(s3Object.key());
    storageFile.setLastModified(new Date(s3Object.lastModified().toEpochMilli()));
    storageFile.setName(fileName);
    storageFile.setDownloadable(true);
    return storageFile;
  }

  public static StorageFolder convertS3ObjectToStorageFolder(S3Object s3Object) {
    String folderName = s3Object.key().split("/")[s3Object.key().split("/").length - 2];
    StorageFolder storageFolder = new StorageFolder();
    storageFolder.setFolderId(s3Object.eTag());
    storageFolder.setPath(s3Object.key());
    storageFolder.setLastModified(new Date(s3Object.lastModified().toEpochMilli()));
    storageFolder.setName(folderName);
    return storageFolder;
  }

  public static StorageFolder convertCommonPrefixToStorageFolder(CommonPrefix commonPrefix) {
    String folderName = commonPrefix.prefix().split("/")[commonPrefix.prefix().split("/").length - 1];
    StorageFolder storageFolder = new StorageFolder();
    storageFolder.setPath(commonPrefix.prefix());
    storageFolder.setName(folderName);
    return storageFolder;
  }

  public static StorageFolder convertS3ObjectsToStorageFolderWithContents(String path,
      Iterable<S3Object> s3Objects, List<CommonPrefix> commonPrefixes) {
    StorageFolder storageFolder = new StorageFolder();
    storageFolder.setPath(path);
    String folderName = path.split("/")[path.split("/").length - 1];
    storageFolder.setName(folderName);
    storageFolder.setParentFolder(deriveParentFolder(path));
    for (S3Object s3Object: s3Objects) {
      if (!s3Object.key().endsWith("/")) {
        storageFolder.addFile(convertS3ObjectToStorageFile(s3Object));
      }
    }
    for (CommonPrefix commonPrefix: commonPrefixes) {
      if (!commonPrefix.prefix().trim().equals("/")) {
        storageFolder.addSubfolder(convertCommonPrefixToStorageFolder(commonPrefix));
      }
    }
    return storageFolder;
  }

  public static StorageFolder deriveParentFolder(String path) {
    if (path.trim().equals("")) return null;
    StorageFolder parentFolder = new StorageFolder();
    List<String> bits = new ArrayList<>(Arrays.asList(path.split("/")));
    String parentPath = "";
    String parentName = "";
    if (bits.size() > 1) {
      bits.remove(bits.size() - 1);
      parentPath = String.join("/", bits) + "/";
      parentName = bits.get(bits.size() - 1);
    }
    parentFolder.setPath(parentPath);
    parentFolder.setName(parentName);
    return parentFolder;
  }

  public static String joinS3Path(String... parts) {
    StringBuilder builder = new StringBuilder();
    for (String part: parts) {
      String p = Objects.requireNonNullElse(part, "");
      if (builder.length() > 0) {
        builder.append("/");
      }
      if (p.startsWith("/")) {
        p = p.substring(1);
      }
      if (p.endsWith("/")) {
        p = p.substring(0, p.length() - 1);
      }
      builder.append(p);
    }
    return builder.toString();
  }

  public static String generateStudyFolderName(Study study) {
    return NamingService.getStudyStorageFolderName(study)
        .replaceAll("_", " ")
        .replaceAll("\\s+", " ")
        .trim();
  }

  public static String generateAssayFolderName(Assay assay) {
    return NamingService.getAssayStorageFolderName(assay)
        .replaceAll("_", " ")
        .replaceAll("\\s+", " ")
        .trim();
  }

  public static String generateProgramFolderName(Program program) {
    return NamingService.getProgramStorageFolderName(program)
        .replaceAll("_", " ")
        .replaceAll("\\s+", " ")
        .trim();
  }


}
