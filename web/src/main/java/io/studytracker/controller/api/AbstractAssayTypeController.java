/*
 * Copyright 2022 the original author or authors.
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
