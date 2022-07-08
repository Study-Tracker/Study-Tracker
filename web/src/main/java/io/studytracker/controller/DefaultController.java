package io.studytracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {

  @GetMapping({
      "/",
      "/login",
      "/study/**",
      "/studies/**",
      "/assay/**",
      "/assays/**",
      "/programs/**",
      "/program/**",
      "/user/**",
      "/users/**",
      "/collection/**",
      "/collections/**",
      "/admin/**",
      "/search/**"
  })
  public String home() {
    return "index";
  }

}
