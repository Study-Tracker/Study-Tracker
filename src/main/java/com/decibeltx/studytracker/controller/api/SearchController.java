package com.decibeltx.studytracker.controller.api;

import com.decibeltx.studytracker.search.SearchService;
import com.decibeltx.studytracker.search.StudySearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {

  @Autowired(required = false)
  private SearchService<?, ?> searchService;

  @GetMapping("")
  public HttpEntity<StudySearchHits<?>> search(@RequestParam("keyword") String keyword,
      @RequestParam(value = "field", required = false) String field) {
    if (searchService != null) {
      StudySearchHits<?> searchHits;
      if (StringUtils.hasText(field)) {
        searchHits = searchService.search(keyword, field);
      } else {
        searchHits = searchService.search(keyword);
      }
      return new ResponseEntity<>(searchHits, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
  }

}
