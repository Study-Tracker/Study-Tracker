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

package io.studytracker.aws.integration;

import io.studytracker.integration.IntegrationType;
import io.studytracker.model.IntegrationInstance;

public class S3IntegrationOptionsFactory {

  public static S3IntegrationOptions create(IntegrationInstance instance) {
    if (!instance.getDefinition().getType().equals(IntegrationType.AWS_S3)) {
      throw new IllegalArgumentException("Integration type must be AWS_S3");
    }

    if (instance.getDefinition().getVersion().equals(1)) {
      return new S3IntegrationV1(instance);
    } else {
      throw new IllegalArgumentException("Unsupported integration version: "
          + instance.getDefinition().getVersion());
    }
  }

}
