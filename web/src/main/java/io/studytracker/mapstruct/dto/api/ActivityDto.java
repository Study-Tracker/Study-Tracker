package io.studytracker.mapstruct.dto.api;

import io.studytracker.events.EventType;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class ActivityDto {

  private Long id;
  private Long userId;
  private Long programId;
  private Long studyId;
  private Long assayId;
  private EventType eventType;
  private Map<String, Object> data = new HashMap<>();
  private Date date;
}
