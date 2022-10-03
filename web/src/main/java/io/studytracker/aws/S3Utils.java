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

package io.studytracker.aws;

import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import java.util.Date;
import software.amazon.awssdk.services.s3.model.S3Object;

public class S3Utils {

  public static StorageFile convertS3ObjectToStorageFile(S3Object s3Object) {
    String fileName = s3Object.key().split("/")[s3Object.key().split("/").length - 1];
    StorageFile storageFile = new StorageFile();
    storageFile.setFileId(s3Object.eTag());
    storageFile.setPath(s3Object.key());
    storageFile.setLastModified(new Date(s3Object.lastModified().toEpochMilli()));
    storageFile.setName(fileName);
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

  public static StorageFolder convertS3ObjectsToStorageFolderWithContents(String path,
      Iterable<S3Object> s3Objects) {
    StorageFolder storageFolder = new StorageFolder();
    storageFolder.setPath(path);
    String folderName = path.split("/")[path.split("/").length - 2];
    storageFolder.setName(folderName);
    for (S3Object s3Object: s3Objects) {
      if (s3Object.key().endsWith("/")) {
        storageFolder.addSubfolder(convertS3ObjectToStorageFolder(s3Object));
      } else {
        storageFolder.addFile(convertS3ObjectToStorageFile(s3Object));
      }
    }
    return storageFolder;
  }

}
