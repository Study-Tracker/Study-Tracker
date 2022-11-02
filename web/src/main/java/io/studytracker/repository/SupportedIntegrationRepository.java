package io.studytracker.repository;

import io.studytracker.model.SupportedIntegration;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SupportedIntegrationRepository extends JpaRepository<SupportedIntegration, Long> {

  List<SupportedIntegration> findByActive(Boolean active);

  List<SupportedIntegration> findByName(String name);

  Optional<SupportedIntegration> findByNameAndVersion(String name, Integer version);

  @Query("select i from SupportedIntegration i where i.name = ?1 and i.active = true "
      + "and i.version = (select max(version) from SupportedIntegration where name = ?1)")
  Optional<SupportedIntegration> findLatestByName(String name);

}
