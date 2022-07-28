package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.form.KeywordFormDto;
import io.studytracker.mapstruct.dto.response.KeywordDetailsDto;
import io.studytracker.model.Keyword;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface KeywordMapper {
  KeywordDetailsDto toDto(Keyword keyword);

  List<KeywordDetailsDto> toDtoList(List<Keyword> keywords);

  Set<KeywordDetailsDto> toDtoSet(Set<Keyword> keywords);

  Keyword fromDto(KeywordDetailsDto dto);

  List<Keyword> fromDtoList(List<KeywordDetailsDto> dtos);

  Set<Keyword> fromDtoSet(Set<KeywordDetailsDto> dtos);

  Keyword fromFormDto(KeywordFormDto dto);

  List<Keyword> fromFormDtoList(List<KeywordFormDto> dtos);

  Set<Keyword> fromFormDtoSet(Set<KeywordFormDto> dtos);
}
