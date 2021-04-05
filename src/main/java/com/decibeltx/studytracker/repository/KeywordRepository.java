package com.decibeltx.studytracker.repository;

import com.decibeltx.studytracker.model.Keyword;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface KeywordRepository extends MongoRepository<Keyword, String> {

  List<Keyword> findByKeyword(String keyword);

  List<Keyword> findByCategory(String category);

  Optional<Keyword> findByKeywordAndCategory(String keyword, String category);

//  List<Keyword> findByKeywordLike(String keyword);

//  List<Keyword> findByCategoryAndKeywordLike(String category, String keyword);

  @Aggregation("{ '$project': { '_id': '$category' } }")
  Set<String> findAllCategories();

  @Query("{ 'keyword': { '$regex': ?0, '$options': 'i'  }}")
  List<Keyword> search(String fragment);

  @Query("{ 'keyword': { '$regex': ?0, '$options': 'i'  }, 'category': ?1}")
  List<Keyword> search(String fragment, String category);

}
