package io.studytracker.repository;

import io.studytracker.model.FileStorageLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileStorageLocationRepository extends JpaRepository<FileStorageLocation, Long> {

}
