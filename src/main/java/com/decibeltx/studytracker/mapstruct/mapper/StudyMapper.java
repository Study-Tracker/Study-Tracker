package com.decibeltx.studytracker.mapstruct.mapper;

import com.decibeltx.studytracker.mapstruct.dto.StudyDetailsDto;
import com.decibeltx.studytracker.mapstruct.dto.StudyFormDto;
import com.decibeltx.studytracker.mapstruct.dto.StudySlimDto;
import com.decibeltx.studytracker.mapstruct.dto.StudySummaryDto;
import com.decibeltx.studytracker.model.Study;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudyMapper {

  Study fromStudyForm(StudyFormDto dto);

  Study fromStudyDetails(StudyDetailsDto dto);
  List<Study> fromStudyDetailsList(List<StudyDetailsDto> dtos);
  Set<Study> fromStudyDetailsSet(Set<StudyDetailsDto> dtos);
  StudyDetailsDto toStudyDetails(Study study);
  List<StudyDetailsDto> toStudyDetailsList(List<Study> studies);
  Set<StudyDetailsDto> toStudyDetailsSet(Set<Study> studies);

  Study fromStudySummary(StudySummaryDto dto);
  List<Study> fromStudySummaryList(List<StudySummaryDto> dtos);
  Set<Study> fromStudySummarySet(Set<StudySummaryDto> dtos);
  StudySummaryDto toStudySummary(Study study);
  List<StudySummaryDto> toStudySummaryList(List<Study> studies);
  Set<StudySummaryDto> toStudySummarySet(Set<Study> studies);

  Study fromStudySlim(StudySlimDto dto);
  List<Study> fromStudySlimList(List<StudySlimDto> dtos);
  Set<Study> fromStudySlimSet(Set<StudySlimDto> dtos);
  StudySlimDto toStudySlim(Study study);
  List<StudySlimDto> toStudySlimList(List<Study> studies);
  Set<StudySlimDto> toStudySlimSet(Set<Study> studies);

}
