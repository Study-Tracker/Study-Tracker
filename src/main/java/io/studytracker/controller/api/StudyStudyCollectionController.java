package io.studytracker.controller.api;

import io.studytracker.controller.UserAuthenticationUtils;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.StudyCollectionSummaryDto;
import io.studytracker.mapstruct.mapper.StudyCollectionMapper;
import io.studytracker.model.Study;
import io.studytracker.model.StudyCollection;
import io.studytracker.model.User;
import io.studytracker.service.StudyCollectionService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/study/{studyId}/studycollection")
public class StudyStudyCollectionController extends AbstractStudyController {

  @Autowired private StudyCollectionService studyCollectionService;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private StudyCollectionMapper mapper;

  @GetMapping("")
  public List<StudyCollectionSummaryDto> getStudyStudyCollections(
      @PathVariable("studyId") String studyId) {

    String username =
        UserAuthenticationUtils.getUsernameFromAuthentication(
            SecurityContextHolder.getContext().getAuthentication());
    User currentUser =
        this.getUserService().findByUsername(username).orElseThrow(RecordNotFoundException::new);

    Study study = this.getStudyFromIdentifier(studyId);
    List<StudyCollection> collections =
        studyCollectionService.findByStudy(study).stream()
            .filter(
                c ->
                    c.isShared()
                        || c.getCreatedBy().getId().equals(currentUser.getId())
                        || currentUser.isAdmin())
            .collect(Collectors.toList());

    return mapper.toSummaryDtoList(collections);
  }
}
