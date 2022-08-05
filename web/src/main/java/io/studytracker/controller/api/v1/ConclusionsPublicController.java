package io.studytracker.controller.api.v1;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/conclusions")
public class ConclusionsPublicController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConclusionsPublicController.class);

  @GetMapping("")
  public Page<?> findAll(Pageable pageable) {
    return null;
  }

  @GetMapping("/{id}")
  public Object findById(Long id) {
    return null;
  }

  @PostMapping("")
  public HttpEntity<?> create(@Valid @RequestBody Object dto) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @PutMapping("/{id}")
  public HttpEntity<?> update(@PathVariable Long id, @Valid @RequestBody Object dto) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> delete(@PathVariable Long id) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

}
