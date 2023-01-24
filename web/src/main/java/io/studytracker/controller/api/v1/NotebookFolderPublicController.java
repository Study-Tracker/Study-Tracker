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

package io.studytracker.controller.api.v1;

import io.studytracker.controller.api.AbstractApiController;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.NotebookFolderDto;
import io.studytracker.mapstruct.mapper.NotebookFolderMapper;
import io.studytracker.model.ELNFolder;
import io.studytracker.repository.ELNFolderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notebook-folder")
public class NotebookFolderPublicController extends AbstractApiController {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotebookFolderPublicController.class);

  @Autowired
  private NotebookFolderMapper notebookFolderMapper;

  @Autowired
  private ELNFolderRepository elnFolderRepository;

  @GetMapping("")
  public Page<NotebookFolderDto> findAll(Pageable pageable) {
    LOGGER.debug("Fetching all notebook folders");
    Page<ELNFolder> page = elnFolderRepository.findAll(pageable);
    return new PageImpl<>(notebookFolderMapper.toDtoList(page.getContent()), pageable, page.getNumberOfElements());
  }

  @GetMapping("/{id}")
  public NotebookFolderDto findById(@PathVariable Long id) {
    LOGGER.debug("Fetching notebook folder with ID: " + id);
    ELNFolder folder = elnFolderRepository.findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Could not find notebook folder with id: " + id));
    return notebookFolderMapper.toDto(folder);
  }

}
