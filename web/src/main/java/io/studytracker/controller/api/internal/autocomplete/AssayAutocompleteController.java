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

package io.studytracker.controller.api.internal.autocomplete;

import io.studytracker.mapstruct.dto.response.AssaySlimDto;
import io.studytracker.mapstruct.mapper.AssayMapper;
import io.studytracker.service.AssayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/internal/autocomplete/assay")
public class AssayAutocompleteController {
  
  @Autowired
  private AssayService assayService;
  
  @Autowired
  private AssayMapper assayMapper;
  
  @GetMapping("")
  public List<AssaySlimDto> assaySearch(@RequestParam("q") String keyword) {
    return assayMapper.toAssaySlimList(assayService.search(keyword));
  }
  
}

