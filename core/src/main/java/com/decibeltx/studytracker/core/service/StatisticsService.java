package com.decibeltx.studytracker.core.service;

import com.decibeltx.studytracker.core.model.Statistics;
import java.util.Date;

public interface StatisticsService {

  Statistics getCurrent();

  Statistics getBeforeDate(Date date);

  Statistics getAfterDate(Date date);

  Statistics getBetweenDates(Date startDate, Date endDate);

  Statistics mainPageSummary();

}
