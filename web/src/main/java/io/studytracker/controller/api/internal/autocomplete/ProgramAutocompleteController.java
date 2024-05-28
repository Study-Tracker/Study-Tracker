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

import io.studytracker.mapstruct.dto.response.ProgramSummaryDto;
import io.studytracker.mapstruct.mapper.ProgramMapper;
import io.studytracker.model.Program;
import io.studytracker.service.ProgramService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/autocomplete/program")
public class ProgramAutocompleteController {

  @Autowired
  private ProgramService programService;

  @Autowired
  private ProgramMapper programMapper;

  @GetMapping("")
  public List<ProgramSummaryDto> programSearch(@RequestParam("q") String keyword) {
    List<Program> programs = programService.findAll().stream()
        .filter(program -> program.getName().toLowerCase().contains(keyword.toLowerCase()))
        .filter(Program::isActive)
        .collect(Collectors.toList());
    return programMapper.toProgramSummaryList(programs);
  }

}
