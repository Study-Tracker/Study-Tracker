package com.decibeltx.studytracker.mapstruct.dto;

import com.decibeltx.studytracker.events.EventType;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class ActivitySummaryDto {

  private Long id;
  private Long programId;
  private Long studyId;
  private Long assayId;
  private EventType eventType;
  private Map<String, Object> data = new HashMap<>();
  private UserSlimDto user;
  private Date date;

}
