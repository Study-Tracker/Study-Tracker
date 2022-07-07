package io.studytracker.repository;

import io.studytracker.model.FileStoreFolder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileStoreFolderRepository extends JpaRepository<FileStoreFolder, Long> {

  List<FileStoreFolder> findByPath(String path);
  List<FileStoreFolder> findByName(String name);

}
