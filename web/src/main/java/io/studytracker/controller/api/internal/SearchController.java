package io.studytracker.controller.api.internal;

import io.studytracker.search.GenericSearchHits;
import io.studytracker.search.SearchService;
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
@RequestMapping("/api/internal/search")
public class SearchController {

  @Autowired(required = false)
  private SearchService searchService;

  @GetMapping("")
  public HttpEntity<GenericSearchHits<?>> search(
      @RequestParam("keyword") String keyword,
      @RequestParam(value = "field", required = false) String field) {
    if (searchService != null) {
      GenericSearchHits<?> genericSearchHits;
      if (StringUtils.hasText(field)) {
        genericSearchHits = searchService.search(keyword, field);
      } else {
        genericSearchHits = searchService.search(keyword);
      }
      return new ResponseEntity<>(genericSearchHits, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
  }
}
