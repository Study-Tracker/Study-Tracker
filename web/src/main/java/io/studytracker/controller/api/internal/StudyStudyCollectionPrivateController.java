package io.studytracker.controller.api.internal;

import io.studytracker.controller.api.AbstractStudyController;
import io.studytracker.mapstruct.dto.response.StudyCollectionSummaryDto;
import io.studytracker.mapstruct.mapper.StudyCollectionMapper;
import io.studytracker.model.Study;
import io.studytracker.model.StudyCollection;
import io.studytracker.model.User;
import io.studytracker.service.StudyCollectionService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/study/{studyId}/studycollection")
public class StudyStudyCollectionPrivateController extends AbstractStudyController {

  @Autowired private StudyCollectionService studyCollectionService;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private StudyCollectionMapper mapper;

  @GetMapping("")
  public List<StudyCollectionSummaryDto> getStudyStudyCollections(
      @PathVariable("studyId") String studyId
  ) {
    User authenticatedUser = this.getAuthenticatedUser();
    Study study = this.getStudyFromIdentifier(studyId);
    List<StudyCollection> collections =
        studyCollectionService.findByStudy(study).stream()
            .filter(
                c ->
                    c.isShared()
                        || c.getCreatedBy().getId().equals(authenticatedUser.getId())
                        || authenticatedUser.isAdmin())
            .collect(Collectors.toList());

    return mapper.toSummaryDtoList(collections);
  }
}
