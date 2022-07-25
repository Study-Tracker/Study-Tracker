package io.studytracker.controller.api;

import io.studytracker.mapstruct.dto.response.SummaryStatisticsDto;
import io.studytracker.mapstruct.dto.response.UserStatisticsDto;
import io.studytracker.service.StatisticsService;
import io.swagger.v3.oas.annotations.Hidden;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@Hidden
public class StatisticsController extends AbstractAPIController {

  private StatisticsService statisticsService;

  @GetMapping("")
  public SummaryStatisticsDto getCurrentStats(
      @RequestParam(required = false, name = "startDate") Date startDate,
      @RequestParam(required = false, name = "endDate") Date endDate) {
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
    return statisticsService.getUserStatistics(this.getAuthenticatedUser());
  }

  @Autowired
  public void setStatisticsService(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

}
