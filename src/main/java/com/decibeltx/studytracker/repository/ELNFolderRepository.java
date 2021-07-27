package com.decibeltx.studytracker.repository;

import com.decibeltx.studytracker.model.ELNFolder;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ELNFolderRepository extends JpaRepository<ELNFolder, Long> {

  @Query("select f from ELNFolder f join Program p on p.notebookFolder.id = f.id where p.id = ?1")
  Optional<ELNFolder> findByProgramId(Long programId);

  @Query("select f from ELNFolder f join Study s on s.notebookFolder.id = f.id where s.id = ?1")
  Optional<ELNFolder> findByStudyId(Long studyId);

  @Query("select f from ELNFolder f join Assay a on a.notebookFolder.id = f.id where a.id = ?1")
  Optional<ELNFolder> findByAssayId(Long assayId);

}
