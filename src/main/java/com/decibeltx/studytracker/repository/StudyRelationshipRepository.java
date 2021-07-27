package com.decibeltx.studytracker.repository;

import com.decibeltx.studytracker.model.StudyRelationship;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudyRelationshipRepository extends JpaRepository<StudyRelationship, Long> {

  @Query("select r from StudyRelationship r where r.sourceStudy.id = ?1")
  List<StudyRelationship> findBySourceStudyId(Long studyId);

  @Query("select r from StudyRelationship r where r.targetStudy.id = ?1")
  List<StudyRelationship> findByTargetStudyId(Long studyId);

  @Query("select r from StudyRelationship r where r.sourceStudy.id = ?1 and r.targetStudy.id = ?2")
  Optional<StudyRelationship> findBySourceAndTargetStudyIds(Long sourceStudyId, Long targetStudyId);

}
