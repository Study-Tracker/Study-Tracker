package com.decibeltx.studytracker.service;

import com.decibeltx.studytracker.mapstruct.dto.SummaryStatisticsDto;
import com.decibeltx.studytracker.mapstruct.dto.UserStatisticsDto;
import com.decibeltx.studytracker.model.Program;
import com.decibeltx.studytracker.model.User;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

  public SummaryStatisticsDto getCurrent() {
    SummaryStatisticsDto summaryStatisticsDto = new SummaryStatisticsDto();
    summaryStatisticsDto.setProgramCount(programService.count());
    summaryStatisticsDto.setStudyCount(studyService.count());
    summaryStatisticsDto.setAssayCount(assayService.count());
    summaryStatisticsDto.setUserCount(userService.count());
    summaryStatisticsDto.setActivityCount(activityService.count());
    summaryStatisticsDto.setActiveUserCount(userService.countActiveUsers());
    Map<String, Long> programCounts = new HashMap<>();
    for (Program program: programService.findAll()) {
      System.out.println(program.getName());
      if (program.isActive()) {
        programCounts.put(program.getName(), studyService.countByProgram(program));
      }
    }
    summaryStatisticsDto.setProgramStudyCounts(programCounts);
    return summaryStatisticsDto;
  }

  public SummaryStatisticsDto getBeforeDate(Date date) {
    SummaryStatisticsDto summaryStatisticsDto = new SummaryStatisticsDto();
    summaryStatisticsDto.setProgramCount(programService.countBeforeDate(date));
    summaryStatisticsDto.setStudyCount(studyService.countBeforeDate(date));
    summaryStatisticsDto.setAssayCount(assayService.countBeforeDate(date));
    summaryStatisticsDto.setUserCount(userService.countBeforeDate(date));
    summaryStatisticsDto.setActivityCount(activityService.countBeforeDate(date));
    summaryStatisticsDto.setActiveUserCount(userService.countActiveUsers());
    return summaryStatisticsDto;
  }

  public SummaryStatisticsDto getAfterDate(Date date) {
    SummaryStatisticsDto summaryStatisticsDto = new SummaryStatisticsDto();
    summaryStatisticsDto.setProgramCount(programService.countFromDate(date));
    summaryStatisticsDto.setStudyCount(studyService.countFromDate(date));
    summaryStatisticsDto.setAssayCount(assayService.countFromDate(date));
    summaryStatisticsDto.setUserCount(userService.countFromDate(date));
    summaryStatisticsDto.setActivityCount(activityService.countFromDate(date));
    summaryStatisticsDto.setActiveUserCount(userService.countActiveUsers());
    Map<String, Long> programCounts = new HashMap<>();
    for (Program program: programService.findAll()) {
      if (program.isActive()) {
        programCounts.put(program.getName(), studyService.countByProgramAfterDate(program, date));
      }
    }
    summaryStatisticsDto.setProgramStudyCounts(programCounts);
    return summaryStatisticsDto;
  }

  public SummaryStatisticsDto getBetweenDates(Date startDate, Date endDate) {
    SummaryStatisticsDto summaryStatisticsDto = new SummaryStatisticsDto();
    summaryStatisticsDto.setProgramCount(programService.countBetweenDates(startDate, endDate));
    summaryStatisticsDto.setStudyCount(studyService.countBetweenDates(startDate, endDate));
    summaryStatisticsDto.setAssayCount(assayService.countBetweenDates(startDate, endDate));
    summaryStatisticsDto.setUserCount(userService.countBetweenDates(startDate, endDate));
    summaryStatisticsDto.setActivityCount(activityService.countBetweenDates(startDate, endDate));
    summaryStatisticsDto.setActiveUserCount(userService.countActiveUsers());
    return summaryStatisticsDto;
  }

  public SummaryStatisticsDto mainPageSummary() {
    SummaryStatisticsDto summaryStatisticsDto = new SummaryStatisticsDto();

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_MONTH, -7);
    Date lastWeek = calendar.getTime();

    calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, -1);
    Date lastMonth = calendar.getTime();

    summaryStatisticsDto.setActivityCount(activityService.countFromDate(lastWeek));
    summaryStatisticsDto.setActiveUserCount(userService.countActiveUsers());
    summaryStatisticsDto.setNewStudyCount(studyService.countFromDate(lastWeek));
    summaryStatisticsDto.setCompletedStudyCount(activityService.countCompletedStudiesFromDate(lastMonth));
    summaryStatisticsDto.setStudyCount(studyService.count());

    return summaryStatisticsDto;
  }

  public UserStatisticsDto getUserStatistics(User user) {

    UserStatisticsDto dto = new UserStatisticsDto();
    dto.setActiveStudyCount(studyService.countUserActiveStudies(user));
    dto.setCompleteStudyCount(studyService.countUserCompleteStudies(user));

    return dto;

  }

}
