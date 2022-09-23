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

import io.studytracker.controller.api.AbstractExternalLinksController;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.ExternalLinkDto;
import io.studytracker.mapstruct.dto.api.ExternalLinkPayloadDto;
import io.studytracker.model.ExternalLink;
import io.studytracker.model.Study;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/external-link")
public class ExternalLinkPublicController extends AbstractExternalLinksController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExternalLinkPublicController.class);

  @GetMapping("")
  public Page<ExternalLinkDto> findAll(
      @RequestParam(required = false) Long studyId, Pageable pageable) {
    LOGGER.debug("Find all external links");
    Page<ExternalLink> page;
    if (studyId != null) {
      page = this.getStudyExternalLinkService().findByStudyId(studyId, pageable);
    } else {
      page = this.getStudyExternalLinkService().findAll(pageable);
    }
    return new PageImpl<>(this.getExternalLinkMapper().toDtoList(page.getContent()),
        pageable, page.getTotalElements());
  }

  @GetMapping("/{id}")
  public ExternalLinkDto findById(@PathVariable Long id) {
    LOGGER.debug("Find external link by id: {}", id);
    ExternalLink externalLink = this.getStudyExternalLinkService().findById(id)
        .orElseThrow(() -> new RecordNotFoundException("External link not found: " + id));
    return this.getExternalLinkMapper().toDto(externalLink);
  }

  @PostMapping("")
  public HttpEntity<ExternalLinkDto> create(@Valid @RequestBody ExternalLinkPayloadDto dto) {
    LOGGER.info("Create external link: {}", dto);
    Study study = this.getStudyService().findById(dto.getStudyId())
        .orElseThrow(() -> new RecordNotFoundException("Study not found: " + dto.getStudyId()));
    ExternalLink externalLink =
        this.createNewExternalLink(study, this.getExternalLinkMapper().fromPayloadDto(dto));
    return new ResponseEntity<>(this.getExternalLinkMapper().toDto(externalLink), HttpStatus.CREATED);

  }

  @PutMapping("/{id}")
  public HttpEntity<ExternalLinkDto> update(
      @PathVariable Long id, @Valid @RequestBody ExternalLinkPayloadDto dto) {
    LOGGER.info("Update external link: {}", dto);
    Study study = this.getStudyService().findById(dto.getStudyId())
        .orElseThrow(() -> new RecordNotFoundException("Study not found: " + dto.getStudyId()));
    ExternalLink externalLink =
        this.updateExistingExternalLink(study, this.getExternalLinkMapper().fromPayloadDto(dto));
    return new ResponseEntity<>(this.getExternalLinkMapper().toDto(externalLink), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> delete(@PathVariable Long id) {
    LOGGER.info("Delete external link: {}", id);
    this.deleteExternalLink(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
