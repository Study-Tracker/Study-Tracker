package com.decibeltx.studytracker.mapstruct.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class SummaryStatisticsDto {

  /* Timeframe */
  private Date startDate;

  private Date endDate;

  /* Total counts */
  private long studyCount;

  private long programCount;

  private long assayCount;

  private long userCount;

  private long activityCount;

  /* Specific counts */
  private long activeUserCount;

  private long newStudyCount;

  private long completedStudyCount;

  private long newAssayCount;

  private long completedAssayCount;

  private Map<String, Long> programStudyCounts = new HashMap<>();

}
