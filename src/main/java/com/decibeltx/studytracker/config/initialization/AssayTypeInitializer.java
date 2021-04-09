package com.decibeltx.studytracker.config.initialization;

import com.decibeltx.studytracker.model.AssayType;
import com.decibeltx.studytracker.service.AssayTypeService;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AssayTypeInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssayTypeInitializer.class);

  @Autowired
  private AssayTypeService assayTypeService;

  @PostConstruct
  public void initializeAdminUser() {
    if (assayTypeService.count() > 0) {
      return;
    }
    LOGGER.info("No assay types defined. Initializing base assay types...");
    AssayType assayType = new AssayType();
    assayType.setName("Generic");
    assayType.setDescription("Generic assay type");
    assayType.setActive(true);
    assayTypeService.create(assayType);
  }

}
