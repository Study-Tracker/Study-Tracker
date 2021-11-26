package com.decibeltx.studytracker.controller.api;

import com.decibeltx.studytracker.controller.UserAuthenticationUtils;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.mapstruct.dto.SummaryStatisticsDto;
import com.decibeltx.studytracker.mapstruct.dto.UserStatisticsDto;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.service.StatisticsService;
import com.decibeltx.studytracker.service.UserService;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/stats")
@ApiIgnore
public class StatisticsController {

  @Autowired
  private StatisticsService statisticsService;

  @Autowired
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

}
