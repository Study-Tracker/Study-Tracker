package com.decibeltx.studytracker.mapstruct.dto;

import com.decibeltx.studytracker.events.EventType;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class ActivityDetailsDto {

  private Long id;
  private ProgramSummaryDto program;
  private StudySlimDto study;
  private AssaySlimDto assay;
  private EventType eventType;
  private Map<String, Object> data = new HashMap<>();
  private UserSlimDto user;
  private Date date;

}
