/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.studytracker.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.Table;

@Entity
@Table(
    name = "keywords",
    indexes = {@Index(name = "idx_keyword", columnList = "category_id, keyword")})
@NamedEntityGraphs({
  @NamedEntityGraph(
      name = "keyword-details",
      attributeNodes = {@NamedAttributeNode("category")})
})
public class Keyword {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private KeywordCategory category;

  @Column(name = "keyword", nullable = false)
  private String keyword;

  public Keyword() {}

  public Keyword(KeywordCategory category, String keyword) {
    this.category = category;
    this.keyword = keyword;
  }

  public Keyword(Long id, KeywordCategory category, String keyword) {
    this.id = id;
    this.category = category;
    this.keyword = keyword;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  public KeywordCategory getCategory() {
    return category;
  }

  public void setCategory(KeywordCategory category) {
    this.category = category;
  }
}
