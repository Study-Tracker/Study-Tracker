/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.studytracker.mapstruct.dto.response;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class SummaryStatisticsDto {

  /* Timeframe */
  private Date startDate;

  private Date endDate;

  /* Total counts */
  private long studyCount;

  private long programCount;

  private long assayCount;

  private long userCount;

  private long activityCount;

  /* Specific counts */
  private long activeUserCount;

  private long newStudyCount;

  private long completedStudyCount;

  private long newAssayCount;

  private long completedAssayCount;

  private Map<String, Long> programStudyCounts = new HashMap<>();
}
