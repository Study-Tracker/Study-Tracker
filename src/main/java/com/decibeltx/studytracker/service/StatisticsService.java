package com.decibeltx.studytracker.service;

import com.decibeltx.studytracker.model.Statistics;
import java.util.Date;

public interface StatisticsService {

  Statistics getCurrent();

  Statistics getBeforeDate(Date date);

  Statistics getAfterDate(Date date);

  Statistics getBetweenDates(Date startDate, Date endDate);

  Statistics mainPageSummary();

}
