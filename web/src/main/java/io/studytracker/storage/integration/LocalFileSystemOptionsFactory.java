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

package io.studytracker.storage.integration;

import io.studytracker.integration.IntegrationType;
import io.studytracker.model.IntegrationInstance;

public class LocalFileSystemOptionsFactory {

  public static LocalFileSystemOptions create(IntegrationInstance instance) {
    if (!instance.getDefinition().getType().equals(IntegrationType.LOCAL_FILE_SYSTEM)) {
      throw new IllegalArgumentException("Integration type is not " + IntegrationType.LOCAL_FILE_SYSTEM);
    }
    if (instance.getDefinition().getVersion() == 1) {
      return new LocalFileSystemIntegrationV1(instance);
    }
    throw new IllegalArgumentException(
        "Unsupported integration version: " + instance.getDefinition().getVersion());
  }

}
