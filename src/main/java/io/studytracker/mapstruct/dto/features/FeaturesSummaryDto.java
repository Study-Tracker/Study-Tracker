package io.studytracker.mapstruct.dto.features;

import lombok.Data;

@Data
public class FeaturesSummaryDto {

  private StorageFeaturesDto storage;
  private NotebookFeaturesDto notebook;
  private SearchFeaturesDto search;

}
