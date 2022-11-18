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

package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.form.IntegrationInstanceFormDto;
import io.studytracker.mapstruct.dto.response.IntegrationInstanceDetailsDto;
import io.studytracker.model.IntegrationInstance;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IntegrationInstanceMapper {

  IntegrationInstanceDetailsDto toDetails(IntegrationInstance integrationInstance);
  Set<IntegrationInstanceDetailsDto> toDetailsSet(Set<IntegrationInstance> integrationInstances);
  List<IntegrationInstanceDetailsDto> toDetailsList(List<IntegrationInstance> integrationInstances);

  IntegrationInstance fromForm(IntegrationInstanceFormDto form);

}
