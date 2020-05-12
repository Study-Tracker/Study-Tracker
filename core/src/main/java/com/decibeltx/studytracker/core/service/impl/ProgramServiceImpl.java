/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.decibeltx.studytracker.core.service.impl;

import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.repository.ProgramRepository;
import com.decibeltx.studytracker.core.service.ProgramService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProgramServiceImpl implements ProgramService {

  @Autowired
  private ProgramRepository programRepository;

  @Override
  public Optional<Program> findById(String id) {
    return programRepository.findById(id);
  }

  @Override
  public Optional<Program> findByName(String name) {
    return programRepository.findByName(name);
  }

  @Override
  public List<Program> findAll() {
    return programRepository.findAll();
  }

  @Override
  public List<Program> findByCode(String code) {
    return programRepository.findByCode(code);
  }

}
