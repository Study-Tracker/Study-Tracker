package io.studytracker.mapstruct.dto.response;

import lombok.Data;

@Data
public class UserStatisticsDto {

  long activeStudyCount;
  long completeStudyCount;
}
