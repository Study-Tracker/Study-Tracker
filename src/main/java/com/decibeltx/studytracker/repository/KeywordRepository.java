package com.decibeltx.studytracker.repository;

import com.decibeltx.studytracker.model.Keyword;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

  List<Keyword> findByKeyword(String keyword);

  List<Keyword> findByCategory(String category);

  Optional<Keyword> findByKeywordAndCategory(String keyword, String category);

  @Query("select distinct(k.category) from Keyword k")
  Set<String> findAllCategories();

  @Query("select k from Keyword k where lower(k.keyword) like lower(concat('%', ?1, '%'))")
  List<Keyword> search(String fragment);

  @Query("select k from Keyword k where lower(k.keyword) like lower(concat('%', ?1, '%')) and k.category = ?2")
  List<Keyword> search(String fragment, String category);

}
