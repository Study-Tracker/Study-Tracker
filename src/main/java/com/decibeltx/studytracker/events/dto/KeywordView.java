package com.decibeltx.studytracker.events.dto;

import com.decibeltx.studytracker.model.Keyword;

public final class KeywordView {

  private String id;

  private String category;

  private String keyword;

  private KeywordView() {
  }

  public static KeywordView from(Keyword keyword) {
    KeywordView view = new KeywordView();
    view.setId(keyword.getId());
    view.setCategory(keyword.getCategory());
    view.setKeyword(keyword.getKeyword());
    return view;
  }

  public String getId() {
    return id;
  }

  private void setId(String id) {
    this.id = id;
  }

  public String getCategory() {
    return category;
  }

  private void setCategory(String category) {
    this.category = category;
  }

  public String getKeyword() {
    return keyword;
  }

  private void setKeyword(String keyword) {
    this.keyword = keyword;
  }
}
