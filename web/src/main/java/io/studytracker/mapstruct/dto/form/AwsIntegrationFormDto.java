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

package io.studytracker.mapstruct.dto.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AwsIntegrationFormDto {

  private Long id;
  private @NotEmpty String name;
  private String accountNumber;
  private @NotEmpty String region;
  private String accessKeyId;
  private String secretAccessKey;
  private boolean useIam;
  private boolean active;

}
