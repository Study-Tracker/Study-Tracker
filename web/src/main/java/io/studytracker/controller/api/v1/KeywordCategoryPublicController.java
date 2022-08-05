package io.studytracker.controller.api.v1;

import io.studytracker.controller.api.AbstractKeywordCategoryController;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.KeywordCategoryDto;
import io.studytracker.mapstruct.dto.api.KeywordCategoryPayloadDto;
import io.studytracker.model.KeywordCategory;
import io.studytracker.repository.KeywordCategoryRepository;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
@RequestMapping("/api/v1/keyword-category")
public class KeywordCategoryPublicController extends AbstractKeywordCategoryController {

  private static final Logger LOGGER = LoggerFactory.getLogger(KeywordCategoryPublicController.class);

  @Autowired
  private KeywordCategoryRepository keywordCategoryRepository;


  @GetMapping("")
  public Page<KeywordCategoryDto> findAll(Pageable pageable) {
    LOGGER.debug("Fetching all keyword categories");
    Page<KeywordCategory> page = keywordCategoryRepository.findAll(pageable);
    return new PageImpl<>(this.getKeywordCategoryMapper().toDtoList(page.getContent()), pageable, page.getTotalElements());
  }

  @GetMapping("/{id}")
  public KeywordCategoryDto findById(@PathVariable Long id) {
    LOGGER.debug("Fetching keyword category with id {}", id);
    KeywordCategory keywordCategory = keywordCategoryRepository.findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Cannot find keyword category: " + id));
    return this.getKeywordCategoryMapper().toDto(keywordCategory);
  }

  @PostMapping("")
  public HttpEntity<KeywordCategoryDto> createKeywordCategory(
      @Valid @RequestBody KeywordCategoryPayloadDto dto) {
    LOGGER.info("Creating keyword category: {}", dto);
    KeywordCategory keywordCategory = this.createNewKeywordCategory(this.getKeywordCategoryMapper().fromPayloadDto(dto));
    return new ResponseEntity<>(this.getKeywordCategoryMapper().toDto(keywordCategory), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<KeywordCategoryDto> updateKeywordCategory(
      @PathVariable Long id, @Valid @RequestBody KeywordCategoryPayloadDto dto) {
    LOGGER.info("Updating keyword category with id {}: {}", id, dto);
    KeywordCategory keywordCategory =
        this.updateExistingKeywordCategory(this.getKeywordCategoryMapper().fromPayloadDto(dto), id);
    return new ResponseEntity<>(this.getKeywordCategoryMapper().toDto(keywordCategory), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> delete(@PathVariable("id") Long id) {
    LOGGER.info("Deleting keyword category: {}", id);
    KeywordCategory keywordCategory =
        this.getKeywordCategoryService()
            .findById(id)
            .orElseThrow(() -> new RecordNotFoundException("Cannot find keyword category with ID: " + id));
    this.getKeywordCategoryService().delete(keywordCategory);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
