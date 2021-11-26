package com.decibeltx.studytracker.mapstruct.dto;

import lombok.Data;

@Data
public class UserStatisticsDto {

  long activeStudyCount;
  long completeStudyCount;

}
