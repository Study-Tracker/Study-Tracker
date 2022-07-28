package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.api.ActivityDto;
import io.studytracker.mapstruct.dto.response.ActivityDetailsDto;
import io.studytracker.mapstruct.dto.response.ActivitySummaryDto;
import io.studytracker.model.Activity;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Mapper(componentModel = "spring")
public interface ActivityMapper {

  ActivityDetailsDto toActivityDetails(Activity activity);

  Set<ActivityDetailsDto> toActivityDetailsSet(Set<Activity> activities);

  List<ActivityDetailsDto> toActivityDetailsList(List<Activity> activities);

  @Mapping(source = "program.id", target = "programId")
  @Mapping(source = "study.id", target = "studyId")
  @Mapping(source = "assay.id", target = "assayId")
  ActivitySummaryDto toActivitySummary(Activity activity);

  List<ActivitySummaryDto> toActivitySummaryList(List<Activity> activities);

  default Page<ActivitySummaryDto> toActivitySummaryPage(Page<Activity> page) {
    List<ActivitySummaryDto> dtos = this.toActivitySummaryList(page.getContent());
    return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
  }

  // API
  List<ActivityDto> toActivityDtoList(List<Activity> activities);
  ActivityDto toActivityDto(Activity activity);

}
