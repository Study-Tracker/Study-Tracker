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

package io.studytracker.controller.api.internal;

import io.studytracker.controller.api.AbstractStudyController;
import io.studytracker.events.util.StudyActivityUtils;
import io.studytracker.mapstruct.dto.form.KeywordFormDto;
import io.studytracker.mapstruct.dto.response.KeywordDetailsDto;
import io.studytracker.mapstruct.mapper.KeywordMapper;
import io.studytracker.model.Activity;
import io.studytracker.model.Keyword;
import io.studytracker.model.Study;
import io.studytracker.service.StudyKeywordsService;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/internal/study/{studyId}/keywords")
@RestController
public class StudyKeywordsController extends AbstractStudyController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyKeywordsController.class);

  @Autowired private StudyKeywordsService studyKeywordsService;

  @Autowired private KeywordMapper keywordMapper;

  @GetMapping("")
  public List<KeywordDetailsDto> getStudyKeywords(@PathVariable("studyId") String studyId) {
    Study study = getStudyFromIdentifier(studyId);
    return keywordMapper.toDetailsDtoList(studyKeywordsService.findStudyKeywords(study));
  }

  @PutMapping("")
  public HttpEntity<?> updateStudyKeywords(
      @PathVariable("studyId") String studyId,
      @RequestBody @Valid Set<KeywordFormDto> dtos
  ) {

    LOGGER.info(String.format("Updating keywords for study %s: %s", studyId, dtos.toString()));
    Set<Keyword> keywords = keywordMapper.fromFormDtoSet(dtos);
    Study study = getStudyFromIdentifier(studyId);
    studyKeywordsService.updateStudyKeywords(study, keywords);

    // Publish events
    Activity activity = StudyActivityUtils.fromUpdatedStudyKeywords(study, this.getAuthenticatedUser());
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(HttpStatus.OK);
  }

}
