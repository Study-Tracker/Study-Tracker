package io.studytracker.controller.api.internal;

import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.response.StudyCollectionSummaryDto;
import io.studytracker.mapstruct.mapper.StudyCollectionMapper;
import io.studytracker.model.StudyCollection;
import io.studytracker.model.User;
import io.studytracker.service.StudyCollectionService;
import io.studytracker.service.UserService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/user/{userId}/studycollection")
public class UserStudyCollectionPrivateController {

  @Autowired private StudyCollectionService studyCollectionService;

  @Autowired private UserService userService;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private StudyCollectionMapper mapper;

  private User getUserFromIdentifier(Long id) {
    Optional<User> optional = userService.findById(id);
    if (optional.isEmpty()) {
      throw new RecordNotFoundException("User not found: " + id);
    }
    return optional.get();
  }

  @GetMapping("")
  public List<StudyCollectionSummaryDto> getUserStudyCollections(
      @PathVariable("userId") Long userId) {
    User user = userService.findById(userId)
        .orElseThrow(() -> new RecordNotFoundException("User not found: " + userId));
    List<StudyCollection> collections = studyCollectionService.findByUser(user);
    return mapper.toSummaryDtoList(collections);
  }
}
