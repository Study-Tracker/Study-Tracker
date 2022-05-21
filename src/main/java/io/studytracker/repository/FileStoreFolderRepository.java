package io.studytracker.repository;

import io.studytracker.model.FileStoreFolder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileStoreFolderRepository extends JpaRepository<FileStoreFolder, Long> {

}
