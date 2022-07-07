package io.studytracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {

  @GetMapping(value = {"/", "/login"})
  public String home() {
    return "index";
  }

}
