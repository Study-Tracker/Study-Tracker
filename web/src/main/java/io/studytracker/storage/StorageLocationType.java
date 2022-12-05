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

package io.studytracker.storage;

import io.studytracker.integration.IntegrationType;

public enum StorageLocationType {
  LOCAL_FILE_SYSTEM,
  EGNYTE_API,
  AWS_S3
  ;

  public static StorageLocationType fromIntegrationType(IntegrationType integrationType) {
    switch (integrationType) {
      case LOCAL_FILE_SYSTEM:
        return LOCAL_FILE_SYSTEM;
      case EGNYTE:
        return EGNYTE_API;
      case AWS_S3:
        return AWS_S3;
      default:
        throw new IllegalArgumentException("Unsupported file storage location integration type: " + integrationType);
    }
  }

}
