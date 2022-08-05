package io.studytracker.controller.api;

import io.studytracker.exception.DuplicateRecordException;
import io.studytracker.mapstruct.mapper.KeywordCategoryMapper;
import io.studytracker.model.KeywordCategory;
import io.studytracker.repository.KeywordCategoryRepository;
import io.studytracker.service.KeywordCategoryService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractKeywordCategoryController extends AbstractApiController {

  private KeywordCategoryRepository keywordCategoryRepository;
  private KeywordCategoryMapper keywordCategoryMapper;
  private KeywordCategoryService keywordCategoryService;

  /**
   * Creates a new keyword category.
   *
   * @param category the keyword category to create
   * @return the created keyword category
   */
  protected KeywordCategory createNewKeywordCategory(KeywordCategory category) {
    Optional<KeywordCategory> optional = keywordCategoryService.findByName(category.getName());
    if (optional.isPresent()) {
      throw new DuplicateRecordException(
          String.format(
              "Keyword category already exists: %s", category.getName()));
    }
    return keywordCategoryService.create(category);
  }

  /**
   * Updates an existing keyword category
   *
   * @param category the keyword category to update
   * @param id the id of the keyword category to update
   * @return the updated keyword category
   */
  protected KeywordCategory updateExistingKeywordCategory(KeywordCategory category, Long id) {
    Optional<KeywordCategory> optional = keywordCategoryService.findByName(category.getName());
    if (optional.isPresent()) {
      KeywordCategory keywordCategory = optional.get();
      if (!keywordCategory.getId().equals(id)) {
        throw new DuplicateRecordException(
            String.format("Keyword category already exists: %s", keywordCategory.getName()));
      }
    }
    return keywordCategoryService.update(category);
  }

  public KeywordCategoryRepository getKeywordCategoryRepository() {
    return keywordCategoryRepository;
  }

  @Autowired
  public void setKeywordCategoryRepository(
      KeywordCategoryRepository keywordCategoryRepository) {
    this.keywordCategoryRepository = keywordCategoryRepository;
  }

  public KeywordCategoryMapper getKeywordCategoryMapper() {
    return keywordCategoryMapper;
  }

  @Autowired
  public void setKeywordCategoryMapper(
      KeywordCategoryMapper keywordCategoryMapper) {
    this.keywordCategoryMapper = keywordCategoryMapper;
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
