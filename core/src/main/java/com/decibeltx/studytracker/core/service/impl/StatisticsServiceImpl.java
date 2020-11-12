package com.decibeltx.studytracker.core.service.impl;

import com.decibeltx.studytracker.core.model.Statistics;
import com.decibeltx.studytracker.core.service.ActivityService;
import com.decibeltx.studytracker.core.service.AssayService;
import com.decibeltx.studytracker.core.service.ProgramService;
import com.decibeltx.studytracker.core.service.StatisticsService;
import com.decibeltx.studytracker.core.service.StudyService;
import com.decibeltx.studytracker.core.service.UserService;
import java.util.Calendar;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsServiceImpl implements StatisticsService {

  @Autowired
  private StudyService studyService;

  @Autowired
  private AssayService assayService;

  @Autowired
  private ProgramService programService;

  @Autowired
  private ActivityService activityService;

  @Autowired
  private UserService userService;

  @Override
  public Statistics getCurrent() {
    Statistics statistics = new Statistics();
    statistics.setProgramCount(programService.count());
    statistics.setStudyCount(studyService.count());
    statistics.setAssayCount(assayService.count());
    statistics.setUserCount(userService.count());
    statistics.setActivityCount(activityService.count());
    statistics.setActiveUserCount(userService.countActiveUsers());
    return statistics;
  }

  @Override
  public Statistics getBeforeDate(Date date) {
    Statistics statistics = new Statistics();
    statistics.setProgramCount(programService.countBeforeDate(date));
    statistics.setStudyCount(studyService.countBeforeDate(date));
    statistics.setAssayCount(assayService.countBeforeDate(date));
    statistics.setUserCount(userService.countBeforeDate(date));
    statistics.setActivityCount(activityService.countBeforeDate(date));
    statistics.setActiveUserCount(userService.countActiveUsers());
    return statistics;
  }

  @Override
  public Statistics getAfterDate(Date date) {
    Statistics statistics = new Statistics();
    statistics.setProgramCount(programService.countFromDate(date));
    statistics.setStudyCount(studyService.countFromDate(date));
    statistics.setAssayCount(assayService.countFromDate(date));
    statistics.setUserCount(userService.countFromDate(date));
    statistics.setActivityCount(activityService.countFromDate(date));
    statistics.setActiveUserCount(userService.countActiveUsers());
    return statistics;
  }

  @Override
  public Statistics getBetweenDates(Date startDate, Date endDate) {
    Statistics statistics = new Statistics();
    statistics.setProgramCount(programService.countBetweenDates(startDate, endDate));
    statistics.setStudyCount(studyService.countBetweenDates(startDate, endDate));
    statistics.setAssayCount(assayService.countBetweenDates(startDate, endDate));
    statistics.setUserCount(userService.countBetweenDates(startDate, endDate));
    statistics.setActivityCount(activityService.countBetweenDates(startDate, endDate));
    statistics.setActiveUserCount(userService.countActiveUsers());
    return statistics;
  }

  @Override
  public Statistics mainPageSummary() {
    Statistics statistics = new Statistics();

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_MONTH, -7);
    Date lastWeek = calendar.getTime();

    calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, -1);
    Date lastMonth = calendar.getTime();

    statistics.setActivityCount(activityService.countFromDate(lastWeek));
    statistics.setActiveUserCount(userService.countActiveUsers());
    statistics.setNewStudyCount(studyService.countFromDate(lastWeek));
    statistics.setCompletedStudyCount(activityService.countCompletedStudiesFromDate(lastMonth));
    statistics.setStudyCount(studyService.count());

    return statistics;
  }
}
