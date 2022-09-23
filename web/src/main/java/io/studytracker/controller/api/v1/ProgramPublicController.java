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

package io.studytracker.controller.api.v1;

import io.studytracker.controller.api.AbstractProgramController;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.ProgramDto;
import io.studytracker.mapstruct.dto.api.ProgramPayloadDto;
import io.studytracker.model.Program;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/program")
public class ProgramPublicController extends AbstractProgramController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProgramPublicController.class);

  @GetMapping("")
  public Page<ProgramDto> findAll(Pageable pageable) {
    LOGGER.debug("Fetching all programs");
    Page<Program> programs = this.getProgramService().findAll(pageable);
    return new PageImpl<>(getProgramMapper()
        .toProgramDtoList(programs.getContent()), pageable, programs.getTotalElements());
  }

  @GetMapping("/{id}")
  public ProgramDto findById(@PathVariable Long id) {
    LOGGER.debug("Fetching program with id {}", id);
    Program program = this.getProgramService().findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Program not found: " + id));
    return this.getProgramMapper().toProgramDto(program);
  }

  @PostMapping("")
  public HttpEntity<ProgramDto> createProgram(@Valid @RequestBody ProgramPayloadDto dto) {
    LOGGER.info("Creating program {}", dto);
    Program program = this.createNewProgram(this.getProgramMapper().fromProgramPayloadDto(dto));
    ProgramDto created = this.getProgramMapper().toProgramDto(program);
    LOGGER.info("Storage Folder: {} {} {}", program.getStorageFolder().getId(), program.getStorageFolder().getName(), program.getStorageFolder().getPath());
    LOGGER.info("Created program {}", created);
    return new ResponseEntity<>(created, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<ProgramDto> updateProgram(@PathVariable Long id, @Valid @RequestBody ProgramPayloadDto dto) {
    LOGGER.info("Updating program {}", dto);
    Program program = this.updateExistingProgram(this.getProgramMapper().fromProgramPayloadDto(dto));
    return new ResponseEntity<>(this.getProgramMapper().toProgramDto(program), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> deleteProgram(@PathVariable Long id) {
    LOGGER.info("Deleting program with id {}", id);
    this.deleteExistingProgram(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
