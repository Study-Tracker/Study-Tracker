package com.decibeltx.studytracker.web.controller.api;

import com.decibeltx.studytracker.core.model.Statistics;
import com.decibeltx.studytracker.core.service.StatisticsService;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
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

  @GetMapping("")
  public Statistics getCurrentStats(
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
  public Statistics getFrontPageStats() {
    return statisticsService.mainPageSummary();
  }

}
