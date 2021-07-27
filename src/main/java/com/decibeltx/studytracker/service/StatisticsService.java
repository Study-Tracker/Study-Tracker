package com.decibeltx.studytracker.service;

import com.decibeltx.studytracker.mapstruct.dto.StatisticsDto;
import java.util.Calendar;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

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

  public StatisticsDto getCurrent() {
    StatisticsDto statisticsDto = new StatisticsDto();
    statisticsDto.setProgramCount(programService.count());
    statisticsDto.setStudyCount(studyService.count());
    statisticsDto.setAssayCount(assayService.count());
    statisticsDto.setUserCount(userService.count());
    statisticsDto.setActivityCount(activityService.count());
    statisticsDto.setActiveUserCount(userService.countActiveUsers());
    return statisticsDto;
  }

  public StatisticsDto getBeforeDate(Date date) {
    StatisticsDto statisticsDto = new StatisticsDto();
    statisticsDto.setProgramCount(programService.countBeforeDate(date));
    statisticsDto.setStudyCount(studyService.countBeforeDate(date));
    statisticsDto.setAssayCount(assayService.countBeforeDate(date));
    statisticsDto.setUserCount(userService.countBeforeDate(date));
    statisticsDto.setActivityCount(activityService.countBeforeDate(date));
    statisticsDto.setActiveUserCount(userService.countActiveUsers());
    return statisticsDto;
  }

  public StatisticsDto getAfterDate(Date date) {
    StatisticsDto statisticsDto = new StatisticsDto();
    statisticsDto.setProgramCount(programService.countFromDate(date));
    statisticsDto.setStudyCount(studyService.countFromDate(date));
    statisticsDto.setAssayCount(assayService.countFromDate(date));
    statisticsDto.setUserCount(userService.countFromDate(date));
    statisticsDto.setActivityCount(activityService.countFromDate(date));
    statisticsDto.setActiveUserCount(userService.countActiveUsers());
    return statisticsDto;
  }

  public StatisticsDto getBetweenDates(Date startDate, Date endDate) {
    StatisticsDto statisticsDto = new StatisticsDto();
    statisticsDto.setProgramCount(programService.countBetweenDates(startDate, endDate));
    statisticsDto.setStudyCount(studyService.countBetweenDates(startDate, endDate));
    statisticsDto.setAssayCount(assayService.countBetweenDates(startDate, endDate));
    statisticsDto.setUserCount(userService.countBetweenDates(startDate, endDate));
    statisticsDto.setActivityCount(activityService.countBetweenDates(startDate, endDate));
    statisticsDto.setActiveUserCount(userService.countActiveUsers());
    return statisticsDto;
  }

  public StatisticsDto mainPageSummary() {
    StatisticsDto statisticsDto = new StatisticsDto();

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_MONTH, -7);
    Date lastWeek = calendar.getTime();

    calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, -1);
    Date lastMonth = calendar.getTime();

    statisticsDto.setActivityCount(activityService.countFromDate(lastWeek));
    statisticsDto.setActiveUserCount(userService.countActiveUsers());
    statisticsDto.setNewStudyCount(studyService.countFromDate(lastWeek));
    statisticsDto.setCompletedStudyCount(activityService.countCompletedStudiesFromDate(lastMonth));
    statisticsDto.setStudyCount(studyService.count());

    return statisticsDto;
  }

}
