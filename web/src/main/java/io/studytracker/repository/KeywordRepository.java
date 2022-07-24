package io.studytracker.repository;

import io.studytracker.model.Keyword;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

  @Override
  @EntityGraph("keyword-details")
  List<Keyword> findAll();

  @Override
  @EntityGraph("keyword-details")
  Optional<Keyword> findById(Long id);

  @EntityGraph("keyword-details")
  List<Keyword> findByKeyword(String keyword);

  @EntityGraph("keyword-details")
  @Query("select k from Keyword k where k.category.name = ?1")
  List<Keyword> findByCategory(String category);

  @EntityGraph("keyword-details")
  @Query("select k from Keyword k where k.category.id = ?1")
  List<Keyword> findByCategoryId(Long categoryId);

  @EntityGraph("keyword-details")
  @Query("select k from Keyword k where k.keyword = ?1 and k.category.name = ?2")
  Optional<Keyword> findByKeywordAndCategory(String keyword, String category);

  @EntityGraph("keyword-details")
  @Query("select k from Keyword k where k.keyword = ?1 and k.category.id = ?2")
  Optional<Keyword> findByKeywordAndCategoryId(String keyword, Long categoryId);

//  @Query("select distinct(k.category) from Keyword k")
//  Set<String> findAllCategories();

  @EntityGraph("keyword-details")
  @Query(value = "select k.* from keywords k join study_keywords sk on k.id = sk.keyword_id where sk.study_id = ?1",
      nativeQuery = true)
  List<Keyword> findByStudyId(Long studyId);

  @EntityGraph("keyword-details")
  @Query("select k from Keyword k where lower(k.keyword) like lower(concat('%', ?1, '%'))")
  List<Keyword> search(String fragment, Pageable pageable);

  @EntityGraph("keyword-details")
  @Query(
      "select k from Keyword k where lower(k.keyword) like lower(concat('%', ?1, '%')) and k.category.name = ?2")
  List<Keyword> search(String fragment, String category, Pageable pageable);

  @EntityGraph("keyword-details")
  @Query("select k from Keyword k where lower(k.keyword) like lower(concat('%', ?1, '%'))")
  List<Keyword> search(String fragment);

  @EntityGraph("keyword-details")
  @Query(
      "select k from Keyword k where lower(k.keyword) like lower(concat('%', ?1, '%')) and k.category.id = ?2")
  List<Keyword> search(String fragment, Long categoryId);

//  @EntityGraph("keyword-details")
//  @Query(
//      value =
//          "select * from keywords k join keyword_categories c on k.category_id = c.id where regexp_replace(k.keyword, '\\s\\(.+?\\)', '') ilike concat('%', ?1, '%') and c.name = ?2 order by k.keyword limit 50",
//      nativeQuery = true)
//  List<Keyword> search(String fragment, String category);
//
//  @EntityGraph("keyword-details")
//  @Query(
//      value =
//          "select * from keywords where regexp_replace(keyword, '\\s\\(.+?\\)', '') ilike concat('%', ?1, '%') order by keyword limit 50",
//      nativeQuery = true)
//  List<Keyword> search(String fragment);
}
