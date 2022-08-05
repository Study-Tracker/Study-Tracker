package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.api.KeywordDto;
import io.studytracker.mapstruct.dto.api.KeywordPayloadDto;
import io.studytracker.mapstruct.dto.form.KeywordFormDto;
import io.studytracker.mapstruct.dto.response.KeywordDetailsDto;
import io.studytracker.model.Keyword;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface KeywordMapper {
  KeywordDetailsDto toDetailsDto(Keyword keyword);

  List<KeywordDetailsDto> toDetailsDtoList(List<Keyword> keywords);

  Set<KeywordDetailsDto> toDetailsDtoSet(Set<Keyword> keywords);

  Keyword fromDetailsDto(KeywordDetailsDto dto);

  List<Keyword> fromDetailsDtoList(List<KeywordDetailsDto> dtos);

  Set<Keyword> fromDetailsDtoSet(Set<KeywordDetailsDto> dtos);

  Keyword fromFormDto(KeywordFormDto dto);

  List<Keyword> fromFormDtoList(List<KeywordFormDto> dtos);

  Set<Keyword> fromFormDtoSet(Set<KeywordFormDto> dtos);

  @Mapping(target = "categoryId", source = "category.id")
  KeywordDto toDto(Keyword keyword);
  List<KeywordDto> toDtoList(List<Keyword> keywords);
  Set<KeywordDto> toDtoSet(Set<Keyword> keywords);
  Keyword fromPayloadDto(KeywordPayloadDto dto);
}
