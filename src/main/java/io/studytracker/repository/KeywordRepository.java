package io.studytracker.repository;

import io.studytracker.model.Keyword;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

  List<Keyword> findByKeyword(String keyword);

  @Query("select k from Keyword k where k.category.name = ?1")
  List<Keyword> findByCategory(String category);

  @Query("select k from Keyword k where k.keyword = ?1 and k.category.name = ?2")
  Optional<Keyword> findByKeywordAndCategory(String keyword, String category);

//  @Query("select distinct(k.category) from Keyword k")
//  Set<String> findAllCategories();

  @Query("select k from Keyword k where lower(k.keyword) like lower(concat('%', ?1, '%'))")
  List<Keyword> search(String fragment, Pageable pageable);

  @Query(
      "select k from Keyword k where lower(k.keyword) like lower(concat('%', ?1, '%')) and k.category.name = ?2")
  List<Keyword> search(String fragment, String category, Pageable pageable);

  @Query(
      value =
          "select * from keywords k join keyword_categories c on k.category_id = c.id where regexp_replace(k.keyword, '\\s\\(.+?\\)', '') ilike concat('%', ?1, '%') and c.name = ?2 order by k.keyword limit 50",
      nativeQuery = true)
  List<Keyword> search(String fragment, String category);

  @Query(
      value =
          "select * from keywords where regexp_replace(keyword, '\\s\\(.+?\\)', '') ilike concat('%', ?1, '%') order by keyword limit 50",
      nativeQuery = true)
  List<Keyword> search(String fragment);
}
