package com.decibeltx.studytracker.controller.api;

import com.decibeltx.studytracker.mapstruct.dto.StatisticsDto;
import com.decibeltx.studytracker.service.StatisticsService;
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
  public StatisticsDto getCurrentStats(
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
  public StatisticsDto getFrontPageStats() {
    return statisticsService.mainPageSummary();
  }

}
