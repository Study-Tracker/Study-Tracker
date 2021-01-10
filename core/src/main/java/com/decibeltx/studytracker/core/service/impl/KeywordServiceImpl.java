package com.decibeltx.studytracker.core.service.impl;

import com.decibeltx.studytracker.core.exception.DuplicateRecordException;
import com.decibeltx.studytracker.core.model.Keyword;
import com.decibeltx.studytracker.core.repository.KeywordRepository;
import com.decibeltx.studytracker.core.service.KeywordService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeywordServiceImpl implements KeywordService {

  private static final Logger LOGGER = LoggerFactory.getLogger(KeywordServiceImpl.class);

  @Autowired
  private KeywordRepository keywordRepository;

  @Override
  public Optional<Keyword> findById(String id) {
    return keywordRepository.findById(id);
  }

  @Override
  public List<Keyword> findAll() {
    return keywordRepository.findAll();
  }

  @Override
  public List<Keyword> findByKeyword(String keyword) {
    return keywordRepository.findByKeyword(keyword);
  }

  @Override
  public List<Keyword> findByCategory(String category) {
    return keywordRepository.findByCategory(category);
  }

  @Override
  public Optional<Keyword> findByKeywordAndCategory(String keyword, String category) {
    return keywordRepository.findByKeywordAndCategory(keyword, category);
  }

  @Override
  public List<Keyword> search(String fragment) {
    return keywordRepository.search(fragment);
  }

  @Override
  public List<Keyword> search(String fragment, String category) {
    return keywordRepository.search(fragment, category);
  }

  @Override
  public Set<String> findAllCategories() {
    return keywordRepository.findAllCategories();
  }

  @Override
  public Keyword create(Keyword keyword) {
    LOGGER.info("Registering new keyword: " + keyword.toString());
    Optional<Keyword> optional
        = this.findByKeywordAndCategory(keyword.getKeyword(), keyword.getCategory());
    if (optional.isPresent()) {
      throw new DuplicateRecordException(
          String.format("Keyword '%s' already exists in category '%s'",
              keyword.getKeyword(), keyword.getCategory()));
    } else {
      return keywordRepository.insert(keyword);
    }
  }

  @Override
  public Keyword update(Keyword keyword) {
    return keywordRepository.save(keyword);
  }

  @Override
  public void delete(Keyword keyword) {
    keywordRepository.delete(keyword);
  }
}
