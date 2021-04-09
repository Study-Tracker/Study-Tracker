/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.decibeltx.studytracker.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebErrorController implements ErrorController {

  @GetMapping("/error")
  public String error(HttpServletRequest request, ModelMap modelMap) {
    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    Integer code = 500;
    if (status != null) {
      code = Integer.valueOf(status.toString());
    }
    modelMap.addAttribute("code", code);
    return "index";
  }

  @Override
  public String getErrorPath() {
    return "/error";
  }
}
