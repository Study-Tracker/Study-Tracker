package io.studytracker.repository;

import io.studytracker.model.KeywordCategory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordCategoryRepository extends JpaRepository<KeywordCategory, Long> {
  Optional<KeywordCategory> findByName(String name);
}
