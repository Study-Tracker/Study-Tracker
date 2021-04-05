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

package com.decibeltx.studytracker.service.impl;

import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.Collaborator;
import com.decibeltx.studytracker.repository.CollaboratorRepository;
import com.decibeltx.studytracker.service.CollaboratorService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollaboratorServiceImpl implements CollaboratorService {

  @Autowired
  private CollaboratorRepository collaboratorRepository;

  @Override
  public List<Collaborator> findAll() {
    return collaboratorRepository.findAll();
  }

  @Override
  public Optional<Collaborator> findById(String id) {
    return collaboratorRepository.findById(id);
  }

  @Override
  public Optional<Collaborator> findByLabel(String label) {
    return collaboratorRepository.findByLabel(label);
  }

  @Override
  public List<Collaborator> findByOrganizationName(String name) {
    return collaboratorRepository.findByOrganizationName(name);
  }

  @Override
  public List<Collaborator> findByCode(String code) {
    return collaboratorRepository.findByCode(code);
  }

  @Override
  public void create(Collaborator collaborator) {
    collaboratorRepository.insert(collaborator);
  }

  @Override
  public void update(Collaborator collaborator) {
    collaboratorRepository.findById(collaborator.getId()).orElseThrow(RecordNotFoundException::new);
    collaboratorRepository.save(collaborator);
  }

  @Override
  public void delete(Collaborator collaborator) {
    collaboratorRepository.delete(collaborator);
  }

}
