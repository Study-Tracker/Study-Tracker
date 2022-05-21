package io.studytracker.controller.api;

import io.studytracker.controller.UserAuthenticationUtils;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.SummaryStatisticsDto;
import io.studytracker.mapstruct.dto.UserStatisticsDto;
import io.studytracker.model.User;
import io.studytracker.service.StatisticsService;
import io.studytracker.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@Hidden
public class StatisticsController {

  private StatisticsService statisticsService;

  private UserService userService;

  @GetMapping("")
  public SummaryStatisticsDto getCurrentStats(
      @RequestParam(required = false, name = "startDate") Date startDate,
      @RequestParam(required = false, name = "endDate") Date endDate
  ) {
    if (startDate == null && endDate == null) {
      return statisticsService.getCurrent();
    } else if (startDate != null && endDate == null) {
      return statisticsService.getAfterDate(startDate);
    } else if (startDate == null && endDate != null) {
      return statisticsService.getBeforeDate(endDate);
    } else {
      return statisticsService.getBetweenDates(startDate, endDate);
    }
  }

  @GetMapping("/frontpage")
  public SummaryStatisticsDto getFrontPageStats() {
    return statisticsService.mainPageSummary();
  }

  @GetMapping("/user")
  public UserStatisticsDto getActiveUserStatistics() {

    // Get authenticated user
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = UserAuthenticationUtils.getUsernameFromAuthentication(authentication);
    User user = userService.findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    return statisticsService.getUserStatistics(user);

  }

  @Autowired
  public void setStatisticsService(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @Autowired
  public void setUserService(UserService userService) {
    this.userService = userService;
  }
}
