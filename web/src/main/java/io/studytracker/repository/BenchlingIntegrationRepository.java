package io.studytracker.repository;

import io.studytracker.model.BenchlingIntegration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BenchlingIntegrationRepository extends JpaRepository<BenchlingIntegration, Long> {
}
