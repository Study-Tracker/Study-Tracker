package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.api.StudyCollectionDto;
import io.studytracker.mapstruct.dto.api.StudyCollectionPayloadDto;
import io.studytracker.mapstruct.dto.form.StudyCollectionFormDto;
import io.studytracker.mapstruct.dto.response.StudyCollectionDetailsDto;
import io.studytracker.mapstruct.dto.response.StudyCollectionSummaryDto;
import io.studytracker.model.Study;
import io.studytracker.model.StudyCollection;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

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

  StudyCollection fromFormDto(StudyCollectionFormDto dto);

  @Mapping(target = "createdBy", source = "createdBy.id")
  @Mapping(target = "lastModifiedBy", source = "lastModifiedBy.id")
  @Mapping(target = "studies", source = "studies", qualifiedByName = "studyToId")
  StudyCollectionDto toDto(StudyCollection collection);
  List<StudyCollectionDto> toDtoList(List<StudyCollection> collections);

  @Mapping(target = "studies", ignore = true)
  StudyCollection fromPayloadDto(StudyCollectionPayloadDto dto);

  @Named("studyToId")
  public static Long studyToId(Study study) { return study.getId();}

}
