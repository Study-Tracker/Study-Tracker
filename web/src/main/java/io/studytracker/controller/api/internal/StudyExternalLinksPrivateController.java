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

package io.studytracker.controller.api.internal;

import io.studytracker.controller.api.AbstractExternalLinksController;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.response.ExternalLinkDetailsDto;
import io.studytracker.model.ExternalLink;
import io.studytracker.model.Study;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
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

@RequestMapping("/api/internal/study/{id}/links")
@RestController
public class StudyExternalLinksPrivateController extends AbstractExternalLinksController {

  @GetMapping("")
  public List<ExternalLinkDetailsDto> getStudyExternalLinks(@PathVariable("id") String studyId) {
    Study study = getStudyFromIdentifier(studyId);
    return this.getExternalLinkMapper().toDetailsDtoList(this.getStudyExternalLinkService().findAllStudyExternalLinks(study));
  }

  @PostMapping("")
  public HttpEntity<ExternalLinkDetailsDto> addExternalLink(
      @PathVariable("id") String studyId,
      @RequestBody @Valid ExternalLinkDetailsDto dto
  ) {
    Study study = getStudyFromIdentifier(studyId);
    ExternalLink externalLink =
        this.createNewExternalLink(study, this.getExternalLinkMapper().fromDetailsDto(dto));
    return new ResponseEntity<>(this.getExternalLinkMapper().toDetailsDto(externalLink), HttpStatus.CREATED);
  }

  @PutMapping("/{linkId}")
  public HttpEntity<ExternalLinkDetailsDto> editExternalLink(
      @PathVariable("id") String studyId,
      @PathVariable("linkId") Long linkId,
      @RequestBody @Valid ExternalLinkDetailsDto dto
  ) {
    Study study = getStudyFromIdentifier(studyId);
    Optional<ExternalLink> optional = this.getStudyExternalLinkService().findById(linkId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException("Cannot find external link with ID: " + linkId);
    }
    ExternalLink updated =
        this.updateExistingExternalLink(study, this.getExternalLinkMapper().fromDetailsDto(dto));
    return new ResponseEntity<>(this.getExternalLinkMapper().toDetailsDto(updated), HttpStatus.OK);
  }

  @DeleteMapping("/{linkId}")
  public HttpEntity<?> removeExternalLink(
      @PathVariable("id") String studyId,
      @PathVariable("linkId") Long linkId
  ) {
    this.deleteExternalLink(linkId);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
