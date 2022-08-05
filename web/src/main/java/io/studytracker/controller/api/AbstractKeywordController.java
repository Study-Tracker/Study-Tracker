package io.studytracker.controller.api;

import io.studytracker.exception.DuplicateRecordException;
import io.studytracker.mapstruct.mapper.KeywordMapper;
import io.studytracker.model.Keyword;
import io.studytracker.model.KeywordCategory;
import io.studytracker.service.KeywordCategoryService;
import io.studytracker.service.KeywordService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractKeywordController extends AbstractApiController{

  private KeywordService keywordService;

  private KeywordCategoryService keywordCategoryService;

  private KeywordMapper keywordMapper;

  /**
   * Creates a new keyword. If the provided category does not exist, a new category record is created for it.
   *
   * @param keyword the keyword to create
   * @return the created keyword
   */
  protected Keyword createNewKeyword(Keyword keyword) {

    // If the category does not exist, create it
    if (keyword.getCategory().getId() == null) {
      KeywordCategory created = keywordCategoryService.create(keyword.getCategory());
      keyword.setCategory(created);
    }

    // Check to see if the keyword already exists
    Optional<Keyword> optional =
        keywordService.findByKeywordAndCategory(keyword.getKeyword(), keyword.getCategory().getName());
    if (optional.isPresent()) {
      throw new DuplicateRecordException(
          String.format(
              "Keyword already exists: %s %s", keyword.getCategory(), keyword.getKeyword()));
    }
    return keywordService.create(keyword);
  }

  protected Keyword updateExistingKeyword(Keyword updated, Long id) {
    Optional<Keyword> optional =
        keywordService.findByKeywordAndCategory(updated.getKeyword(), updated.getCategory().getName());
    if (optional.isPresent()) {
      Keyword keyword = optional.get();
      if (!keyword.getId().equals(id)) {
        throw new DuplicateRecordException(
            String.format(
                "Keyword already exists: %s %s", updated.getCategory(), updated.getKeyword()));
      }
    }
    return keywordService.update(updated);
  }

  public KeywordService getKeywordService() {
    return keywordService;
  }

  @Autowired
  public void setKeywordService(KeywordService keywordService) {
    this.keywordService = keywordService;
  }

  public KeywordMapper getKeywordMapper() {
    return keywordMapper;
  }

  @Autowired
  public void setKeywordMapper(KeywordMapper keywordMapper) {
    this.keywordMapper = keywordMapper;
  }

  public KeywordCategoryService getKeywordCategoryService() {
    return keywordCategoryService;
  }

  @Autowired
  public void setKeywordCategoryService(
      KeywordCategoryService keywordCategoryService) {
    this.keywordCategoryService = keywordCategoryService;
  }
}
