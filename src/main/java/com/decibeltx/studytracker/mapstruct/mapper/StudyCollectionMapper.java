package com.decibeltx.studytracker.mapstruct.mapper;

import com.decibeltx.studytracker.mapstruct.dto.StudyCollectionDetailsDto;
import com.decibeltx.studytracker.mapstruct.dto.StudyCollectionSummaryDto;
import com.decibeltx.studytracker.model.StudyCollection;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudyCollectionMapper {

  StudyCollection fromSummaryDto(StudyCollectionSummaryDto dto);
  List<StudyCollection> fromSummaryDtoList(List<StudyCollectionSummaryDto> dtos);
  StudyCollectionSummaryDto toSummaryDto(StudyCollection collection);
  List<StudyCollectionSummaryDto> toSummaryDtoList(List<StudyCollection> collections);

  StudyCollection fromDetailsDto(StudyCollectionDetailsDto dto);
  List<StudyCollection> fromDetailsDtoList(List<StudyCollectionDetailsDto> dtos);
  StudyCollectionDetailsDto toDetailsDto(StudyCollection collection);
  List<StudyCollectionDetailsDto> toDetailsDtoList(List<StudyCollection> collections);

}
