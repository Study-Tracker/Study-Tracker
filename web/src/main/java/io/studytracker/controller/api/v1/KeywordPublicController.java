package io.studytracker.controller.api.v1;

import io.studytracker.controller.api.AbstractKeywordController;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.KeywordDto;
import io.studytracker.mapstruct.dto.api.KeywordPayloadDto;
import io.studytracker.model.Keyword;
import io.studytracker.model.KeywordCategory;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/keyword")
public class KeywordPublicController extends AbstractKeywordController {

  private static final Logger LOGGER = LoggerFactory.getLogger(KeywordPublicController.class);

  @RequestMapping("")
  public Page<KeywordDto> findKeywords(Pageable pageable) {
    Page<Keyword> page = this.getKeywordService().findAll(pageable);
    return new PageImpl<>(this.getKeywordMapper().toDtoList(page.getContent()), pageable, page.getTotalElements());
  }

  @RequestMapping("/{id}")
  public KeywordDto findKeywordById(@PathVariable("id") Long id) {
    Keyword keyword = this.getKeywordService().findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Cannot find keyword with ID: " + id));
    return this.getKeywordMapper().toDto(keyword);
  }

  @PostMapping("")
  public HttpEntity<KeywordDto> create(@RequestBody @Valid KeywordPayloadDto dto) {

    LOGGER.info("Creating keyword");
    LOGGER.info(dto.toString());
    Keyword keyword = this.getKeywordMapper().fromPayloadDto(dto);
    KeywordCategory category = this.getKeywordCategoryService().findById(dto.getCategoryId())
        .orElseThrow(() -> new RecordNotFoundException("Cannot find keyword category with ID: " + dto.getCategoryId()));
    keyword.setCategory(category);
    keyword = this.createNewKeyword(keyword);
    return new ResponseEntity<>(this.getKeywordMapper().toDto(keyword), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<KeywordDto> update(
      @PathVariable("id") Long id, @RequestBody @Valid KeywordPayloadDto dto) {
    LOGGER.info("Updating keyword");
    LOGGER.info(dto.toString());
    Keyword keyword = this.getKeywordMapper().fromPayloadDto(dto);
    KeywordCategory category = this.getKeywordCategoryService().findById(dto.getCategoryId())
        .orElseThrow(() -> new RecordNotFoundException("Cannot find keyword category with ID: " + dto.getCategoryId()));
    keyword.setCategory(category);
    keyword.setId(id);
    Keyword updated = this.updateExistingKeyword(keyword, id);
    return new ResponseEntity<>(this.getKeywordMapper().toDto(updated), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> delete(@PathVariable("id") Long id) {
    LOGGER.info("Deleting assay type: " + id);
    Keyword keyword =
        this.getKeywordService()
            .findById(id)
            .orElseThrow(() -> new RecordNotFoundException("Cannot find keyword with ID: " + id));
    this.getKeywordService().delete(keyword);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
