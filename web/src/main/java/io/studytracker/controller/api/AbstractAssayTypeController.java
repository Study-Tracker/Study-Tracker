package io.studytracker.controller.api;

import io.studytracker.mapstruct.mapper.AssayTypeMapper;
import io.studytracker.model.AssayType;
import io.studytracker.service.AssayTypeService;
import io.studytracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractAssayTypeController extends AbstractApiController{

  private AssayTypeService assayTypeService;
  private UserService userService;
  private AssayTypeMapper assayTypeMapper;

  protected AssayType createNewAssayType(AssayType assayType) {
    assayType.setActive(true);
    return assayTypeService.create(assayType);
  }

  protected AssayType updateExistingAssayType(AssayType assayType) {
    return assayTypeService.update(assayType);
  }

  protected void deleteAssayType(AssayType assayType) {
    this.getAssayTypeService().delete(assayType);
  }

  public AssayTypeService getAssayTypeService() {
    return assayTypeService;
  }

  @Autowired
  public void setAssayTypeService(AssayTypeService assayTypeService) {
    this.assayTypeService = assayTypeService;
  }

  public UserService getUserService() {
    return userService;
  }

  @Autowired
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  public AssayTypeMapper getAssayTypeMapper() {
    return assayTypeMapper;
  }

  @Autowired
  public void setAssayTypeMapper(AssayTypeMapper assayTypeMapper) {
    this.assayTypeMapper = assayTypeMapper;
  }
}
