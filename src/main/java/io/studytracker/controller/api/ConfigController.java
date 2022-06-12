package io.studytracker.controller.api;

import io.studytracker.mapstruct.dto.features.FeaturesSummaryDto;
import io.studytracker.mapstruct.dto.features.NotebookFeaturesDto;
import io.studytracker.mapstruct.dto.features.SearchFeaturesDto;
import io.studytracker.mapstruct.dto.features.StorageFeaturesDto;
import io.swagger.v3.oas.annotations.Hidden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Hidden
@RequestMapping("/api/config")
public class ConfigController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigController.class);

  @Autowired private Environment env;

  @GetMapping("/features")
  public FeaturesSummaryDto getFeatures() {

    LOGGER.info("Getting features");
    FeaturesSummaryDto features = new FeaturesSummaryDto();

    // Storage
    StorageFeaturesDto storageFeaturesDto = new StorageFeaturesDto();
    storageFeaturesDto.setMode(env.getProperty("storage.mode", "local"));
    features.setStorage(storageFeaturesDto);

    // ELN
    NotebookFeaturesDto notebookFeaturesDto = new NotebookFeaturesDto();
    notebookFeaturesDto.setMode(env.getProperty("notebook.mode", "none"));
    features.setNotebook(notebookFeaturesDto);

    // Search
    SearchFeaturesDto searchFeaturesDto = new SearchFeaturesDto();
    searchFeaturesDto.setMode(env.getProperty("search.mode", "none"));
    features.setSearch(searchFeaturesDto);

    return features;

  }


}
