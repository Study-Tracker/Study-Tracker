package io.studytracker.controller.api;

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
@RequestMapping("/api/user/{userId}/studycollection")
public class UserStudyCollectionController {

  @Autowired private StudyCollectionService studyCollectionService;

  @Autowired private UserService userService;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private StudyCollectionMapper mapper;

  private User getUserFromIdentifier(String id) {
    Optional<User> optional = userService.findByUsername(id);
    User user;
    if (optional.isPresent()) {
      user = optional.get();
    } else {
      try {
        optional = userService.findById(Long.parseLong(id));
      } catch (Exception e) {
        e.printStackTrace();
      }
      if (optional.isPresent()) {
        user = optional.get();
      } else {
        throw new RecordNotFoundException("User not found: " + id);
      }
    }
    return user;
  }

  @GetMapping("")
  public List<StudyCollectionSummaryDto> getUserStudyCollections(
      @PathVariable("userId") String userId) {
    User user = getUserFromIdentifier(userId);
    List<StudyCollection> collections = studyCollectionService.findByUser(user);
    return mapper.toSummaryDtoList(collections);
  }
}
