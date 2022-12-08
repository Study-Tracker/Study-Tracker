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

package io.studytracker.config.properties;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "study")
@Validated
@Getter
@Setter
@ToString
public class StudyProperties {

  @NotNull
  @Min(1)
  private Integer studyCodeCounterStart;

  @NotNull
  @Min(3)
  private Integer studyCodeMinDigits;

  @NotNull
  @Min(1)
  private Integer assayCodeCounterStart;

  @NotNull
  @Min(3)
  private Integer assayCodeMinDigits;

  @NotNull
  @Min(1)
  private Integer externalCodeCounterStart;

  @NotNull
  @Min(3)
  private Integer externalCodeMinDigits;

}
